package kashyap.anurag.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import de.hdodenhof.circleimageview.CircleImageView;
import kashyap.anurag.whatsappclone.Adapter.AdapterGroupMessage;
import kashyap.anurag.whatsappclone.Adapter.AdapterMessage;
import kashyap.anurag.whatsappclone.Adapter.AdapterUsers;
import kashyap.anurag.whatsappclone.Model.ModelGroupMessage;
import kashyap.anurag.whatsappclone.Model.ModelMessage;
import kashyap.anurag.whatsappclone.Model.ModelUsers;
import kashyap.anurag.whatsappclone.databinding.ActivityGroupChatBinding;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
    private ActivityGroupChatBinding binding;
    private Toolbar mToolbar;
    private String message, groupName, currentDate, currentTime;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference groupNameRef, groupMessageKeyRef;

    private LinearLayoutManager linearLayoutManager;
    private AdapterGroupMessage adapterGroupMessage;
    private ArrayList<ModelGroupMessage> groupMessageArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        groupName = getIntent().getStringExtra("groupName");

        groupNameRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

        adapterGroupMessage = new AdapterGroupMessage(this, groupMessageArrayList);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.groupChatsMsgList.setLayoutManager(linearLayoutManager);
        binding.groupChatsMsgList.setAdapter(adapterGroupMessage);
        loadAllMessages(groupName);

        mToolbar = (Toolbar) findViewById(R.id.groupChatBarLayout);
        userName = (TextView) findViewById(R.id.userName);
        userLastSeen = (TextView) findViewById(R.id.lastSeen);
        userImage = (CircleImageView) findViewById(R.id.profileIv);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(groupName);

        loadGroupDetails(groupName);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        binding.sendMsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                binding.inputMsgEt.setText("");
                binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }

    private void validateData() {
        message = binding.inputMsgEt.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Enter Message!!!!", Toast.LENGTH_SHORT).show();
        } else {
            sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        String messageKey = groupNameRef.push().getKey();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        HashMap<String, Object> groupMessageKey = new HashMap<>();
        groupNameRef.updateChildren(groupMessageKey);

        groupMessageKeyRef = groupNameRef.child("Messages").child(messageKey);

        HashMap<String, Object> messageInfoMap = new HashMap<>();
        messageInfoMap.put("message", "" + message);
        messageInfoMap.put("date", "" + currentDate);
        messageInfoMap.put("time", "" + currentTime);
        messageInfoMap.put("senderUid", "" + firebaseAuth.getCurrentUser().getUid());

        groupMessageKeyRef.updateChildren(messageInfoMap);

    }
    private void loadAllMessages(String groupName) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupName).child("Messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ModelGroupMessage modelGroupMessage = snapshot.getValue(ModelGroupMessage.class);
                        groupMessageArrayList.add(modelGroupMessage);
                        adapterGroupMessage.notifyDataSetChanged();
                        binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadGroupDetails(String groupName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String groupName = snapshot.child("groupName").getValue().toString();
                            String adminId = snapshot.child("adminId").getValue().toString();
                            String groupIcon = snapshot.child("groupIcon").getValue().toString();

                            userLastSeen.setText("To be Integrated!!!!");
                            userName.setText(groupName);
                            try {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.ic_person_black).into(userImage);
                            } catch (Exception e) {
                                userImage.setImageResource(R.drawable.ic_person_black);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}