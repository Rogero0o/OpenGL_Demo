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
      -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,

      -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,

      -0.5f, 0f, 0.5f, 0f,

      0f, -0.25f, 0f, 0.25f,

      -0.6f, -0.6f, 0.6f, 0.6f, -0.6f, 0.6f,

      -0.6f, -0.6f, 0.6f, -0.6f, 0.6f, 0.6f,
  };

  private static final String U_COLOR = "u_Color";
  private int uColorLocation;

  private static final String A_POSITION = "a_Position";
  private int aPostionLocation;

  public AirHockeyRenderer(Context context) {
    this.context = context;
    vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    vertexData.put(tableVertices);
  }

  @Override public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);//清屏
    String vertexShaderSource =
        TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);//读取顶点着色器
    String fragmentShaderSource =
        TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);//读取片段着色器

    int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);//生成并编译顶点着色器
    int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);//生成并编译片段着色器

    program = ShaderHelper.linkProgram(vertexShader, fragmentShader);//将着色器附加到程序对象上并执行链接操作

    if (LoggerConfig.ON) {
      ShaderHelper.validateProgram(program);
    }

    GLES20.glUseProgram(program);
    uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);//获取 u_Color 在 shader 中的位置
    aPostionLocation = GLES20.glGetAttribLocation(program, A_POSITION);//获取 a_Position 在 shader 中的位置
    /*
    u_Color a_Position 都是一个 Uniform   Uniform 以及 其他两种变量详解： http://blog.csdn.net/renai2008/article/details/7844495
    Uniform是变量类型的一种修饰符,是OpenGL ES  中被着色器中的常量值,使用存储各种着色器需要的数据，例如：转换矩阵、光照参数或者颜色。

　　uniform 的空间被顶点着色器和片段着色器分享。也就是说顶点着色器和片段着色器被链接到一起进入项目，它们分享同样的uniform。因此一个在顶点着色器中声明的uniform，相当于在片段着色器中也声明过了。
   当应用程序装载uniform 时，它的值在顶点着色器和片段着色器都可用。在链接阶段，链接器将分配常量在项目里的实际地址，那个地址是被应用程序使用和加载的标识。

　　另一个需要注意的是，uniform 被存储在硬件被称为常量存储，这是一种分配在硬件上的存储常量值的空间。因为这种存储需要的空间是固定的，在程序中这种uniform 的数量是受限的。这个限制能通过读gl_MaxVertexUniformVectors 和gl_MaxFragmentUniformVectors编译变量得出。（ 或者用GL_MAX_VERTEX_UNIFORM_VECTORS 或GL_MAX_FRAGMENT_UNIFORM_ VECTORS 为参数调用glGetIntegerv）OpenGL ES 2.0必须至少提供256 个顶点着色器uniform 和224个片段着色器uniform。
     */

    vertexData.position(0);//将读取指针复位
    GLES20.glVertexAttribPointer(aPostionLocation, POSITION_COMOPNENT_COUNT, GLES20.GL_FLOAT, false,
        0, vertexData);// 指定了渲染时索引值为 aPostionLocation 的顶点属性数组的数据格式和位置
    GLES20.glEnableVertexAttribArray(
        aPostionLocation);// Enable or disable a generic vertex attribute array
  }

  @Override public void onSurfaceChanged(GL10 gl10, int i, int i1) {
    GLES20.glViewport(0, 0, i, i1);//创建窗口，第一二个参数为窗口坐标，三四参数宽高
  }

  @Override public void onDrawFrame(GL10 gl10) {
    /*
函数原型:
      void glClear(GLbitfield mask);
参数说明：
      GLbitfield：可以使用 | 运算符组合不同的缓冲标志位，表明需要清除的缓冲，例如glClear（GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT）表示要清除颜色缓冲以及深度缓冲，可以使用以下标志位
      GL_COLOR_BUFFER_BIT:    当前可写的颜色缓冲
      GL_DEPTH_BUFFER_BIT:    深度缓冲
      GL_ACCUM_BUFFER_BIT:   累积缓冲
　　  GL_STENCIL_BUFFER_BIT: 模板缓冲
函数说明：
      glClear（）函数的作用是用当前缓冲区清除值，也就是glClearColor或者glClearDepth、glClearIndex、glClearStencil、glClearAccum等函数所指定的值来清除指定的缓冲区，也可以使用glDrawBuffer一次清除多个颜色缓存。比如：
　　  glClearColor（0.0，0.0，0.0，0.0）;
　　  glClear（GL_COLOR_BUFFER_BIT）;
　　  第一条语句表示清除颜色设为黑色，第二条语句表示实际完成了把整个窗口清除为黑色的任务，glClear（）的唯一参数表示需要被清除的缓冲区。
     像素检验、裁剪检验、抖动和缓存的写屏蔽都会影响glClear的操作，其中，裁剪范围限制了清除的区域，而glClear命令还会忽略alpha函数、融合函数、逻辑操作、模板、纹理映射和z缓存；
     */
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    GLES20.glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);//为 u_Color 这个 Uniform 设置颜色值 RGB 为 0 1 0 1 绿色
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 10, 6);//画三角形

    GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

    GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
    GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

    GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
    GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);

    GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
    GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
  }
}
