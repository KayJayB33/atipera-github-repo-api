package dev.kayjaybee.atiperagithubrepoapi.github;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
class GithubRepoService {

    private final GithubRepoRepository githubRepoRepository;
    private final BranchRepository branchRepository;

    public GithubRepoService(GithubRepoRepository githubRepoRepository, BranchRepository branchRepository) {
        this.githubRepoRepository = githubRepoRepository;
        this.branchRepository = branchRepository;
    }

    public List<GithubRepoDTO> getGithubReposByOwnerLogin(String ownerLogin, boolean includeForks) {
        final var repos = githubRepoRepository.getGithubReposByOwnerLogin(ownerLogin);

        if(repos == null) {
            return Collections.emptyList();
        }

        if (!includeForks) {
            repos.removeIf(GithubRepo::fork);
        }

        final var repoDTOs = repos.stream()
                .map(GithubRepoDTO::new)
                .toList();

        repoDTOs.forEach(repo -> {
            final var branches = branchRepository.getBranchesByOwnerLoginAndRepoName(ownerLogin, repo.name())
                    .stream()
                    .map(BranchDTO::new)
                    .toList();
            repo.branches().addAll(branches);
        });

        return repoDTOs;
    }
}
