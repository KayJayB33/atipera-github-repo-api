package dev.kayjaybee.atiperagithubrepoapi.github;

import dev.kayjaybee.atiperagithubrepoapi.exception.GithubApiException;
import dev.kayjaybee.atiperagithubrepoapi.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

record GithubPaginated(String firstPage, String lastPage, String nextPage, String prevPage) {

    static <T> List<T> getAllData(WebClient githubWebClient, String url, Class<T> clazz) {
        return getPage(githubWebClient, url, clazz)
                .expand(response -> {
                    var linkHeader = response.getHeaders().getFirst("link");
                    var nextPageUrl = fromHeader(linkHeader).nextPage();
                    if (nextPageUrl == null) {
                        return Mono.empty();
                    }
                    return getPage(githubWebClient, url, clazz);
                })
                .flatMap(response -> Optional.ofNullable(response.getBody()).orElseGet(Flux::empty))
                .collectList()
                .block();
    }

    private static <T> Mono<ResponseEntity<Flux<T>>> getPage(WebClient githubWebClient, String url, Class<T> clazz) {
        return githubWebClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new NotFoundException(body))))
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new GithubApiException(body))))
                .toEntityFlux(clazz);
    }

    private static GithubPaginated fromHeader(String linkHeader) {
        if(linkHeader == null) {
            return new GithubPaginated(null, null, null, null);
        }

        String first = null;
        String last = null;
        String next = null;
        String prev = null;

        var links = linkHeader.split(",");
        for (String link : links) {
            var segments = link.split(";");
            var url = segments[0].replace("<", "").replace(">", "").trim();
            var rel = Rel.of(segments[1].trim());
            switch (rel) {
                case Rel.FIRST -> first = url;
                case Rel.LAST -> last = url;
                case Rel.NEXT -> next = url;
                case Rel.PREV -> prev = url;
            }
        }
        return new GithubPaginated(first, last, next, prev);
    }

    private enum Rel {
        FIRST("rel=\"first\""),
        LAST("rel=\"last\""),
        NEXT("rel=\"next\""),
        PREV("rel=\"prev\"");

        private final String pattern;

        Rel(String rel) {
            this.pattern = rel;
        }

        String getPattern() {
            return pattern;
        }

        static Rel of(String text) {
            for (Rel value : Rel.values()) {
                if (value.getPattern().equals(text)) {
                    return value;
                }
            }

            throw new IllegalArgumentException("No matching Rel for " + text);
        }
    }
}
