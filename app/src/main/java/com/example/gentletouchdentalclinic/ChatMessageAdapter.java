package com.example.gentletouchdentalclinic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter
        extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private final List<ChatMessage> messageList;
    private final String currentUserId;

    public ChatMessageAdapter(
            List<ChatMessage> messageList,
            String currentUserId) {

        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {

        ChatMessage message =
                messageList.get(position);

        if (message.getSenderId() != null
                && message.getSenderId().equals(currentUserId)) {

            return VIEW_TYPE_SENT;

        } else {

            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        int layout;

        if (viewType == VIEW_TYPE_SENT) {

            layout = R.layout.item_patient_message;

        } else {

            layout = R.layout.item_doctor_message;
        }

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                layout,
                                parent,
                                false
                        );

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MessageViewHolder holder,
            int position) {

        ChatMessage message =
                messageList.get(position);

        holder.txtMessage.setText(
                message.getMessage()
        );

        Timestamp timestamp =
                message.getTimestamp();

        if (timestamp != null) {

            Date date =
                    timestamp.toDate();

            SimpleDateFormat formatter =
                    new SimpleDateFormat(
                            "h:mm a",
                            Locale.getDefault()
                    );

            holder.txtTime.setText(
                    formatter.format(date)
            );

        } else {

            holder.txtTime.setText("");
        }
    }

    @Override
    public int getItemCount() {

        return messageList.size();
    }

    public static class MessageViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtMessage;
        TextView txtTime;

        public MessageViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtMessage =
                    itemView.findViewById(
                            R.id.txtMessage
                    );

            txtTime =
                    itemView.findViewById(
                            R.id.txtTime
                    );
        }
    }
}