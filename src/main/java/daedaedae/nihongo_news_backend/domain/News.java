package daedaedae.nihongo_news_backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;
    private String date;
    private String image;

    public News() {
    }

    public News(String title, String url, String date, String image) {
        this.title = title;
        this.url = url;
        this.date = date;
        this.image = image;
    }
}
