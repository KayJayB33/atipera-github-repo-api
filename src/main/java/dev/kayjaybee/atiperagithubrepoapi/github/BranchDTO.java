package dev.kayjaybee.atiperagithubrepoapi.github;

record BranchDTO(String name, String lastCommitHash) {
    BranchDTO(Branch branch) {
        this(branch.name(), branch.commit().sha());
    }
}
