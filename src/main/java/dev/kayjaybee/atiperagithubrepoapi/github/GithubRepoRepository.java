package dev.kayjaybee.atiperagithubrepoapi.github;

import java.util.List;

interface GithubRepoRepository {
    List<GithubRepo>  getGithubReposByOwnerLogin(String ownerLogin);
}
