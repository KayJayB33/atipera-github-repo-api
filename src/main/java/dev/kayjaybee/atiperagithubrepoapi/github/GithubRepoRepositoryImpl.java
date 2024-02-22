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
            ResponseEntity<List<JsonNode>> response = getGithubApiResponse(ownerLogin, url);

            if (response != null && response.hasBody()) {
                List<GithubRepo> repos = response.getBody()
                        .stream()
                        .map(this::mapFromJsonToEntity)
                        .toList();
                allRepos.addAll(repos);
            }

            HttpHeaders headers = response != null ? response.getHeaders() : null;
            url = getNextPageUrl(headers);
        }

        return allRepos;
    }

    private ResponseEntity<List<JsonNode>> getGithubApiResponse(String ownerLogin, String url) {
        return githubWebClient.get()
                .uri(url)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                        Mono.error(new UserNotFoundException("User " + ownerLogin + " not found")))
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new GithubApiException("GitHub API Error: " + errorBody))))
                .toEntityList(JsonNode.class)
                .block();
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
                return part.split(";")[0]
                        .trim()
                        .replace("<", "")
                        .replace(">", "");
            }
        }
        return null;
    }

    private GithubRepo mapFromJsonToEntity(JsonNode repoNode) {
        GithubRepo repo = new GithubRepo();
        repo.setName(repoNode.get("name").asText());
        repo.setOwnerLogin(repoNode.get("owner").get("login").asText());
        repo.setFork(repoNode.get("fork").asBoolean());
        repo.setBranches(findBranches(repo));
        return repo;
    }

    private List<Branch> findBranches(GithubRepo repo) {
        return githubWebClient.get()
                .uri("/repos/" + repo.getOwnerLogin() + "/" + repo.getName() + "/branches")
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
    }
}