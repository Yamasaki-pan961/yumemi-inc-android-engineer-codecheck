/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.util.*
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

/**
 * SearchRepositoryFragmentのViewModel
 * @see SearchRepositoryFragment
 * */
class SearchRepositoryViewModel : ViewModel() {
    // 検索結果
    // TODO: エラーハンドリングを追加する
    /**
     * GitHubのAPIを使ってリポジトリを検索する関数
     * @param inputText 検索キーワード
     * */
    suspend fun searchResults(inputText: String): List<RepositoryInfo> =
        withContext(Dispatchers.IO) {
            val client = HttpClient(Android)
            async {
                // FIX: responseはnullable
                val response: HttpResponse =
                    client.get("https://api.github.com/search/repositories") {
                        header("Accept", "application/vnd.github.v3+json")
                        parameter("q", inputText)
                    }

                val jsonBody = JSONObject(response.receive<String>())

                val jsonItems = jsonBody.optJSONArray("items")!!

                val items = mutableListOf<RepositoryInfo>()

                /** アイテムの個数分ループする */
                for (i in 0 until jsonItems.length()) {
                    val jsonItem = jsonItems.optJSONObject(i)!!
                    val name = jsonItem.optString("full_name")
                    val ownerIconUrl = jsonItem.optJSONObject("owner")!!.optString("avatar_url")
                    val language = jsonItem.optString("language")
                    val stargazersCount = jsonItem.optLong("stargazers_count")
                    val watchersCount = jsonItem.optLong("watchers_count")
                    val forksCount = jsonItem.optLong("forks_count")
                    val openIssuesCount = jsonItem.optLong("open_issues_count")

                    items.add(
                        RepositoryInfo(
                            name = name,
                            ownerIconUrl = ownerIconUrl,
                            language = language,
                            stargazersCount = stargazersCount,
                            watchersCount = watchersCount,
                            forksCount = forksCount,
                            openIssuesCount = openIssuesCount
                        )
                    )
                }

                lastSearchDate = Date()

                return@async items.toList()
            }.await()
        }
}

// TODO: Modelとして単一のファイルに切り出す
/**
 * GitHubリポジトリの詳細情報を格納するクラス
 * */
@Parcelize
data class RepositoryInfo(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable
