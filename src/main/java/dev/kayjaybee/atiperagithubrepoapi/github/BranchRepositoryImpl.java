package dev.kayjaybee.atiperagithubrepoapi.github;

import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Repository
public class BranchRepositoryImpl implements BranchRepository {

    private final WebClient githubWebClient;

    BranchRepositoryImpl(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    @Override
    public List<Branch> getBranchesByOwnerLoginAndRepoName(String ownerLogin, String repoName) {
        var uri = UriComponentsBuilder.fromUriString("/repos/{owner}/{repo}/branches")
                .queryParam("per_page", 100)
                .build()
                .expand(ownerLogin, repoName)
                .toUriString();

        return GithubPaginated.getAllData(githubWebClient, uri, Branch.class);
    }
}
