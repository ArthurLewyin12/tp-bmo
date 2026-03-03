import core.generics.DBRepository;
import core.models.ChatMessage;
import core.models.Reunion;
import core.models.User;
import core.services.ChatMessageService;
import core.services.ReunionService;
import core.services.UserService;
import network.Server;

import java.io.IOException;

void main() throws IOException {

    // Repositories
    DBRepository<User>        userRepo    = new DBRepository<>();
    DBRepository<Reunion>     reunionRepo = new DBRepository<>();
    DBRepository<ChatMessage> messageRepo = new DBRepository<>();

    // Services
    UserService        userService        = new UserService(userRepo);
    ReunionService     reunionService     = new ReunionService(reunionRepo);
    ChatMessageService chatMessageService = new ChatMessageService(messageRepo);

    // Serveur TCP
    Server server = new Server(8080, userService, reunionService, chatMessageService);
    server.start();
}
