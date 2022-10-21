package kashyap.anurag.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.whatsappclone.databinding.ActivityPhoneLoginBinding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private ActivityPhoneLoginBinding binding;
    private String phoneNumber, verificationId, otp;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        phoneNumber = binding.phoneEt.getText().toString().trim();
        if (phoneNumber.length() < 10) {
            Toast.makeText(this, "Enter your Phone Number!!!", Toast.LENGTH_SHORT).show();
        } else {
            sendOtp(phoneNumber);
        }
        binding.verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOtp();
            }
        });
    }

    private void sendOtp(String phoneNumber) {

        progressDialog.setMessage("Sending OTP...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+91" + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(PhoneLoginActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressDialog.dismiss();
                        Toast.makeText(PhoneLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.otpView.setVisibility(View.GONE);
                        binding.sendOtpBtn.setVisibility(View.VISIBLE);
                        binding.verifyOtpBtn.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        progressDialog.dismiss();
                        verificationId = verifyId;

                        binding.otpView.setVisibility(View.VISIBLE);
                        binding.sendOtpBtn.setVisibility(View.GONE);
                        binding.verifyOtpBtn.setVisibility(View.VISIBLE);
                        binding.phoneEt.setEnabled(false);

                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyOtp() {
        otp = binding.otpView.getText().toString().trim();
        if (otp.isEmpty()) {
            Toast.makeText(PhoneLoginActivity.this, "Enter otp!!!", Toast.LENGTH_SHORT).show();
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                       checkUserNameExist();
                        Toast.makeText(PhoneLoginActivity.this, "Otp Verified.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(PhoneLoginActivity.this, "Otp Doesn't matches", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    private void checkUserNameExist() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("userName").exists()){

                            Toast.makeText(PhoneLoginActivity.this, "Welcome!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
                            progressDialog.dismiss();

                        }else {
                            startActivity(new Intent(PhoneLoginActivity.this, SettingsActivity.class));
                            finishAffinity();
                            progressDialog.dismiss();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}