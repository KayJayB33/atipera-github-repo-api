package dev.kayjaybee.atiperagithubrepoapi.github;

import java.util.List;

class GithubRepo {
    private String name;
    private String ownerLogin;
    private List<Branch> branches;
    private boolean fork;

    public GithubRepo(String name, String ownerLogin, List<Branch> branches, boolean fork) {
        this.name = name;
        this.ownerLogin = ownerLogin;
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


    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    @Override
    public String toString() {
        return "GithubRepo{" +
                "name='" + name + '\'' +
                ", ownerName='" + ownerLogin + '\'' +
                ", branches=" + branches +
                ", fork=" + fork +
                '}';
    }
}
