package dev.kayjaybee.atiperagithubrepoapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GithubWebClientConfig {
    @Bean
    public WebClient getGithubWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }
}
