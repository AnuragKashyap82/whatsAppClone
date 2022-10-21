package kashyap.anurag.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.whatsappclone.databinding.ActivityImageViewBinding;

import android.os.Bundle;

import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {
    private ActivityImageViewBinding binding;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageUrl = getIntent().getStringExtra("url");


        try {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_person_black).into(binding.imageViewer);
        } catch (Exception e) {
            binding.imageViewer.setImageResource(R.drawable.ic_person_black);
        }

    }
}