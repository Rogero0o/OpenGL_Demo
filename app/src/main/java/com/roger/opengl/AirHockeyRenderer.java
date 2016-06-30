package com.roger.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import com.roger.opengl.util.LoggerConfig;
import com.roger.opengl.util.ShaderHelper;
import com.roger.opengl.util.TextResourceReader;
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
      -0.5f,-0.5f,
      0.5f,0.5f,
      -0.5f,0.5f,

      -0.5f,-0.5f,
      0.5f,-0.5f,
      0.5f,0.5f,

      -0.5f,0f,
      0.5f,0f,

      0f,-0.25f,
      0f,0.25f
  };

  private static final String U_COLOR = "u_Color";
  private int uColorLocation;

  private static final String A_POSITION = "a_Position";
  private int aPostionLocation;

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
    uColorLocation = GLES20.glGetUniformLocation(program,U_COLOR);
    aPostionLocation = GLES20.glGetAttribLocation(program,A_POSITION);

    vertexData.position(0);
    GLES20.glVertexAttribPointer(aPostionLocation,POSITION_COMOPNENT_COUNT,GLES20.GL_FLOAT,false,0,vertexData);
    GLES20.glEnableVertexAttribArray(aPostionLocation);
  }

  @Override public void onSurfaceChanged(GL10 gl10, int i, int i1) {
    GLES20.glViewport(0, 0, i, i1);
  }

  @Override public void onDrawFrame(GL10 gl10) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    GLES20.glUniform4f(uColorLocation,1.0f,1.0f,1.0f,1.0f);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);

    GLES20.glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
    GLES20.glDrawArrays(GLES20.GL_LINES,6,2);

    GLES20.glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);
    GLES20.glDrawArrays(GLES20.GL_LINES,8,1);

    GLES20.glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f);
    GLES20.glDrawArrays(GLES20.GL_LINES,9,1);
  }
}
