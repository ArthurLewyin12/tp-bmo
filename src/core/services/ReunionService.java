package core.services;

import core.enums.Permission;
import core.enums.ReunionType;
import core.enums.Role;
import core.generics.DBRepository;
import core.interfaces.IMeetingService;
import core.models.Reunion;
import core.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

public class ReunionService implements IMeetingService {

    private final DBRepository<Reunion> repository;

    public ReunionService(DBRepository<Reunion> repository) {
        this.repository = repository;
    }

    @Override
    public Reunion createMeeting(String name, String topic, LocalDateTime startTime,
                                  int durationMinutes, User organizer, ReunionType type, String agenda) {
        String id = UUID.randomUUID().toString();
        Reunion reunion = new Reunion(id, name, topic, startTime, durationMinutes, organizer, type, agenda);
        repository.save(id, reunion);
        return reunion;
    }

    @Override
    public Optional<Reunion> getMeetingById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Reunion> getAllMeetings() {
        return repository.findAll();
    }

    @Override
    public void addParticipant(Reunion reunion, User user) {
        if (reunion.getType() == ReunionType.PRIVATE
                && !reunion.getAllowedUsers().contains(user)
                && !user.equals(reunion.getOrganizer())) {
            throw new IllegalStateException("Accès refusé : réunion privée");
        }
        if (!reunion.getParticipants().contains(user)) {
            reunion.getParticipants().add(user);
        }
    }

    @Override
    public void removeParticipant(Reunion reunion, User user) {
        reunion.getParticipants().remove(user);
        reunion.getSpeechQueue().remove(user);
        if (user.equals(reunion.getCurrentSpeaker())) {
            reunion.setCurrentSpeaker(null);
        }
    }

    @Override
    public void setModerator(Reunion reunion, User user) {
        reunion.setModerator(user);
    }

    @Override
    public void startMeeting(Reunion reunion) {
        reunion.setOpen(true);
    }

    @Override
    public void endMeeting(Reunion reunion) {
        reunion.setOpen(false);
        reunion.getSpeechQueue().clear();
        reunion.setCurrentSpeaker(null);
    }

    @Override
    public void requestToSpeak(Reunion reunion, User user) {
        if (!reunion.isOpen()) {
            throw new IllegalStateException("La réunion n'est pas ouverte");
        }
        if (!reunion.getSpeechQueue().contains(user)) {
            reunion.getSpeechQueue().add(user);
        }
        // Réunion démocratique : le premier demandeur est accordé automatiquement
        if (reunion.getType() == ReunionType.DEMOCRATIC && reunion.getCurrentSpeaker() == null) {
            grantNextSpeaker(reunion);
        }
    }

    /**
     * Accorde la parole à un utilisateur (utilisé par l'animateur dans les réunions STANDARD/PRIVATE).
     */
    public void grantSpeech(Reunion reunion, User moderator, User user) {
        if (!moderator.equals(reunion.getModerator()) && !moderator.equals(reunion.getOrganizer())) {
            throw new IllegalStateException("Seul l'animateur peut accorder la parole");
        }
        reunion.setCurrentSpeaker(user);
    }

    @Override
    public void speak(Reunion reunion, User user, String message) {
        if (!reunion.isOpen()) {
            throw new IllegalStateException("La réunion n'est pas ouverte");
        }
        boolean isPrivileged = user.equals(reunion.getOrganizer()) || user.equals(reunion.getModerator());
        if (!isPrivileged && !user.equals(reunion.getCurrentSpeaker())) {
            throw new IllegalStateException("La parole n'a pas été accordée à " + user.getFirstName());
        }
        System.out.printf("[%s] %s %s : %s%n",
                reunion.getName(), user.getFirstName(), user.getLastName(), message);

        // Réunion démocratique : on passe au suivant automatiquement
        if (reunion.getType() == ReunionType.DEMOCRATIC && user.equals(reunion.getCurrentSpeaker())) {
            reunion.getSpeechQueue().remove(user);
            grantNextSpeaker(reunion);
        }
    }

    @Override
    public List<User> getParticipants(Reunion reunion) {
        return reunion.getParticipants();
    }

    @Override
    public Queue<User> getSpeechQueue(Reunion reunion) {
        return reunion.getSpeechQueue();
    }

    @Override
    public boolean canUserPerform(Reunion reunion, User user, Permission permission) {
        if (user.equals(reunion.getOrganizer())) {
            return Role.ORGANIZER.hasPermission(permission);
        }
        if (user.equals(reunion.getModerator())) {
            return Role.MODERATOR.hasPermission(permission);
        }
        if (reunion.getParticipants().contains(user)) {
            return Role.PARTICIPANT.hasPermission(permission);
        }
        return false;
    }



    private void grantNextSpeaker(Reunion reunion) {
        reunion.setCurrentSpeaker(reunion.getSpeechQueue().peek());
    }
}
