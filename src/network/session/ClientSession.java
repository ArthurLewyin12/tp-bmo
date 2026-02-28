package network.session;

import core.models.Reunion;
import core.models.User;
import network.protocol.Message;

import java.io.*;
import java.net.Socket;

public class ClientSession {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    private User user;               // null tant que non connecté
    private Reunion currentMeeting;  // null si pas dans une réunion

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        this.in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public void send(Message message) {
        out.println(message.serialize());
    }

    public String readLine() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        socket.close();
    }

    public boolean isLoggedIn() { return user != null; }



    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Reunion getCurrentMeeting() { return currentMeeting; }
    public void setCurrentMeeting(Reunion currentMeeting) { this.currentMeeting = currentMeeting; }

    public String getAddress() { return socket.getInetAddress().toString(); }
}
