package daedaedae.nihongo_news_backend.domain;

import jakarta.persistence.*;

@Entity
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    private String title;
    private String url;
    private String date;
    private String image;

    public Bookmark() {
    }

    public Bookmark(Long userId, String title, String url, String date, String image) {
        this.userId = userId;
        this.title = title;
        this.url = url;
        this.date = date;
        this.image = image;
    }
}
