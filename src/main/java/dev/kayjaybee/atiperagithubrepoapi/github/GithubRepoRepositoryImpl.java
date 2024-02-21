package dev.kayjaybee.atiperagithubrepoapi.github;

import com.fasterxml.jackson.databind.JsonNode;
import dev.kayjaybee.atiperagithubrepoapi.exception.GithubApiException;
import dev.kayjaybee.atiperagithubrepoapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
class GithubRepoRepositoryImpl implements GithubRepoRepository {

    private final WebClient webClient;
    private HttpHeaders responseHeaders;

    @Autowired
    GithubRepoRepositoryImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    responseHeaders = clientResponse.headers().asHttpHeaders();
                    return Mono.just(clientResponse);
                }))
                .build();
    }

    @Override
    public List<GithubRepo> getGithubReposByOwnerLogin(String ownerLogin) {
        List<GithubRepo> allRepos = new ArrayList<>();
        String url = "/users/" + ownerLogin + "/repos?per_page=100";

        // GitHub API paginates results, so we need to follow the "next" link to get all repos
        while (url != null) {
            List<GithubRepo> repos = webClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.error(new UserNotFoundException("User not found")))
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(JsonNode.class)
                                    .flatMap(jsonNode -> Mono.error(new GithubApiException("GitHub API Error: " + jsonNode.get("message").asText())))
                    )
                    .bodyToFlux(JsonNode.class)
                    .map(jsonNode -> {
                        String name = jsonNode.get("name").asText();
                        String ownerName = jsonNode.get("owner").get("login").asText();
                        boolean isFork = jsonNode.get("fork").asBoolean();
                        return new GithubRepo(name, ownerName, null, isFork);
                    })
                    .collectList()
                    .block();

            if (repos != null) {
                allRepos.addAll(repos);
            }

            url = getNextPageUrl(responseHeaders);
        }

        return mapBranches(ownerLogin, allRepos);
    }

    private String getNextPageUrl(HttpHeaders headers) {
        if (headers.containsKey("link")) {
            String linkHeader = headers.getFirst("link");
            String[] links = linkHeader.split(", ");
            for (String link : links) {
                if (link.endsWith("rel=\"next\"")) {
                    // Extract URL from <url>; rel="next"
                    String url = link.substring(link.indexOf('<') + 1, link.indexOf('>'));
                    return UriComponentsBuilder.fromUriString(url)
                            .replaceQueryParam("per_page")
                            .build()
                            .getPath();
                }
            }
        }
        return null;
    }

    private List<GithubRepo> mapBranches(String ownerLogin, List<GithubRepo> repos) {
        for (GithubRepo repo : repos) {
            List<Branch> branches = webClient.get()
                    .uri("/repos/" + ownerLogin + "/" + repo.getName() + "/branches")
                    .retrieve()
                    .bodyToFlux(JsonNode.class)
                    .map(jsonNode -> {
                        String name = jsonNode.get("name").asText();
                        String lastCommitHash = jsonNode.get("commit").get("sha").asText();
                        return new Branch(name, lastCommitHash);
                    })
                    .collectList()
                    .blockOptional()
                    .orElse(Collections.emptyList());

            repo.setBranches(branches);
        }

        return repos;
    }
}