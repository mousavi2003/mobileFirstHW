import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ir.sharif.api.GitHubApi
import ir.sharif.model.CachedUser
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.reflect.Type
import java.util.Scanner


val cacheFile = File("cache.json")
val cacheType: Type = object : TypeToken<MutableMap<String, CachedUser>>() {}.type
val gson = GsonBuilder().setPrettyPrinting().create()
val cache: MutableMap<String, CachedUser> =
    if (cacheFile.exists()) gson.fromJson(cacheFile.readText(), cacheType) else mutableMapOf()

fun saveCacheToFile() {
    cacheFile.writeText(gson.toJson(cache))
}

fun main() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api = retrofit.create(GitHubApi::class.java)
    val scanner = Scanner(System.`in`)

    loop@ while (true) {
        println("""
        Program Menu:
        1- Get user information
        2- Display the list of users in memory
        3- Search by username
        4- Search by repository name
        5️- exit
        """.trimIndent())

        when (scanner.nextLine()) {
            "1" -> {
                print("github username:  ")
                val username = scanner.nextLine()
                if (cache.containsKey(username)) {
                    println("Information has already been received!")
                } else {
                    try {
                        val userResponse = api.getUser(username).execute()
                        val repoResponse = api.getRepos(username).execute()
                        if (userResponse.isSuccessful && repoResponse.isSuccessful) {
                            val user = userResponse.body()!!
                            val repos = repoResponse.body()!!
                            cache[username] = CachedUser(user, repos)
                            saveCacheToFile()
                            println("Information was saved successfully.")
                        } else {
                            println("Error retrieving information. Code: ${userResponse.code()} / ${repoResponse.code()}")
                        }
                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }
                }
            }
            "2" -> {
                println("List of users:")
                cache.keys.forEach { println("- $it") }
            }
            "3" -> {
                print("Username to search: ")
                val username = scanner.nextLine()
                val data = cache[username]
                if (data != null) {
                    println("User: ${data.user.login}")
                    println("Followers: ${data.user.followers}, following: ${data.user.following}")
                    println("Account creation date: ${data.user.created_at}")
                    println("repositories:")
                    data.repos.forEach { println("- ${it.name}") }
                } else {
                    println("User not found in memory!")
                }
            }
            "4" -> {
                print("repo name: ")
                val repoName = scanner.nextLine()
                val foundUsers = cache.filterValues { it.repos.any { repo -> repo.name == repoName } }
                if (foundUsers.isNotEmpty()) {
                    println("The repository was found in the following users:")
                    foundUsers.forEach { (username, _) -> println("- $username") }
                } else {
                    println("Repository not found.")
                }
            }
            "5" -> break@loop
            else -> println("The option is invalid.")
        }
    }
    println("Exit the app...")
}
