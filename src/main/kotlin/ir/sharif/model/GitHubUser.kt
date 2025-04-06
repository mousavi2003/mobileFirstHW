package ir.sharif.model

data class GitHubUser(
    val login: String,
    val followers: Int,
    val following: Int,
    val created_at: String
)