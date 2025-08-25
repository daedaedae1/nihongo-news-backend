package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.service.DeepLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/deepl")
public class DeepLController {

    @Autowired
    private DeepLService deepLService;

    @PostMapping(value = "/ja2ko", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ja2ko(@RequestBody Map<String, String> req) {
        String text = req.get("text");
        String lemma = text == null ? "" : text.trim();
        String ko = deepLService.ja2ko(lemma);

        return Map.of("translated", ko);
    }

}
