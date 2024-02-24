package dev.kayjaybee.atiperagithubrepoapi.github;

record GithubRepo(String name, Owner owner, boolean fork) {
    record Owner(String login) {
    }
}
