package daedaedae.nihongo_news_backend.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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
