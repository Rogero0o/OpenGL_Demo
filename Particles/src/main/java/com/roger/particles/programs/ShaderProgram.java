package com.roger.particles.programs;

import android.content.Context;
import android.opengl.GLES20;
import com.roger.particles.util.ShaderHelper;
import com.roger.particles.util.TextResourceReader;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ShaderProgram {

  protected static final String U_MATRIX = "u_Matrix";
  protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
  protected static final String U_TIME = "u_Time";


  protected static final String A_POSITION = "a_Position";
  protected static final String A_COLOR = "a_Color";
  protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

  protected static final String U_COLOR = "u_Color";

  protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
  protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

  protected final int program;

  protected ShaderProgram(Context context, int vertexShaderResourceId,
      int fragmentShaderResourceId) {
    program = ShaderHelper.buildProgram(
        TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
        TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId));
  }

  public void useProgram() {
    GLES20.glUseProgram(program);
  }
}
