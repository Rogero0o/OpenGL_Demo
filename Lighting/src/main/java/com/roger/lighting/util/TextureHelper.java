package com.roger.lighting.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by Administrator on 2016/7/5.
 */
public class TextureHelper {

  private final static String TAG = "TextureHelper";

  public static int loadTexture(Context context,int resourceId){
    final int[] textureObjectIds = new int[1];
    GLES20.glGenTextures(1,textureObjectIds,0);
    if(textureObjectIds[0] == 0 ){
      if(LoggerConfig.ON){
        Log.w(TAG,"Could not generate a new OpenGL texture object.");
      }
      return 0;
    }
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;

    final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId,options);

    if(bitmap==null){
      if(LoggerConfig.ON){
        Log.w(TAG,"Resource ID "+resourceId+" could not be decoded");
      }
      GLES20.glDeleteTextures(1,textureObjectIds,0);
      return 0;
    }
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureObjectIds[0]);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR_MIPMAP_LINEAR);
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

    bitmap.recycle();

    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
    return textureObjectIds[0];
  }

  /**
   * Loads a cubemap texture from the provided resources and returns the
   * texture ID. Returns 0 if the load failed.
   *
   * @param context
   * @param cubeResources
   *            An array of resources corresponding to the cube map. Should be
   *            provided in this order: left, right, bottom, top, front, back.
   * @return
   */
  public static int loadCubeMap(Context context, int[] cubeResources) {
    final int[] textureObjectIds = new int[1];
    glGenTextures(1, textureObjectIds, 0);

    if (textureObjectIds[0] == 0) {
      if (LoggerConfig.ON) {
        Log.w(TAG, "Could not generate a new OpenGL texture object.");
      }
      return 0;
    }
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;
    final Bitmap[] cubeBitmaps = new Bitmap[6];
    for (int i = 0; i < 6; i++) {
      cubeBitmaps[i] =
          BitmapFactory.decodeResource(context.getResources(),
              cubeResources[i], options);

      if (cubeBitmaps[i] == null) {
        if (LoggerConfig.ON) {
          Log.w(TAG, "Resource ID " + cubeResources[i]
              + " could not be decoded.");
        }
        glDeleteTextures(1, textureObjectIds, 0);
        return 0;
      }
    }
    // Linear filtering for minification and magnification
    glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
    texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);

    texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
    texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);

    texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
    texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);
    glBindTexture(GL_TEXTURE_2D, 0);

    for (Bitmap bitmap : cubeBitmaps) {
      bitmap.recycle();
    }

    return textureObjectIds[0];
  }
}
