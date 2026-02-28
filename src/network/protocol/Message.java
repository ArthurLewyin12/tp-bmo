package network.protocol;

import java.util.Arrays;

public class Message {

    private static final String SEPARATOR = "|";

    private final Action action;
    private final String[] params;

    public Message(Action action, String... params) {
        this.action = action;
        this.params = params;
    }

    // Désérialisation : "SPEAK|meetingId|Bonjour !" → Message
    public static Message parse(String raw) {
        String[] parts = raw.trim().split("\\|");
        Action action = Action.valueOf(parts[0].toUpperCase());
        String[] params = parts.length > 1
                ? Arrays.copyOfRange(parts, 1, parts.length)
                : new String[0];
        return new Message(action, params);
    }

    // Sérialisation : Message → "SPEAK|meetingId|Bonjour !"
    public String serialize() {
        if (params == null || params.length == 0) return action.name();
        return action.name() + SEPARATOR + String.join(SEPARATOR, params);
    }

    public Action getAction() { return action; }
    public String[] getParams() { return params; }

    public String getParam(int index) {
        if (index >= params.length) throw new IllegalArgumentException("Paramètre manquant à l'index " + index);
        return params[index];
    }
}
