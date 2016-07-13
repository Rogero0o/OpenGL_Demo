package com.roger.skybox;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ParticlesActivity extends AppCompatActivity {

  private GLSurfaceView glSurfaceView;
  private boolean rendererSet = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    glSurfaceView = new GLSurfaceView(this);
    final ActivityManager activityManager =
        (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

    final ParticlesRenderer particlesRenderer = new ParticlesRenderer(this);

    final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
    if (supportsEs2) {
      glSurfaceView.setEGLContextClientVersion(2);
      glSurfaceView.setRenderer(particlesRenderer);
      rendererSet = true;
    } else {
      Toast.makeText(this, "Not support OpenGL2.0", Toast.LENGTH_SHORT).show();
      return;
    }
    glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
      float previousX, previousY;

      @Override public boolean onTouch(View v, MotionEvent event) {
        if (event != null) {
          if (event.getAction() == MotionEvent.ACTION_DOWN) {
            previousX = event.getX();
            previousY = event.getY();
          } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            final float deltaX = event.getX() - previousX;
            final float deltaY = event.getY() - previousY;

            previousX = event.getX();
            previousY = event.getY();

            glSurfaceView.queueEvent(new Runnable() {
              @Override public void run() {
                particlesRenderer.handleTouchDrag(deltaX, deltaY);
              }
            });
          }

          return true;
        } else {
          return false;
        }
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
