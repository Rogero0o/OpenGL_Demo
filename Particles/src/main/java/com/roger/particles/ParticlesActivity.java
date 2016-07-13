package com.roger.particles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
