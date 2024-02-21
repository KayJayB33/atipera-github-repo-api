package dev.kayjaybee.atiperagithubrepoapi.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GithubRepoService {

    private final GithubRepoRepository githubRepoRepository;

    @Autowired
    public GithubRepoService(GithubRepoRepository githubRepoRepository) {
        this.githubRepoRepository = githubRepoRepository;
    }

    public List<GithubRepo> getGithubReposByOwnerLogin(String ownerLogin, boolean includeForks) {
        final List<GithubRepo> repos = githubRepoRepository.getGithubReposByOwnerLogin(ownerLogin);

        if (repos != null && !includeForks) {
            repos.removeIf(GithubRepo::isFork);
        }

        return repos;
    }
}
