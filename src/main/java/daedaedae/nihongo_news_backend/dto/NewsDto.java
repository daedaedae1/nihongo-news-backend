package daedaedae.nihongo_news_backend.dto;

import lombok.AllArgsConstructor;

public class NewsDto {

    private String title;
    private String url;
    private String image;
    private String date;
    private String summary;
    private String body;

    // = @NoArgsConstructor ?
    public NewsDto() {
    }

    // = @AllArgsConstructor
    public NewsDto(String title, String url, String image, String date, String body, String summary) {
        this.title = title;
        this.url = url;
        this.image = image;
        this.date = date;
        this.body = body;
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
