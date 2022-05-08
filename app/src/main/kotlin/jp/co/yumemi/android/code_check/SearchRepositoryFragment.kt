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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.databinding.SearchRepositoryFragmentBinding

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

        val _viewModel = SearchRepositoryViewModel(requireContext())

        val _layoutManager = LinearLayoutManager(requireContext())
        val _dividerItemDecoration = DividerItemDecoration(requireContext(), _layoutManager.orientation)
        val _adapter =
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
                    _viewModel.searchResults(it).apply { _adapter.submitList(this) }
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.recyclerView.also {
            it.layoutManager = _layoutManager
            it.addItemDecoration(_dividerItemDecoration)
            it.adapter = _adapter
        }
    }

    fun gotoRepositoryFragment(item: RepositoryInfo) {
        val _action =
            SearchRepositoryFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(
                item = item
            )
        findNavController().navigate(_action)
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
        val _view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return ViewHolder(_view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val _item = getItem(position)
        (holder.itemView.findViewById<View>(R.id.repositoryNameView) as TextView).text = _item.name

        holder.itemView.setOnClickListener { itemClickListener.itemClick(_item) }
    }
}
