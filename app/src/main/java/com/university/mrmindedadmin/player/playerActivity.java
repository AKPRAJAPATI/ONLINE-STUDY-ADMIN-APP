package com.university.mrmindedadmin.player;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ActivityPlayerBinding;

public class playerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    private String videoUrl;
    private String videoName;
    private Activity activity;

    PlayerView playerView;
    ImageView skipping;
    boolean isFullScreen = true;
    SimpleExoPlayer player;
    ProgressBar progressBar;
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        videoUrl = getIntent().getStringExtra("url");
        videoName = getIntent().getStringExtra("name");
        binding.videoName.setText(videoName);

        binding.fullscreenVideoView.videoUrl(videoUrl).
                addSeekBackwardButton().
                addSeekForwardButton().
                enableAutoStart().
                playDrawable(bg.devlabs.fullscreenvideoview.R.drawable.ic_play_arrow_white_48dp).
                pauseDrawable(bg.devlabs.fullscreenvideoview.R.drawable.ic_pause_white_48dp).
                fastForwardDrawable(R.drawable.ic_baseline_fast_forward_24).
                fastForwardSeconds(10).
                rewindDrawable(R.drawable.ic_baseline_fast_rewind_24).
                rewindSeconds(10)
                .enterFullscreenDrawable(bg.devlabs.fullscreenvideoview.R.drawable.ic_fullscreen_white_48dp)
                .exitFullscreenDrawable(bg.devlabs.fullscreenvideoview.R.drawable.ic_fullscreen_white_48dp);




        }
}