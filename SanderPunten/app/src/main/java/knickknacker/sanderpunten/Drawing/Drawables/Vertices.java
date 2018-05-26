package knickknacker.sanderpunten.Drawing.Drawables;

import android.opengl.GLES20;

import knickknacker.sanderpunten.Drawing.Tools.Matrices;
import knickknacker.sanderpunten.Drawing.Tools.Shaders;

/**
 * Created by Niek on 25-5-2018.
 */

public class Vertices implements Drawable {
    protected float[] points;
    protected float[] color;
    protected float[] texcoords;
    protected int texture;
    protected int programHandle;
    protected int vertexCount;
    protected int[] buffers;
    protected int draw_method;
    protected float[] transformMatrix;

    public Vertices(float[] points, float[] color, int texture, float[] texcoords, int draw_method) {
        this.points = points;
        this.color = color;
        this.texcoords = texcoords;
        this.texture = texture;
        this.draw_method = draw_method;
    }

    public void init() {
        vertexCount = points.length / 2;
        buffers = Shaders.genVBO(3);
        Shaders.setVBO(buffers, 0, Shaders.getFloatBuffer(points));
        Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.perVertex(color, vertexCount)));

        if (texture > -1) {
            Shaders.setVBO(buffers, 2, Shaders.getFloatBuffer(texcoords));
        }
    }

    public void draw() {
        GLES20.glUseProgram(programHandle);
        int aPosition = GLES20.glGetAttribLocation(programHandle, "a_Position");
        int aColor = GLES20.glGetAttribLocation(programHandle, "a_Color");
        int aTexCoords = GLES20.glGetAttribLocation(programHandle, "a_texCoord");
        int uTransform = GLES20.glGetUniformLocation(programHandle, "u_Transform");
        if (transformMatrix == null) {
            GLES20.glUniformMatrix4fv(uTransform, 1, false, Shaders.getFloatBuffer(Matrices.identity));
        } else {
            GLES20.glUniformMatrix4fv(uTransform, 1, false, Shaders.getFloatBuffer(transformMatrix));
        }

        if (texture > -1) {
            int uTexture = GLES20.glGetUniformLocation(programHandle, "u_Texture");
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
            GLES20.glUniform1i(uTexture, 0);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, 0);
        if (texture > -1) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
            GLES20.glEnableVertexAttribArray(aTexCoords);
            GLES20.glVertexAttribPointer(aTexCoords, 2, GLES20.GL_FLOAT, false, 0, 0);
        }

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDrawArrays(draw_method, 0, vertexCount);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aColor);
        if (texture > -1) {
            GLES20.glDisableVertexAttribArray(aTexCoords);
        }
    }

    public int getTexture() {
        return this.texture;
    }

    public void setTexture(int texture) {
        this.texture = texture;
    }

    public void setProgramHandle(int programHandle) {
        this.programHandle = programHandle;
    }

    public float[] getPoints() {
        return points;
    }

    public float[] getColor() {
        return color;
    }

    public float[] getTexcoords() {
        return texcoords;
    }

    public void setTransformMatrix(float[] transformMatrix) {
        this.transformMatrix = transformMatrix;
    }
}
