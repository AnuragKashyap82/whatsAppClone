package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
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
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.R;

public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.holderContacts> {

    private Context context;
    private ArrayList<ModelUsers> contactArrayList;

    public AdapterContacts(Context context, ArrayList<ModelUsers> contactArrayList) {
        this.context = context;
        this.contactArrayList = contactArrayList;
    }

    @NonNull
    @Override
    public holderContacts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users_item, parent, false);
        return new holderContacts(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderContacts holder, int position) {
        final ModelUsers modelUsers = contactArrayList.get(position);
        String uid = modelUsers.getUid();

        loadContactDetails(uid, holder);

    }

    private void loadContactDetails(String uid, holderContacts holder) {
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

                                }else if (state.equals("offline")){
                                    holder.onlineStatus.setVisibility(View.GONE);
                                }

                            }else {
                            }

                            if (profileImage == null){
                                holder.userName.setText(userName1);
                                holder.userStatus.setText(userStatus1);
                            }else {
                                holder.userName.setText(userName1);
                                holder.userStatus.setText(userStatus1);

                                try {
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(holder.profileIv);
                                } catch (Exception e) {
                                    holder.profileIv.setImageResource(R.drawable.ic_person_black);
                                }
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
        return contactArrayList.size();
    }

    public class holderContacts extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        ImageView profileIv, onlineStatus;

        public holderContacts(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);
            profileIv = itemView.findViewById(R.id.profileIv);
            onlineStatus = itemView.findViewById(R.id.onlineStatus);
        }
    }
}
