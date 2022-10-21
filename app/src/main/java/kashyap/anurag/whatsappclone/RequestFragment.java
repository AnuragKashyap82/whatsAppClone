package kashyap.anurag.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.whatsappclone.Adapter.AdapterContacts;
import kashyap.anurag.whatsappclone.Adapter.AdapterRequest;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.databinding.FragmentRequestBinding;

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

public class RequestFragment extends Fragment {
    private FragmentRequestBinding binding;
    private AdapterRequest adapterRequest;
    private ArrayList<ModelUsers> requestArrayList;

    private FirebaseAuth firebaseAuth;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(getLayoutInflater());


        firebaseAuth = FirebaseAuth.getInstance();
        loadAllRequest();

        return binding.getRoot();
    }
    private void loadAllRequest() {
        requestArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatRequest");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String uid = snapshot.getKey();
                            requestArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                                requestArrayList.add(modelUsers);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            binding.requestRv.setLayoutManager(layoutManager);

                            binding.requestRv.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapterRequest = new AdapterRequest(getContext(), requestArrayList);
                            binding.requestRv.setAdapter(adapterRequest);

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