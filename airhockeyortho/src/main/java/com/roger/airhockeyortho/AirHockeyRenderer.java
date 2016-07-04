package com.roger.airhockeyortho;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.roger.airhockeyortho.util.LoggerConfig;
import com.roger.airhockeyortho.util.ShaderHelper;
import com.roger.airhockeyortho.util.TextResourceReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2016/6/30.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

  private static final int BYTES_PER_FLOAT = 4;
  private final FloatBuffer vertexData;
  private static final int POSITION_COMOPNENT_COUNT = 2;
  private Context context;
  private int program;
  float[] tableVertices = {
      //Order of coordinates:X,Y,R,G,B


      //Triangle Fan
       0f,0f,1f,1f,1f,
      -0.5f,-0.8f, 0.7f, 0.7f, 0.7f,
       0.5f,-0.8f, 0.7f, 0.7f, 0.7f,
       0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
      -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
      -0.5f,-0.8f, 0.7f, 0.7f, 0.7f,

      -0.5f,0f,1f,0f,0f,
       0.5f,0f,1f,0f,0f,

       0f,-0.25f,0f,0f,1f,
       0f, 0.25f,1f,0f,0f
  };

  private static final String A_POSITION = "a_Position";
  private int aPostionLocation;

  private static final String A_COLOR = "a_Color";
  private static final int COLOR_COMPONENT_COUNT = 3;
  private static final int STRIDE = (POSITION_COMOPNENT_COUNT+COLOR_COMPONENT_COUNT)*BYTES_PER_FLOAT;
  private int aColorLocation;

  private static final String U_MATRIX = "u_Matrix";
  private final float[] projectionMatrix = new float[16];
  private int uMatrixLocation;


  public AirHockeyRenderer(Context context) {
    this.context = context;
    vertexData = ByteBuffer.allocateDirect(tableVertices.length*BYTES_PER_FLOAT).order(
        ByteOrder.nativeOrder()).asFloatBuffer();
    vertexData.put(tableVertices);
  }

  @Override public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    String vertexShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_vertex_shader);
    String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_fragment_shader);

    int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
    int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

    program = ShaderHelper.linkProgram(vertexShader,fragmentShader);

    if(LoggerConfig.ON){
      ShaderHelper.validateProgram(program);
    }

    GLES20.glUseProgram(program);
    aColorLocation = GLES20.glGetAttribLocation(program,A_COLOR);
    aPostionLocation = GLES20.glGetAttribLocation(program,A_POSITION);

    vertexData.position(0);
    GLES20.glVertexAttribPointer(aPostionLocation,POSITION_COMOPNENT_COUNT,GLES20.GL_FLOAT,false,STRIDE,vertexData);
    GLES20.glEnableVertexAttribArray(aPostionLocation);

    vertexData.position(POSITION_COMOPNENT_COUNT);
    GLES20.glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT,GLES20.GL_FLOAT,false,STRIDE,vertexData);
    GLES20.glEnableVertexAttribArray(aColorLocation);

    uMatrixLocation = GLES20.glGetUniformLocation(program,U_MATRIX);
  }

  @Override public void onSurfaceChanged(GL10 gl10, int width, int height) {
    GLES20.glViewport(0, 0, width,height);

    final float aspectRatio = width > height ?(float)width/(float)height:(float)height/(float)width;

    if(width>height){
      Matrix.orthoM(projectionMatrix,0,-aspectRatio,aspectRatio,-1f,1f,-1f,1f);
    }else{
      Matrix.orthoM(projectionMatrix,0,-1f,1f,-aspectRatio,aspectRatio,-1f,1f);
    }
  }

  @Override public void onDrawFrame(GL10 gl10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,projectionMatrix,0);

    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,6);

    GLES20.glDrawArrays(GLES20.GL_LINES,6,2);

    GLES20.glDrawArrays(GLES20.GL_POINTS,8,1);

    GLES20.glDrawArrays(GLES20.GL_POINTS,9,1);

  }
}
