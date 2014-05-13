package pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.framework.Game;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Player;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.agent.Robot;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map.Bomb;
import pt.ulisboa.tecnico.cmov.emdc.dgs.bomberman.world.map.Map;

/**
 * Created by savasci on 5/2/2014.
 */
public class World {
    public static final char WALL = 'W';
    public static final char OBSTACLE = 'O';
    public static final char EMPTY = '-';
    public static final char ROBOT = 'R';
    public static final char BOMB = 'B';
    public static final char FLAMES = 'F';

    public static Random randomSource;
    int level;
    Game game;
    public Map map;
    public List<Robot> robots;
    public List<Player> players;
    public List<Bomb> bombs;
    public Player myPlayer;
    public static int gameDuration;
    public static int explosionTimeOut;
    public static int explosionDuration;
    public static int explosionRange;
    public static int robotSpeed;
    public static int pointsPerRobot;
    public static int pointsPerOpponent;

    public World(Game game, int level) {
        this.game = game;
        this.level = level;
        this.randomSource = new Random();
        robots = new ArrayList<Robot>();
        players =new ArrayList<Player>();
        bombs = new ArrayList<Bomb>();
        char[][] mapArray = loadLevelConfigs();
        map = new Map(game,mapArray);
    }


    private char[][] loadLevelConfigs()
    {
        char[][] mapArray = null;
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            String fileName="levels/level_"+level+".xml";
            InputStream in_s = game.getFileIO().readAsset(fileName);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            mapArray = parseXML(parser);

        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mapArray;
    }

    private char[][] parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        int eventType = parser.getEventType();
        char[][] map = null;
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            String name = null;
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("GAME_DURATION") )
                    {
                        gameDuration=Integer.parseInt(parser.nextText());
                    }
                    else if(name.equalsIgnoreCase("EXPLOSION_TIMEOUT") )
                    {
                        explosionTimeOut=Integer.parseInt(parser.nextText());
                    }
                    else if(name.equalsIgnoreCase("EXPLOSION_DURATION") )
                    {
                        explosionDuration=Integer.parseInt(parser.nextText());
                    }
                    else if(name.equalsIgnoreCase("EXPLOSION_RANGE") )
                    {
                        explosionRange=Integer.parseInt(parser.nextText());
                    }
                    else if(name.equalsIgnoreCase("ROBOT_SPEED") )
                    {
                        robotSpeed=Integer.parseInt(parser.nextText());
                    }
                    else if(name.equalsIgnoreCase("POINTS_PER_ROBOT") )
                    {
                        pointsPerRobot=Integer.parseInt(parser.nextText());
                    }
                    else if(name.equalsIgnoreCase("POINTS_PER_OPPONENT") )
                    {
                        pointsPerOpponent=Integer.parseInt(parser.nextText());
                    }
                    else if(name.equalsIgnoreCase("MAP_LAYOUT") )
                    {
                        String[] mapRows=parser.nextText().trim().split("\n");
                        for(int i=0;i<mapRows.length;i++) {
                            mapRows[i] = mapRows[i].trim();
                        }

                        int height = mapRows.length;
                        int width = mapRows[0].length();
                        map = new char[height][width];

                        for (int i = 0; i < height; i++) {
                            for (int j = 0; j < width; j++) {

                                switch (mapRows[i].charAt(j)) {
                                    case WALL:
                                    case OBSTACLE:
                                    case EMPTY:
                                        map[i][j] = mapRows[i].charAt(j);
                                        break;
                                    case ROBOT:
                                        map[i][j] = EMPTY;
                                        robots.add(new Robot(game,i,j));
                                        break;
                                    case '1':
                                        map[i][j] = EMPTY;
                                        players.add(new Player(game,i,j,0));
                                        break;
                                    case '2':
                                        map[i][j] = EMPTY;
                                        players.add(new Player(game,i,j,1));
                                        break;
                                    case '3':
                                        map[i][j] = EMPTY;
                                        players.add(new Player(game,i,j,2));
                                        break;
                                }
                            }
                        }

                    }
                    break;
            }
            eventType = parser.next();
        }
        return map;

    }



}
