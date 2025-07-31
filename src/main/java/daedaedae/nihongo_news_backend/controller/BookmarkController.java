package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.domain.Bookmark;
import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.dto.NewsDto;
import daedaedae.nihongo_news_backend.service.BookmarkService;
import daedaedae.nihongo_news_backend.service.NewsCrawlerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmark")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @PostMapping("/save")
    public Bookmark saveNews(@RequestBody NewsDto newsDto, HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        return bookmarkService.saveNews(newsDto, user);
    }
}
