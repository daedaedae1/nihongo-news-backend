package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.service.SudachiService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import daedaedae.nihongo_news_backend.dto.JpToken;

import java.util.List;

@RestController
@RequestMapping("/api/sudachi")
public class SudachiController {

    @Autowired
    private SudachiService sudachiService;

    @PostMapping(value = "/tokens", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<JpToken> tokens(@RequestBody TextReq body) {
        return sudachiService.tokenize(body.getText());
    }

    @Data
    public static class TextReq { private String text; }
}
