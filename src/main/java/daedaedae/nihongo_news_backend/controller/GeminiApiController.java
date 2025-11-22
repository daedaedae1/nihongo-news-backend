package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.dto.NewsDetailDto;
import daedaedae.nihongo_news_backend.dto.JpToken;
import daedaedae.nihongo_news_backend.service.GeminiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
public class GeminiApiController {

    @Autowired
    private GeminiApiService geminiApiService;

    @PostMapping("/translate/detail")
    public NewsDetailDto translateFullNews(@RequestBody NewsDetailDto original) {
        NewsDetailDto translated = new NewsDetailDto();

        // 본문 번역
        List<NewsDetailDto.Section> translatedSections = new ArrayList<>();
        for (NewsDetailDto.Section section : original.getSections()) {
            NewsDetailDto.Section tSection = new NewsDetailDto.Section();
            // null/빈/공백이면 번역하지 않음
            tSection.setTitle(
                    (section.getTitle() == null || section.getTitle().trim().isEmpty())
                            ? ""
                            : geminiApiService.translateJp2Kr(section.getTitle())
            );
            tSection.setBody(
                    (section.getBody() == null || section.getBody().trim().isEmpty())
                            ? ""
                            : geminiApiService.translateJp2Kr(section.getBody())
            );
            translatedSections.add(tSection);
        }
        translated.setSections(translatedSections);

        return translated;
    }

    // 제목(또는 임의 문자열) 번역
    @PostMapping("/translate/text")
    public String translateText(@RequestBody Map<String, String> req) {
        String text = req.get("text");
        if (text == null || text.isBlank()) return "";
        return geminiApiService.translateJp2Kr(text);
    }

    // 레마(원형) 배치 번역: JpToken 배열을 받아 { baseJA: "한국어" } 맵 반환
    @PostMapping("/translate/lemmas")
    public Map<String, String> translateLemmas(@RequestBody List<JpToken> tokens) {
        if (tokens == null || tokens.isEmpty()) return Map.of();
        Map<String, String> lemmaPos = new LinkedHashMap<>();
        for (JpToken t : tokens) {
            String base = (t.base() == null || t.base().isBlank()) ? t.surface() : t.base();
            if (base == null || base.isBlank()) continue;
            String posHead = headOfPos(t.pos());
            // 조사/기호 등은 제외
            if ("助詞".equals(posHead) || "記号".equals(posHead)) continue;
            lemmaPos.putIfAbsent(base, posHead);
        }
        return geminiApiService.translateLemmas(lemmaPos);
    }

    private String headOfPos(String pos) {
        if (pos == null || pos.isBlank()) return "";
        int i = pos.indexOf('-');
        return (i > 0) ? pos.substring(0, i) : pos;
    }
}
