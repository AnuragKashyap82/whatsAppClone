package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.ProfileActivity;
import kashyap.anurag.whatsappclone.R;

public class AdapterRequest extends RecyclerView.Adapter<AdapterRequest.holderViewRequest> {

    private Context context;
    private ArrayList<ModelUsers> requestArrayList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference chatRequestRef, contactRef;
    private String senderUserId;

    public AdapterRequest(Context context, ArrayList<ModelUsers> requestArrayList) {
        this.context = context;
        this.requestArrayList = requestArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
        chatRequestRef = FirebaseDatabase.getInstance().getReference("ChatRequest");
        contactRef = FirebaseDatabase.getInstance().getReference("Contacts");
        senderUserId = firebaseAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public holderViewRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_request_item, parent, false);
        return new holderViewRequest(view);


    }

    @Override
    public void onBindViewHolder(@NonNull holderViewRequest holder, int position) {
        final ModelUsers modelUsers = requestArrayList.get(position);
        String uid = modelUsers.getUid();

        loadRequestDetails(uid, holder);

        checkRequestSentReceived(uid, holder);

        holder.acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptChatRequest(uid);
            }
        });
        holder.declineRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelChatRequest(uid);
            }
        });
        holder.cancelRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelChatRequest(uid);
            }
        });
    }

    private void checkRequestSentReceived(String uid, holderViewRequest holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatRequest");
        databaseReference.child(firebaseAuth.getUid()).child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("requestType")){
                            String requestType = snapshot.child("requestType").getValue().toString();
                            if (requestType.equals("sent")){
                                holder.declineRequestBtn.setVisibility(View.GONE);
                                holder.acceptRequestBtn.setVisibility(View.GONE);
                                holder.cancelRequestBtn.setVisibility(View.VISIBLE);
                            }else if (requestType.equals("received")){
                                holder.declineRequestBtn.setVisibility(View.VISIBLE);
                                holder.acceptRequestBtn.setVisibility(View.VISIBLE);
                                holder.cancelRequestBtn.setVisibility(View.GONE);
                            }
                        }else {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadRequestDetails(String uid, holderViewRequest holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName1 = snapshot.child("userName").getValue().toString();
                        String userStatus1 = snapshot.child("userStatus").getValue().toString();
                        String profileImage = snapshot.child("profileImage").getValue().toString();

                        if (profileImage != null){
                            holder.userName.setText(userName1);
                            holder.userStatus.setText(userStatus1);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(holder.profileIv);
                            } catch (Exception e) {
                                holder.profileIv.setImageResource(R.drawable.ic_person_black);
                            }
                        }else {
                            holder.userName.setText(userName1);
                            holder.userStatus.setText(userStatus1);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void acceptChatRequest(String receiverUserId) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", receiverUserId);
        hashMap.put("Contacts", "Saved");

        contactRef.child(senderUserId).child(receiverUserId).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", senderUserId);
                            hashMap.put("Contacts", "Saved");

                            contactRef.child(receiverUserId).child(senderUserId).setValue(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                chatRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(context, "Request Accepted!!!!", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest(String receiverUserId) {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return requestArrayList.size();
    }

    public class holderViewRequest extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        ImageView profileIv;
        Button acceptRequestBtn, declineRequestBtn, cancelRequestBtn;

        public holderViewRequest(@NonNull View itemView) {
            super(itemView);


            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);
            profileIv = itemView.findViewById(R.id.profileIv);
            acceptRequestBtn = itemView.findViewById(R.id.acceptRequestBtn);
            declineRequestBtn = itemView.findViewById(R.id.declineRequestBtn);
            cancelRequestBtn = itemView.findViewById(R.id.cancelRequestBtn);
        }
    }
}
