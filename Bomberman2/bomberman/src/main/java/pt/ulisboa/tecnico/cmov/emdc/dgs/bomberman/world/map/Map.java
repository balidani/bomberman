package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGame;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.GLGraphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Texture;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Vertices;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Coordinate;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;

/**
 * Created by savasci on 5/2/2014.
 */
public class Map {
    Game game;

    public int width;
    public int height;
    static char [][] elements;
    public Map(Game game,char[][] mapArray) {
        this.game = game;
        this.elements = mapArray;
        this.height = mapArray.length;
        this.width = mapArray[0].length;
        GLGraphics glGraphics = ((GLGame)game).getGlGraphics();

    }

    public void draw(GL10 gl,Vertices generalModel,Texture wallTexture,Texture obstacleTexture,Texture emptyTexture) {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int x = j*GameAssets.ASSET_DIMENSION;
                int y = - i*GameAssets.ASSET_DIMENSION;
                switch (elements[i][j])
                {
                    case World.WALL:
                        //g.drawPixmap(GameAssets.wall,x,y);
                        gl.glEnable(GL10.GL_TEXTURE_2D);
                        wallTexture.bind();

                        gl.glMatrixMode(GL10.GL_MODELVIEW);
                        gl.glLoadIdentity();
                        gl.glTranslatef(x,y,0);
                        generalModel.draw(GL10.GL_TRIANGLES,0,6);
                        break;
                    case World.OBSTACLE:
                        gl.glEnable(GL10.GL_TEXTURE_2D);
                        obstacleTexture.bind();

                        gl.glMatrixMode(GL10.GL_MODELVIEW);
                        gl.glLoadIdentity();
                        gl.glTranslatef(x,y,0);
                        generalModel.draw(GL10.GL_TRIANGLES,0,6);
                        break;
                    case World.EMPTY:
                        gl.glEnable(GL10.GL_TEXTURE_2D);
                        emptyTexture.bind();

                        gl.glMatrixMode(GL10.GL_MODELVIEW);
                        gl.glLoadIdentity();
                        gl.glTranslatef(x,y,0);
                        generalModel.draw(GL10.GL_TRIANGLES,0,6);
                        break;
                    default:
                        gl.glEnable(GL10.GL_TEXTURE_2D);
                        emptyTexture.bind();

                        gl.glMatrixMode(GL10.GL_MODELVIEW);
                        gl.glLoadIdentity();
                        gl.glTranslatef(x,y,0);
                        generalModel.draw(GL10.GL_TRIANGLES,0,6);
                        break;
                }
            }
        }
    }

    public static boolean isValidDestination(int i,int j)
    {
        if(elements[i][j] == World.EMPTY) {
            return true;
        }
        return  false;
    }

    public static boolean isDestructible(int i,int j)
    {
        if(elements[i][j] != World.WALL) {
            return true;
        }
        return false;
    }

    public void setCell(int i,int j,char k)
    {
        elements[i][j] = k;
    }

    public static char getCell(int i,int j) { return elements[i][j];};

    public void clearCell(int i,int j) { elements[i][j] = World.EMPTY;}
}
