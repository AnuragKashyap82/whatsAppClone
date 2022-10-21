package kashyap.anurag.whatsappclone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import kashyap.anurag.whatsappclone.Adapter.TabsAccessorAdapter;
import kashyap.anurag.whatsappclone.databinding.ActivityMainBinding;
import kashyap.anurag.whatsappclone.databinding.CreateGroupDialogBinding;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Toolbar mToolBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri image_uri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mToolBar = (Toolbar) findViewById(R.id.mainPageToolbar);
        viewPager = (ViewPager) findViewById(R.id.main_tabs_pager);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("WhatsApp");

        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAccessorAdapter);

        tabLayout = (TabLayout) findViewById(R.id.mainTab);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String currentUser = firebaseAuth.getUid();
        if (currentUser != null){
         updateUserStatus("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String currentUser = firebaseAuth.getUid();
        if (currentUser != null){
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String currentUser = firebaseAuth.getUid();
        if (currentUser != null){
            updateUserStatus("offline");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options__menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId() == R.id.logoutBtn){

             progressDialog.setTitle("Logging out");
             progressDialog.setMessage("Please Wait!!!");
             progressDialog.setCanceledOnTouchOutside(false);
             progressDialog.show();

             updateUserStatus("offline");
             firebaseAuth.signOut();
             startActivity(new Intent(MainActivity.this, LoginActivity.class));
             finishAffinity();

         }else if (item.getItemId() == R.id.settingBtn){

             startActivity(new Intent(MainActivity.this, SettingsActivity.class));

         }else if (item.getItemId() == R.id.createGroupBtn){

             CreateGroupDialogBinding createGroup = CreateGroupDialogBinding.inflate(LayoutInflater.from(MainActivity.this));

             android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
             builder.setView(createGroup.getRoot());

             AlertDialog alertDialog = builder.create();
             alertDialog.show();

             createGroup.backBtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     alertDialog.dismiss();
                 }
             });
             createGroup.groupIcon.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent intent = new Intent(Intent.ACTION_PICK);
                     intent.setType("image/*");
                     galleryActivityResultLauncher.launch(intent);
                 }
             });
             if (image_uri != null){
                 createGroup.groupIcon.setImageURI(image_uri);
             }
             createGroup.createGrpBtn.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     String groupName = createGroup.groupNameEt.getText().toString().trim();

                     if (TextUtils.isEmpty(groupName)) {
                         Toast.makeText(MainActivity.this, "Enter Group Name!!!", Toast.LENGTH_SHORT).show();
                     } else if(image_uri == null){
                         alertDialog.dismiss();
                         createGroupNoIcon(groupName);
                     }else {
                         alertDialog.dismiss();
                         createNewGroup(groupName, image_uri);
                     }
                 }
             });


         }else if (item.getItemId() == R.id.findFriendBtn){
             startActivity(new Intent(MainActivity.this, FindFriendActivity.class));
         }
         return  true;
    }

    private void createGroupNoIcon(String groupName) {
        progressDialog.setTitle("Creating Group: "+groupName);
        progressDialog.setMessage("Please Wait!!!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("groupName", ""+groupName);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("groupIcon", "");
        hashMap.put("adminId", ""+firebaseAuth.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.child(groupName)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, groupName+"\nGroup Created Successfully!!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void createNewGroup(String groupName, Uri image_uri) {

        progressDialog.setTitle("Creating Group: "+groupName);
        progressDialog.setMessage("Please Wait!!!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String filePathAndName = "profile_images/" + "" + groupName;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadImageUri = uriTask.getResult();

                        if (uriTask.isSuccessful()) {

                            long timestamp = System.currentTimeMillis();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("groupName", ""+groupName);
                            hashMap.put("timestamp", ""+timestamp);
                            hashMap.put("groupIcon", ""+downloadImageUri);
                            hashMap.put("adminId", ""+firebaseAuth.getUid());

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
                            databaseReference.child(groupName)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, groupName+"\nGroup Created Successfully!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateUserStatus(String state){
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate =  Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime =  Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTimeFormat.format(calForTime.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("state", state);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userState")
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                    }
                });
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        image_uri = data.getData();

//                        binding.profileIv.setImageURI(image_uri);
                    } else {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}