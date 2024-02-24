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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WireMockTest(httpPort = 8081)
class GithubRepoRepositoryImplTest {

    private GithubRepoRepositoryImpl sut;

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8081").build();
        sut = new GithubRepoRepositoryImpl(webClient);
    }

    @Test
    void itShouldGetGithubReposByOwnerLogin() throws IOException {
        // Given
        String ownerLogin = "octocat";
        String responseBody = IOUtils.resourceToString("/files/github-repository-test/correct-repository-endpoint-response.json", UTF_8);

        stubFor(get(urlEqualTo("/users/octocat/repos?per_page=100")).willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(responseBody)));

        // When
        List<GithubRepo> repos = sut.getGithubReposByOwnerLogin(ownerLogin);

        // Then
        assertEquals(1, repos.size());
        assertEquals("Hello-World", repos.getFirst().name());
        assertEquals("octocat", repos.getFirst().owner().login());
        assertFalse(repos.getFirst().fork());
    }

    @Test
    void itShouldThrowUserNotFoundExceptionWhenOwnerLoginDoesNotExist() {
        // Given
        String ownerLogin = "octocat";
        String responseMessage = "Not found";
        stubFor(get(urlEqualTo("/users/octocat/repos?per_page=100")).willReturn(aResponse().withStatus(404).withBody(responseMessage)));

        // When
        // Then
        var exception = assertThrows(NotFoundException.class, () -> sut.getGithubReposByOwnerLogin(ownerLogin));
        assertEquals(responseMessage, exception.getMessage());
    }

    @Test
    void itShouldThrowGithubApiExceptionWhenGithubApiReturnsError() {
        // Given
        String ownerLogin = "octocat";
        String responseMessage = "Internal server error";
        stubFor(get(urlEqualTo("/users/octocat/repos?per_page=100")).willReturn(aResponse().withStatus(500).withBody(responseMessage)));

        // When
        // Then
        var exception = assertThrows(GithubApiException.class, () -> sut.getGithubReposByOwnerLogin(ownerLogin));
        assertEquals(responseMessage, exception.getMessage());
    }
}