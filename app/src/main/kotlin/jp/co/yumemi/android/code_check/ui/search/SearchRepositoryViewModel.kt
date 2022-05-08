/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui.search

import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.util.*
import jp.co.yumemi.android.code_check.ui.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.model.RepositoryInfo
import kotlinx.coroutines.*
import org.json.JSONObject

/**
 * SearchRepositoryFragmentのViewModel
 * @see SearchRepositoryFragment
 * */
class SearchRepositoryViewModel : ViewModel() {

    private var _repositoryList: List<RepositoryInfo>? = null
    val repositoryList get() = _repositoryList

    // 検索結果
    // TODO: エラーハンドリングを追加する
    /**
     * GitHubのAPIを使ってリポジトリを検索する関数
     * @param inputText 検索キーワード
     * */
    suspend fun searchResults(inputText: String): Unit =
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
                    val stargazersCount = jsonItem.optInt("stargazers_count")
                    val watchersCount = jsonItem.optInt("watchers_count")
                    val forksCount = jsonItem.optInt("forks_count")
                    val openIssuesCount = jsonItem.optInt("open_issues_count")

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

                _repositoryList = items.toList()
            }.await()
        }
}
