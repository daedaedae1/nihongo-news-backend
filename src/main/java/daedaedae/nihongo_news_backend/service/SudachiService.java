package daedaedae.nihongo_news_backend.service;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;
import daedaedae.nihongo_news_backend.dto.JpToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class SudachiService {

    private final Dictionary dictionary;

    public List<JpToken> tokenize(String text) {
        List<JpToken> out = new ArrayList<>();
        if (text == null || text.isBlank()) return out;

        Tokenizer t = dictionary.create();
        List<Morpheme> ms = t.tokenize(Tokenizer.SplitMode.B, text); // C = 긴 단위

        for (Morpheme m : ms) {
            out.add(new JpToken(
                    m.surface(),
                    nz(m.dictionaryForm(), m.surface()),   // 기본형 없으면 표면형
                    nz(m.readingForm(), ""),            // 읽는법
                    joinPos(m)                             // 품사
            ));
        }
        return out;
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
