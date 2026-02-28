package network;

import core.services.ChatMessageService;
import core.services.ReunionService;
import core.services.UserService;
import network.handlers.ClientHandler;
import network.handlers.MessageDispatcher;
import network.session.ClientSession;
import network.session.SessionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;
    private final SessionManager sessionManager;
    private final MessageDispatcher dispatcher;
    private volatile boolean running = false;

    public Server(int port, UserService userService, ReunionService reunionService, ChatMessageService chatMessageService) {
        this.port = port;
        this.sessionManager = new SessionManager();
        this.dispatcher = new MessageDispatcher(userService, reunionService, chatMessageService, sessionManager);
    }

    public void start() throws IOException {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur BMO démarré sur le port " + port);
            System.out.println("En attente de connexions...");

            while (running) {
                Socket clientSocket = serverSocket.accept();
                try {
                    ClientSession session = new ClientSession(clientSocket);
                    sessionManager.add(session);
                    new Thread(new ClientHandler(session, dispatcher, sessionManager)).start();
                } catch (IOException e) {
                    System.out.println("Erreur lors de la connexion d'un client : " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;
    }
}
