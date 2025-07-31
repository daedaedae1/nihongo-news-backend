package daedaedae.nihongo_news_backend.repository;

import daedaedae.nihongo_news_backend.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 중복 저장 방지용
    boolean existsByUserIdAndUrl(Long userId, String url);

    List<Bookmark> findByUserId(Long userId);

    Optional<Bookmark> findByUserIdAndUrl(Long userId, String url);

}
