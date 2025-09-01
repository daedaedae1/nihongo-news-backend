package daedaedae.nihongo_news_backend.service;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import daedaedae.nihongo_news_backend.dto.JpToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SudachiService {

    private final Dictionary dictionary;

    // 원형→읽기 캐시
    private final Map<String, String> lemmaReadingCache = new ConcurrentHashMap<>();

    public List<JpToken> tokenize(String text) {
        List<JpToken> out = new ArrayList<>();
        if (text == null || text.isBlank()) return out;

        Tokenizer t = dictionary.create();
        List<Morpheme> ms = t.tokenize(Tokenizer.SplitMode.B, text); // C = 긴 단위

        for (Morpheme m : ms) {
            String surface = m.surface();
            String base = nz(m.dictionaryForm(), surface);   // 기본형 없으면 표면형
            String reading = nz(m.readingForm(), "");        // 현재 형태의 읽기

            // 원형 읽기 구하기
            String baseReading;
            if (base.equals(surface)) {
                baseReading = reading; // 이미 원형이면 그대로
            } else {
                baseReading = readingOfBase(t, base);
            }

            out.add(new JpToken(
                    surface,
                    base,
                    reading,
                    joinPos(m),
                    baseReading
            ));
        }
        return out;
    }

    // 원형 문자열을 다시 토크나이즈해서 읽기 구함 (캐시 포함)
    private String readingOfBase(Tokenizer t, String base) {
        if (base == null || base.isEmpty()) return "";
        String cached = lemmaReadingCache.get(base);
        if (cached != null) return cached;

        List<Morpheme> list = t.tokenize(Tokenizer.SplitMode.C, base);
        if (list == null || list.isEmpty()) {
            lemmaReadingCache.put(base, "");
            return "";
        }
        // 원형이 한 토큰일 가능성이 높지만, 안전하게 모두 이어붙임
        StringBuilder sb = new StringBuilder();
        for (Morpheme m : list) {
            String r = nz(m.readingForm(), "");
            sb.append(r);
        }
        String val = sb.toString();
        lemmaReadingCache.put(base, val);
        return val;
    }

    private String joinPos(Morpheme m) {
        StringJoiner j = new StringJoiner("-");
        if (m.partOfSpeech() != null) m.partOfSpeech().forEach(s -> { if (s != null && !s.isEmpty()) j.add(s); });
        return j.toString();
    }
    private String nz(String v, String fb) { return (v == null || v.isEmpty()) ? fb : v; }
    private String escapeHtml(String s) { return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }
    private String attr(String s) { return escapeHtml(s).replace("\"","&quot;"); }

}
