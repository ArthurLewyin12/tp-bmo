package network.session;

import core.models.Reunion;
import network.protocol.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionManager {

    // CopyOnWriteArrayList pour éviter les conflits entre threads
    private final List<ClientSession> sessions = new CopyOnWriteArrayList<>();

    public void add(ClientSession session) {
        sessions.add(session);
        System.out.println("Nouveau client connecté : " + session.getAddress());
    }

    public void remove(ClientSession session) {
        sessions.remove(session);
        System.out.println("Client déconnecté : " + session.getAddress());
    }

    // Envoie un message à tous les participants d'une réunion
    public void broadcast(Reunion reunion, Message message) {
        sessions.stream()
                .filter(s -> reunion.equals(s.getCurrentMeeting()))
                .forEach(s -> s.send(message));
    }

    // Envoie un message à tous les clients connectés
    public void broadcastAll(Message message) {
        sessions.forEach(s -> s.send(message));
    }

    public int count() { return sessions.size(); }
}
