package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.domain.Wordbook;
import daedaedae.nihongo_news_backend.dto.WordbookDto;
import daedaedae.nihongo_news_backend.service.GeminiApiService;
import daedaedae.nihongo_news_backend.service.WordbookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wordbook")
public class WordbookController {

    @Autowired
    private WordbookService wordbookService;

    @Autowired
    private GeminiApiService geminiApiService;

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody WordbookDto req, HttpSession session) {
        User user = (User) session.getAttribute("loginMember");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 유저 정보 없음"));
        }

        try {
            Wordbook saved = wordbookService.save(user.getId(), req.jpWord(), req.jpReading(), req.krWord());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", "단어 저장에 성공했습니다."));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 유니크 제약(중복) 레이스까지 커버
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "이미 존재하는 단어입니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "단어 저장에 실패했습니다."));
        }
    }

    @GetMapping("/list")
    public List<Wordbook> getWordList(HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        return wordbookService.fetchWordList(user);
    }

    @GetMapping("/ex")
    public List<ExampleItem> makeEx(@RequestParam("jp") String word) {

        String jpEx = geminiApiService.makeExSent(word);
        String jpReading = geminiApiService.translateJp2Reading(jpEx);
        String jp2kr = geminiApiService.translateJp2Kr(jpEx);
        List<ExampleItem> list = new ArrayList<>();
        ExampleItem item = new ExampleItem(jpEx, jpReading, jp2kr);
        list.add(item);
        return list;
    }

    public record ExampleItem(String ja, String jaRd, String ko) {}
}
