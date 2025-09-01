package daedaedae.nihongo_news_backend.repository;

import daedaedae.nihongo_news_backend.domain.Wordbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordbookRepository extends JpaRepository<Wordbook, Long> {

    boolean existsByUserIdAndJpWord(Long id, String jpWord);
    Optional<Wordbook> findByUserIdAndJpWord(Long id, String jpWord);
    List<Wordbook> findByUserId(Long id);
}
