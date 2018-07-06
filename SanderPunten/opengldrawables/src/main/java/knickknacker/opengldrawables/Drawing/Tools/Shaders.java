package knickknacker.opengldrawables.Drawing.Tools;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Niek on 26-1-2018.
 */

public abstract class Shaders {
    public final static int FLOAT_SIZE = 4;

    public static void clear(int[] program, int[] vertex, int[] fragment) {
        /** Clear the given program, vertex and fragment shaders.  */
        if (program != null && vertex != null && fragment != null)
            for (int i = 0; i < program.length; i++) {
                if (program[i] < 1) {
                    GLES20.glDeleteProgram(program[i]);
                    GLES20.glDeleteShader(vertex[i]);
                    GLES20.glDeleteShader(fragment[i]);
                }
            }
    }

    public static int loadShader(int shaderType, String shaderSource) {
        /** Load the given Vertex or Fragment Shader. */
        int handle = GLES20.glCreateShader(shaderType);
        if (handle == GLES20.GL_FALSE) {
            throw new RuntimeException("Shader was not created!");
        }

        GLES20.glShaderSource(handle, shaderSource);
        GLES20.glCompileShader(handle);

        int[] status = new int[1];
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, status, 0);

        if (status[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(handle);
            GLES20.glDeleteShader(handle);
            throw new RuntimeException("Shader compile error: " + error);
        } else {
            return handle;
        }
    }

    public static int loadProgram(int vertexShader, int fragmentShader) {
        /** Load a program with the given vertex and fragment shaders. */
        int handle = GLES20.glCreateProgram();

        if (handle == GLES20.GL_FALSE) {
            throw new RuntimeException("Program not created!");
        }

        GLES20.glAttachShader(handle, vertexShader);
        GLES20.glAttachShader(handle, fragmentShader);
        GLES20.glLinkProgram(handle);

        int[] status = new int[1];
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, status, 0);

        if (status[0] == 0) {
            String error = GLES20.glGetProgramInfoLog(handle);
            GLES20.glDeleteProgram(handle);
            throw new RuntimeException("Program linking error: " + error);
        } else {
            return handle;
        }
    }

    public static FloatBuffer getFloatBuffer(float[] array) {
        /** Make a FloatBuffer out of the given float array. */
        FloatBuffer b = ByteBuffer.allocateDirect(array.length * FLOAT_SIZE).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        b.put(array);
        b.position(0);
        return b;
    }

    public static float[] colorPerVertex(float[] a, int c) {
        /** Create a per vertex color array for the color 'a' and vertex count 'c'. */
        float[] colors = new float[a.length * c];
        for (int i = 0; i < 4 * c; i += 4) {
            colors[i] = a[0];
            colors[i + 1] = a[1];
            colors[i + 2] = a[2];
            colors[i + 3] = a[3];
        }

        return colors;
    }

    public static void setAttribVertexArray(int handle, float[] array, int pos, int size, int stride) {
        /** Set an Attribute Vertex Array. */
        FloatBuffer b = getFloatBuffer(array);
        b.position(pos);
        GLES20.glVertexAttribPointer(handle, size, GLES20.GL_FLOAT, false, stride, b);
    }

    public static int[] genVBO(int count) {
        /** Generate 'count' VBOs */
        int buffers[] = new int[count];
        GLES20.glGenBuffers(count, buffers, 0);
        return buffers;
    }

    public static void setVBO(int[] buffers, int index, FloatBuffer data) {
        /** Load the given data to the VBO in the GPU. */
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[index]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, data.capacity() * 4, data, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public static void updateVBO(int[] buffers, int index, FloatBuffer data) {
        /** Update the data of the given VBO. */
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[index]);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0,data.capacity() * 4, data);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public static void deleteVBO(int[] vbo) {
        /** Delete the given VBO. */
        GLES20.glDeleteBuffers(vbo.length, vbo, 0);
    }

    /** A Default vertex shader. */
    public final static String DEFAULT_VERTEX_SHADER =
            "attribute vec3 a_Position;" +
                    "uniform mat4 u_Projection;" +
                    "uniform mat4 u_Transform;" +
                    "attribute vec4 a_Color;" +

                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  v_Color = a_Color;" +
                    "  gl_Position = vec4(a_Position, 1.0) * u_Transform * u_Projection;" +
                    "}";

    /** A Default fragment shader. */
    public final static String DEFAULT_FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_FragColor = v_Color;" +
                    "}";

    /** A Vertex shader with texture. */
    public final static String TEXTURE_VERTEX_SHADER =
            "attribute vec3 a_Position;" +
                    "uniform mat4 u_Projection;" +
                    "uniform mat4 u_Transform;" +
                    "attribute vec2 a_texCoord;" +
                    "attribute vec4 a_Color;" +

                    "varying vec2 v_texCoord;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_Position = vec4(a_Position, 1.0) * u_Transform * u_Projection;" +
                    "  v_texCoord = a_texCoord;" +
                    "  v_Color = a_Color;" +
                    "}";

    /** A Fragment shader with texture. */
    public final static String TEXTURE_FRAGMENT_SHADER =
            "precision mediump float;" +

                    "uniform sampler2D u_Texture;" +

                    "varying vec2 v_texCoord;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_FragColor = v_Color * texture2D(u_Texture, v_texCoord);" +
                    "}";

    /** A Fragment shader for rendering text. */
    public final static String FONT_FRAGMENT_SHADER =
            "precision mediump float;" +

                    "uniform sampler2D u_Texture;" +

                    "varying vec2 v_texCoord;" +
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    "  gl_FragColor = v_Color * texture2D(u_Texture, v_texCoord).w;" +
                    "}";
}
