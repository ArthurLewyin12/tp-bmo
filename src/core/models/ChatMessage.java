package core.models;

import java.time.LocalDateTime;

public record ChatMessage(String id, User author, Reunion reunion, String content, LocalDateTime sentAt) {}
