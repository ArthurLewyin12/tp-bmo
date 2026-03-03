package core.models;

import core.enums.ReunionType;

import java.time.LocalDateTime;
import java.util.*;

public class Reunion {

    private final String id;
    private final String name;
    private String topic;
    private LocalDateTime startTime;
    private int durationMinutes;
    private final User organizer;
    private User moderator;
    private final List<User> participants;
    private final Queue<User> speechQueue;
    private boolean isOpen;
    private final ReunionType type;
    private final List<User> allowedUsers; // pour les réunions PRIVATE
    private final List<User> mutedUsers;   // participants mutés
    private String agenda;
    private User currentSpeaker;

    public Reunion(String id, String name, String topic, LocalDateTime startTime,
                   int durationMinutes, User organizer, ReunionType type, String agenda) {
        this.id = id;
        this.name = name;
        this.topic = topic;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.organizer = organizer;
        this.type = type;
        this.agenda = agenda;
        this.participants = new ArrayList<>();
        this.speechQueue = new LinkedList<>();
        this.allowedUsers = new ArrayList<>();
        this.mutedUsers = new ArrayList<>();
        this.isOpen = false;
        this.moderator = organizer; // l'organisateur est animateur par défaut
    }


    public String getId() { return id; }
    public String getName() { return name; }
    public String getTopic() { return topic; }
    public LocalDateTime getStartTime() { return startTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public User getOrganizer() { return organizer; }
    public User getModerator() { return moderator; }
    public List<User> getParticipants() { return participants; }
    public Queue<User> getSpeechQueue() { return speechQueue; }
    public boolean isOpen() { return isOpen; }
    public ReunionType getType() { return type; }
    public List<User> getAllowedUsers() { return allowedUsers; }
    public List<User> getMutedUsers() { return mutedUsers; }
    public String getAgenda() { return agenda; }
    public User getCurrentSpeaker() { return currentSpeaker; }



    public void setTopic(String topic) { this.topic = topic; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setModerator(User moderator) { this.moderator = moderator; }
    public void setOpen(boolean open) { this.isOpen = open; }
    public void setAgenda(String agenda) { this.agenda = agenda; }
    public void setCurrentSpeaker(User currentSpeaker) { this.currentSpeaker = currentSpeaker; }

    @Override
    public String toString() {
        return String.format("Reunion{id='%s', name='%s', type=%s, open=%s}", id, name, type, isOpen);
    }
}
