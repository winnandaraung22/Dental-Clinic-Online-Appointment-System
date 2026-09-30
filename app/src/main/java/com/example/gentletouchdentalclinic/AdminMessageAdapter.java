package com.example.gentletouchdentalclinic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminMessageAdapter
        extends RecyclerView.Adapter<AdminMessageAdapter.MessageViewHolder> {

    private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED = 2;
    private final List<AdminMessage> messageList;
    private final String adminId;

    public AdminMessageAdapter(
            List<AdminMessage> messageList,
            String adminId) {

        this.messageList = messageList;
        this.adminId = adminId;
    }

    @Override
    public int getItemViewType(int position) {

        AdminMessage message =
                messageList.get(position);

        if (adminId != null
                && adminId.equals(message.getSenderId())) {

            return MESSAGE_SENT;
        }

        return MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        int layout;

        if (viewType == MESSAGE_SENT) {

            layout = R.layout.item_admin_message_sent;

        } else {

            layout = R.layout.item_admin_message_received;
        }

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MessageViewHolder holder,
            int position) {

        AdminMessage message =
                messageList.get(position);

        holder.txtMessage.setText(
                message.getMessage()
        );
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtMessage;

        MessageViewHolder(@NonNull View itemView) {

            super(itemView);

            txtMessage =
                    itemView.findViewById(
                            R.id.txtMessage
                    );
        }
    }
}