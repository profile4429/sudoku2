package com.example.sudoku.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.sudoku.R
import com.example.sudoku.game.Cell
import com.example.sudoku.view.custom.BoardView
import com.example.sudoku.viewmodel.SudokuViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), BoardView.OnTouchListener{

    private lateinit var viewModel: SudokuViewModel     //View model

    private lateinit var numberButtons: List<Button>    //Number input buttons

    private lateinit var musicPlayer: MediaPlayer       //Background music player

    private var isMusicPlaying: Boolean = true         //Khac so voi bien isPlaying cua MediaPlayer, bien nay dung de xac dinh su dung nut mute/unmute va trang thai onPause, onResume


    private val rootRef = FirebaseDatabase.getInstance().reference
    private lateinit var Ref : Query

    private lateinit var level :String
    private lateinit var id :String
    private var reset : Int = 0
    private var temp : Int =5
    //Override function on create app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        boardView.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(SudokuViewModel::class.java)
        viewModel.sudokuGame.selectCellLiveData.observe(this, Observer { updateSelectCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.highlightKeysLiveData.observe(this, Observer { updateHighlightKeys(it) })
        viewModel.sudokuGame.isFinishedLiveData.observe(this, Observer { updateFinishGame(it) })

        numberButtons = listOf(firstButton, secondButton, thirdButton, fourthButton, fifthButton, sixthButton, seventhButton, eighthButton, ninthButton)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.sudokuGame.handleInput(index + 1)
                if (!viewModel.sudokuGame.ischeck(index+1)) {
                    reset++
                }
                mistakes.text = reset.toString()
            }
        }

        exitButton.setOnClickListener { showReturnDialog() }

        musicButton.setOnClickListener { musicToggle() }

        newButton.setOnClickListener { NewGameDialog(viewModel) }

        hintButton.setOnClickListener {
            viewModel.sudokuGame.hint()
            temp--
            tvHint.text=temp.toString()
            //LimitedHint()
        }

        resetButton.setOnClickListener { viewModel.sudokuGame.reset() }

        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }


        Playgame(viewModel)

        musicPlayer = MediaPlayer.create(applicationContext, R.raw.background_music)
        musicPlayer.isLooping = true
        musicPlayer.setVolume(20F, 20F)
        musicPlayer.start()
        timer.start()

    }
    private fun LimitedHint(){
        var a = Integer.parseInt(tvHint.text.toString())
        if(a<=0){
            hintButton.isEnabled=false
            Toast.makeText(this,"Bạn đã hết số lần trợ giúp",
                Toast.LENGTH_SHORT).show()
        }
    }
    //Override function on pause
    override fun onPause() {
        super.onPause()
        if (isMusicPlaying)
            musicPlayer.pause()
    }

    //Override function on resume
    override fun onResume() {
        super.onResume()
        if (isMusicPlaying)
            musicPlayer.start()
    }

    //Override function khi nhan nut back tren dien thoai
    override fun onBackPressed() {
        super.onBackPressed()
        showExitDialog()
    }

    //Function update selected cell
    private fun updateSelectCellUI(cell: Pair<Int, Int>?) = cell?.let{
        boardView.updateSelectCellUI(cell.first, cell.second)
    }

    //Function update cells
    private fun updateCells(cells: List<Cell>?) = cells?.let {
        boardView.updateCells(cells)
    }

    //Function update cac number buttons duoc highlight
    private fun updateHighlightKeys(set: Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.colorPrimary)
                        else Color.LTGRAY
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    //Function update da finish game hay chua
    private fun updateFinishGame(isFinished: Boolean?) = isFinished?.let {
        val a = mistakes.text.toString()
        val b: Int? = a.toInt()
        if (it) {
            timer.stop()
            showCongratsDialog(viewModel)
            reset = 0
            mistakes.text= "0"
            temp=5
            tvHint.text = "5"
            hintButton.isEnabled = true
        }
        if (b==6) {
            timer.stop()
            GameOver()
            reset = 0
            mistakes.text = "0"
            temp=5
            tvHint.text = "5"
            hintButton.isEnabled = true
        }
    }

    //Function update selected cell khi touch vao cell
    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectCell(row, col)
    }

    //Function khi nhan unmute button
    private fun musicToggle() {
        isMusicPlaying = musicButton.isChecked

        isMusicPlaying = if (!isMusicPlaying) {
            musicPlayer.start()
            true
        } else {
            musicPlayer.pause()
            false
        }
    }

    //Function show dialog khi finish game
    private fun showCongratsDialog(viewModel: SudokuViewModel) {
        val time = (SystemClock.elapsedRealtime() - timer.base) / 1000
        var intent : Intent = getIntent()
        var tab:String = "                        "
        var Tab:String=""
        id  = intent.getStringExtra("ID")
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        dialog.setIcon(R.drawable.finish_icon)
        dialog.setTitle("Congratulations!")
        dialog.setMessage("You have finished the sudoku at $time seconds. Cheers for the hard work!")
        Firebase.database.reference.child("Users").child(id).child("time").push().setValue(time)

        val df: DateFormat = SimpleDateFormat("d MMM yyyy, HH:mm:ss")
        val date: String = df.format(Calendar.getInstance().getTime())
        Ref = rootRef.child("Users").child(id).child("level").limitToLast(1)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    level = ds.getValue().toString()
                    if(level == "Easy" || level == "Hard"){
                        Tab = "         "
                    }
                    else if (level == "Normal"){
                        Tab = "     "
                    }
                    var level_time : String = level+Tab+time.toString() + " seconds " +"\n"+tab+ date
                    var result : String= level+" win"
                    Firebase.database.reference.child("Users").child(id).child("result").push().setValue(result)
                    Firebase.database.reference.child("Users").child(id).child("level_Time").push().setValue(level_time)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        Ref.addListenerForSingleValueEvent(eventListener)

        dialog.setPositiveButton("New game") {
            dlg, _ ->
            run {
                dlg.dismiss()
                showNewGameDialog(viewModel)
            }
        }
        dialog.setNegativeButton("Exit") {
            dlg, _ ->
            run {
                dlg.dismiss()
                showExitDialog()
            }
        }

        dialog.show()
    }
    //Xoa value cuối cùng ở nút level khi click exit button
    private fun Delete(){
        var intent : Intent = getIntent()
        id=intent.getStringExtra("ID")
        Ref = rootRef.child("Users").child(id).child("level").limitToLast(1)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    var key: String = ds.key.toString()
                    Firebase.database.reference.child("Users").child(id).child("level").child(key).removeValue()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        Ref.addListenerForSingleValueEvent(eventListener)
    }
    //Game Over
    private fun GameOver(){
        var intent : Intent = getIntent()
        id=intent.getStringExtra("ID")
        Ref = rootRef.child("Users").child(id).child("level").limitToLast(1)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    var level = ds.getValue().toString()
                    var result : String= level+" lose"
                    Firebase.database.reference.child("Users").child(id).child("result").push().setValue(result)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        Ref.addListenerForSingleValueEvent(eventListener)
        dialogGameOver()
    }
    //Function show exit dialog khi nhan nut exit
    private fun showReturnDialog(){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        dialog.setIcon(R.drawable.back_icon)
        dialog.setTitle("Exit!")
        dialog.setMessage("Do you want to exit? ")
        dialog.setNegativeButton("No") {
                dlg, _ -> dlg.dismiss()
        }
        dialog.setPositiveButton("Yes") {
                _, _ ->
            run {
                Delete()
                dialogGameOver()
            }
        }
        dialog.show()
    }
    @SuppressLint("NewApi")
    private fun dialogGameOver(){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        dialog.setIcon(R.drawable.exit_game_icon)
        dialog.setTitle("Notify!")
        dialog.setMessage("Game over !!!")
        dialog.setPositiveButton("Yes") {
                _, _ ->
            run {
                timer.base = SystemClock.elapsedRealtime()
                timer.stop()
                musicPlayer.stop()
                musicPlayer.release()
                finish()
                exitProcess(0)
            }
        }
        dialog.setOnDismissListener {dialogGameOver()}
        dialog.show()
    }
    //Function show exit dialog khi ket thuc tro choi va nhan nut exit
    private fun showExitDialog() {
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        dialog.setIcon(R.drawable.exit_game_icon)
        dialog.setTitle("Exit!")
        dialog.setMessage("Exit the game?")
        dialog.setNegativeButton("No") {
                dlg, _ -> dlg.dismiss()
        }
        dialog.setPositiveButton("Yes") {
                _, _ ->
            run {
                timer.base = SystemClock.elapsedRealtime()
                timer.stop()
                musicPlayer.stop()
                musicPlayer.release()
                finish()
                exitProcess(0)
            }
        }
        dialog.show()
    }
    //Function play game
    private fun Playgame(viewModel: SudokuViewModel){
        var intent : Intent = getIntent()

        var value: Int = intent.getIntExtra("Key_1",0)

        viewModel.sudokuGame.newGame(value)
    }
    //Function show new game dialog khi ket thuc tro choi va nhan nut newgame
    private fun showNewGameDialog(viewModel: SudokuViewModel) {
        var intent : Intent = getIntent()
        id=intent.getStringExtra("ID")
        val diffNumber: Array<Int> = arrayOf(32, 40, 48)
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        var diffChoice = 32
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        dialog.setIcon(R.drawable.ic_level)
        dialog.setTitle("Choose your game difficulty:")
        dialog.setSingleChoiceItems(diffName, 0) {
            _, i -> diffChoice = diffNumber[i]
        }
        dialog.setPositiveButton("OK") {
                _, _ ->
                run {
                    if(diffChoice == 32){
                        level = "Easy"
                    }
                    else if(diffChoice == 40){
                        level = "Normal"
                    }
                    else if(diffChoice == 48){
                        level = "Hard"
                    }
                    Firebase.database.reference.child("Users").child(id).child("level").push().setValue(level)
                    timer.base = SystemClock.elapsedRealtime()
                    timer.stop()
                    viewModel.sudokuGame.newGame(diffChoice)
                    timer.start()
                    reset=0
                    temp = 5
                    tvHint.text = "5"
                    mistakes.text="0"
                    hintButton.isEnabled = true
                }
        }
        dialog.setNegativeButton("Cancel") {
            dlg, _ ->  dlg.cancel()
        }

        dialog.show()

    }
    //Function show new game dialog khi nhan nut new game
    private fun NewGameDialog(viewModel: SudokuViewModel) {
        var intent : Intent = getIntent()
        id=intent.getStringExtra("ID")
        val diffNumber: Array<Int> = arrayOf(32, 40, 48)
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        var diffChoice = 32
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        dialog.setIcon(R.drawable.ic_level)
        dialog.setTitle("Choose your game difficulty:")
        dialog.setSingleChoiceItems(diffName, 0) {
                _, i -> diffChoice = diffNumber[i]
        }
        dialog.setPositiveButton("OK") {
                _, _ ->
            run {
                Delete()
                if(diffChoice == 32){
                    level = "Easy"
                }
                else if(diffChoice == 40){
                    level = "Normal"
                }
                else if(diffChoice == 48){
                    level = "Hard"
                }
                val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
                dialog.setIcon(R.drawable.new_game_icon)
                dialog.setTitle("Notify!")
                dialog.setMessage("You didn't finish the game! Let's start the new game !")
                dialog.setPositiveButton("Yes") {
                        _, _ ->
                    run {
                        Firebase.database.reference.child("Users").child(id).child("level").push().setValue(level)
                        timer.base = SystemClock.elapsedRealtime()
                        timer.stop()
                        viewModel.sudokuGame.newGame(diffChoice)
                        timer.start()
                        reset=0
                        temp = 5
                        tvHint.text = "5"
                        mistakes.text="0"
                        hintButton.isEnabled= true
                    }
                }
                dialog.show()
            }
        }
        dialog.setNegativeButton("Cancel") {
                dlg, _ ->  dlg.cancel()
        }
        dialog.show()
    }
}
