package com.desrielkiki.vaultdonation.ui.util.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.desrielkiki.vaultdonation.databinding.LoadStateViewBinding

class MainLoadStateAdapter : LoadStateAdapter<MainLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(val binding: LoadStateViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): MainLoadStateAdapter.LoadStateViewHolder {
        return LoadStateViewHolder(
            LoadStateViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: MainLoadStateAdapter.LoadStateViewHolder,
        loadState: LoadState,
    ) {
        holder.binding.apply {
            progress.isVisible = loadState is LoadState.Loading
        }
    }
}
