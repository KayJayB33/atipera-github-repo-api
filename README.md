# Atipera Recruitment Task

This application allows to retrieve all public repositories from a given GitHub user.

## Usage

To use it, send a GET request to the following endpoint:
```
http://localhost:8080/api/v1/github-repositories/{username}(?fork={true|false})
```

Where `{username}` is the GitHub user you want to retrieve the repositories from.
You can also retrieve the forked repositories by adding the `fork` query parameter 
(default is `false`).

### Example

Example response for the user `KayJayB33` looks like this:
```json
[
    {
        "name": "IR-Head-Tracker",
        "ownerLogin": "KayJayB33",
        "branches": [
            {
                "name": "enhancement/udp",
                "lastCommitHash": "ccce1e5418ac1e4e58d0bf9af625ab0009e2c0da"
            },
            {
                "name": "master",
                "lastCommitHash": "655f29932e24e8725bd1f27370e49b31ca27b414"
            }
        ],
        "fork": false
    },
    {
        "name": "ZTP-Projekt-1",
        "ownerLogin": "KayJayB33",
        "branches": [
            {
                "name": "master",
                "lastCommitHash": "71c4a637e7656f52a95c9d314808ff2753d00b9e"
            }
        ],
        "fork": false
    },
    {
        "name": "ZTP-Projekt-2",
        "ownerLogin": "KayJayB33",
        "branches": [
            {
                "name": "master",
                "lastCommitHash": "36d22a3d4b9ee21683afd824f6ac61c6d20a607b"
            }
        ],
        "fork": false
    },
    {
        "name": "ZTP-Projekt-3",
        "ownerLogin": "KayJayB33",
        "branches": [
            {
                "name": "master",
                "lastCommitHash": "60c55e5f335a4316f015660eb10704afa0849ac9"
            }
        ],
        "fork": false
    }
]
```

## Errors

When an error occurs, the application will return a response with appropriate HTTP Status Code
and body with the following structure:
```json
{
  "status": {httpStatusCode},
  "message": {errorMessage}
}
```

## Used technologies

This app was written in Java 21 using Spring Boot framework.
It uses GitHub API to retrieve the repositories using WebClient from Spring WebFlux.