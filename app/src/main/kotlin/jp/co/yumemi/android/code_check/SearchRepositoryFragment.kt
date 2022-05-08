/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.databinding.SearchRepositoryFragmentBinding
import jp.co.yumemi.android.code_check.model.RepositoryInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

// TODO: MVVMを導入してViewModelに処理を移す
/**
 * キーワード入力からGitHubリポジトリを検索し、一覧表示するフラグメント
 * */
class SearchRepositoryFragment : Fragment(R.layout.search_repository_fragment) {

    private var _binding: SearchRepositoryFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SearchRepositoryFragmentBinding.bind(view)

        val viewModel = SearchRepositoryViewModel()

        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        val adapter =
            RepositoryListAdapter(
                object : RepositoryListAdapter.OnItemClickListener {
                    override fun itemClick(item: RepositoryInfo) {
                        gotoRepositoryFragment(item)
                    }
                }
            )

        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                editText.text.toString().let {
                    viewModel.viewModelScope.launch(
                        CoroutineExceptionHandler { _, exception ->
                            Toast.makeText(
                                requireContext(),
                                exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        viewModel.searchResults(it).apply { adapter.submitList(this) }
                    }
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }
    }

    fun gotoRepositoryFragment(item: RepositoryInfo) {
        val action =
            SearchRepositoryFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(
                item = item
            )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
