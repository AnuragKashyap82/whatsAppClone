package kashyap.anurag.whatsappclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import kashyap.anurag.whatsappclone.GroupChatActivity;
import kashyap.anurag.whatsappclone.Model.ModelGroup;
import kashyap.anurag.whatsappclone.R;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.holderGroup> {

    private Context context;
    private ArrayList<ModelGroup> groupArrayList;

    public AdapterGroup(Context context, ArrayList<ModelGroup> groupArrayList) {
        this.context = context;
        this.groupArrayList = groupArrayList;
    }

    @NonNull
    @Override
    public holderGroup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_group_item, parent, false);
        return new holderGroup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderGroup holder, int position) {
        final ModelGroup modelGroup = groupArrayList.get(position);
        String groupName = modelGroup.getGroupName();
        String timestamp = modelGroup.getTimestamp();
        String groupIcon = modelGroup.getGroupIcon();
        String adminId = modelGroup.getAdminId();

        holder.groupName.setText(groupName);

        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_person_black).into(holder.profileIv);
        } catch (Exception e) {
            holder.profileIv.setImageResource(R.drawable.ic_person_black);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupName", groupName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupArrayList.size();
    }

    public class holderGroup extends RecyclerView.ViewHolder {

        private TextView groupName;
        private CircleImageView profileIv;

        public holderGroup(@NonNull View itemView) {
            super(itemView);

            groupName  = itemView.findViewById(R.id.groupName);
            profileIv  = itemView.findViewById(R.id.profileIv);
        }
    }
}
