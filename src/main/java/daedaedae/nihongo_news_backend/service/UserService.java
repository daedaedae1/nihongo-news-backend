package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    public User addUser(User user) {
        return userRepository.save(user);
    }

}
