package com.example.mypc.a8;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<getMessages> messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public MessagesAdapter(List<getMessages> messagesList) {
        this.messagesList = messagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView senderMessage, receiverMessage;
        private CircleImageView imageView;

        public MessageViewHolder(View itemView) {
            super(itemView);

            senderMessage = itemView.findViewById(R.id.sender_message);
            receiverMessage = itemView.findViewById(R.id.receiver_massage);
            imageView = itemView.findViewById(R.id.receiver_image);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_row , parent , false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {

        String MessageSenderId = mAuth.getCurrentUser().getUid();
        getMessages messages = messagesList.get(position);

        String fromId = messages.getFrom();
        String fromMessageType = messages.getType();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String profileimage = dataSnapshot.child("profileImage").getValue().toString();
                    if (profileimage.equals("" + ".jpg")){
                        Picasso.with(holder.receiverMessage.getContext()).load(R.mipmap.profile).placeholder(R.mipmap.profile).into(holder.imageView);
                    }
                    else {
                        Picasso.with(holder.receiverMessage.getContext()).load(profileimage).placeholder(R.mipmap.profile).into(holder.imageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")){
                holder.receiverMessage.setVisibility(View.INVISIBLE);
                holder.imageView.setVisibility(View.INVISIBLE);

                if (fromId.equals(MessageSenderId)){

                    holder.senderMessage.setBackgroundResource(R.drawable.sender_message);
                    holder.senderMessage.setTextColor(Color.BLACK);
                    holder.senderMessage.setGravity(Gravity.LEFT);
                    holder.senderMessage.setText(messages.getMessage());

                }
                else {
                    holder.senderMessage.setVisibility(View.INVISIBLE);
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.receiverMessage.setVisibility(View.VISIBLE);
                    holder.receiverMessage.setBackgroundResource(R.drawable.receiver_message);
                    holder.receiverMessage.setTextColor(Color.BLACK);
                    holder.receiverMessage.setGravity(Gravity.LEFT);
                    holder.receiverMessage.setText(messages.getMessage());
                }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

}
