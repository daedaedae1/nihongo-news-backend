package daedaedae.nihongo_news_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // 쿠키 허용


        // addMapping : CORS를 적용할 url의 패턴을 정의 ("/**"로 모든 패턴 가능)
        // allowedOrigins : 허용할 origin ("*"로 모든 origin 허용 가능, 여러 개도 지정 가능)
        // allowedMethods : 허용할 HTTP Method ("*"로 모든 Method 허용 가능)
        // maxAge : 요청을 캐싱할 시간
    }
}
