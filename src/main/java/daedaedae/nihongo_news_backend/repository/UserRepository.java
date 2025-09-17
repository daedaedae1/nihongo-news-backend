package daedaedae.nihongo_news_backend.repository;

import daedaedae.nihongo_news_backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserid(String userid);
    boolean existsByNicknameIgnoreCase(String nickname);    // 대소문자 무시
    User findByUseridAndNickname(String userid, String nickname);

}
