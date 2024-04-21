package com.ashokbala.android.sms

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashokbala.android.sms.Utils.toFormattedDate

class SMSAdapter(private val smsList: List<SMSModel>) : RecyclerView.Adapter<SMSAdapter.SMSViewHolder>() {

    inner class SMSViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderNameTextView: TextView = itemView.findViewById(R.id.text_sender)
        val messageBodyTextView: TextView = itemView.findViewById(R.id.text_message_body)
        val timestampTextView: TextView = itemView.findViewById(R.id.text_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return SMSViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: SMSAdapter.SMSViewHolder,
        position: Int
    ) {
        val currentItem = smsList[position]
        val key=PreferenceHelper.getKey(holder.senderNameTextView.context, "secureKey","")
        // Decrypt the message
        val decryptedMessage = AESCrypt.decrypt(currentItem.msg, key)
        println("Decrypted message: $decryptedMessage")
        holder.senderNameTextView.text = currentItem.name
        holder.messageBodyTextView.text = if(decryptedMessage.isEmpty()) currentItem.msg else decryptedMessage
        holder.timestampTextView.text = currentItem.timestamp?.toFormattedDate()
    }

    override fun getItemCount() = smsList.size
}