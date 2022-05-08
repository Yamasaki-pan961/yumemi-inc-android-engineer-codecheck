/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.databinding.SearchRepositoryFragmentBinding
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
            CustomAdapter(
                object : CustomAdapter.OnItemClickListener {
                    override fun itemClick(item: RepositoryInfo) {
                        gotoRepositoryFragment(item)
                    }
                }
            )

        binding.searchInputText.setOnEditorActionListener { editText, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                // FIX: ActionなのでFragmentに書くべきでないかも
                editText.text.toString().let {
                    viewModel.viewModelScope.launch {
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
    // TODO:onDestroyView()に_binding=nullを追記しメモリリークを防ぐ
}

// FIX: privateにする
val diff_util =
    object : DiffUtil.ItemCallback<RepositoryInfo>() {
        override fun areItemsTheSame(
            oldItem: RepositoryInfo,
            newItem: RepositoryInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: RepositoryInfo,
            newItem: RepositoryInfo
        ): Boolean {
            return oldItem == newItem
        }
    }

/**
 * 検索にヒットしたGitHubリポジトリをリスト表示するための
 * ListAdapterを継承したカスタムアダプター
 * */
class CustomAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<RepositoryInfo, CustomAdapter.ViewHolder>(diff_util) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {
        fun itemClick(item: RepositoryInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        (holder.itemView.findViewById<View>(R.id.repositoryNameView) as TextView).text = item.name

        holder.itemView.setOnClickListener { itemClickListener.itemClick(item) }
    }
}
