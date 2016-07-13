package com.roger.particles.data;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2016/7/5.
 */
public class VertexArray {
  private final FloatBuffer floatBuffer;

  public VertexArray(float[] vertexData) {
    floatBuffer = ByteBuffer.allocateDirect(vertexData.length * Constands.BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertexData);
  }

  public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount,
      int stride) {
    floatBuffer.position(dataOffset);
    GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT, false, stride,
        floatBuffer);
    GLES20.glEnableVertexAttribArray(attributeLocation);
    floatBuffer.position(0);
  }

  public void updateBuffer(float[] vertexData, int start, int count) {
    floatBuffer.position(start);
    floatBuffer.put(vertexData, start, count);
    floatBuffer.position(0);
  }
}
