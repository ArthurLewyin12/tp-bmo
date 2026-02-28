package core.services;

import core.generics.IRepository;
import core.interfaces.IChatMessageService;
import core.models.ChatMessage;
import core.models.Reunion;
import core.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChatMessageService implements IChatMessageService {

    private final IRepository<ChatMessage> repository;

    public ChatMessageService(IRepository<ChatMessage> repository) {
        this.repository = repository;
    }

    @Override
    public ChatMessage save(Reunion reunion, User author, String content) {
        String id = UUID.randomUUID().toString();
        ChatMessage message = new ChatMessage(id, author, reunion, content, LocalDateTime.now());
        repository.save(id, message);
        return message;
    }

    @Override
    public List<ChatMessage> getHistory(Reunion reunion) {
        return repository.findAll().stream()
                .filter(m -> m.getReunion().getId().equals(reunion.getId()))
                .toList();
    }
}
