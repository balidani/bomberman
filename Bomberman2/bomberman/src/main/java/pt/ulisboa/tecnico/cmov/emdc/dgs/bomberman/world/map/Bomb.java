package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map;

import android.graphics.drawable.GradientDrawable;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Texture;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Vertices;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Direction;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.GridLocation;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Player;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Robot;

/**
 * Created by savasci on 5/6/2014.
 */
public class Bomb {
    float endTime;
    float currentTime;
    float explosionTime;
    GridLocation location;
    float scale = 0.5f;
    boolean ticking = false;
    public boolean ended = false;
    List<GridLocation> flames;
    World world;
    Player player;
    GridLocation topLeft;
    GridLocation bottomRight;


    public Bomb(World world,Player player, int i, int j) {
        location = new GridLocation(i,j);
        this.world = world;
        this.player = player;
        player.hasBomb = false;
        world.map.setCell(i,j,World.BOMB);
        this.currentTime = 0;
        this.explosionTime = World.explosionTimeOut/ 1000.0f;
        this.endTime = explosionTime + World.explosionDuration/1000.0f;// ms to s conversion
        ticking = true;
        flames = new ArrayList<GridLocation>();
    }

    public void simulate(float deltaTime) {
        currentTime+= deltaTime;
        if(currentTime<explosionTime) return;
        else if(currentTime<endTime) explode();
        else end();

    }

    private void end() {
        for(GridLocation flame : flames) {
            world.map.clearCell(flame.i,flame.j);
        }
        player.hasBomb = true;
        ended = true;
    }

    private void explode() {
        if(ticking){
            ticking = false;
            world.map.clearCell(location.i, location.j);
            flames.add(location);
            for(int t=0;t< Direction.AllDirections.length;t++) {
                for (int s = 1; s <= World.explosionRange; s++) {
                    GridLocation current = new GridLocation(location.i + s * Direction.AllDirections[t].i, location.j + s * Direction.AllDirections[t].j);
                    if (Map.isDestructible(current.i, current.j)) {
                        world.map.setCell(current.i, current.j, World.FLAMES);
                        flames.add(current);
                    } else break;
                }
            }
        }
        for(Robot current : world.robots) {
            if(current.isDying || !current.isAlive) continue;

            topLeft = new GridLocation( (int) (-1 * (current.position.y-5) / GameAssets.ASSET_DIMENSION),
                (int) ((current.position.x+5) / GameAssets.ASSET_DIMENSION));
            bottomRight = new GridLocation( (int) (-1 * (current.position.y - GameAssets.ASSET_DIMENSION + 6) / GameAssets.ASSET_DIMENSION),
                    (int) ((current.position.x + GameAssets.ASSET_DIMENSION - 6) / GameAssets.ASSET_DIMENSION));

            if(location.i != topLeft.i && location.i != bottomRight.i && location.j != topLeft.j && location.j != topLeft.j) continue;

            for(GridLocation flame: flames)
            {
                if(current.isDying) break;
                if(flame.equals(topLeft) || flame.equals(bottomRight)) {
                        player.score += World.pointsPerRobot;
                        current.die();
                }
            }

        }

        for(Player current : world.players) {
            if(current.isDying || !current.isAlive) continue;

            topLeft = new GridLocation( (int) (-1 * (current.position.y-5) / GameAssets.ASSET_DIMENSION),
                (int) ((current.position.x+5) / GameAssets.ASSET_DIMENSION));
            bottomRight = new GridLocation( (int) (-1 * (current.position.y - GameAssets.ASSET_DIMENSION + 6) / GameAssets.ASSET_DIMENSION),
                    (int) ((current.position.x + GameAssets.ASSET_DIMENSION - 6) / GameAssets.ASSET_DIMENSION));

            if(location.i != topLeft.i && location.i != bottomRight.i && location.j != topLeft.j && location.j != topLeft.j) continue;

            for(GridLocation flame: flames)
            {
                if(current.isDying) break;
                if(flame.equals(topLeft) || flame.equals(bottomRight)) {
                    player.score += World.pointsPerOpponent;
                    current.die();
                }
            }
        }



    }

    public void draw(GL10 gl, Vertices generalModel, Texture bombTexture,Texture flameTexture) {
        gl.glEnable(GL10.GL_TEXTURE_2D);

        if(ticking) {

            bombTexture.bind();
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(location.j * GameAssets.ASSET_DIMENSION, -1 * location.i * GameAssets.ASSET_DIMENSION, 0);
            gl.glTranslatef(GameAssets.ASSET_DIMENSION / 2, -1 * GameAssets.ASSET_DIMENSION / 2, 0);
            gl.glScalef(scale, scale, 1.0f);
            gl.glTranslatef(-1 * GameAssets.ASSET_DIMENSION / 2, GameAssets.ASSET_DIMENSION / 2, 0);
            generalModel.draw(GL10.GL_TRIANGLES, 0, 6);

            scale = 0.5f + 0.6f* currentTime / explosionTime;
        }
        else if(!ended){
            flameTexture.bind();
            for(GridLocation flame : flames){
                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();
                gl.glTranslatef(flame.j * GameAssets.ASSET_DIMENSION, -1 * flame.i * GameAssets.ASSET_DIMENSION, 0);
                if((int)((currentTime-explosionTime)*10)%2 == 0)
                {
                    gl.glTranslatef(GameAssets.ASSET_DIMENSION,0,0);
                    gl.glRotatef(180,0,1,0);

                }
                generalModel.draw(GL10.GL_TRIANGLES, 0, 6);
            }

        }
    }
}
