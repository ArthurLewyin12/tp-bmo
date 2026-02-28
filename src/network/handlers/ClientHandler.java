package network.handlers;

import network.protocol.Action;
import network.protocol.Message;
import network.session.ClientSession;
import network.session.SessionManager;

import java.io.IOException;

public class ClientHandler implements Runnable {

    private final ClientSession session;
    private final MessageDispatcher dispatcher;
    private final SessionManager sessionManager;

    public ClientHandler(ClientSession session, MessageDispatcher dispatcher, SessionManager sessionManager) {
        this.session = session;
        this.dispatcher = dispatcher;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = session.readLine()) != null) {
                try {
                    Message msg = Message.parse(line);
                    dispatcher.dispatch(session, msg);
                } catch (IllegalArgumentException e) {
                    session.send(new Message(Action.ERROR, "PROTOCOL", "Action inconnue ou message malformé"));
                } catch (Exception e) {
                    session.send(new Message(Action.ERROR, "SERVER", e.getMessage()));
                }
            }
        } catch (IOException e) {
            System.out.println("Connexion perdue : " + session.getAddress());
        } finally {
            sessionManager.remove(session);
            try { session.close(); } catch (IOException ignored) {}
        }
    }
}
