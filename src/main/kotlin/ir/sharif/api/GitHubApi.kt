package ir.sharif.api

import ir.sharif.model.GitHubRepo
import ir.sharif.model.GitHubUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<GitHubUser>

    @GET("users/{username}/repos")
    fun getRepos(@Path("username") username: String): Call<List<GitHubRepo>>
}