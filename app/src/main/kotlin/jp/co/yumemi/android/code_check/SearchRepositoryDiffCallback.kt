package jp.co.yumemi.android.code_check

import androidx.recyclerview.widget.DiffUtil
import jp.co.yumemi.android.code_check.model.RepositoryInfo

object SearchRepositoryDiffCallback : DiffUtil.ItemCallback<RepositoryInfo>() {
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