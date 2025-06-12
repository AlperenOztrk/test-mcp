package fin.kk.mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds
        factory.setReadTimeout(30000);    // 30 seconds
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Add User-Agent header to mimic browser request
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            request.getHeaders().add("Accept", 
                "application/json, text/plain, */*");
            request.getHeaders().add("Accept-Language", 
                "de-DE,de;q=0.9,en;q=0.8");
            return execution.execute(request, body);
        });
        
        return restTemplate;
    }
} 