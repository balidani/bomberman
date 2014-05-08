package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;



import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.FileIO;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGame;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGraphics;

/**
 * Created by savasci on 4/28/2014.
 */
public class Texture {
    GLGraphics glGraphics;
    FileIO fileIO;
    String fileName;
    int textureId;
    int minFilter;
    int magFilter;
    int width;
    int height;

    public Texture(GLGame glGame,String fileName) {
        this.glGraphics = glGame.getGlGraphics();
        this.fileIO = glGame.getFileIO();
        this.fileName = fileName;
        load();
    }

    private void load() {
        GL10 gl10 = glGraphics.getGl10();
        int[] textureIds = new int[1];
        gl10.glGenTextures(1,textureIds,0);
        textureId = textureIds[0];

        InputStream in = null;
        try {
            in = fileIO.readAsset(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            gl10.glBindTexture(GL10.GL_TEXTURE_2D,textureId);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap,0);
            setFilters(GL10.GL_LINEAR,GL10.GL_LINEAR);
            gl10.glBindTexture(GL10.GL_TEXTURE_2D,0);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load texture '"+fileName+"'",e );
        } finally {
            if(in!= null)
                try {
                    in.close();
                } catch (IOException e) {

                }
        }
    }

    public void reload() {
        load();
        bind();
        setFilters(minFilter,magFilter);
        glGraphics.getGl10().glBindTexture(GL10.GL_TEXTURE_2D,0);
    }

    public void setFilters(int minFilter,int magFilter) {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        GL10 gl = glGraphics.getGl10();

        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,minFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,magFilter);
    }

    public void bind() {
        GL10 gl10 = glGraphics.getGl10();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D,textureId);
    }

    public void dispose() {
        GL10 gl10 = glGraphics.getGl10();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D,textureId);
        int[] textureIds = {textureId};
        gl10.glDeleteTextures(1,textureIds,0);
    }
}
