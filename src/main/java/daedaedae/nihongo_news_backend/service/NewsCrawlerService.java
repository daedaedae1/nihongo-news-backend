package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.dto.NewsDetailDto;
import daedaedae.nihongo_news_backend.dto.NewsDto;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsCrawlerService {
    String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    // 뉴스 리스트 불러오기
    public List<NewsDto> fetchNewsList(int limit) throws Exception {
        String url = "https://www3.nhk.or.jp/news/";
        Connection connection = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(5000);
        Document doc = connection.get();

        List<NewsDto> articles = new ArrayList<>();

        Elements items = doc.select(".content--list.grid--col li");
        for (Element item : items.subList(0, Math.min(limit, items.size()))) {
            // URL
            Element aTag = item.selectFirst("dl dt a");
            String articleUrl = (aTag != null && aTag.hasAttr("href"))
                    ? (aTag.attr("href").startsWith("http") ? aTag.attr("href") : "https://www3.nhk.or.jp" + aTag.attr("href"))
                    : "";

            // 이미지
            Element imgTag = item.selectFirst("dl dt a img");
            String imgUrl = "";
            if (imgTag != null && imgTag.hasAttr("data-src")) {
                String src = imgTag.attr("data-src");
                // src가 http로 시작하지 않으면, 도메인을 붙여줌
                if (src.startsWith("http")) {
                    imgUrl = src;
                } else {
                    imgUrl = "https://www3.nhk.or.jp" + src;
                }
            }

            // 제목
            Element titleTag = item.selectFirst("em.title");
            String title = (titleTag != null) ? titleTag.text().trim() : "";

            // 날짜
            Element timeTag = item.selectFirst("time");
            String date = (timeTag != null) ? timeTag.text().trim() : "";

            articles.add(new NewsDto(title, articleUrl, imgUrl, date));
        }
        return articles;
    }

    // 뉴스의 세부 내용 불러오기
    public NewsDetailDto fetchNewsDetail(String url) throws Exception {
        Document doc = Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(5000)
                .get();

        NewsDetailDto news = new NewsDetailDto();

        // 요약 가져오기
        Element summary = doc.selectFirst("p.content--summary");
        if (summary != null) {
            news.setSummary(summary.text());
        }

        // 각 단락별로 처리
        Elements sections = doc.select(".content--body");
        StringBuilder fullContent = new StringBuilder();

        for (Element section : sections) {
            Element title = section.selectFirst(".body-title");
            Element body = section.selectFirst(".body-text");

            if (title != null) {
                fullContent.append("[제목] ").append(title.text()).append("\n");
            }

            if (body != null) {
                Elements paragraphs = body.select("p");
                for (Element p : paragraphs) {
                    fullContent.append("[본문] ").append(p.text()).append("\n\n");
                }
            }

        }

        news.setContent(fullContent.toString());

        return news;
    }


}
