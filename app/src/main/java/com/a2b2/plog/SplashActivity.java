package com.a2b2.plog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import android.media.MediaPlayer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.vectormap.KakaoMapSdk;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // KakaoMap SDK 초기화
        KakaoMapSdk.init(this, "1b96fc67568f72bcc29317e838ad740f");

        videoView = findViewById(R.id.video_view);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_fullscreen);
        videoView.setVideoURI(videoUri);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                overridePendingTransition(0, 0);
                startActivity(intent);
                finish();
            }
        });

        videoView.start();
    }
}
