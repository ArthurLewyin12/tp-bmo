package core.interfaces;

import core.enums.Permission;
import core.enums.ReunionType;
import core.models.Reunion;
import core.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

public interface IMeetingService {

    // Création et récupération des réunions
    Reunion createMeeting(String name, String topic,
                          java.time.LocalDateTime startTime,
                          int durationMinutes,
                          User organizer,
                          ReunionType type,
                          String agenda);

    Optional<Reunion> getMeetingById(String id);
    List<Reunion> getAllMeetings();

    // Gestion des participants
    void addParticipant(Reunion reunion, User user);
    void removeParticipant(Reunion reunion, User user);

    // Gestion du rôle de modérateur
    void setModerator(Reunion reunion, User user);

    // Gestion de l'état de la réunion
    void startMeeting(Reunion reunion);
    void endMeeting(Reunion reunion);

    // Gestion de la parole pendant la réunion
    void requestToSpeak(Reunion reunion, User user);
    void speak(Reunion meeting, User user, String message);

    // Consultation
    List<User> getParticipants(Reunion reunion);
    Queue<User> getSpeechQueue(Reunion reunion);

    // Vérification des permissions
    boolean canUserPerform(Reunion reunion, User user, Permission permission);
}