package knickknacker.opengldrawables.Drawing.Drawables;

import android.opengl.GLES20;

import knickknacker.opengldrawables.Properties.Colors;
import knickknacker.opengldrawables.Drawing.Tools.Matrices;
import knickknacker.opengldrawables.Drawing.Tools.Shaders;

/** This class creates VBOs for the position, color and texture coordinates of something that you
 * want to be drawn by the GLRenderer. After creating a Drawable with the constructor it is not
 * ready to be drawn, it should first be initialized by the init function and the needed shader
 * program has to be set by the setProgramHandle function. To change the position color or texture
 * coordinates of the drawable, one of the edit functions should be called. Before the Drawable
 * will be drawn again, the checkForUpdates function will be called to send the changes to the GPU.
 * also a transformation matrix can be set, which will be used to transform the position vertices
 * of the drawable when the shader program get executed on the GPU. */

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
    protected boolean updateScissor = false;
    protected int[] scissorEdits;
    protected int scissorX = 0;
    protected int scissorY = 0;
    protected int scissorWidth = 0;
    protected int scissorHeight = 0;
    protected boolean useScissor = false;

    public Drawable(float[] points, float[] color, int texture, float[] texcoords, int draw_method) {
        this.points = points;
        this.color = color;
        this.texcoords = texcoords;
        this.texture = texture;
        this.draw_method = draw_method;
    }

    public void init() {
        /** Initialize the drawable. This means that VBO buffers will be created and loaded with
         * the position, color and texture vertices. */
        vertexCount = points.length / 3;
        buffers = Shaders.genVBO(3);
        Shaders.setVBO(buffers, 0, Shaders.getFloatBuffer(points));
        if (color != null) {
            Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.colorPerVertex(color, vertexCount)));
        } else {
            Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.colorPerVertex(Colors.WHITE, vertexCount)));
        }

        if (texture > -1) {
            Shaders.setVBO(buffers, 2, Shaders.getFloatBuffer(texcoords));
        }
    }

    public void checkForUpdates() {
        /** Check if one of the edit functions has been called, and update the VBOs if this is
         * the case. */
        if (updatePoints) {
            Shaders.setVBO(buffers, 0, Shaders.getFloatBuffer(this.points));
            updatePoints = false;
        }

        if (updateColor) {
            if (color != null) {
                Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.colorPerVertex(color, vertexCount)));
            } else {
                Shaders.setVBO(buffers, 1, Shaders.getFloatBuffer(Shaders.colorPerVertex(Colors.WHITE, vertexCount)));
            }

            updateColor = false;
        }

        if (updateTexels) {
            Shaders.setVBO(buffers, 2, Shaders.getFloatBuffer(this.texcoords));
            updateTexels = false;
        }

        if (updateScissor) {
            scissorX = scissorEdits[0];
            scissorY = scissorEdits[1];
            scissorWidth = scissorEdits[2];
            scissorHeight = scissorEdits[3];
        }
    }

    public void draw() {
        /** Draw the drawable if it is ready. */
        if (!ready) {
            return;
        }

        GLES20.glUseProgram(programHandle);
        int aPosition = GLES20.glGetAttribLocation(programHandle, "a_Position");
        int aColor = GLES20.glGetAttribLocation(programHandle, "a_Color");
        int aTexCoords = GLES20.glGetAttribLocation(programHandle, "a_texCoord");
        int uTransform = GLES20.glGetUniformLocation(programHandle, "u_Transform");

        if (useScissor) {
            GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
        }

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
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0, 0);

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

        if (useScissor) {
            GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        }
    }

    public void editPoints(float[] points) {
        /** Edit the position vertices. If there are more or less vertices than last time, also the
         * color vectors will be updated. */
        if (points.length != this.points.length) {
            vertexCount = points.length / 3;
            updateColor = true;
        }

        if (points != null) {
            this.points = points;
            updatePoints = true;
        }
    }

    public void editTexels(float[] texcoords) {
        /** Edit the texture coordinates of the drawable. */
        if (texcoords != null) {
            this.texcoords = texcoords;
            updateTexels = true;
        }
    }

    public void editColor(float[] color) {
        /** Edit the color of the Drawable. */
        if (color != null) {
            this.color = color;
            updateColor = true;
        }
    }

    public void destroy() {
        /** Destroy the used VBO buffers by this Drawables. */
        Shaders.deleteVBO(buffers);
    }

    public int getTexture() {
        /** Get the texture handle of this Drawable. */
        return this.texture;
    }

    public void setTexture(int texture) {
        /** Set the texture handle of this Drawable. */
        this.texture = texture;
    }

    public void setProgramHandle(int programHandle) {
        /** Set the program handle of this Drawable. Assumes that the program has been loaded to
         * the GPU when it will be used. */
        this.programHandle = programHandle;
    }

    public float[] getPoints() {
        /** Get the position vertices. */
        return points;
    }

    public float[] getColor() {
        /** Get the color vector. */
        return color;
    }

    public float[] getTexcoords() {
        /** Get the texture coordinates. */
        return texcoords;
    }

    public boolean isReady() {
        /** Return if the Drawable is ready to be drawn. */
        return ready;
    }

    public void setReady(boolean ready) {
        /** Set the Drawable to be ready to be drawn. */
        this.ready = ready;
    }

    public void setTransformMatrix(float[] transformMatrix) {
        /** Set the Transformation matrix of the drawable. */
        this.transformMatrix = transformMatrix;
    }

    public void setScissor(int x, int y, int width, int height) {
        scissorX = x;
        scissorY = y;
        scissorWidth = width;
        scissorHeight = height;
        useScissor = true;
    }

    public void disableScissor() {
        useScissor = false;
    }

    public void editScissor(int x, int y, int width, int height) {
        if (scissorEdits == null) {
            scissorEdits = new int[4];
        }

        scissorEdits[0] = x;
        scissorEdits[1] = y;
        scissorEdits[2] = width;
        scissorEdits[3] = height;
        updateScissor = true;
    }
}
