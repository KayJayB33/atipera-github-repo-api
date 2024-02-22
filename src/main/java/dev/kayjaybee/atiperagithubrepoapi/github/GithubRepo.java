package dev.kayjaybee.atiperagithubrepoapi.github;

import java.util.List;

class GithubRepo {
    private String name;
    private List<Branch> branches;
    private boolean fork;

    public GithubRepo(String name, List<Branch> branches, boolean fork) {
        this.name = name;
        this.branches = branches;
        this.fork = fork;
    }

    public GithubRepo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    @Override
    public String toString() {
        return "GithubRepo{" +
                "name='" + name + '\'' +
                ", branches=" + branches +
                ", fork=" + fork +
                '}';
    }
}
