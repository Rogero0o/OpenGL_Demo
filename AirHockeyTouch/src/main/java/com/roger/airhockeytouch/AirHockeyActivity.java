package com.roger.airhockeytouch;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class AirHockeyActivity extends AppCompatActivity {

  private GLSurfaceView glSurfaceView;
  private boolean rendererSet = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    glSurfaceView = new GLSurfaceView(this);
    final ActivityManager activityManager =
        (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

    final AirHockeyRenderer airHockeyRenderer = new AirHockeyRenderer(this);

    final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
    if (supportsEs2) {
      glSurfaceView.setEGLContextClientVersion(2);
      glSurfaceView.setRenderer(airHockeyRenderer);
      rendererSet = true;
    } else {
      Toast.makeText(this, "Not support OpenGL2.0", Toast.LENGTH_SHORT).show();
      return;
    }

    glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent != null) {
          final float normalizedX = (motionEvent.getX() / (float) view.getWidth()) * 2 - 1;
          final float normalizedY = -((motionEvent.getY() / (float) view.getHeight()) * 2 - 1);

          if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            glSurfaceView.queueEvent(new Runnable() {
              @Override public void run() {
                airHockeyRenderer.handleTouchPress(normalizedX, normalizedY);
              }
            });
          } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            glSurfaceView.queueEvent(new Runnable() {
              @Override public void run() {
                airHockeyRenderer.handleTouchDrag(normalizedX, normalizedY);
              }
            });
          }
          return true;
        }

        return false;
      }
    });

    setContentView(glSurfaceView);
  }

  @Override protected void onResume() {
    super.onResume();
    if (rendererSet) {
      glSurfaceView.onResume();
    }
  }

  @Override protected void onPause() {
    super.onPause();
    if (rendererSet) {
      glSurfaceView.onPause();
    }
  }
}
