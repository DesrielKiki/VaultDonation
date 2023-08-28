package com.desrielkiki.vaultdonation.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.desrielkiki.vaultdonation.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.btnLinkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/desrielfrizky/"
            val intent = Intent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            startActivity(intent)
        }
        binding.btnLinktree.setOnClickListener {
            val url = "https://bit.ly/JokiJanneth"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}