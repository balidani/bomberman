package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Texture;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Vertices;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Coordinate;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Direction;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map.Map;

/**
 * Created by savasci on 5/2/2014.
 */
public class Player extends Agent{
    public int id;
    public boolean hasBomb;
    public int score;
    public Player(Game game,int i, int j,int id){
        super(game,i,j);
        this.id = id;
        speed = World.robotSpeed*2; // TODO properly read from level files
        this.hasBomb = true;
        this.score = 0;
    }


    public void simulate(float deltaTime) {
        currentTime += deltaTime;
        if(!isDying) {
            if (currentTime >= destinationArrivalTime) {
                position.x = destination.x;
                position.y = destination.y;
                direction = null;
            }
            if (direction != null) {
                move(deltaTime);
            }
        } else if(currentTime-destinationArrivalTime >= 1.0f) { // 1 second for dying animation
            isAlive = false;
        }
    }

    public void move(float deltaTime) {
        // fix retrieving player speed from level assets, currently double speed
        position.x += direction.j * speed * deltaTime ;
        position.y -= direction.i * speed * deltaTime ;
    }

    public void draw(GL10 gl, Vertices generalModel, Texture playerTexture,Texture playerCrispyTexture) {

        if(isAlive) {
            if(!isDying) {
                gl.glEnable(GL10.GL_TEXTURE_2D);
                playerTexture.bind();

                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();
                gl.glTranslatef(position.x,position.y,0);
                generalModel.draw(GL10.GL_TRIANGLES,0,6);
            } else {
                gl.glEnable(GL10.GL_TEXTURE_2D);
                playerCrispyTexture.bind();

                gl.glMatrixMode(GL10.GL_MODELVIEW);
                gl.glLoadIdentity();
                gl.glTranslatef(position.x,position.y,0);
                gl.glTranslatef(GameAssets.ASSET_DIMENSION / 2, -1 * GameAssets.ASSET_DIMENSION / 2, 0);
                gl.glRotatef(-90.0f * currentTime,0,0,1);
                gl.glTranslatef(-1 * GameAssets.ASSET_DIMENSION / 2, GameAssets.ASSET_DIMENSION / 2, 0);
                generalModel.draw(GL10.GL_TRIANGLES,0,6);
                gl.glPopMatrix();
            }
        }
     }

    public void moveIssued(Direction currentPick) {
        if(direction ==null && Map.isValidDestination( (int)(-1*position.y/GameAssets.ASSET_DIMENSION)+ currentPick.i,(int)(position.x/GameAssets.ASSET_DIMENSION)+currentPick.j)) {
            direction = currentPick;
            destination.x = position.x + direction.j* GameAssets.ASSET_DIMENSION;
            destination.y = position.y - direction.i* GameAssets.ASSET_DIMENSION;
            currentTime = 0.0f;
            destinationArrivalTime = direction.j * ((destination.x - position.x)/speed) - direction.i * ((destination.y - position.y)/speed);
        }
    }

}
