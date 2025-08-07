package daedaedae.nihongo_news_backend.dto;

import java.util.List;

public class NewsDetailDto {
    private String summary; // 요약
    private List<Section> sections; // 단락별

    public static class Section {
        private String title; // 소제목
        private String body;  // 본문

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

}