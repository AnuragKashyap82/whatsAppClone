package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
import android.media.Image;
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

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import kashyap.anurag.whatsappclone.Model.ModelGroupMessage;
import kashyap.anurag.whatsappclone.R;

public class AdapterGroupMessage extends RecyclerView.Adapter<AdapterGroupMessage.holderGroupMessage> {

    private Context context;
    private ArrayList<ModelGroupMessage> groupMessageArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterGroupMessage(Context context, ArrayList<ModelGroupMessage> groupMessageArrayList) {
        this.context = context;
        this.groupMessageArrayList = groupMessageArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public holderGroupMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_group_message, parent, false);
        return new holderGroupMessage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderGroupMessage holder, int position) {
        final  ModelGroupMessage modelGroupMessage = groupMessageArrayList.get(position);
        String messageSenderId = modelGroupMessage.getSenderUid();
        String message = modelGroupMessage.getMessage();
        String time = modelGroupMessage.getTime();
        String date = modelGroupMessage.getDate();

        String fromUserId = firebaseAuth.getCurrentUser().getUid();

        holder.receiverMsgText.setVisibility(View.GONE);
        holder.profileIv.setVisibility(View.GONE);
        holder.senderMsgText.setVisibility(View.GONE);
        holder.userName.setVisibility(View.GONE);

        if (fromUserId.equals(messageSenderId)){

            holder.senderMsgText.setVisibility(View.VISIBLE);
            holder.senderMsgText.setBackgroundResource(R.drawable.sender_message_layout);
            holder.senderMsgText.setText(message + "\n \n" + time + " - " + date);
        }else {

            holder.profileIv.setVisibility(View.VISIBLE);
            holder.receiverMsgText.setVisibility(View.VISIBLE);
            holder.userName.setVisibility(View.VISIBLE);
            holder.receiverMsgText.setBackgroundResource(R.drawable.receiver_message_layout);
            holder.receiverMsgText.setText(message + "\n \n" + time + " - " + date);
        }

        loadSederDetails(holder, messageSenderId);
    }

    private void loadSederDetails(holderGroupMessage holder, String messageSenderId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(messageSenderId)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String userName1 = snapshot.child("userName").getValue().toString();
                    String profileImage = snapshot.child("profileImage").getValue().toString();

                    holder.userName.setText(userName1);
                    try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(holder.profileIv);
                    } catch (Exception e) {
                        holder.profileIv.setImageResource(R.drawable.ic_person_black);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupMessageArrayList.size();
    }

    public class holderGroupMessage extends RecyclerView.ViewHolder {

        CircleImageView profileIv;
        TextView userName, receiverMsgText, senderMsgText;

        public holderGroupMessage(@NonNull View itemView) {
            super(itemView);

            profileIv  = itemView.findViewById(R.id.profileIv);
            receiverMsgText  = itemView.findViewById(R.id.receiverMsgText);
            senderMsgText  = itemView.findViewById(R.id.senderMsgText);
            userName  = itemView.findViewById(R.id.userName);
        }
    }
}
