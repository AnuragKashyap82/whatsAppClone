package kashyap.anurag.whatsappclone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import kashyap.anurag.whatsappclone.Adapter.AdapterMessage;
import kashyap.anurag.whatsappclone.Model.ModelMessage;
import kashyap.anurag.whatsappclone.databinding.ActivityChatsBinding;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {
    private ActivityChatsBinding binding;
    private String messageReceiverId, message, messageSenderId;
    private Toolbar chatToolBar;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;

    private ArrayList<ModelMessage> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AdapterMessage adapterMessage;
    private RecyclerView privateChatsMsgList;
    private ProgressDialog progressDialog;

    private String saveCurrentDate, saveCurrentTime;
    private String checker = "", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        messageSenderId = firebaseAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getStringExtra("messageReceiverId");
        loadReceiverDetails(messageReceiverId);

        chatToolBar = (Toolbar) findViewById(R.id.chatToolBar);
        userName = (TextView) findViewById(R.id.userName);
        userImage = (CircleImageView) findViewById(R.id.profileIv);
        userLastSeen = (TextView) findViewById(R.id.lastSeen);

        adapterMessage = new AdapterMessage(this, messagesList);
        privateChatsMsgList = (RecyclerView) findViewById(R.id.privateChatsMsgList);
        linearLayoutManager = new LinearLayoutManager(this);
        privateChatsMsgList.setLayoutManager(linearLayoutManager);
        privateChatsMsgList.setAdapter(adapterMessage);

        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        loadAllMessages();
        displayLastSeen();

        progressDialog = new ProgressDialog(this);

        Calendar calForDate =  Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime =  Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTimeFormat.format(calForTime.getTime());

        binding.sendMsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence  options[] = new CharSequence[]{

                        "Images",
                        "PDF Files",
                        "Ms Words Files"

                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatsActivity.this);
                builder.setTitle("Select the File");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            checker = "image";

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            galleryActivityResultLauncher.launch(intent);

                        }else if (i == 1){
                            checker = "pdf";

                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            galleryActivityResultLauncher.launch(intent);

                        }else if (i == 2){
                            checker = "docx";

                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            galleryActivityResultLauncher.launch(intent);
                        }
                    }
                }).show();
            }
        });

    }

    private void loadAllMessages() {

        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ModelMessage modelMessage = snapshot.getValue(ModelMessage.class);
                        messagesList.add(modelMessage);
                        adapterMessage.notifyDataSetChanged();
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

    private void validateData() {
        message = binding.inputMsgEt.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Enter Your Message!!!", Toast.LENGTH_SHORT).show();
        } else {
            sendMessage();
        }
    }

    private void sendMessage() {
        String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
        String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

        DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                .child(messageSenderId).child(messageReceiverId).push();

        String messagePushId = userMessageKeyRef.getKey();

        Map messageTextBody = new HashMap();
        messageTextBody.put("message", message);
        messageTextBody.put("type", "text");
        messageTextBody.put("from", messageSenderId);
        messageTextBody.put("to", messageReceiverId);
        messageTextBody.put("messageId", messagePushId);
        messageTextBody.put("time", saveCurrentTime);
        messageTextBody.put("date", saveCurrentDate);

        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
        messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

        rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChatsActivity.this, "Message Sent!!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                binding.inputMsgEt.setText("");
            }
        });
    }

    private void loadReceiverDetails(String messageReceiverId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(messageReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userName1 = snapshot.child("userName").getValue().toString();
                        String userStatus1 = snapshot.child("userStatus").getValue().toString();
                        String profileImage = snapshot.child("profileImage").getValue().toString();

                        userName.setText(userName1);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_black).into(userImage);
                        } catch (Exception e) {
                            userImage.setImageResource(R.drawable.ic_person_black);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void displayLastSeen() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(messageReceiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("userState").hasChild("state")) {

                                String state = snapshot.child("userState").child("state").getValue().toString();
                                String date = snapshot.child("userState").child("date").getValue().toString();
                                String time = snapshot.child("userState").child("time").getValue().toString();

                                if (state.equals("online")) {
                                    userLastSeen.setText("online");
                                } else if (state.equals("offline")) {
                                    userLastSeen.setText(time + "  " + date);
                                }

                            } else {
                                userLastSeen.setText("update kro sale!!!");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        progressDialog.setTitle("Sending File");
                        progressDialog.setMessage("Please Wait!!!!");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        Intent data = result.getData();
                        fileUri = data.getData();

                        if (!checker.equals("image")){

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Documents Files");

                            String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
                            String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

                            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                                    .child(messageSenderId).child(messageReceiverId).push();

                            String messagePushId = userMessageKeyRef.getKey();
                            StorageReference filePath = storageReference.child(messagePushId + "." + checker);

                            uploadTask = filePath.putFile(fileUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!uriTask.isSuccessful()) ;
                                            myUrl = "" + uriTask.getResult();

                                            Map messageTextBody = new HashMap();
                                            messageTextBody.put("message", myUrl);
                                            messageTextBody.put("name", fileUri.getLastPathSegment());
                                            messageTextBody.put("type", checker);
                                            messageTextBody.put("from", messageSenderId);
                                            messageTextBody.put("to", messageReceiverId);
                                            messageTextBody.put("messageId", messagePushId);
                                            messageTextBody.put("time", saveCurrentTime);
                                            messageTextBody.put("date", saveCurrentDate);

                                            Map messageBodyDetails = new HashMap();
                                            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
                                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);
                                            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ChatsActivity.this, "Message Sent!!!!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ChatsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                    binding.inputMsgEt.setText("");
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ChatsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            double p = (100.0*snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                            progressDialog.setMessage((int) p+ "% uploading");
                                        }
                                    });

                        }else if (checker.equals("image")){
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                            String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
                            String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

                            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                                    .child(messageSenderId).child(messageReceiverId).push();

                            String messagePushId = userMessageKeyRef.getKey();
                            StorageReference filePath = storageReference.child(messagePushId + "." + "jpg");
                            uploadTask = filePath.putFile(fileUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!uriTask.isSuccessful()) ;
                                            myUrl = "" + uriTask.getResult();

                                            Map messageTextBody = new HashMap();
                                            messageTextBody.put("message", myUrl);
                                            messageTextBody.put("name", fileUri.getLastPathSegment());
                                            messageTextBody.put("type", checker);
                                            messageTextBody.put("from", messageSenderId);
                                            messageTextBody.put("to", messageReceiverId);
                                            messageTextBody.put("messageId", messagePushId);
                                            messageTextBody.put("time", saveCurrentTime);
                                            messageTextBody.put("date", saveCurrentDate);

                                            Map messageBodyDetails = new HashMap();
                                            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
                                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

                                            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ChatsActivity.this, "Message Sent!!!!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ChatsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                    binding.inputMsgEt.setText("");
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ChatsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }else {
                            Toast.makeText(ChatsActivity.this, "cancelled!!!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(ChatsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}