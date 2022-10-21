package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.whatsappclone.ChatsActivity;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.R;

public class AdapterChats extends RecyclerView.Adapter<AdapterChats.holderChats> {

    private Context context;
    private ArrayList<ModelUsers> chatsArrayList;

    public AdapterChats(Context context, ArrayList<ModelUsers> chatsArrayList) {
        this.context = context;
        this.chatsArrayList = chatsArrayList;
    }

    @NonNull
    @Override
    public holderChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chats_items, parent, false);
        return new holderChats(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderChats holder, int position) {
        final ModelUsers modelUsers = chatsArrayList.get(position);
        String uid = modelUsers.getUid();

        loadChatsDetails(uid, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatsActivity.class);
                intent.putExtra("messageReceiverId", uid);
                context.startActivity(intent);
            }
        });
    }

    private void loadChatsDetails(String uid, holderChats holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String userName1 = snapshot.child("userName").getValue().toString();
                            String userStatus1 = snapshot.child("userStatus").getValue().toString();
                            String profileImage = snapshot.child("profileImage").getValue().toString();

                            if (snapshot.child("userState").hasChild("state")){

                                String state = snapshot.child("userState").child("state").getValue().toString();
                                String date = snapshot.child("userState").child("date").getValue().toString();
                                String time = snapshot.child("userState").child("time").getValue().toString();

                                if (state.equals("online")){
                                    holder.onlineStatus.setVisibility(View.VISIBLE);
                                    holder.lastSeen.setText("online");

                                }else if (state.equals("offline")){
                                    holder.onlineStatus.setVisibility(View.GONE);
                                    holder.lastSeen.setText(time +"  "+date);
                                }

                            }else {
                                holder.lastSeen.setText("update kro sale!!!");
                            }

                            if (profileImage != null){
                                holder.userName.setText(userName1);


                                try {
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(holder.profileIv);
                                } catch (Exception e) {
                                    holder.profileIv.setImageResource(R.drawable.ic_person_black);
                                }
                            }else {
                                holder.userName.setText(userName1);

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
        return chatsArrayList.size();
    }

    public class holderChats extends RecyclerView.ViewHolder {

        TextView userName, userStatus, lastSeen;
        ImageView profileIv, onlineStatus;

        public holderChats(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);
            profileIv = itemView.findViewById(R.id.profileIv);
            lastSeen = itemView.findViewById(R.id.lastSeen);
            onlineStatus = itemView.findViewById(R.id.onlineStatus);

        }
    }
}
