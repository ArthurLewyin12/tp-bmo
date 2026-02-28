package core.models;

import java.time.LocalDateTime;

public class ChatMessage {

    private final String id;
    private final User author;
    private final Reunion reunion;
    private final String content;
    private final LocalDateTime sentAt;

    public ChatMessage(String id, User author, Reunion reunion, String content, LocalDateTime sentAt) {
        this.id = id;
        this.author = author;
        this.reunion = reunion;
        this.content = content;
        this.sentAt = sentAt;
    }

    public String getId() { return id; }
    public User getAuthor() { return author; }
    public Reunion getReunion() { return reunion; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
}
