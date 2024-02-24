package dev.kayjaybee.atiperagithubrepoapi.github;

import java.util.List;

public interface BranchRepository {
    List<Branch>  getBranchesByOwnerLoginAndRepoName(String ownerLogin, String repoName);
}
