package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGraphics;

/**
 * Created by savasci on 4/28/2014.
 */
public class Vertices {
    final GLGraphics glGraphics;
    final boolean hasColor;
    final boolean hasTexCoords;
    final int vertexSize;
    final FloatBuffer vertices;
    final ShortBuffer indices;

    public Vertices(GLGraphics glGraphics, int maxVertices, int maxIndices, boolean hasColor,
                    boolean hasTexCoords) {

        this.glGraphics = glGraphics;
        this.hasColor = hasColor;
        this.hasTexCoords = hasTexCoords;
        this.vertexSize = (2 + (hasColor?4:0) + (hasTexCoords?2:0) )*4;

        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();

        if(maxIndices > 0) {
            buffer = ByteBuffer.allocateDirect(maxIndices*Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();
        } else {
            indices = null;
        }
    }

    public void setVertices(float[] vertices, int offset, int length) {
        this.vertices.clear();
        this.vertices.put(vertices,offset,length);
        this.vertices.flip();
    }

    public void setIndices(short[] indices, int offset, int length) {
        this.indices.clear();
        this.indices.put(indices,offset,length);
        this.indices.flip();
    }

    public void draw(int primitiveType, int offset, int numVertices) {
        GL10 gl10 = glGraphics.getGl10();

        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        gl10.glVertexPointer(2,GL10.GL_FLOAT,vertexSize,vertices);

        if(hasColor) {
            gl10.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(2);
            gl10.glColorPointer(4,GL10.GL_FLOAT,vertexSize,vertices);
        }
        if(hasTexCoords) {
            gl10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(hasColor?6:2);
            gl10.glTexCoordPointer(2,GL10.GL_FLOAT,vertexSize,vertices);
        }
        if(indices != null) {
            indices.position(offset);
            gl10.glDrawElements(primitiveType,numVertices,GL10.GL_UNSIGNED_SHORT,indices);
        } else {
            gl10.glDrawArrays(primitiveType,offset,numVertices);
        }

        if(hasTexCoords) {
            gl10.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        if(hasColor) {
            gl10.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    }
}
