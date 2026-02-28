package core.interfaces;
import core.models.Reunion;
import core.models.User;

import java.util.List;
import java.util.Optional;


public interface IUserService {


        User register(String firstName, String lastName, String email, String phone, String password);
        Optional<User> login(String email, String password);
        User updateProfile(User user, String newFirstName, String newLastName, String newPhone, String newPassword);
        void addMeetingToHistory(User user, Reunion reunion);
        List<Reunion> getUserMeetingsHistory(User user);
        Optional<User> getUserByEmail(String email);

}
