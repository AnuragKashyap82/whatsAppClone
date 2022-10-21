package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.ProfileActivity;
import kashyap.anurag.whatsappclone.R;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.holderUsers> {

    private Context context;
    private ArrayList<ModelUsers> usersArrayList;

    public AdapterUsers(Context context, ArrayList<ModelUsers> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public holderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users_item, parent, false);
        return new holderUsers(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderUsers holder, int position) {
        final ModelUsers modelUsers = usersArrayList.get(position);
        String userName = modelUsers.getUserName();
        String userStatus = modelUsers.getUserStatus();
        String uid = modelUsers.getUid();
        String profileImage = modelUsers.getProfileImage();

        holder.userName.setText(userName);
        holder.userStatus.setText(userStatus);

        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(holder.profileIv);
        } catch (Exception e) {
            holder.profileIv.setImageResource(R.drawable.ic_person_black);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("receiverUserId", uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class holderUsers extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        ImageView profileIv;

        public holderUsers(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);
            profileIv = itemView.findViewById(R.id.profileIv);

        }
    }
}
