package daedaedae.nihongo_news_backend.controller;

import daedaedae.nihongo_news_backend.domain.News;
import daedaedae.nihongo_news_backend.dto.NewsDto;
import daedaedae.nihongo_news_backend.service.NewsCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    private NewsCrawlerService newsCrawlerService;

    // 뉴스 리스트
    @GetMapping("/list")
    public List<NewsDto> getNewsList(@RequestParam(name = "limit", defaultValue = "6") int limit) throws Exception {
        return newsCrawlerService.fetchNewsList(limit);
    }

    @PostMapping("/save")
    public News saveNews(@RequestBody NewsDto newsDto) {
        return newsCrawlerService.saveNews(newsDto);
    }
}
