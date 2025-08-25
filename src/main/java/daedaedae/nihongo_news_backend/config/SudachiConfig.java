package daedaedae.nihongo_news_backend.config;

import com.worksap.nlp.sudachi.Config;
import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class SudachiConfig {

    @Bean(destroyMethod = "close")
    public Dictionary sudachiDictionary() throws Exception {
        // 사전 파일의 실제 Path를 구함
        Path systemDic = resolveClasspath("sudachi/system_core.dic");
        // Config.defaultConfig() : Sudachi 기본 설정 객체 생성
        // systemDictionary(systemDic) : 시스템 사전 경로를 지정
        Config cfg = Config.defaultConfig().systemDictionary(systemDic);
        // Dictionary 생성
        return new DictionaryFactory().create(cfg);
    }

    private Path resolveClasspath(String cp) throws Exception {
        // 선행 슬래시 제거 -> ClassLoader.getResource는 슬래시 없는 경로 형태를 기대
        if (cp.startsWith("/")) cp = cp.substring(1);
        URL url = Thread.currentThread().getContextClassLoader().getResource(cp);
        if (url == null) throw new IllegalStateException("Classpath resource not found: " + cp);
        Path p = Paths.get(url.toURI());
        if (!Files.exists(p)) throw new IllegalStateException("File not found: " + p);
        return p;
    }
}
