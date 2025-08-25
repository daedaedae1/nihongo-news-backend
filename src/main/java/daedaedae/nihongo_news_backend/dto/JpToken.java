package daedaedae.nihongo_news_backend.dto;

// List<JpToken>에서 토큰 1개 단위의 데이터 모델
// surface : 잘린 문자열 | base : 기본형 | reading : 읽는 법 | pos : 품사
public record JpToken(String surface, String base, String reading, String pos) {}
