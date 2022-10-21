package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import kashyap.anurag.whatsappclone.ImageViewActivity;
import kashyap.anurag.whatsappclone.Model.ModelMessage;
import kashyap.anurag.whatsappclone.R;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.messageViewHolder> {

    private Context context;
    private ArrayList<ModelMessage> userMessagesList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

    public AdapterMessage(Context context, ArrayList<ModelMessage> userMessagesList) {
        this.context = context;
        this.userMessagesList = userMessagesList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_messages_layout, parent, false);
        return new messageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {

        String messageSenderId = firebaseAuth.getCurrentUser().getUid();
        ModelMessage modelMessage = userMessagesList.get(position);

        String fromUserId = modelMessage.getFrom();
        String fromMessageType = modelMessage.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("profileImage")){
                    String receiverProfileImage = snapshot.child("profileImage").getValue().toString();

                    try {
                        Picasso.get().load(receiverProfileImage).placeholder(R.drawable.ic_person_black).into(holder.receiverProfileIv);
                    } catch (Exception e) {
                        holder.receiverProfileIv.setImageResource(R.drawable.ic_person_black);
                    }
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileIv.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderMsgImageView.setVisibility(View.GONE);
        holder.receiverMsgImageView.setVisibility(View.GONE);

        if (fromMessageType.equals("text")){

            if (fromUserId.equals(messageSenderId)){

                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                holder.senderMessageText.setText(modelMessage.getMessage() + "\n \n" + modelMessage.getTime() + " - " + modelMessage.getDate());
            }else {

                holder.receiverProfileIv.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiverMessageText.setText(modelMessage.getMessage() + "\n \n" + modelMessage.getTime() + " - " + modelMessage.getDate());
            }
        }else if (fromMessageType.equals("image")){
            if (fromUserId.equals(messageSenderId)){
                holder.senderMsgImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(modelMessage.getMessage()).into(holder.senderMsgImageView);
            }else {
                holder.receiverMsgImageView.setVisibility(View.VISIBLE);
                holder.receiverProfileIv.setVisibility(View.VISIBLE);
                Picasso.get().load(modelMessage.getMessage()).into(holder.receiverMsgImageView);
            }
        }else if (fromMessageType.equals("pdf")){
            if (fromUserId.equals(messageSenderId)){

                holder.senderMsgImageView.setVisibility(View.VISIBLE);
                holder.senderMsgImageView.setBackgroundResource(R.drawable.ic_pdf_black);

            }else {

                holder.receiverMsgImageView.setVisibility(View.VISIBLE);
                holder.receiverMsgImageView.setBackgroundResource(R.drawable.ic_pdf_black);
                holder.receiverProfileIv.setVisibility(View.VISIBLE);

            }
        }else if (fromMessageType.equals("docx")) {
            if (fromUserId.equals(messageSenderId)) {

                holder.senderMsgImageView.setVisibility(View.VISIBLE);
                holder.senderMsgImageView.setBackgroundResource(R.drawable.ic_docx_black);


            } else {

                holder.receiverMsgImageView.setVisibility(View.VISIBLE);
                holder.receiverMsgImageView.setBackgroundResource(R.drawable.ic_docx_black);
                holder.receiverProfileIv.setVisibility(View.VISIBLE);

            }
        }

        if (fromUserId.equals(messageSenderId)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
                       CharSequence options[] = new CharSequence[]{
                               "Download",
                               "Delete For Me",
                               "Delete For Everyone",
                       };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("Delete Message?");
                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               if (i == 0){
                                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                   holder.itemView.getContext().startActivity(intent);
                               }else if (i == 1){
                                   deleteSentMessages(position, holder);
                               }else if (i == 2){
                                   deleteMessageEveryone(position, holder);
                               }
                           }
                       }).show();
                   }else if (userMessagesList.get(position).getType().equals("text")){
                       CharSequence options[] = new CharSequence[]{
                               "Delete For Me",
                               "Delete For Everyone",
                       };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("Delete Message?");
                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               if (i == 0){
                                   deleteSentMessages(position, holder);
                               }else if (i == 1){
                                   deleteMessageEveryone(position, holder);
                               }
                           }
                       }).show();
                   }else if (userMessagesList.get(position).getType().equals("image")){
                       CharSequence options[] = new CharSequence[]{
                               "View Image",
                               "Delete For Me",
                               "Delete For Everyone",
                       };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("Delete Message?");
                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               if (i == 0){
                                   Intent intent = new Intent(context, ImageViewActivity.class);
                                   intent.putExtra("url", userMessagesList.get(position).getMessage());
                                   context.startActivity(intent);
                               }else if (i == 1){
                                   deleteSentMessages(position, holder);
                               }else if (i == 2){
                                   deleteMessageEveryone(position, holder);
                               }
                           }
                       }).show();
                   }
                }
            });
        }else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")){
                        CharSequence options[] = new CharSequence[]{
                                "Download",
                                "Delete For Me"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                    holder.itemView.getContext().startActivity(intent);
                                }else if (i == 1){
                                    deleteReceivedMessages(position, holder);
                                }else if (i == 2){

                                }
                            }
                        }).show();
                    }else if (userMessagesList.get(position).getType().equals("text")){
                        CharSequence options[] = new CharSequence[]{
                                "Delete For Me"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    deleteReceivedMessages(position, holder);
                                }
                            }
                        }).show();
                    }else if (userMessagesList.get(position).getType().equals("image")){
                        CharSequence options[] = new CharSequence[]{
                                "View Image",
                                "Delete For Me"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                        builder.setTitle("Delete Message?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0){
                                    Intent intent = new Intent(context, ImageViewActivity.class);
                                    intent.putExtra("url", userMessagesList.get(position).getMessage());
                                    context.startActivity(intent);
                                }else if (i == 1){
                                    deleteReceivedMessages(position, holder);
                                }
                            }
                        }).show();
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class messageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileIv;
        public ImageView senderMsgImageView, receiverMsgImageView;

        public messageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.senderMsgText);
            receiverMessageText = itemView.findViewById(R.id.receiverMsgText);
            receiverProfileIv = itemView.findViewById(R.id.profileIv);
            receiverMsgImageView = itemView.findViewById(R.id.receiverMsgImageView);
            senderMsgImageView = itemView.findViewById(R.id.senderMsgImageView);
        }
    }
    private void deleteSentMessages(final int position, final messageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Message Deleted!!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Error Deleting Message!!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void deleteReceivedMessages(final int position, final messageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Message Deleted!!!!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Error Deleting Message!!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void deleteMessageEveryone(final int position, final messageViewHolder holder){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            rootRef.child("Messages")
                                    .child(userMessagesList.get(position).getFrom())
                                    .child(userMessagesList.get(position).getTo())
                                    .child(userMessagesList.get(position).getMessageId())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(context, "Message deleted for Everyone!!!!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(context, "Error deleting!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(context, "Error Deleting Message!!!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
