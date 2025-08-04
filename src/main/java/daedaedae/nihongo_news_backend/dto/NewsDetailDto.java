package daedaedae.nihongo_news_backend.dto;

public class NewsDetailDto {
    private String summary;
    private String content;

    public NewsDetailDto() {
    }

    public NewsDetailDto(String summary, String content) {
        this.summary = summary;
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
