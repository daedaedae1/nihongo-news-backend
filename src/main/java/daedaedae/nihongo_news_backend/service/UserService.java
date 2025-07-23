package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User isUserExists(User user) {
        return userRepository.findByUserid(user.getUserid());
    }

}
