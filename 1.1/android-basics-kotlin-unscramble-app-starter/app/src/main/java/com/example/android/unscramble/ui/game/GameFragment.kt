package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GameFragment : Fragment() {
    private val viewModel: GameViewModel by viewModels()
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        viewModel.score.observe(viewLifecycleOwner, { score ->
            binding.score.text = getString(R.string.score, score)
        })

        viewModel.currentWordCount.observe(viewLifecycleOwner, { count ->
            binding.wordCount.text = getString(R.string.word_count, count, MAX_NO_OF_WORDS)
        })

        viewModel.currentScrambledWord.observe(viewLifecycleOwner, { word ->
            binding.textViewUnscrambledWord.text = word
        })
    }

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if(viewModel.isUserWordCorrect(playerWord)){
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        }else{
            setErrorTextField(true)
        }
    }

    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    private fun showFinalScoreDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)){ _, _ ->
                activity?.finish()
            }
            .setPositiveButton(getString(R.string.play_again)){ _, _ ->
                viewModel.reinitializeData()
                setErrorTextField(false)
            }
            .show()
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
