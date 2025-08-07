package daedaedae.nihongo_news_backend.service;

import org.springframework.stereotype.Service;

@Service
public class GeminiApiService {
    private static final String API_KEY = "";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

}
