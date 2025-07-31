package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.domain.Bookmark;
import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.dto.NewsDto;
import daedaedae.nihongo_news_backend.repository.BookmarkRepository;
import daedaedae.nihongo_news_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Bookmark saveNews(User user, NewsDto newsDto) {
        if (!bookmarkRepository.existsByUserIdAndUrl(user.getId(), newsDto.getUrl())) {
            Bookmark bookmark = new Bookmark(
                    user.getId(),
                    newsDto.getTitle(),
                    newsDto.getUrl(),
                    newsDto.getDate(),
                    newsDto.getImage()
            );
            return bookmarkRepository.save(bookmark);
        }
        else return null;
    }

    public List<NewsDto> fetchBookmarkList(User user) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(user.getId());
        List<NewsDto> articles = new ArrayList<>();

        for (Bookmark b : bookmarks) {
            articles.add(new NewsDto(
                    b.getTitle(),
                    b.getUrl(),
                    b.getImage(),
                    b.getDate()
            ));
        }
        return articles;
    }

    @Transactional
    public boolean deleteBookmark(Long userId, String url) {
        Optional<Bookmark> bookmarkOpt = bookmarkRepository.findByUserIdAndUrl(userId, url);
        if (bookmarkOpt.isPresent()) {
            bookmarkRepository.delete(bookmarkOpt.get());
            return true;
        }
        else {
            return false;
        }
    }

}
