package core.services;

import core.generics.DBRepository;
import core.interfaces.IUserService;
import core.models.Reunion;
import core.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService implements IUserService {

    private final DBRepository<User> repository;

    public UserService(DBRepository<User> repository) {
        this.repository = repository;
    }

    @Override
    public User register(String firstName, String lastName, String email, String phone, String password) {
        if (getUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà : " + email);
        }
        String id = UUID.randomUUID().toString();
        User user = new User(id, firstName, lastName, email, phone, password, LocalDateTime.now());
        repository.save(id, user);
        return user;
    }

    @Override
    public Optional<User> login(String email, String password) {
        return getUserByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    @Override
    public User updateProfile(User user, String newFirstName, String newLastName, String newPhone, String newPassword) {
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setPhone(newPhone);
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    @Override
    public void addMeetingToHistory(User user, Reunion reunion) {
        user.getReunionsHistory().add(reunion);
    }

    @Override
    public List<Reunion> getUserMeetingsHistory(User user) {
        return user.getReunionsHistory();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return repository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
