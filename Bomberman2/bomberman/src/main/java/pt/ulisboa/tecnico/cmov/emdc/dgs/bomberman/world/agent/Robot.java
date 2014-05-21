package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.BombingActivity;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.GameAssets;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Graphics;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Texture;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.impl.gl.Vertices;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Coordinate;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.Direction;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.GridLocation;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.World;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map.Map;

/**
 * Created by savasci on 5/2/2014.
 */
public class Robot extends Agent {
    List<Direction> possibleDirections;
    Direction currentPick;
    GridLocation topLeft;
    GridLocation bottomRight;
    public int id;
    public Direction receivedDirection;
    public int receivedX;
    public int receivedY;
    public Robot(Game game,int id, int i, int j) {

        super(game, i, j);
        this.id = id;
        possibleDirections = new ArrayList<Direction>(10);
        speed = World.robotSpeed;
        receivedDirection = null;
    }

//    public void draw() {
//        Graphics g = game.getGraphics();
//        g.drawPixmap(GameAssets.robot, (int) position.x, (int) position.y);
//    }

    public void simulate(float deltaTime, List<Player> players) {
        currentTime += deltaTime;
        if(!isDying) {
            if(receivedDirection!= null) {
                position.x = receivedX * GameAssets.ASSET_DIMENSION;
                position.y = -1 *receivedY * GameAssets.ASSET_DIMENSION;
                moveIssued(receivedDirection);
                receivedDirection = null;
            }

            if (currentTime >= destinationArrivalTime) {
                position.x = destination.x;
                position.y = destination.y;
                if( !((BombingActivity)game).multiplayer || ((BombingActivity)game).isOwner ) {
                    pickNewDestination();
                }
            }
            if (direction != null) {
                move(deltaTime);
            }


        } else if(currentTime-destinationArrivalTime >= 1.0f) { // 1 second for dying animation
            isAlive = false;
        }


        for(Player current : players) {
            if(current.isDying || !current.isAlive) continue;

            topLeft = new GridLocation( (int) (-1 * (current.position.y-10) / GameAssets.ASSET_DIMENSION),
                    (int) ((current.position.x+10) / GameAssets.ASSET_DIMENSION));
            bottomRight = new GridLocation( (int) (-1 * (current.position.y - GameAssets.ASSET_DIMENSION + 11) / GameAssets.ASSET_DIMENSION),
                    (int) ((current.position.x + GameAssets.ASSET_DIMENSION - 11) / GameAssets.ASSET_DIMENSION));

            if(topLeft.equals(new GridLocation((int)(-1*position.y/GameAssets.ASSET_DIMENSION),(int)(position.x/GameAssets.ASSET_DIMENSION))) ||
               bottomRight.equals(new GridLocation((int)(-1*position.y/GameAssets.ASSET_DIMENSION),(int)(position.x/GameAssets.ASSET_DIMENSION))) ) {
                current.die();
            }

        }


    }


    public void move(float deltaTime) {
            position.x += direction.j * speed * deltaTime;
            position.y -= direction.i * speed * deltaTime;
    }

    private void pickNewDestination() {
        possibleDirections.clear();
        for(int i=0;i<Direction.AllDirections.length;i++) {
            currentPick = Direction.AllDirections[i];
            if(Map.isValidDestination( (int)(-1*position.y/GameAssets.ASSET_DIMENSION)+ currentPick.i,(int)(position.x/GameAssets.ASSET_DIMENSION)+currentPick.j)) {
                possibleDirections.add(currentPick);
            }
        }
        if(direction != null && possibleDirections.contains(direction)) { // 7x extra chance for current direction, looks more natural
            for(int i=0;i<6;i++){
                possibleDirections.add(direction);
            }
        }

        if(possibleDirections.size() == 0){
            currentTime = 0.0f;
            destinationArrivalTime = (float)GameAssets.ASSET_DIMENSION/speed; // assume we move for one block in order to avoid checking frequently in simulate method
            direction = null;
        } else {
            direction =  possibleDirections.get(World.randomSource.nextInt(possibleDirections.size()));
            //Log.d("Bomberman","Robot Direction: "+direction.i+","+direction.j);
            destination.x = position.x + direction.j* GameAssets.ASSET_DIMENSION;
            destination.y = position.y - direction.i* GameAssets.ASSET_DIMENSION;
            currentTime = 0.0f;
            destinationArrivalTime = direction.j * ((destination.x - position.x)/speed) - direction.i * ((destination.y - position.y)/speed);

            //Log.d("Bomberman", Integer.toString((((BombingActivity)game).multiplayer && ((BombingActivity)game).isOwner)?1:0) );
            if( ((BombingActivity)game).multiplayer &&((BombingActivity)game).isOwner ){
                String msg = "ROBOT "+id+" "+(int)(position.x/GameAssets.ASSET_DIMENSION)+" "+(int)(-1*position.y/GameAssets.ASSET_DIMENSION)+" "+direction.dir;
                ((BombingActivity)game).sendMessage(msg);
                Log.d("Bomberman", "Sent: " + msg);
            }
        }


    }

    public void moveIssued(Direction currentPick) {


        if (direction == null && Map.isValidDestination((int) (-1 * position.y / GameAssets.ASSET_DIMENSION) + currentPick.i, (int) (position.x / GameAssets.ASSET_DIMENSION) + currentPick.j)) {
            direction = currentPick;
            destination.x = position.x + direction.j * GameAssets.ASSET_DIMENSION;
            destination.y = position.y - direction.i * GameAssets.ASSET_DIMENSION;
            currentTime = 0.0f;
            destinationArrivalTime = direction.j * ((destination.x - position.x) / speed) - direction.i * ((destination.y - position.y) / speed);
        }
        else if( Map.isValidDestination((int) (-1 * position.y / GameAssets.ASSET_DIMENSION) + currentPick.i, (int) (position.x / GameAssets.ASSET_DIMENSION) + currentPick.j)) {
            direction = currentPick;
            destination.x = position.x + direction.j * GameAssets.ASSET_DIMENSION;
            destination.y = position.y - direction.i * GameAssets.ASSET_DIMENSION;
            currentTime = 0.0f;
            destinationArrivalTime = direction.j * ((destination.x - position.x) / speed) - direction.i * ((destination.y - position.y) / speed);

        }
    }


    public void draw(GL10 gl, Vertices generalModel, Texture robotTexture,Texture robotCrispyTexture) {
        if(!isDying) {
            gl.glEnable(GL10.GL_TEXTURE_2D);
            robotTexture.bind();

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(position.x,position.y,0);
            generalModel.draw(GL10.GL_TRIANGLES,0,6);
        }
        else {

            gl.glEnable(GL10.GL_TEXTURE_2D);
            robotCrispyTexture.bind();

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
