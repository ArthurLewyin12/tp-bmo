package core.interfaces;

import core.models.ChatMessage;
import core.models.Reunion;
import core.models.User;

import java.util.List;

public interface IChatMessageService {

    ChatMessage save(Reunion reunion, User author, String content);
    List<ChatMessage> getHistory(Reunion reunion);
}
