package com.example.sudoku.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sudoku.game.SudokuGame

class SudokuViewModel: ViewModel() {

    var sudokuGame = SudokuGame()

}
class ShareViewModel: ViewModel() {
    val inputString = MutableLiveData<String>()
}