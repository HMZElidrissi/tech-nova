package ma.hmzelidrissi.services;

import lombok.Setter;
import ma.hmzelidrissi.models.User;
import ma.hmzelidrissi.repositories.UserRepository;

import java.util.List;

@Setter
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> searchUsers(String query) {
        return userRepository.findByFirstNameContainingOrLastNameContaining(query, query);
    }
}
