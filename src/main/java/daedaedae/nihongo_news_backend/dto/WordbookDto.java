package daedaedae.nihongo_news_backend.dto;

import daedaedae.nihongo_news_backend.domain.Wordbook;

public record WordbookDto(Long userId, String jpWord, String jpReading, String krWord) {
    public static WordbookDto of(Wordbook e) {
        return new WordbookDto(e.getUserId(), e.getJpWord(), e.getJpReading(), e.getKrWord());
    }
}
