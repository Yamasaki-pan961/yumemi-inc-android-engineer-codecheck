/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.RepositoryDetailFragmentBinding

/**
 * GitHubリポジトリの詳細情報を表示するためのフラグメント
 * */
class RepositoryDetailFragment : Fragment(R.layout.repository_detail_fragment) {

    private val args: RepositoryDetailFragmentArgs by navArgs()

    private var binding: RepositoryDetailFragmentBinding? = null
    private val _binding
        get() = binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        binding = RepositoryDetailFragmentBinding.bind(view)

        val item = args.item

        _binding.ownerIconView.load(item.ownerIconUrl)
        _binding.nameView.text = item.name
        _binding.languageView.text = getString(R.string.written_language, item.language)
        _binding.starsView.text =
            getString(R.string.repository_stars, item.stargazersCount.toString())
        _binding.watchersView.text =
            getString(R.string.repository_watchers, item.watchersCount.toString())
        _binding.forksView.text = getString(R.string.repository_forks, item.forksCount.toString())
        _binding.openIssuesView.text =
            getString(R.string.repository_open_issues, item.openIssuesCount.toString())
    }
}
