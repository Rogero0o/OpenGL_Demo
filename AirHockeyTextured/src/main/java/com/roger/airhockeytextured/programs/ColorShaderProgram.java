package com.roger.airhockeytextured.programs;

import android.content.Context;
import android.opengl.GLES20;
import com.roger.airhockeytextured.R;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ColorShaderProgram extends ShaderProgram {

  private final int uMatrixLocation;

  private final int aPositionLocation;
  private final int aColorLocation;

  public ColorShaderProgram(Context context) {
    super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
    uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

    aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
  }

  @Override public void useProgram() {
    super.useProgram();
  }

  public void setUniforms(float[] matrix) {
    GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
  }

  public int getPositionAttributeLocation() {
    return aPositionLocation;
  }

  public int getColorAttributeLocation() {
    return aColorLocation;
  }
}
