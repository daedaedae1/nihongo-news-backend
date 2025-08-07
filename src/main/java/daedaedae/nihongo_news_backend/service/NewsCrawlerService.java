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
        List<NewsDetailDto.Section> sections = new ArrayList<>();

        // 요약
        Element summary = doc.selectFirst("p.content--summary");
        if (summary != null) {
            news.setSummary(summary.text());
        }

        Elements sectionElems = doc.select(".content--body");
        for (Element sectionElem : sectionElems) {
            NewsDetailDto.Section section = new NewsDetailDto.Section();
            Element titleElem = sectionElem.selectFirst(".body-title");
            section.setTitle(titleElem != null ? titleElem.text() : "");

            StringBuilder bodyBuilder = new StringBuilder();
            Elements paragraphs = sectionElem.select(".body-text p");

            for (Element p : paragraphs) {
                // <br>과 <br /> 모두 \n으로 변경
                String html = p.html().replaceAll("(?i)<br\\s*/?>", "\n");
                // 모든 HTML 태그 제거 - 줄 바꿈 제외
                String cleanText = html.replaceAll("<[^>]+>", "");
                bodyBuilder.append(cleanText.trim()).append("\n\n");
            }
            section.setBody(bodyBuilder.toString().trim());
            sections.add(section);
        }

        news.setSections(sections);
        return news;
    }

}
