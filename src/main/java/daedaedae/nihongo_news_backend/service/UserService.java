package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public boolean existsByUserid(String userid) {
        User user = userRepository.findByUserid(userid);
        return (user != null)? true : false;
    }

    // 문제가 생기면 롤백하여 데이터 보존?
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }


    public User updateUser(User user) {
        User loginUser = userRepository.findByUserid(user.getUserid());
        if (loginUser != null) {
            loginUser.setNickname(user.getNickname());
            loginUser.setName((user.getName()));
            return userRepository.save(loginUser);
        } else return null;
    }

}
