package daedaedae.nihongo_news_backend.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;  // HttpHeaders

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
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "DeepL-Auth-Key " + apiKey);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("text", word);
        form.add("source_lang", "JA");
        form.add("target_lang", "KO");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

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
