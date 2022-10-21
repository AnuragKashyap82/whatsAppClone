package kashyap.anurag.whatsappclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.whatsappclone.Adapter.AdapterChats;
import kashyap.anurag.whatsappclone.Adapter.AdapterFeeds;
import kashyap.anurag.whatsappclone.Adapter.AdapterGroup;
import kashyap.anurag.whatsappclone.Model.ModelFeeds;
import kashyap.anurag.whatsappclone.Model.ModelGroup;
import kashyap.anurag.whatsappclone.databinding.FragmentFeedsBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedsFragment extends Fragment {
    private FragmentFeedsBinding binding;
    private FirebaseAuth firebaseAuth;
    private AdapterFeeds adapterFeeds;
    private ArrayList<ModelFeeds> feedsArrayList;


    public FeedsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedsBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();

        loadAllFeeds();

        binding.createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreatePostActivity.class));
            }
        });

        return binding.getRoot();
    }

    private void loadAllFeeds() {
        feedsArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Feeds");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            feedsArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelFeeds modelFeeds = ds.getValue(ModelFeeds.class);
                                String uid = modelFeeds.getUid();
                                checkIsOnContact(uid, modelFeeds);
                            }
                        }else {
                            Toast.makeText(getContext(), "No Feeds", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIsOnContact(String uid, ModelFeeds modelFeeds) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");
        databaseReference.child(firebaseAuth.getUid()).child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            feedsArrayList.add(modelFeeds);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            binding.feedsRv.setLayoutManager(layoutManager);

                            binding.feedsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

                            adapterFeeds = new AdapterFeeds(requireContext(), feedsArrayList);
                            binding.feedsRv.setAdapter(adapterFeeds);
                        }else {
                            Toast.makeText(requireContext(), "Not on contact!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}