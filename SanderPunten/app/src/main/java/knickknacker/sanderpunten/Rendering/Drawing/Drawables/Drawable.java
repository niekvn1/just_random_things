package knickknacker.sanderpunten.Rendering.Drawing.Drawables;

import android.opengl.GLES20;

import knickknacker.sanderpunten.Rendering.Drawing.Properties.Colors;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.Matrices;
import knickknacker.sanderpunten.Rendering.Drawing.Tools.Shaders;

/**
 * Created by Niek on 25-5-2018.
 */

public class Drawable {
    protected float[] points;
    protected float[] color;
    protected float[] texcoords;
    protected int texture;
    protected int programHandle;
    protected int vertexCount;
    protected int[] buffers;
    protected int draw_method;
    protected float[] transformMatrix;
    protected boolean ready = false;
    protected boolean updatePoints = false;
    protected boolean updateColor = false;
    protected boolean updateTexels = false;

    public Drawable(float[] points, float[] color, int texture, float[] texcoords, int draw_method) {
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
        if (color != null) {
            Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.perVertex(color, vertexCount)));
        } else {
            Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.perVertex(Colors.WHITE, vertexCount)));
        }

        if (texture > -1) {
            Shaders.setVBO(buffers, 2, Shaders.getFloatBuffer(texcoords));
        }
    }

    public void checkForUpdates() {
        if (updatePoints) {
            Shaders.setVBO(buffers, 0, Shaders.getFloatBuffer(this.points));
            updatePoints = false;
        }

        if (updateColor) {
            if (color != null) {
                Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.perVertex(color, vertexCount)));
            } else {
                Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.perVertex(Colors.WHITE, vertexCount)));
            }

            updateColor = false;
        }

        if (updateTexels) {
            Shaders.setVBO(buffers, 2, Shaders.getFloatBuffer(this.texcoords));
            updateTexels = false;
        }
    }

    public void draw() {
        if (!ready) {
            return;
        }

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

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
            GLES20.glEnableVertexAttribArray(aTexCoords);
            GLES20.glVertexAttribPointer(aTexCoords, 2, GLES20.GL_FLOAT, false, 0, 0);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, 0);

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

    public void editPoints(float[] points) {
        if (points.length != this.points.length) {
            vertexCount = points.length / 2;
            updateColor = true;
        }

        if (points != null) {
            this.points = points;
            updatePoints = true;
        }
    }

    public void editTexels(float[] texcoords) {
        if (texcoords != null) {
            this.texcoords = texcoords;
            updateTexels = true;
        }
    }

    public void editColor(float[] color) {
        if (color != null) {
            this.color = color;
            updateColor = true;
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setTransformMatrix(float[] transformMatrix) {
        this.transformMatrix = transformMatrix;
    }
}
