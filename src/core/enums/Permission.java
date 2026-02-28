package core.enums;

public enum Permission {

    SPEAK,                // Parler / envoyer du texte
    REQUEST_SPEAK,        // Demander la parole
    END_MEETING,          // Clôturer la réunion
    SET_MODERATOR,        // Nommer un animateur
    REMOVE_PARTICIPANT,   // Éjecter quelqu’un (optionnel)
    VIEW_PARTICIPANTS,     // Voir qui est dans la réunion
    MUTE_PARTICIPANT, // muter un participant
    ADD_PARTICIPANT,
    DELETE_PARTICIPANT,
}