package com.roger.airhockeytextured.objects;

import com.roger.airhockeytextured.data.Constands;

/**
 * Created by Administrator on 2016/7/5.
 */
public class Table {
  private static final int POSITION_COMPONENT_COUNT = 2;
  private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
  private static final int STRIDE = (POSITION_COMPONENT_COUNT+TEXTURE_COORDINATES_COMPONENT_COUNT)* Constands.BYTES_PER_FLOAT;

  private static final float[] VERTEX_DATA = {
      0f,0f,0.5f,0.5f,
      -0.5f,-0.8f, 0f, 0.9f,
      0.5f, -0.8f, 1f, 0.9f,
      0.5f, 0.8f, 1f, 0.1f,
      -0.5f, 0.8f, 0f, 0.1f,
      -0.5f,-0.8f, 0f, 0.9f
  };
}
