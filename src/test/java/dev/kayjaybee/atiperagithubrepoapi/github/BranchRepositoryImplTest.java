package dev.kayjaybee.atiperagithubrepoapi.github;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import dev.kayjaybee.atiperagithubrepoapi.exception.GithubApiException;
import dev.kayjaybee.atiperagithubrepoapi.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@WireMockTest(httpPort = 8081)
class BranchRepositoryImplTest {

    private BranchRepository sut;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081").build();
        sut = new BranchRepositoryImpl(webClient);
    }

    @Test
    void itShouldGetBranchesByOwnerLoginAndRepoName() throws IOException {
        // Given
        String ownerLogin = "octocat";
        String repoName = "Hello-World";
        String responseBody =
                IOUtils.resourceToString("/files/github-repository-test/correct-branch-endpoint-response.json", UTF_8);

        stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches?per_page=100"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)));

        // When
        List<Branch> branches = sut.getBranchesByOwnerLoginAndRepoName(ownerLogin, repoName);

        // Then
        assertEquals(1, branches.size());
        assertEquals("master", branches.getFirst().name());
        assertEquals("9ec739ee4e1654aa32f99ee4b986e5489c1ae4c4", branches.getFirst().commit().sha());
    }

    @Test
    void itShouldThrowUserNotFoundExceptionWhenOwnerLoginOrRepoNameDoesNotExist() {
        // Given
        String ownerLogin = "octocat";
        String repoName = "Hello-World";
        String responseMessage = "Not found";
        stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody(responseMessage)));

        // When
        // Then
        var exception = assertThrows(NotFoundException.class, () -> sut.getBranchesByOwnerLoginAndRepoName(ownerLogin, repoName));
        assertEquals(responseMessage, exception.getMessage());
    }

    @Test
    void itShouldThrowGithubApiExceptionWhenGithubApiReturnsError() {
        // Given
        String ownerLogin = "octocat";
        String repoName = "Hello-World";
        String responseMessage = "Internal server error";

        stubFor(get(urlEqualTo("/repos/octocat/Hello-World/branches?per_page=100"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody(responseMessage)));

        // When
        // Then
        var exception = assertThrows(GithubApiException.class, () -> sut.getBranchesByOwnerLoginAndRepoName(ownerLogin, repoName));
        assertEquals(responseMessage, exception.getMessage());
    }
}
