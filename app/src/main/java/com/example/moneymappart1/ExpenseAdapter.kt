package com.example.moneymappart1

import android.content.Intent // 🆕 Add this!
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymappart1.databinding.ItemExpenseBinding

class ExpenseAdapter(private val expenseList: List<ExpenseEntity>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.binding.tvDate.text = expense.date
        holder.binding.tvDescription.text = expense.description
        holder.binding.tvCategory.text = expense.category

        // Show/hide View Photo button
        if (!expense.photoUri.isNullOrEmpty()) {
            holder.binding.btnViewPhoto.visibility = View.VISIBLE
            holder.binding.btnViewPhoto.setOnClickListener {
                val intent = Intent(holder.itemView.context, ViewPhotoActivity::class.java)
                intent.putExtra("photoUri", expense.photoUri)
                holder.itemView.context.startActivity(intent)
            }
        } else {
            holder.binding.btnViewPhoto.visibility = View.GONE
        }
        // Show recurring info if applicable
        if (expense.isRecurring) {
            holder.binding.tvRecurringInfo.text = "Recurring: ${expense.recurrencePeriod}"
            holder.binding.tvRecurringInfo.visibility = View.VISIBLE
        } else {
            holder.binding.tvRecurringInfo.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int = expenseList.size
}
