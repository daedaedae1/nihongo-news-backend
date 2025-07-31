package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.domain.Bookmark;
import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.dto.NewsDto;
import daedaedae.nihongo_news_backend.service.BookmarkService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmark")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping("/list")
    public List<NewsDto> getBookmarkList(HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        return bookmarkService.fetchBookmarkList(user);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveNews(@RequestBody NewsDto newsDto, HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        Bookmark saveNews = bookmarkService.saveNews(user, newsDto);

        if (saveNews != null){
            return ResponseEntity.ok(Map.of("success", "저장 성공"));
        }
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "이미 존재하는 기사입니다."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBookmark(@RequestBody NewsDto newsDto, HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        boolean deleted = bookmarkService.deleteBookmark(user.getId(), newsDto.getUrl());

        if (deleted) {
            return ResponseEntity.ok(Map.of("success", "북마크 삭제 성공"));
        }
        else
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "북마크 삭제 실패"));
    }

}
