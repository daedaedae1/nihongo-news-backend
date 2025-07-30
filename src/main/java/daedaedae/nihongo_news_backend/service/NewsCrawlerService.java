package daedaedae.nihongo_news_backend.service;

import daedaedae.nihongo_news_backend.domain.News;
import daedaedae.nihongo_news_backend.dto.NewsDto;
import daedaedae.nihongo_news_backend.repository.NewsRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsCrawlerService {
    @Autowired
    private NewsRepository newsRepository;

    public List<NewsDto> fetchNewsList(int limit) throws Exception {
        String url = "https://www3.nhk.or.jp/news/";
        Connection connection = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
                .timeout(10000);
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

//            // 저장 & Dto
//            if (!newsRepository.existsByUrl(articleUrl)) {
//                News news = new News(title, articleUrl, date, imgUrl);
//                newsRepository.save(news);
//            }
            articles.add(new NewsDto(title, articleUrl, imgUrl, date));
        }
        return articles;
    }

    @Transactional
    public News saveNews(NewsDto newsDto) {
        News news = new News(
                newsDto.getTitle(),
                newsDto.getUrl(),
                newsDto.getDate(),
                newsDto.getImage()
        );
        return newsRepository.save(news);
    }

    /*
    public NewsDto fetchArticleDetail(String url) throws Exception {
        Document doc = Jsoup.connect(url)
                .userAgent("MyProjectBot/1.0")
                .get();
        Element title = doc.selectFirst(".content--title span");
        Element date = doc.selectFirst(".content--date time");
        Element summary = doc.selectFirst(".content--summary");
        Elements bodyElems = doc.select(".content--body .body-text p");
        StringBuilder body = new StringBuilder();
        for (Element p : bodyElems) {
            body.append(p.text().trim()).append("\n");
        }
        Element img = doc.selectFirst(".content--thumb img");
        String imgUrl = (img != null && img.hasAttr("data-src")) ? img.attr("data-src") : "";

        return new NewsDto(
                title != null ? title.text().trim() : "",
                url,
                imgUrl,
                date != null ? date.text().trim() : "",
                summary != null ? summary.text().trim() : "",
                body.toString().trim()
        );
    }
     */

}
