package kashyap.anurag.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.whatsappclone.Adapter.AdapterChats;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.databinding.FragmentChatsBinding;

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


public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;
    private FirebaseAuth firebaseAuth;

    private AdapterChats adapterChats;
    private ArrayList<ModelUsers> chatsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllContacts();

        return binding.getRoot();
    }
    private void loadAllContacts() {
        chatsArrayList = new ArrayList<>();
        String uid  =  firebaseAuth.getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");
        databaseReference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            chatsArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                chatsArrayList.add(modelUsers);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            binding.chatListRv.setLayoutManager(layoutManager);

                            binding.chatListRv.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapterChats = new AdapterChats(getContext(), chatsArrayList);
                            binding.chatListRv.setAdapter(adapterChats);
                        }else {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}