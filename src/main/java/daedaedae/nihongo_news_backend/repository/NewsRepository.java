package daedaedae.nihongo_news_backend.repository;

import daedaedae.nihongo_news_backend.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    // 중복 저장 방지용
    boolean existsByUrl(String url);
}
