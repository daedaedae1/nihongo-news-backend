package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.dto.NewsDetailDto;
import daedaedae.nihongo_news_backend.service.GeminiApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

        // 요약 번역
        translated.setSummary(
                original.getSummary() == null ? "" : geminiApiService.translateJapaneseToKorean(original.getSummary())
        );

        // 본문 번역
        List<NewsDetailDto.Section> translatedSections = new ArrayList<>();
        for (NewsDetailDto.Section section : original.getSections()) {
            NewsDetailDto.Section tSection = new NewsDetailDto.Section();
            // null/빈/공백이면 번역하지 않음
            tSection.setTitle(
                    (section.getTitle() == null || section.getTitle().trim().isEmpty())
                            ? ""
                            : geminiApiService.translateJapaneseToKorean(section.getTitle())
            );
            tSection.setBody(
                    (section.getBody() == null || section.getBody().trim().isEmpty())
                            ? ""
                            : geminiApiService.translateJapaneseToKorean(section.getBody())
            );
            translatedSections.add(tSection);
        }
        translated.setSections(translatedSections);

        return translated;
    }

    // 제목(또는 임의 문자열) 단건 번역
    @PostMapping("/translate/text")
    public String translateText(@RequestBody Map<String, String> req) {
        String text = req.get("text");
        if (text == null || text.isBlank()) return "";
        return geminiApiService.translateJapaneseToKorean(text);
    }

}
