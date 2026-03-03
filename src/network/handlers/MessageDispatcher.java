package network.handlers;

import core.enums.Permission;
import core.enums.ReunionType;
import core.models.ChatMessage;
import core.models.Reunion;
import core.models.User;
import core.services.ChatMessageService;
import core.services.ReunionService;
import core.services.UserService;
import network.protocol.Action;
import network.protocol.Message;
import network.session.ClientSession;
import network.session.SessionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MessageDispatcher {

    private final UserService userService;
    private final ReunionService reunionService;
    private final ChatMessageService chatMessageService;
    private final SessionManager sessionManager;

    public MessageDispatcher(UserService userService, ReunionService reunionService,
                             ChatMessageService chatMessageService, SessionManager sessionManager) {
        this.userService = userService;
        this.reunionService = reunionService;
        this.chatMessageService = chatMessageService;
        this.sessionManager = sessionManager;
    }

    public void dispatch(ClientSession session, Message msg) {
        switch (msg.getAction()) {
            case REGISTER       -> handleRegister(session, msg);
            case LOGIN          -> handleLogin(session, msg);
            case CREATE_MEETING -> handleCreateMeeting(session, msg);
            case GET_MEETINGS        -> handleGetMeetings(session);
            case GET_MEETING_DETAILS -> handleGetMeetingDetails(session, msg);
            case UPDATE_MEETING      -> handleUpdateMeeting(session, msg);
            case ADD_ALLOWED_USER    -> handleAddAllowedUser(session, msg);
            case JOIN_MEETING        -> handleJoinMeeting(session, msg);
            case LEAVE_MEETING  -> handleLeaveMeeting(session);
            case START_MEETING  -> handleStartMeeting(session, msg);
            case END_MEETING    -> handleEndMeeting(session, msg);
            case SET_MODERATOR  -> handleSetModerator(session, msg);
            case MUTE_PARTICIPANT -> handleMuteParticipant(session, msg);
            case REQUEST_SPEAK  -> handleRequestSpeak(session, msg);
            case GRANT_SPEECH   -> handleGrantSpeech(session, msg);
            case SPEAK          -> handleSpeak(session, msg);
            case GET_HISTORY    -> handleGetHistory(session, msg);
            default             -> session.send(new Message(Action.ERROR, "PROTOCOL", "Action non supportée"));
        }
    }

    // ==================== AUTH ====================

    // REGISTER|firstName|lastName|email|phone|password
    private void handleRegister(ClientSession session, Message msg) {
        try {
            User user = userService.register(
                    msg.getParam(0), msg.getParam(1), msg.getParam(2),
                    msg.getParam(3), msg.getParam(4)
            );
            session.send(new Message(Action.OK, "REGISTER", user.getId(), user.getFirstName()));
        } catch (IllegalArgumentException e) {
            session.send(new Message(Action.ERROR, "REGISTER", e.getMessage()));
        }
    }

    // LOGIN|email|password
    private void handleLogin(ClientSession session, Message msg) {
        userService.login(msg.getParam(0), msg.getParam(1))
                .ifPresentOrElse(
                        user -> {
                            session.setUser(user);
                            session.send(new Message(Action.OK, "LOGIN",
                                    user.getId(), user.getFirstName(), user.getLastName()));
                        },
                        () -> session.send(new Message(Action.ERROR, "LOGIN", "Email ou mot de passe incorrect"))
                );
    }

    // ==================== RÉUNIONS ====================

    // CREATE_MEETING|name|topic|durationMinutes|type|agenda
    private void handleCreateMeeting(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        try {
            ReunionType type = ReunionType.valueOf(msg.getParam(3).toUpperCase());
            Reunion reunion = reunionService.createMeeting(
                    msg.getParam(0), msg.getParam(1),
                    LocalDateTime.now(),
                    Integer.parseInt(msg.getParam(2)),
                    session.getUser(), type, msg.getParam(4)
            );
            session.send(new Message(Action.OK, "CREATE_MEETING", reunion.getId(), reunion.getName()));
        } catch (Exception e) {
            session.send(new Message(Action.ERROR, "CREATE_MEETING", e.getMessage()));
        }
    }

    // GET_MEETINGS
    private void handleGetMeetings(ClientSession session) {
        if (!checkLogin(session)) return;
        List<Reunion> meetings = reunionService.getAllMeetings();
        if (meetings.isEmpty()) {
            session.send(new Message(Action.OK, "GET_MEETINGS", "AUCUNE"));
            return;
        }
        // Format : id,name,type,isOpen séparés par ";"
        String list = meetings.stream()
                .map(r -> r.getId() + "," + r.getName() + "," + r.getType() + "," + r.isOpen())
                .collect(Collectors.joining(";"));
        session.send(new Message(Action.OK, "GET_MEETINGS", list));
    }

    // GET_MEETING_DETAILS|meetingId
    private void handleGetMeetingDetails(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> session.send(new Message(Action.OK, "GET_MEETING_DETAILS",
                                reunion.getId(),
                                reunion.getName(),
                                reunion.getTopic(),
                                reunion.getType().name(),
                                String.valueOf(reunion.isOpen()),
                                String.valueOf(reunion.getDurationMinutes()),
                                reunion.getOrganizer().getFirstName() + " " + reunion.getOrganizer().getLastName(),
                                reunion.getModerator().getFirstName() + " " + reunion.getModerator().getLastName(),
                                reunion.getAgenda()
                        )),
                        () -> session.send(new Message(Action.ERROR, "GET_MEETING_DETAILS", "Réunion introuvable"))
                );
    }

    // UPDATE_MEETING|meetingId|newTopic|newDurationMinutes|newAgenda
    private void handleUpdateMeeting(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            if (!session.getUser().equals(reunion.getOrganizer())) {
                                session.send(new Message(Action.ERROR, "UPDATE_MEETING", "Seul l'organisateur peut modifier la réunion"));
                                return;
                            }
                            try {
                                reunion.setTopic(msg.getParam(1));
                                reunion.setDurationMinutes(Integer.parseInt(msg.getParam(2)));
                                reunion.setAgenda(msg.getParam(3));
                                session.send(new Message(Action.OK, "UPDATE_MEETING", reunion.getId(), reunion.getName()));
                            } catch (NumberFormatException e) {
                                session.send(new Message(Action.ERROR, "UPDATE_MEETING", "Durée invalide"));
                            }
                        },
                        () -> session.send(new Message(Action.ERROR, "UPDATE_MEETING", "Réunion introuvable"))
                );
    }

    // ADD_ALLOWED_USER|meetingId|userEmail
    private void handleAddAllowedUser(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            if (!session.getUser().equals(reunion.getOrganizer())) {
                                session.send(new Message(Action.ERROR, "ADD_ALLOWED_USER", "Seul l'organisateur peut inviter des membres"));
                                return;
                            }
                            userService.getUserByEmail(msg.getParam(1))
                                    .ifPresentOrElse(
                                            user -> {
                                                if (!reunion.getAllowedUsers().contains(user)) {
                                                    reunion.getAllowedUsers().add(user);
                                                }
                                                session.send(new Message(Action.OK, "ADD_ALLOWED_USER",
                                                        user.getFirstName(), user.getLastName()));
                                            },
                                            () -> session.send(new Message(Action.ERROR, "ADD_ALLOWED_USER", "Utilisateur introuvable"))
                                    );
                        },
                        () -> session.send(new Message(Action.ERROR, "ADD_ALLOWED_USER", "Réunion introuvable"))
                );
    }

    // JOIN_MEETING|meetingId
    private void handleJoinMeeting(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            try {
                                reunionService.addParticipant(reunion, session.getUser());
                                session.setCurrentMeeting(reunion);
                                session.send(new Message(Action.OK, "JOIN_MEETING", reunion.getId(), reunion.getName()));
                                sessionManager.broadcast(reunion, new Message(Action.BROADCAST,
                                        "PARTICIPANT_JOINED", session.getUser().getFirstName(), session.getUser().getLastName()));
                            } catch (IllegalStateException e) {
                                session.send(new Message(Action.ERROR, "JOIN_MEETING", e.getMessage()));
                            }
                        },
                        () -> session.send(new Message(Action.ERROR, "JOIN_MEETING", "Réunion introuvable"))
                );
    }

    // LEAVE_MEETING
    private void handleLeaveMeeting(ClientSession session) {
        if (!checkLogin(session)) return;
        Reunion reunion = session.getCurrentMeeting();
        if (reunion == null) {
            session.send(new Message(Action.ERROR, "LEAVE_MEETING", "Vous n'êtes pas dans une réunion"));
            return;
        }
        reunionService.removeParticipant(reunion, session.getUser());
        sessionManager.broadcast(reunion, new Message(Action.BROADCAST,
                "PARTICIPANT_LEFT", session.getUser().getFirstName(), session.getUser().getLastName()));
        session.setCurrentMeeting(null);
        session.send(new Message(Action.OK, "LEAVE_MEETING"));
    }

    // START_MEETING|meetingId
    private void handleStartMeeting(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            if (!reunionService.canUserPerform(reunion, session.getUser(), Permission.END_MEETING)) {
                                session.send(new Message(Action.ERROR, "START_MEETING", "Permission refusée"));
                                return;
                            }
                            reunionService.startMeeting(reunion);
                            sessionManager.broadcast(reunion, new Message(Action.BROADCAST, "MEETING_STARTED", reunion.getName()));
                            session.send(new Message(Action.OK, "START_MEETING", reunion.getId()));
                        },
                        () -> session.send(new Message(Action.ERROR, "START_MEETING", "Réunion introuvable"))
                );
    }

    // END_MEETING|meetingId
    private void handleEndMeeting(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            if (!reunionService.canUserPerform(reunion, session.getUser(), Permission.END_MEETING)) {
                                session.send(new Message(Action.ERROR, "END_MEETING", "Permission refusée"));
                                return;
                            }
                            reunionService.endMeeting(reunion);
                            sessionManager.broadcast(reunion, new Message(Action.BROADCAST, "MEETING_ENDED", reunion.getName()));
                            session.send(new Message(Action.OK, "END_MEETING", reunion.getId()));
                        },
                        () -> session.send(new Message(Action.ERROR, "END_MEETING", "Réunion introuvable"))
                );
    }

    // ==================== MODÉRATION ====================

    // SET_MODERATOR|meetingId|userId
    private void handleSetModerator(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            if (!reunionService.canUserPerform(reunion, session.getUser(), Permission.SET_MODERATOR)) {
                                session.send(new Message(Action.ERROR, "SET_MODERATOR", "Permission refusée"));
                                return;
                            }
                            reunion.getParticipants().stream()
                                    .filter(u -> u.getId().equals(msg.getParam(1)))
                                    .findFirst()
                                    .ifPresentOrElse(
                                            user -> {
                                                reunionService.setModerator(reunion, user);
                                                session.send(new Message(Action.OK, "SET_MODERATOR", user.getFirstName(), user.getLastName()));
                                                sessionManager.broadcast(reunion, new Message(Action.BROADCAST,
                                                        "MODERATOR_SET", user.getFirstName(), user.getLastName()));
                                            },
                                            () -> session.send(new Message(Action.ERROR, "SET_MODERATOR", "Utilisateur introuvable dans la réunion"))
                                    );
                        },
                        () -> session.send(new Message(Action.ERROR, "SET_MODERATOR", "Réunion introuvable"))
                );
    }



    // MUTE_PARTICIPANT|meetingId|userId
    private void handleMuteParticipant(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            if (!reunionService.canUserPerform(reunion, session.getUser(), Permission.MUTE_PARTICIPANT)) {
                                session.send(new Message(Action.ERROR, "MUTE_PARTICIPANT", "Permission refusée"));
                                return;
                            }
                            reunion.getParticipants().stream()
                                    .filter(u -> u.getId().equals(msg.getParam(1)))
                                    .findFirst()
                                    .ifPresentOrElse(
                                            user -> {
                                                reunionService.muteParticipant(reunion, user);
                                                session.send(new Message(Action.OK, "MUTE_PARTICIPANT", user.getFirstName(), user.getLastName()));
                                                sessionManager.broadcast(reunion, new Message(Action.BROADCAST,
                                                        "PARTICIPANT_MUTED", user.getFirstName(), user.getLastName()));
                                            },
                                            () -> session.send(new Message(Action.ERROR, "MUTE_PARTICIPANT", "Utilisateur introuvable dans la réunion"))
                                    );
                        },
                        () -> session.send(new Message(Action.ERROR, "MUTE_PARTICIPANT", "Réunion introuvable"))
                );
    }

    // REQUEST_SPEAK|meetingId
    private void handleRequestSpeak(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            try {
                                reunionService.requestToSpeak(reunion, session.getUser());
                                session.send(new Message(Action.OK, "REQUEST_SPEAK", "Demande enregistrée"));
                                // Si DEMOCRATIC et parole accordée directement
                                if (session.getUser().equals(reunion.getCurrentSpeaker())) {
                                    sessionManager.broadcast(reunion, new Message(Action.BROADCAST,
                                            "SPEECH_GRANTED", session.getUser().getFirstName(), session.getUser().getLastName()));
                                }
                            } catch (IllegalStateException e) {
                                session.send(new Message(Action.ERROR, "REQUEST_SPEAK", e.getMessage()));
                            }
                        },
                        () -> session.send(new Message(Action.ERROR, "REQUEST_SPEAK", "Réunion introuvable"))
                );
    }

    // GRANT_SPEECH|meetingId|userId
    private void handleGrantSpeech(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> reunion.getParticipants().stream()
                                .filter(u -> u.getId().equals(msg.getParam(1)))
                                .findFirst()
                                .ifPresentOrElse(
                                        user -> {
                                            try {
                                                reunionService.grantSpeech(reunion, session.getUser(), user);
                                                session.send(new Message(Action.OK, "GRANT_SPEECH", user.getFirstName(), user.getLastName()));
                                                sessionManager.broadcast(reunion, new Message(Action.BROADCAST,
                                                        "SPEECH_GRANTED", user.getFirstName(), user.getLastName()));
                                            } catch (IllegalStateException e) {
                                                session.send(new Message(Action.ERROR, "GRANT_SPEECH", e.getMessage()));
                                            }
                                        },
                                        () -> session.send(new Message(Action.ERROR, "GRANT_SPEECH", "Utilisateur introuvable"))
                                ),
                        () -> session.send(new Message(Action.ERROR, "GRANT_SPEECH", "Réunion introuvable"))
                );
    }

    // SPEAK|meetingId|message
    private void handleSpeak(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            try {
                                String message = msg.getParam(1);
                                reunionService.speak(reunion, session.getUser(), message);
                                chatMessageService.save(reunion, session.getUser(), message);
                                sessionManager.broadcast(reunion, new Message(Action.BROADCAST,
                                        "SPEAK", session.getUser().getFirstName(), session.getUser().getLastName(), message));
                            } catch (IllegalStateException e) {
                                session.send(new Message(Action.ERROR, "SPEAK", e.getMessage()));
                            }
                        },
                        () -> session.send(new Message(Action.ERROR, "SPEAK", "Réunion introuvable"))
                );
    }



    // GET_HISTORY|meetingId
    private void handleGetHistory(ClientSession session, Message msg) {
        if (!checkLogin(session)) return;
        reunionService.getMeetingById(msg.getParam(0))
                .ifPresentOrElse(
                        reunion -> {
                            List<ChatMessage> history = chatMessageService.getHistory(reunion);
                            if (history.isEmpty()) {
                                session.send(new Message(Action.OK, "GET_HISTORY", "AUCUN"));
                                return;
                            }
                            // Format : firstName,lastName,content,sentAt séparés par ";"
                            String result = history.stream()
                                    .map(m -> m.author().getFirstName() + "," +
                                              m.author().getLastName() + "," +
                                              m.content() + "," +
                                              m.sentAt().toString())
                                    .collect(Collectors.joining(";"));
                            session.send(new Message(Action.OK, "GET_HISTORY", result));
                        },
                        () -> session.send(new Message(Action.ERROR, "GET_HISTORY", "Réunion introuvable"))
                );
    }

    private boolean checkLogin(ClientSession session) {
        if (!session.isLoggedIn()) {
            session.send(new Message(Action.ERROR, "AUTH", "Vous devez être connecté"));
            return false;
        }
        return true;
    }
}
