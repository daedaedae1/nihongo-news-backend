package daedaedae.nihongo_news_backend.domain;

import jakarta.persistence.*;

@Entity
public class Wordbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="jp_word")
    private String jpWord;

    @Column(name="jp_reading")
    private String jpReading;

    @Column(name="kr_word")
    private String krWord;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJpWord() {
        return jpWord;
    }

    public void setJpWord(String jpWord) {
        this.jpWord = jpWord;
    }

    public String getJpReading() {
        return jpReading;
    }

    public void setJpReading(String jpReading) {
        this.jpReading = jpReading;
    }

    public String getKrWord() {
        return krWord;
    }

    public void setKrWord(String krWord) {
        this.krWord = krWord;
    }
}
