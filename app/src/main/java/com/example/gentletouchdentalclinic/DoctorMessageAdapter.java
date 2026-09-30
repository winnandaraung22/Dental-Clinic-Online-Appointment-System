package com.example.gentletouchdentalclinic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorMessageAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ADMIN = 0;
    private static final int TYPE_DOCTOR = 1;
    private final Context context;
    private final String doctorId;
    private final List<DoctorMessage> messageList =
            new ArrayList<>();

    public DoctorMessageAdapter(
            Context context,
            String doctorId) {

        this.context = context;
        this.doctorId = doctorId;
    }

    public void addMessage(
            String messageId,
            String message,
            String senderId,
            String receiverId,
            Timestamp timestamp,
            boolean isRead) {

        DoctorMessage doctorMessage =
                new DoctorMessage(
                        messageId,
                        message,
                        senderId,
                        receiverId,
                        timestamp,
                        isRead
                );

        messageList.add(doctorMessage);

        notifyItemInserted(
                messageList.size() - 1
        );
    }

    public void clearMessages() {

        messageList.clear();

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        DoctorMessage message =
                messageList.get(position);

        if (doctorId != null &&
                doctorId.equals(
                        message.getSenderId()
                )) {

            return TYPE_DOCTOR;
        }

        return TYPE_ADMIN;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        LayoutInflater inflater =
                LayoutInflater.from(
                        parent.getContext()
                );

        if (viewType == TYPE_DOCTOR) {

            View view =
                    inflater.inflate(
                            R.layout.item_chat_message_doctor,
                            parent,
                            false
                    );

            return new DoctorViewHolder(view);
        }

        else {

            View view =
                    inflater.inflate(
                            R.layout.item_chat_message_admin,
                            parent,
                            false
                    );

            return new AdminViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position) {

        DoctorMessage message =
                messageList.get(position);


        if (holder instanceof DoctorViewHolder) {

            DoctorViewHolder doctorHolder =
                    (DoctorViewHolder) holder;

            doctorHolder.txtMessage.setText(
                    message.getMessage()
            );

            doctorHolder.txtTime.setText(
                    formatTimestamp(
                            message.getTimestamp()
                    )
            );
        }

        else if (
                holder instanceof AdminViewHolder) {

            AdminViewHolder adminHolder =
                    (AdminViewHolder) holder;

            adminHolder.txtMessage.setText(
                    message.getMessage()
            );

            adminHolder.txtTime.setText(
                    formatTimestamp(
                            message.getTimestamp()
                    )
            );
        }
    }

    @Override
    public int getItemCount() {

        return messageList.size();
    }

    private String formatTimestamp(
            Timestamp timestamp) {

        if (timestamp == null) {

            return "";
        }


        Date date =
                timestamp.toDate();


        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "h:mm a",
                        Locale.getDefault()
                );


        return formatter.format(date);
    }

    static class AdminViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgProfile;
        TextView txtMessage;
        TextView txtTime;

        AdminViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtMessage =
                    itemView.findViewById(
                            R.id.txtMessage
                    );

            txtTime =
                    itemView.findViewById(
                            R.id.txtMessageTime
                    );
        }
    }
    static class DoctorViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtMessage;
        TextView txtTime;

        DoctorViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtMessage =
                    itemView.findViewById(
                            R.id.txtMessage
                    );

            txtTime =
                    itemView.findViewById(
                            R.id.txtMessageTime
                    );
        }
    }

    public static class DoctorMessage {
        private final String messageId;
        private final String message;
        private final String senderId;
        private final String receiverId;
        private final Timestamp timestamp;
        private final boolean isRead;

        public DoctorMessage(
                String messageId,
                String message,
                String senderId,
                String receiverId,
                Timestamp timestamp,
                boolean isRead) {

            this.messageId = messageId;
            this.message = message;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.timestamp = timestamp;
            this.isRead = isRead;
        }

        public String getMessageId() {

            return messageId;
        }

        public String getMessage() {

            return message;
        }

        public String getSenderId() {

            return senderId;
        }

        public String getReceiverId() {

            return receiverId;
        }

        public Timestamp getTimestamp() {

            return timestamp;
        }

        public boolean isRead() {

            return isRead;
        }
    }
}