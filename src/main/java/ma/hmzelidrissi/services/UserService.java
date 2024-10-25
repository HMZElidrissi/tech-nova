package ma.hmzelidrissi.services;

import ma.hmzelidrissi.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User saveUser(User user);
    void deleteUser(Long id);
    List<User> searchUsers(String query);
}
