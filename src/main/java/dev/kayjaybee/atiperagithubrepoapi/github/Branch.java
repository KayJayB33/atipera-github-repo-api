package dev.kayjaybee.atiperagithubrepoapi.github;

record Branch(String name, Commit commit) {
    record Commit(String sha) {
    }
}
