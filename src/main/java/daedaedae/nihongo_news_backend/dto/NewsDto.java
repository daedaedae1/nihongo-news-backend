package daedaedae.nihongo_news_backend.dto;

public class NewsDto {

    private String title;
    private String url;
    private String image;
    private String date;

    // = @NoArgsConstructor ?
    public NewsDto() {
    }

    // = @AllArgsConstructor
    public NewsDto(String title, String url, String image, String date) {
        this.title = title;
        this.url = url;
        this.image = image;
        this.date = date;
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

}
