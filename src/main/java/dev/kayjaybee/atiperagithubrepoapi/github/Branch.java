package dev.kayjaybee.atiperagithubrepoapi.github;

import java.util.Objects;

class Branch {
    private String name;
    private String lastCommitHash;

    public Branch(String name, String lastCommitHash) {
        this.name = name;
        this.lastCommitHash = lastCommitHash;
    }

    public Branch() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCommitHash() {
        return lastCommitHash;
    }

    public void setLastCommitHash(String lastCommitHash) {
        this.lastCommitHash = lastCommitHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return Objects.equals(name, branch.name) && Objects.equals(lastCommitHash, branch.lastCommitHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lastCommitHash);
    }

    @Override
    public String toString() {
        return "Branch{" +
                "name='" + name + '\'' +
                ", lastCommitHash='" + lastCommitHash + '\'' +
                '}';
    }
}
