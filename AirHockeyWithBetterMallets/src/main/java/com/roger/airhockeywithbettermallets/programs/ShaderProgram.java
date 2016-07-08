package com.roger.airhockeywithbettermallets.programs;

import android.content.Context;
import android.opengl.GLES20;
import com.roger.airhockeywithbettermallets.util.ShaderHelper;
import com.roger.airhockeywithbettermallets.util.TextResourceReader;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ShaderProgram {

  protected static final String U_MATRIX = "u_Matrix";
  protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

  protected static final String A_POSITION = "a_Position";
  protected static final String A_COLOR = "a_Color";
  protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

  protected static final String U_COLOR = "u_Color";

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
