import gradle.Repository
import gradle.Stargazers

import static util.RequestUtil.relativePath

final fromUser = params.from_user
final fromBranch = params.from_branch
final intoRepo = params.into_repo
final intoBranch = params.into_branch
final gradleVersion = params.gradle_version
assert fromUser instanceof String
assert fromBranch instanceof String
assert intoRepo instanceof String
assert intoBranch instanceof String
assert gradleVersion instanceof String

final stargazers = new Stargazers()

final title = "Gradle $gradleVersion"
final body = """
[Gradle $gradleVersion](https://gradle.org/docs/$gradleVersion/release-notes) is available now.

This pull request updates Gradle wrapper and build.gradle in the repository.
Merge it if all tests passed with the latest Gradle.

Automatic pull request can be turned off by unstar [gradleupdate repository](${stargazers.htmlUrl}).
"""

final pullRequest = new Repository(intoRepo).createPullRequest(intoBranch, fromUser, fromBranch, title, body)

log.info("Pull request #${pullRequest.number} has been created on ${pullRequest.html_url}")
defaultQueue.add(
        url: relativePath(request, '6-done.groovy'),
        params: [
                gradleVersion: gradleVersion,
                url: pullRequest.url,
                htmlUrl: pullRequest.html_url,
                createdAt: pullRequest.created_at,
                repo: pullRequest.base.repo.name,
                owner: pullRequest.base.repo.owner.login,
                ownerId: pullRequest.base.repo.owner.id,
        ])
