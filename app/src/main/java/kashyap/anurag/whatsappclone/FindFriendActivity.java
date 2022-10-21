package kashyap.anurag.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.whatsappclone.Adapter.AdapterGroup;
import kashyap.anurag.whatsappclone.Adapter.AdapterUsers;
import kashyap.anurag.whatsappclone.Model.ModelGroup;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.databinding.ActivityFindFriendBinding;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendActivity extends AppCompatActivity {
    private ActivityFindFriendBinding binding;
    private AdapterUsers adapterUsers;
    private ArrayList<ModelUsers> usersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadAllUsers();
    }

    private void loadAllUsers() {
        usersArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            usersArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                usersArrayList.add(modelUsers);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(FindFriendActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.usersRv.setLayoutManager(layoutManager);

                            binding.usersRv.setLayoutManager(new LinearLayoutManager(FindFriendActivity.this));

                            adapterUsers = new AdapterUsers(FindFriendActivity.this, usersArrayList);
                            binding.usersRv.setAdapter(adapterUsers);
                        }else {
                            Toast.makeText(FindFriendActivity.this, "", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}