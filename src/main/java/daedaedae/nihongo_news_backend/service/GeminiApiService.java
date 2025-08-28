package daedaedae.nihongo_news_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;

@Service
public class GeminiApiService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String translateJp2Kr(String japaneseText) {
        // Spring에서 HTTP 요청을 보낼 때 쓰는 클래스
        RestTemplate restTemplate = new RestTemplate();

        String prompt = "아래 일본어 문장을 자연스럽고 확실한 한국어로 번역해줘. " +
                "오로지 내가 준 일본어 문장에 대한 한국어 번역만 답변으로 내줘." +
                "이외의 다른 말들은 절대 하지 마." +
                "원문 줄바꿈은 그대로 유지.\n\n" + japaneseText;

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
            // HTTP 요청 실행
            // exchange로 URL, 메서드, 바디, 응답 타입을 한 번에 지정 가능.
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

    // 레마(원형) 배치 번역: { baseJA: posHead } → { baseJA: "한국어" }
    public Map<String, String> translateLemmas(Map<String, String> lemmaPos) {
        if (lemmaPos == null || lemmaPos.isEmpty()) return Map.of();

        // 프롬프트: JSON만 내도록 강하게 규정
        StringBuilder sb = new StringBuilder();
        sb.append("다음 일본어 표제어(원형)를 한국어 사전식 표제어로 번역해줘.\n");
        sb.append("- 출력은 반드시 하나의 JSON 객체만. 설명/코드블럭/문장 금지.\n");
        sb.append("- 키: 입력 표제어 그대로.\n");
        sb.append("- 값: 한국어 번역 한 단어(또는 최대 2단어)만.\n");
        sb.append("- 품사 규칙:\n");
        sb.append("  * 名詞: 일반 명사로.\n");
        sb.append("  * 動詞: 한국어 사전형 ‘…다’로.\n");
        sb.append("  * 形容詞/形状詞: 자연스러운 한국어 형용사(…하다/…스럽다 등)로.\n");
        sb.append("  * 副詞: 한국어 부사로.\n");
        sb.append("- 예: {\"進む\":\"나아가다\",\"余り\":\"나머지\"}\n\n");
        sb.append("입력:\n");

        int i = 1;
        for (Map.Entry<String,String> e : lemmaPos.entrySet()) {
            String base = e.getKey();
            String pos = e.getValue() == null ? "" : e.getValue();
            sb.append(i++).append(". ").append(base).append(" (").append(pos).append(")\n");
        }

        RestTemplate restTemplate = new RestTemplate();
        String prompt = sb.toString();

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> contentsItem = new HashMap<>();
        contentsItem.put("parts", List.of(part));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(contentsItem));

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
                    String text = (String) partMap.get("text"); // JSON 문자열 기대

                    if (text == null) return Map.of();
                    String json = sanitizeJsonOnly(text);

                    ObjectMapper om = new ObjectMapper();
                    Map<String,String> parsed = om.readValue(json, new TypeReference<Map<String,String>>(){});
                    return (parsed == null) ? Map.of() : parsed;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of();
    }

    private String sanitizeJsonOnly(String raw) {
        String s = raw.trim();
        // ```json ... ``` 혹은 ``` ... ``` 제거
        if (s.startsWith("```")) {
            int start = s.indexOf('{');
            int end = s.lastIndexOf('}');
            if (start >= 0 && end > start) {
                s = s.substring(start, end + 1);
            }
        }
        // 앞/뒤에 잡말이 섞였을 때도 중괄호만 추출
        if (!s.startsWith("{")) {
            int start = s.indexOf('{');
            int end = s.lastIndexOf('}');
            if (start >= 0 && end > start) {
                s = s.substring(start, end + 1);
            }
        }
        return s;
    }
}
