package com.example.android.unscramble.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _score = MutableLiveData<Int>().apply { value = 0 }
    val score: LiveData<Int> get() = _score

    private val _currentWordCount = MutableLiveData<Int>().apply { value = 0 }
    val currentWordCount: LiveData<Int> get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String> get() = _currentScrambledWord

    private lateinit var currentWord: String
    private var wordsList: MutableList<String> = mutableListOf()

    init {
        getNextWord()
    }

    private fun getNextWord(){
        currentWord = allWordsList.random()

        val tempWord = currentWord.toCharArray()
        do {
            tempWord.shuffle()
        }while(String(tempWord).equals(currentWord, false))

        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    fun reinitializeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    fun isUserWordCorrect(playerWord: String):Boolean{
        if(playerWord.equals(currentWord, true)){
            _score.value = _score.value?.plus(SCORE_INCREASE)
            return true
        }
        return false
    }

    fun nextWord(): Boolean {
        return if (currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}