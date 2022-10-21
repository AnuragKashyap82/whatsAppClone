package kashyap.anurag.whatsappclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.whatsappclone.Adapter.AdapterGroup;
import kashyap.anurag.whatsappclone.Model.ModelGroup;
import kashyap.anurag.whatsappclone.databinding.FragmentGroupsBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class GroupsFragment extends Fragment {
    private FragmentGroupsBinding binding;
    private AdapterGroup adapterGroup;
    private ArrayList<ModelGroup> groupArrayList;

    public GroupsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGroupsBinding.inflate(getLayoutInflater());

        loadAllGroups();

       return binding.getRoot();
    }

    private void loadAllGroups() {
        groupArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            groupArrayList.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelGroup modelGroup = ds.getValue(ModelGroup.class);
                                groupArrayList.add(modelGroup);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            binding.groupRv.setLayoutManager(layoutManager);

                            binding.groupRv.setLayoutManager(new LinearLayoutManager(requireContext()));

                            adapterGroup = new AdapterGroup(requireContext(), groupArrayList);
                            binding.groupRv.setAdapter(adapterGroup);
                        }else {
                            Toast.makeText(getContext(), "No Groups!!!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}