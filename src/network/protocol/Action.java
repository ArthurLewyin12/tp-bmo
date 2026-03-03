package network.protocol;

public enum Action {

    // Client → Serveur
    REGISTER,
    LOGIN,
    CREATE_MEETING,
    GET_MEETINGS,
    GET_MEETING_DETAILS,
    GET_PARTICIPANTS,
    UPDATE_MEETING,
    ADD_ALLOWED_USER,
    JOIN_MEETING,
    LEAVE_MEETING,
    START_MEETING,
    END_MEETING,
    SET_MODERATOR,
    MUTE_PARTICIPANT,
    REQUEST_SPEAK,
    GRANT_SPEECH,
    SPEAK,
    GET_HISTORY,

    // Serveur → Client (réponses directes)
    OK,
    ERROR,

    // Serveur → Client (broadcasts temps réel)
    BROADCAST
}
