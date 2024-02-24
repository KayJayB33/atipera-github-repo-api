package dev.kayjaybee.atiperagithubrepoapi.github;

import dev.kayjaybee.atiperagithubrepoapi.exception.ErrorResponse;
import dev.kayjaybee.atiperagithubrepoapi.exception.GithubApiException;
import dev.kayjaybee.atiperagithubrepoapi.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github-repos")
class GithubRepoController {
    private final GithubRepoService githubRepoService;

    @Autowired
    GithubRepoController(GithubRepoService githubRepoService) {
        this.githubRepoService = githubRepoService;
    }

    @GetMapping("/{ownerLogin}")
    List<GithubRepoDTO> getGithubReposByOwnerLogin(
            @PathVariable String ownerLogin,
            @RequestParam(name = "fork", defaultValue = "false") boolean isFork) {
        return githubRepoService.getGithubReposByOwnerLogin(ownerLogin, isFork);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleUserNotFoundException(NotFoundException ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(GithubApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleGithubApiException(GithubApiException ex) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
}
