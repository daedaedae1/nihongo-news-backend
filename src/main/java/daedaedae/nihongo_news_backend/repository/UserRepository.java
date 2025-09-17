package daedaedae.nihongo_news_backend.repository;

import daedaedae.nihongo_news_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserid(String userid);
    boolean existsByNickname(String nickname);
    User findByNameAndNickname(String name, String nickname);

}
