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
@RequestMapping("/api")
public class GeminiApiController {

    @Autowired
    private GeminiApiService geminiApiService;

    @PostMapping("/translate")
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
            tSection.setTitle(
                    section.getTitle() == null ? "" : geminiApiService.translateJapaneseToKorean(section.getTitle())
            );
            tSection.setBody(
                    section.getBody() == null ? "" : geminiApiService.translateJapaneseToKorean(section.getBody())
            );
            translatedSections.add(tSection);
        }
        translated.setSections(translatedSections);

        return translated;
    }


}
