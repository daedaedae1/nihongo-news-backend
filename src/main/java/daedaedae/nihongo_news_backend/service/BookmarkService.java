package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.domain.Bookmark;
import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.dto.NewsDto;
import daedaedae.nihongo_news_backend.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Transactional
    public Bookmark saveNews(NewsDto newsDto, User user) {
        Bookmark bookmark = new Bookmark(
                user.getId(),
                newsDto.getTitle(),
                newsDto.getUrl(),
                newsDto.getDate(),
                newsDto.getImage()
        );
        return bookmarkRepository.save(bookmark);
    }

}
