package dev.kayjaybee.atiperagithubrepoapi.github;

import com.fasterxml.jackson.databind.JsonNode;
import dev.kayjaybee.atiperagithubrepoapi.exception.GithubApiException;
import dev.kayjaybee.atiperagithubrepoapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
class GithubRepoRepositoryImpl implements GithubRepoRepository {

    private final WebClient githubWebClient;

    @Autowired
    GithubRepoRepositoryImpl(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }


    @Override
    public List<GithubRepo> getGithubReposByOwnerLogin(String ownerLogin) {
        List<GithubRepo> allRepos = new ArrayList<>();
        String url = "/users/" + ownerLogin + "/repos?per_page=100";
        while (url != null) {
            ResponseEntity<List<GithubRepo>> response = githubWebClient.get()
                    .uri(url)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                            Mono.error(new UserNotFoundException("User " + ownerLogin + " not found")))
                    .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new GithubApiException("GitHub API Error: " + errorBody))))
                    .toEntityList(GithubRepo.class)
                    .block();

            if (response != null && response.hasBody()) {
                List<GithubRepo> repos = response.getBody();
                allRepos.addAll(mapBranches(ownerLogin, repos));
            }

            HttpHeaders headers = response != null ? response.getHeaders() : null;
            url = getNextPageUrl(headers);
        }

        return allRepos;
    }

    private String getNextPageUrl(HttpHeaders headers) {
        if (headers != null && headers.containsKey(HttpHeaders.LINK)) {
            List<String> links = headers.get(HttpHeaders.LINK);
            for (String link : links) {
                if (link.contains("rel=\"next\"")) {
                    return extractUrl(link);
                }
            }
        }
        return null;
    }

    private String extractUrl(String link) {
        String[] parts = link.split(",");
        for (String part : parts) {
            if (part.contains("rel=\"next\"")) {
                return part.split(";")[0].trim().replace("<", "").replace(">", "");
            }
        }
        return null;
    }

    private List<GithubRepo> mapBranches(String ownerLogin, List<GithubRepo> repos) {
        for (GithubRepo repo : repos) {
            List<Branch> branches = githubWebClient.get()
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