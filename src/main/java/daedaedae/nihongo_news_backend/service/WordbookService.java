package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.domain.User;
import daedaedae.nihongo_news_backend.domain.Wordbook;
import daedaedae.nihongo_news_backend.dto.WordbookDto;
import daedaedae.nihongo_news_backend.repository.WordbookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WordbookService {

    @Autowired
    private WordbookRepository wordbookRepository;

    @Transactional
    public Wordbook save(Long userId, String jpWord, String jpReading, String krWord) {
        String jpW = normAndTrim(jpWord);
        String jpR = normAndTrim(jpReading);
        String krW = normAndTrim(krWord);

        if (jpW.isEmpty() || krW.isEmpty()) {
            throw new IllegalArgumentException("단어가 존재하지 않습니다.");
        }

        Wordbook data = new Wordbook();
        data.setUserId(userId);
        data.setJpWord(jpW);
        data.setJpReading(jpR);
        data.setKrWord(krW);

        return wordbookRepository.save(data); // 중복이면 컨트롤러에서 catch
    }

    @Transactional
    public boolean delete(Long userId, Long wordId) {
        long result = wordbookRepository.deleteByUserIdAndId(userId, wordId);
        return result > 0;
    }

    private static String normAndTrim(String s) {
        if (s == null) return "";
        String t = s.trim();
        // 일본어/기호 중복 방지에 도움
        return java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFKC);
    }

    public List<Wordbook> fetchWordList(User user) {
        List<Wordbook> words = wordbookRepository.findByUserId(user.getId());
        return words;
    }

}
