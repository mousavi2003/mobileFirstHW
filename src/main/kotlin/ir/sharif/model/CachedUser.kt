package ir.sharif.model

data class CachedUser(
    val user: GitHubUser,
    val repos: List<GitHubRepo>
)