package dev.kayjaybee.atiperagithubrepoapi.github;

import java.util.ArrayList;
import java.util.List;

record GithubRepoDTO(String name, String ownerLogin, List<BranchDTO> branches, boolean fork) {
    GithubRepoDTO(GithubRepo githubRepo) {
        this(githubRepo.name(),
                githubRepo.owner().login(),
                new ArrayList<>(),
                githubRepo.fork());
    }
}
