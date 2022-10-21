package kashyap.anurag.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.whatsappclone.databinding.ActivityProfileBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private String receiverUserId, currentState, senderUserId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference chatRequestRef, contactRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        receiverUserId = getIntent().getStringExtra("receiverUserId");
        loadUserDetails(receiverUserId);
        currentState = "new";

        chatRequestRef = FirebaseDatabase.getInstance().getReference("ChatRequest");
        contactRef = FirebaseDatabase.getInstance().getReference("Contacts");

        senderUserId = firebaseAuth.getCurrentUser().getUid();
    }

    private void loadUserDetails(String receiverUserId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(receiverUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userName = snapshot.child("userName").getValue().toString();
                            String userStatus = snapshot.child("userStatus").getValue().toString();
                            String profileImage = snapshot.child("profileImage").getValue().toString();

                            binding.userName.setText(userName);
                            binding.userStatus.setText(userStatus);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(binding.profileIv);
                            } catch (Exception e) {
                                binding.profileIv.setImageResource(R.drawable.ic_person_black);
                            }

                            ManageChatRequest();

                        } else {

                            ManageChatRequest();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void ManageChatRequest() {

        chatRequestRef.child(senderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverUserId)) {
                            String requestType = snapshot.child(receiverUserId).child("requestType").getValue().toString();
                            if (requestType.equals("sent")) {
                                currentState = "requestSent";
                                binding.requestBtn.setText("Cancel Chat Request");
                            } else if (requestType.equals("received")) {
                                currentState = "requestReceived";
                                binding.requestBtn.setText("Accept Chat Request");
                                binding.declineBtn.setVisibility(View.VISIBLE);
                                binding.declineBtn.setEnabled(true);
                                binding.declineBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cancelChatRequest();
                                    }
                                });
                            }
                        } else {
                            contactRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(receiverUserId)) {
                                                currentState = "friends";
                                                binding.requestBtn.setText("Remove Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (!senderUserId.equals(receiverUserId)) {
            binding.requestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.requestBtn.setEnabled(false);
                    if (currentState.equals("new")) {
                        sendChatRequest();
                    }
                    if (currentState.equals("requestSent")) {
                        cancelChatRequest();
                    }
                    if (currentState.equals("requestReceived")) {
                        acceptChatRequest();
                    }
                    if (currentState.equals("friends")) {
                        removeSpecificContact();
                    }else {

                    }
                }
            });
        } else {
            binding.requestBtn.setVisibility(View.GONE);
        }
    }

    private void removeSpecificContact() {
        contactRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.requestBtn.setEnabled(true);
                                                currentState = "new";
                                                binding.requestBtn.setText("Send Message");
                                                binding.declineBtn.setVisibility(View.GONE);
                                            }
                                            Toast.makeText(ProfileActivity.this, "Request Cancelled!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptChatRequest() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", receiverUserId);
        hashMap.put("Contacts", "Saved");

        contactRef.child(senderUserId).child(receiverUserId).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", senderUserId);
                            hashMap.put("Contacts", "Saved");

                            contactRef.child(receiverUserId).child(senderUserId).setValue(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                chatRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        binding.requestBtn.setVisibility(View.VISIBLE);
                                                                                        currentState = "friends";
                                                                                        binding.requestBtn.setText("Remove Contact");
                                                                                        binding.declineBtn.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.requestBtn.setEnabled(true);
                                                currentState = "new";
                                                binding.requestBtn.setText("Send Message");
                                                binding.declineBtn.setVisibility(View.GONE);
                                            }
                                            Toast.makeText(ProfileActivity.this, "Request Cancelled!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendChatRequest() {
        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("requestType", "sent");
        hashMap1.put("uid", receiverUserId);

        chatRequestRef.child(senderUserId).child(receiverUserId)
                .setValue(hashMap1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("requestType", "received");
                            hashMap.put("uid", senderUserId);

                            chatRequestRef.child(receiverUserId).child(senderUserId)
                                    .setValue(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.requestBtn.setEnabled(true);
                                                currentState = "requestSent";
                                                binding.requestBtn.setText("Cancel Chat Request");
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}