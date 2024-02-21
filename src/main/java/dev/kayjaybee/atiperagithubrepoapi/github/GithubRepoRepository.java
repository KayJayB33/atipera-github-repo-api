package dev.kayjaybee.atiperagithubrepoapi.github;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface GithubRepoRepository {
    List<GithubRepo>  getGithubReposByOwnerLogin(String ownerLogin);
}
