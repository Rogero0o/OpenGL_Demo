package com.roger.livewallpaper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.roger.livewallpaper.objects.Heightmap;
import com.roger.livewallpaper.objects.ParticleShooter;
import com.roger.livewallpaper.objects.ParticleSystem;
import com.roger.livewallpaper.objects.Skybox;
import com.roger.livewallpaper.programs.HeightmapShaderProgram;
import com.roger.livewallpaper.programs.ParticleShaderProgram;
import com.roger.livewallpaper.programs.SkyboxShaderProgram;
import com.roger.livewallpaper.util.Geometry;
import com.roger.livewallpaper.util.Geometry.Point;
import com.roger.livewallpaper.util.Geometry.Vector;
import com.roger.livewallpaper.util.MatrixHelper;
import com.roger.livewallpaper.util.TextureHelper;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ParticlesRenderer implements GLSurfaceView.Renderer {

  private final Context context;

  private final float[] modelMatrix = new float[16];
  private final float[] viewMatrix = new float[16];
  private final float[] viewMatrixForSkybox = new float[16];
  private final float[] projectionMatrix = new float[16];

  private final float[] tempMatrix = new float[16];
  private final float[] modelViewMatrix = new float[16];
  private final float[] it_modelViewMatrix = new float[16];
  private final float[] modelViewProjectionMatrix = new float[16];

  private HeightmapShaderProgram heightmapProgram;
  private Heightmap heightmap;
  //private final float[] projectionMatrix = new float[16];
  //private final float[] viewMatrix = new float[16];
  //private final float[] viewProjectionMatrix = new float[16];

  final float[] vectorToLight = { 0.30f, 0.35f, -0.89f, 0f };

  private final float[] pointLightPositions = new float[] {
      -1f, 1f, 0f, 1f, 0f, 1f, 0f, 1f, 1f, 1f, 0f, 1f
  };

  private final float[] pointLightColors = new float[] {
      1.00f, 0.20f, 0.02f, 0.02f, 0.25f, 0.02f, 0.02f, 0.20f, 1.00f
  };

  private SkyboxShaderProgram skyboxProgram;
  private Skybox skybox;
    /*
    // Maximum saturation and value.
    private final float[] hsv = {0f, 1f, 1f};*/

  private ParticleShaderProgram particleProgram;
  private ParticleSystem particleSystem;
  private ParticleShooter redParticleShooter;
  private ParticleShooter greenParticleShooter;
  private ParticleShooter blueParticleShooter;
  /*private ParticleFireworksExplosion particleFireworksExplosion;
  private Random random;*/
  private long globalStartTime;
  private int skyboxTexture;
  private int particleTexture;

  private float xRotation, yRotation;

  private float xOffset, yOffset;

  private long frameStartTimeMs;
  private long startTimeMs;
  private int frameCount;

  public ParticlesRenderer(Context context) {
    this.context = context;
  }

  public void handleTouchDrag(float deltaX, float deltaY) {
    xRotation += deltaX / 16f;
    yRotation += deltaY / 16f;

    if (yRotation < -90) {
      yRotation = -90;
    } else if (yRotation > 90) {
      yRotation = 90;
    }

    // Setup view matrix
    updateViewMatrices();
  }

  public void handleOffsetsChanged(float xOffset, float yOffset) {
    // Offsets range from 0 to 1.
    this.xOffset = (xOffset - 0.5f) * 2.5f;
    this.yOffset = (yOffset - 0.5f) * 2.5f;
    updateViewMatrices();
  }

  private void updateViewMatrices() {
    setIdentityM(viewMatrix, 0);
    rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
    rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
    System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.length);

    // We want the translation to apply to the regular view matrix, and not
    // the skybox.
    translateM(viewMatrix, 0, 0, -1.5f, -5f);
  }

  @Override public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);

    heightmapProgram = new HeightmapShaderProgram(context);
    heightmap = new Heightmap(
        ((BitmapDrawable) context.getResources().getDrawable(R.drawable.heightmap)).getBitmap());

    skyboxProgram = new SkyboxShaderProgram(context);
    skybox = new Skybox();

    particleProgram = new ParticleShaderProgram(context);
    particleSystem = new ParticleSystem(10000);
    globalStartTime = System.nanoTime();

    final Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);

    final float angleVarianceInDegrees = 5f;
    final float speedVariance = 1f;
    redParticleShooter =
        new ParticleShooter(new Point(-1f, 0f, 0f), particleDirection, Color.rgb(255, 50, 5),
            angleVarianceInDegrees, speedVariance);

    greenParticleShooter =
        new ParticleShooter(new Point(0f, 0f, 0f), particleDirection, Color.rgb(25, 255, 25),
            angleVarianceInDegrees, speedVariance);

    blueParticleShooter =
        new ParticleShooter(new Point(1f, 0f, 0f), particleDirection, Color.rgb(5, 50, 255),
            angleVarianceInDegrees, speedVariance);
    particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);
    skyboxTexture = TextureHelper.loadCubeMap(context, new int[] {
        R.drawable.night_left, R.drawable.night_right, R.drawable.night_bottom,
        R.drawable.night_top, R.drawable.night_front, R.drawable.night_back
    });
  }

  @Override public void onSurfaceChanged(GL10 gl10, int width, int height) {
    GLES20.glViewport(0, 0, width, height);
    MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 100f);
    updateViewMatrices();
  }

  @Override public void onDrawFrame(GL10 gl10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    drawHeightmap();
    drawSkybox();
    drawParticles();
  }

  private void drawHeightmap() {
    setIdentityM(modelMatrix, 0);
    // Expand the heightmap's dimensions, but don't expand the height as
    // much so that we don't get insanely tall mountains.
    Matrix.scaleM(modelMatrix, 0, 100f, 10f, 100f);
    updateMvpMatrix();
    heightmapProgram.useProgram();
    final float[] vectorToLightInEyeSpace = new float[4];
    final float[] pointPositionsInEyeSpace = new float[12];
    multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
    multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
    multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4);
    multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8);
    heightmapProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix,
        vectorToLightInEyeSpace, pointPositionsInEyeSpace, pointLightColors);
    heightmap.bindData(heightmapProgram);
    heightmap.draw();
  }

  private void drawSkybox() {
    setIdentityM(modelMatrix, 0);
    updateMvpMatrixForSkybox();

    glDepthFunc(GL_LEQUAL); // This avoids problems with the skybox itself getting clipped.
    skyboxProgram.useProgram();
    skyboxProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture);
    skybox.bindData(skyboxProgram);
    skybox.draw();
    glDepthFunc(GL_LESS);
  }

  private void drawParticles() {
    float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

    redParticleShooter.addParticles(particleSystem, currentTime, 1);
    greenParticleShooter.addParticles(particleSystem, currentTime, 1);
    blueParticleShooter.addParticles(particleSystem, currentTime, 1);

    setIdentityM(modelMatrix, 0);
    updateMvpMatrix();

    glEnable(GL_BLEND);
    glBlendFunc(GL_ONE, GL_ONE);

    particleProgram.useProgram();
    particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture);
    particleSystem.bindData(particleProgram);
    particleSystem.draw();

    glDisable(GL_BLEND);
    glDepthMask(true);
  }

  private void updateMvpMatrix() {
    multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
    Matrix.invertM(tempMatrix, 0, modelViewMatrix, 0);
    Matrix.transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
    multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
  }

  private void updateMvpMatrixForSkybox() {
    multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0);
    multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
  }
}
