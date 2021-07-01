package com.example.sudoku.Fragment.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sudoku.R
import com.example.sudoku.login.Item

public class RankAdapter(val movieList: List<Item>) :
    RecyclerView.Adapter<RankAdapter.MovieVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_resgister, parent, false)
        return MovieVH(view)
    }

    override fun onBindViewHolder(holder: MovieVH, position: Int) {
        val item: Item = movieList[position]
        holder.nameTextView.setText(item.Name)
        holder.emailTextView.setText(item.Email)
        holder.levelTextView.setText(item.Level)
        when (holder.levelTextView.text.toString()) {
            " 1" -> {
                holder.levelTextView.setText("")
                holder.levelTextView.setBackgroundResource(R.drawable.ic_first)
            }
            " 2" -> {
                holder.levelTextView.setText("")
                holder.levelTextView.setBackgroundResource(R.drawable.ic_second)
            }
            " 3" -> {
                holder.levelTextView.setText("")
                holder.levelTextView.setBackgroundResource(R.drawable.ic_third)
            }
        }
        holder.timeTextView.setText(item.Time)
        val isExpanded: Boolean = movieList[position].isExpanded()
        holder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.relative.setOnClickListener {
            val user: Item = movieList[position]
            user.setExpanded(!user.isExpanded())
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

     class MovieVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var relative : RelativeLayout= itemView.findViewById(R.id.relative)
        var expandableLayout: ConstraintLayout= itemView.findViewById(R.id.expandableLayout)
        var nameTextView: TextView= itemView.findViewById(R.id.tv_name)
        var emailTextView: TextView= itemView.findViewById(R.id.tv_email)
        var levelTextView : TextView= itemView.findViewById(R.id.tv_level)
        var timeTextView: TextView= itemView.findViewById(R.id.tv_time)

    }



}