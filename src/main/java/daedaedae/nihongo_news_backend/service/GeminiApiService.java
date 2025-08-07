package daedaedae.nihongo_news_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiApiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String translateJapaneseToKorean(String japaneseText) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = "아래 일본어 문장을 한국어로 번역해줘. 오직 번역 결과만 출력해줘. 설명이나 해설, 예시, 대체 문장은 쓰지 마.\n" + japaneseText;

        // Gemini API body 포맷
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> contentsItem = new HashMap<>();
        contentsItem.put("parts", List.of(part));
        // role: "user"는 넣지 않아도 됨

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(contentsItem));

        // 헤더 세팅 (api-key를 헤더로 전달)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", geminiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    GEMINI_API_URL, HttpMethod.POST, entity, Map.class
            );
            Map result = response.getBody();
            if (result != null && result.containsKey("candidates")) {
                List candidates = (List) result.get("candidates");
                if (!candidates.isEmpty()) {
                    Map candidate = (Map) candidates.get(0);
                    Map contentMap = (Map) candidate.get("content");
                    List parts = (List) contentMap.get("parts");
                    Map partMap = (Map) parts.get(0);
                    return (String) partMap.get("text");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "번역 오류: " + e.getMessage();
        }
        return "번역 결과 없음";
    }
}
