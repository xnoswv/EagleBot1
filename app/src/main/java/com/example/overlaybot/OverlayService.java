package com.example.overlaybot;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.Nullable;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class OverlayService extends Service {

    private WindowManager windowManager;
    private View overlayView;
    private Button buyButton, sellButton;
    private CoordinatesManager cm;
    private WebSocketClient ws;

    @Override
    public void onCreate() {
        super.onCreate();
        cm = new CoordinatesManager(this);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(this);
        overlayView = inflater.inflate(R.layout.overlay_layout, null);

        buyButton = overlayView.findViewById(R.id.buyButton);
        sellButton = overlayView.findViewById(R.id.sellButton);

        int[] buyCoords = cm.load("BUY");
        int[] sellCoords = cm.load("SELL");

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = buyCoords != null ? buyCoords[0] : 0;
        params.y = buyCoords != null ? buyCoords[1] : 100;

        overlayView.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                params.x = (int) event.getRawX() - overlayView.getWidth()/2;
                params.y = (int) event.getRawY() - overlayView.getHeight()/2;
                windowManager.updateViewLayout(overlayView, params);
                return true;
            }
            return false;
        });

        windowManager.addView(overlayView, params);

        try {
            ws = new WebSocketClient(new URI("ws://YOUR_BOT_IP:PORT")) {
                @Override public void onOpen(ServerHandshake handshakedata) {}
                @Override public void onMessage(String message) {
                    if(message.equalsIgnoreCase("BUY")){
                        buyButton.performClick();
                    } else if(message.equalsIgnoreCase("SELL")){
                        sellButton.performClick();
                    }
                }
                @Override public void onClose(int code, String reason, boolean remote) {}
                @Override public void onError(Exception ex) {}
            };
            ws.connect();
        } catch(Exception e){ e.printStackTrace(); }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) windowManager.removeView(overlayView);
        if(ws != null) ws.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
