package kashyap.anurag.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.whatsappclone.Adapter.AdapterContacts;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.databinding.FragmentContactsBinding;

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


public class ContactsFragment extends Fragment {
    private FragmentContactsBinding binding;

    private AdapterContacts adapterContacts;
    private ArrayList<ModelUsers> contactArrayList;

    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllContacts();

        return binding.getRoot();
    }
    private void loadAllContacts() {
        contactArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Contacts");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String uid = snapshot.getKey();
                            contactArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                contactArrayList.add(modelUsers);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            binding.contactRv.setLayoutManager(layoutManager);

                            binding.contactRv.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapterContacts = new AdapterContacts(getContext(), contactArrayList);
                            binding.contactRv.setAdapter(adapterContacts);

                        }else {
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}