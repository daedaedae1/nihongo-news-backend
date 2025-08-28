package daedaedae.nihongo_news_backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;  // HttpHeaders

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeepLService {

    @Value("${deepl.api.key}")
    private String apiKey;

    private String baseUrl = "https://api-free.deepl.com";

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String ja2ko(String text) {
        RestTemplate restTemplate = new RestTemplate();

        if (text == null) return "";
        String word = text.trim();
        if (word.isEmpty()) return "";

        String cached = cache.get(word);
        if (cached != null) return cached;

        String url = baseUrl + "/v2/translate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "DeepL-Auth-Key " + apiKey);   // key 뒤 공백 중요

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("text", List.of(word));     // DeepL은 JSON에서 배열 형태 권장
        body.put("source_lang", "JA");
        body.put("target_lang", "KO");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<DeepLResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, DeepLResponse.class);
        String ko = "";

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                && response.getBody().translations != null && !response.getBody().translations.isEmpty()) {
            DeepLResponse.Translation t0 = response.getBody().translations.get(0);
            if (t0 != null && t0.text != null) ko = t0.text;
        }
        cache.put(word, ko == null ? "" : ko);
        return ko == null ? "" : ko;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeepLResponse {
        public List<Translation> translations;
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Translation {
            public String detected_source_language;
            public String text;
        }
    }

}