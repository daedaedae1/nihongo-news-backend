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
        Path systemDic = resolveClasspath("sudachi/system_core.dic");
        Config cfg = Config.defaultConfig().systemDictionary(systemDic);
        return new DictionaryFactory().create(cfg);
    }

    private Path resolveClasspath(String cp) throws Exception {
        if (cp.startsWith("/")) cp = cp.substring(1);
        URL url = Thread.currentThread().getContextClassLoader().getResource(cp);
        if (url == null) throw new IllegalStateException("Classpath resource not found: " + cp);
        Path p = Paths.get(url.toURI());
        if (!Files.exists(p)) throw new IllegalStateException("File not found: " + p);
        return p;
    }
}
