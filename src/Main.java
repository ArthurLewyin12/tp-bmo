import core.enums.ReunionType;
import core.generics.DBRepository;
import core.models.Reunion;
import core.models.User;
import core.services.ReunionService;
import core.services.UserService;

import java.time.LocalDateTime;

void main() {

    // --- Initialisation ---
    DBRepository<User> userRepo = new DBRepository<>();
    DBRepository<Reunion> reunionRepo = new DBRepository<>();

    UserService userService = new UserService(userRepo);
    ReunionService reunionService = new ReunionService(reunionRepo);

    // --- Inscription ---
    User alice = userService.register("Alice", "Martin", "alice@mail.com", "0600000001", "pass123");
    User bob   = userService.register("Bob",   "Dupont", "bob@mail.com",   "0600000002", "pass456");
    User carol = userService.register("Carol", "Blanc",  "carol@mail.com", "0600000003", "pass789");

    // --- Connexion ---
    userService.login("alice@mail.com", "pass123")
            .ifPresent(u -> System.out.println("Connecté : " + u.getFirstName() + " " + u.getLastName()));

    // --- Créer une réunion démocratique ---
    Reunion reunion = reunionService.createMeeting(
            "Réunion projet",
            "Avancement du TP Java",
            LocalDateTime.now().plusHours(1),
            60,
            alice,
            ReunionType.DEMOCRATIC,
            "1. Présentation\n2. Revue de code\n3. Questions"
    );
    System.out.println("Réunion créée : " + reunion.getName());

    // --- Ouvrir la réunion et ajouter des participants ---
    reunionService.startMeeting(reunion);
    reunionService.addParticipant(reunion, bob);
    reunionService.addParticipant(reunion, carol);

    // --- Demandes de parole (DEMOCRATIC → accordée dans l'ordre) ---
    reunionService.requestToSpeak(reunion, bob);   // bob parle en premier
    reunionService.requestToSpeak(reunion, carol);

    reunionService.speak(reunion, bob,   "Bonjour à tous, j'ai terminé ma partie !");
    reunionService.speak(reunion, carol, "Super, moi aussi. On peut merger ?");

    // --- Alice (organisatrice) prend la parole directement ---
    reunionService.speak(reunion, alice, "Parfait, je clôture la réunion.");

    // --- Historique ---
    userService.addMeetingToHistory(alice, reunion);
    userService.addMeetingToHistory(bob, reunion);

    // --- Clôturer ---
    reunionService.endMeeting(reunion);
    System.out.println("Réunion terminée. Participants : " + reunion.getParticipants().size());
}
