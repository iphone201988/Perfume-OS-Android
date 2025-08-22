package com.tech.perfumos.ui.notification

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tech.perfumos.R
import com.tech.perfumos.databinding.NotificationAlertItemViewBinding
import com.tech.perfumos.databinding.NotificationFollowItemViewBinding
import com.tech.perfumos.databinding.NotificationInviteItemViewBinding
import com.tech.perfumos.ui.notification.model.NotificationItem
import com.tech.perfumos.ui.notification.model.NotificationModel
import com.tech.perfumos.utils.Utils.getTimeAgo


class NotificationAdapter(val context: Context, var items: List<NotificationItem>,var callback: (Any) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_QUIZ_INVITE = 1
        private const val TYPE_FOLLOW = 2
        private const val TYPE_ALERT = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationItem.QuizInviteItem -> TYPE_QUIZ_INVITE
            is NotificationItem.FollowItem -> TYPE_FOLLOW
            is NotificationItem.AlertItem -> TYPE_ALERT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_QUIZ_INVITE -> QuizInviteViewHolder(
                NotificationInviteItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            TYPE_FOLLOW -> FollowViewHolder(
                NotificationFollowItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            TYPE_ALERT -> AlertViewHolder(
                NotificationAlertItemViewBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is QuizInviteViewHolder ->  holder.binding.apply {
                val model = items[position] as NotificationItem.QuizInviteItem
                val time  = getTimeAgo(model.createdAt.toString())

                tvTitle.text = model.title
                tvNotification.text = model.message
                tvTime.text = time

                btnJoinNow.setOnClickListener {
                    if(model.quiz?.status== "waiting"){
                        callback(model)

                    }else{
                        Toast.makeText(context, "I think quiz is expire", Toast.LENGTH_SHORT).show()
                    }
                }


            }
            is FollowViewHolder -> holder.binding.apply {
                val model = items[position] as NotificationItem.FollowItem
                val time  = getTimeAgo(model.createdAt.toString())
                Log.d("model", "onBindViewHolder: ${model.isRead}")
                if (model.isRead == true) {
                    ivDot.visibility = View.GONE
                    //clMain.backgroundTintList = holder.itemView.context.resources.getColorStateList(R.color.white)
                    clMain.background =
                        holder.itemView.context.resources.getDrawable(R.drawable.bg_corner10_white)

                } else {
                    ivDot.visibility = View.VISIBLE
                    clMain.background =
                        holder.itemView.context.resources.getDrawable(R.drawable.bg_accent_stoke)
                }
                tvTitle.text = model.title
                tvNotification.text = model.message
                tvTime.text = time

            }
            is AlertViewHolder ->  holder.binding.apply {
                val model = items[position] as NotificationItem.AlertItem
                val time  = getTimeAgo(model.createdAt.toString())

                tvTitle.text = model.title
                tvNotification.text = model.message
                tvTime.text = time
            }
        }
    }

    override fun getItemCount(): Int = items.size

    // ---------------- View Holders ----------------

    class QuizInviteViewHolder(itemView: NotificationInviteItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    class FollowViewHolder(itemView: NotificationFollowItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    class AlertViewHolder(itemView: NotificationAlertItemViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        val binding  = itemView
    }
}
