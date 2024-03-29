package dev.kayjaybee.atiperagithubrepoapi.github;

import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Repository
class GithubRepoRepositoryImpl implements GithubRepoRepository {

    private final WebClient githubWebClient;

    GithubRepoRepositoryImpl(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }


    @Override
    public List<GithubRepo> getGithubReposByOwnerLogin(String ownerLogin) {
        var uri = UriComponentsBuilder.fromUriString("/users/{owner}/repos")
                .queryParam("per_page", 100)
                .build()
                .expand(ownerLogin)
                .toUriString();

        return GithubPaginated.getAllData(githubWebClient, uri, GithubRepo.class);
    }
}