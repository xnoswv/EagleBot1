package com.example.overlaybot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // طلب صلاحية Overlay
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }

        // بدء OverlayService عند الضغط
        Button startOverlay = findViewById(R.id.startOverlay);
        startOverlay.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, OverlayService.class);
            startService(serviceIntent);
        });
    }
}
