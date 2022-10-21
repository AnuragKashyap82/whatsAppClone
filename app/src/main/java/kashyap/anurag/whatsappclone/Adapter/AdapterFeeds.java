package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import kashyap.anurag.whatsappclone.Model.ModelFeeds;
import kashyap.anurag.whatsappclone.R;

public class AdapterFeeds extends RecyclerView.Adapter<AdapterFeeds.HolderFeeds> {

    private Context context;
    private ArrayList<ModelFeeds> feedsArrayList;


    public AdapterFeeds(Context context, ArrayList<ModelFeeds> feedsArrayList) {
        this.context = context;
        this.feedsArrayList = feedsArrayList;

    }

    @NonNull
    @Override
    public HolderFeeds onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_feeds_item, parent, false);
        return new HolderFeeds(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFeeds holder, int position) {

        final ModelFeeds modelFeeds = feedsArrayList.get(position);
        String uid = modelFeeds.getUid();
        String postImage = modelFeeds.getPostImage();
        String postId = modelFeeds.getPostId();
        String caption = modelFeeds.getCaption();

        try {
            Picasso.get().load(postImage).placeholder(R.drawable.ic_person_black).into(holder.postImage);
        } catch (Exception e) {
            holder.postImage.setImageResource(R.drawable.ic_person_black);
        }
        holder.captionTv.setText(caption);
        loadSenderDetails(holder, uid);
    }

    private void loadSenderDetails(HolderFeeds holder, String uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.child("userName").getValue().toString();
                        String profileImage = snapshot.child("profileImage").getValue().toString();

                        holder.userNameTv.setText(userName);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(holder.profileIv);
                        } catch (Exception e) {
                            holder.profileIv.setImageResource(R.drawable.ic_person_black);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return feedsArrayList.size();
    }

    public class HolderFeeds extends RecyclerView.ViewHolder {

        private CircleImageView profileIv;
        private TextView userNameTv, captionTv;
        private ImageView moreBtn, postImage, likeBtn;

        public HolderFeeds(@NonNull View itemView) {
            super(itemView);


            profileIv = itemView.findViewById(R.id.profileIv);
            userNameTv = itemView.findViewById(R.id.userNameTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            postImage = itemView.findViewById(R.id.postImage);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            captionTv = itemView.findViewById(R.id.captionTv);
        }
    }
}
