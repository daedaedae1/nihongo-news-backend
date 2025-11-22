//package daedaedae.nihongo_news_backend.service;
//
//import daedaedae.nihongo_news_backend.dto.NewsDetailDto;
//import daedaedae.nihongo_news_backend.dto.NewsDto;
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class NewsCrawlerService {
//    String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
//
//    // 뉴스 리스트 불러오기
//    public List<NewsDto> fetchNewsList(int limit) throws Exception {
//        String url = "https://www3.nhk.or.jp/news/";
//        Connection connection = Jsoup.connect(url)
//                .userAgent(userAgent)
//                .timeout(5000);
//        Document doc = connection.get();
//
//        List<NewsDto> articles = new ArrayList<>();
//
//        Elements items = doc.select(".content--list.grid--col li");
//        for (Element item : items.subList(0, Math.min(limit, items.size()))) {
//            // URL
//            Element aTag = item.selectFirst("dl dt a");
//            String articleUrl = (aTag != null && aTag.hasAttr("href"))
//                    ? (aTag.attr("href").startsWith("http") ? aTag.attr("href") : "https://www3.nhk.or.jp" + aTag.attr("href"))
//                    : "";
//
//            // 이미지
//            Element imgTag = item.selectFirst("dl dt a img");
//            String imgUrl = "";
//            if (imgTag != null && imgTag.hasAttr("data-src")) {
//                String src = imgTag.attr("data-src");
//                // src가 http로 시작하지 않으면, 도메인을 붙여줌
//                if (src.startsWith("http")) {
//                    imgUrl = src;
//                } else {
//                    imgUrl = "https://www3.nhk.or.jp" + src;
//                }
//            }
//
//            // 제목
//            Element titleTag = item.selectFirst("em.title");
//            String title = (titleTag != null) ? titleTag.text().trim() : "";
//
//            // 날짜
//            Element timeTag = item.selectFirst("time");
//            String date = (timeTag != null) ? timeTag.text().trim() : "";
//
//            articles.add(new NewsDto(title, articleUrl, imgUrl, date));
//        }
//        return articles;
//    }
//
//    // 뉴스의 세부 내용 불러오기
//    public NewsDetailDto fetchNewsDetail(String url) throws Exception {
//        Document doc = Jsoup.connect(url)
//                .userAgent(userAgent)
//                .timeout(5000)
//                .get();
//
//        NewsDetailDto news = new NewsDetailDto();
//        List<NewsDetailDto.Section> sections = new ArrayList<>();
//
//        /* 요약 */
//        // p.content--summary 한 개를 찾아, set - 태그까지 포함
//        Element summary = doc.selectFirst("p.content--summary");
//
//        if (summary != null) {
//            // 텍스트만 추출
//            news.setSummary(summary.text());
//        }
//
//        /* 본문 */
//        // 모든 .content--body를 찾음
//        Elements sectionElems = doc.select(".content--body");
//
//        // 각 블록마다
//        for (Element sectionElem : sectionElems) {
//            NewsDetailDto.Section section = new NewsDetailDto.Section();
//
//            // .body-title가 있으면 텍스트 세팅, 없으면 빈 문자열
//            Element titleElem = sectionElem.selectFirst(".body-title");
//            section.setTitle(titleElem != null ? titleElem.text() : "");
//
//            StringBuilder bodyBuilder = new StringBuilder();
//
//            // .body-text p들을 전부 긁어와서 이어 붙임
//            Elements paragraphs = sectionElem.select(".body-text p");
//            for (Element p : paragraphs) {
//                // <br>과 <br /> 모두 \n으로 변경
//                String html = p.html().replaceAll("(?i)<br\\s*/?>", "\n");
//                // 모든 HTML 태그 제거
//                String cleanText = html.replaceAll("<[^>]+>", "");
//                bodyBuilder.append(cleanText.trim()).append("\n\n");
//            }
//
//            section.setBody(bodyBuilder.toString().trim());
//            sections.add(section);
//        }
//
//        news.setSections(sections);
//        return news;
//    }
//
//}

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
import java.util.Locale;

@Service
public class NewsCrawlerService {
    private static final String BASE = "https://www.fnn.jp";
    private static final String HOME = BASE + "/";
    private static final int RANKING_COUNT = 6;

    private final String userAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    /** FNN: アクセスランキング 상위 6개만 */
    public List<NewsDto> fetchNewsList(int limit) throws Exception {
        Connection connection = Jsoup.connect(HOME)
                .userAgent(userAgent)
                .referrer(HOME)
                .timeout(8000);

        Document doc = connection.get();
        List<NewsDto> articles = new ArrayList<>();

        // 1) 홈 상단 "アクセスランキング"
        Elements items = doc.select("section.top-ranking div.m-ranking.--grid section.m-ranking-item.--rank");
        // 2) 폴백: 사이드바 랭킹 위젯
        if (items.isEmpty()) {
            items = doc.select("div.m-ranking .m-ranking-item.--rank");
        }

        int take = Math.min(Math.min(limit, RANKING_COUNT), items.size());
        for (Element item : items.subList(0, take)) {
            Element aTag = item.selectFirst("a.m-ranking-item__link");
            String href = aTag != null ? aTag.attr("href") : "";
            String articleUrl = resolveUrl(href);

            // 제목: 랭킹에서는 h3 또는 div(사이드바) 둘 다 커버
            String title = "";
            Element h3 = item.selectFirst("h3.m-ranking-item__ttl");
            if (h3 != null) title = h3.text().trim();
            if (title.isEmpty()) {
                Element divTtl = item.selectFirst(".m-ranking-item__ttl");
                if (divTtl != null) title = divTtl.text().trim();
            }

            // 이미지: lazy 로딩(data-src / data-srcset / srcset)
            Element imgTag = item.selectFirst(".m-ranking-item-imgwrap img");
            String imgUrl = pickImage(imgTag);

            // 랭킹에는 날짜가 안 보임 → 빈 값
            String date = "";

            articles.add(new NewsDto(title, articleUrl, imgUrl, date));
        }
        return articles;
    }

    /** FNN 기사 상세: meta description을 요약으로, 본문은 .article-body 내부를 h2 단위 섹션으로 그룹핑 */
    public NewsDetailDto fetchNewsDetail(String url) throws Exception {
        String target = resolveUrl(url);
        Document doc = Jsoup.connect(target)
                .userAgent(userAgent)
                .referrer(HOME)
                .timeout(8000)
                .get();

        NewsDetailDto news = new NewsDetailDto();
        List<NewsDetailDto.Section> sections = new ArrayList<>();

//        /* 요약: <meta name="description"> 우선 */
//        String summary = "";
//        Element metaDesc = doc.selectFirst("meta[name=description]");
//        if (metaDesc != null && metaDesc.hasAttr("content")) {
//            summary = metaDesc.attr("content").trim();
//        }
//        // 폴백: 본문 첫 단락 2개
//        if (summary.isEmpty()) {
//            Elements firstParas = doc.select("div.article-body > p");
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < Math.min(2, firstParas.size()); i++) {
//                String t = cleanParagraph(firstParas.get(i));
//                if (!t.isEmpty()) sb.append(t).append(" ");
//            }
//            summary = sb.toString().trim();
//        }
//        news.setSummary(summary);

        /* 본문 섹션: .article-body 자식들을 순회하며 h2 기준으로 묶기 */
        Element body = doc.selectFirst("div.article-body");
        if (body != null) {
            NewsDetailDto.Section current = new NewsDetailDto.Section();
            current.setTitle("");
            StringBuilder bodyBuf = new StringBuilder();

            for (Element child : body.children()) {
                String tag = child.tagName();
                if ("h2".equalsIgnoreCase(tag)) {
                    // 이전 섹션 flush
                    String accumulated = bodyBuf.toString().trim();
                    if (!accumulated.isEmpty() || (current.getTitle() != null && !current.getTitle().isEmpty())) {
                        current.setBody(accumulated);
                        sections.add(current);
                    }
                    // 새 섹션 시작
                    current = new NewsDetailDto.Section();
                    current.setTitle(child.text().trim());
                    bodyBuf = new StringBuilder();
                } else if ("p".equalsIgnoreCase(tag)) {
                    String line = cleanParagraph(child);
                    if (!line.isEmpty()) {
                        if (bodyBuf.length() > 0) bodyBuf.append("\n\n");
                        bodyBuf.append(line);
                    }
                } else if ("ul".equalsIgnoreCase(tag)) {
                    // 리스트는 한 덩어리 텍스트로 변환
                    Elements lis = child.select("li");
                    for (Element li : lis) {
                        String line = cleanParagraph(li);
                        if (!line.isEmpty()) {
                            if (bodyBuf.length() > 0) bodyBuf.append("\n");
                            bodyBuf.append("・").append(line);
                        }
                    }
                    if (lis.size() > 0) bodyBuf.append("\n");
                }
            }
            // 마지막 섹션 flush
            String accumulated = bodyBuf.toString().trim();
            if (!accumulated.isEmpty() || (current.getTitle() != null && !current.getTitle().isEmpty())) {
                current.setBody(accumulated);
                sections.add(current);
            }
        }

        // 섹션이 하나도 없으면 전체 p를 모아서 단일 섹션으로
        if (sections.isEmpty()) {
            Elements paras = doc.select("article p");
            StringBuilder all = new StringBuilder();
            for (Element p : paras) {
                String line = cleanParagraph(p);
                if (!line.isEmpty()) {
                    if (all.length() > 0) all.append("\n\n");
                    all.append(line);
                }
            }
            if (all.length() > 0) {
                NewsDetailDto.Section s = new NewsDetailDto.Section();
                s.setTitle("");
                s.setBody(all.toString());
                sections.add(s);
            }
        }

        news.setSections(sections);

        String publishedDisplayJa = "";
        // 1) 화면에 보이는 문자열
        Element timeSpan = doc.selectFirst("time.article-header-info__time-wrap span.article-header-info__time");
        if (timeSpan != null) {
            publishedDisplayJa = timeSpan.text().trim();
        }
        if (publishedDisplayJa.isEmpty()) {
            // 혹시 구조가 조금 다를 때 대비
            Element timeAll = doc.selectFirst("div.article-header-info__date time");
            if (timeAll != null) publishedDisplayJa = timeAll.text().trim();
        }

        news.setDate(publishedDisplayJa);

        return news;
    }

    /* ===================== 유틸 ===================== */

    private String resolveUrl(String href) {
        if (href == null || href.isEmpty()) return "";
        if (href.startsWith("http://") || href.startsWith("https://")) return href;
        if (!href.startsWith("/")) href = "/" + href;
        return BASE + href;
    }

    private String pickImage(Element imgTag) {
        if (imgTag == null) return "";

        // 1) data-srcset / srcset (마지막 후보가 보통 고해상도)
        String srcset = imgTag.hasAttr("data-srcset") ? imgTag.attr("data-srcset")
                : (imgTag.hasAttr("srcset") ? imgTag.attr("srcset") : "");
        if (!srcset.isEmpty()) {
            String[] parts = srcset.split(",");
            String candidate = parts[parts.length - 1].trim();
            int sp = candidate.indexOf(' ');
            String url = sp > 0 ? candidate.substring(0, sp) : candidate;
            if (!isDataUrl(url)) return url;
        }

        // 2) data-src
        String dataSrc = imgTag.hasAttr("data-src") ? imgTag.attr("data-src") : "";
        if (!dataSrc.isEmpty() && !isDataUrl(dataSrc)) return dataSrc;

        // 3) src (일부는 data:image 이므로 필터)
        String src = imgTag.hasAttr("src") ? imgTag.attr("src") : "";
        if (!src.isEmpty() && !isDataUrl(src)) return src;

        return "";
    }

    private boolean isDataUrl(String s) {
        return s != null && s.toLowerCase(Locale.ROOT).startsWith("data:");
    }

    private String cleanParagraph(Element e) {
        // <br> -> \n, 나머지 태그 제거
        String html = e.html().replaceAll("(?i)<br\\s*/?>", "\n");
        String text = html.replaceAll("<[^>]+>", "");
        return text.replace('\u00A0', ' ').trim(); // &nbsp; 제거
    }
}
