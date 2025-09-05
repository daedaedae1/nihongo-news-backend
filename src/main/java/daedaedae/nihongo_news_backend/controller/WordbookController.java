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
    public List<ExampleItem> makeEx(@RequestParam("jp") String jpWord) {
        List<Map<String, String>> raw = geminiApiService.makeExamplesJaRdKo(jpWord, 3);

        List<ExampleItem> out = new ArrayList<>();
        if (raw != null) {
           for (Map<String, String> m : raw) {
               String ja = m.getOrDefault("ja", "");
               String jaRd = m.getOrDefault("jaRd", "");
               String ko = m.getOrDefault("ko", "");
               out.add(new ExampleItem(ja, jaRd, ko));
           }
        }
        return out;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id, HttpSession session) {
        User user = (User) session.getAttribute("loginMember");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인 유저 정보 없음"));
        }

        boolean result = wordbookService.delete(user.getId(), id);
        if (result) {
            return ResponseEntity.ok(Map.of("success", "단어가 삭제되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "단어를 찾을 수 없습니다."));
        }
    }

    public record ExampleItem(String ja, String jaRd, String ko) {}
}
