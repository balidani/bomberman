package pt.ulisboa.tecnico.bomberman.game;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class Config {
	
	public static int gameDuration;
	public static int explosionTimeOut;
	public static int explosionDuration;
	public static int explosionRange;
	public static int robotSpeed;
	public static int pointsPerRobot;
	public static int pointsPerOpponent;
	public static String mapString;
	
	public static void LoadGameSettings(GameActivity game, int level)
	{
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();
			String fileName="level_"+level+".xml";
		    InputStream in_s = game.getApplicationContext().getAssets().open(fileName);
	        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            ParseXML(parser);

		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void ParseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
        int eventType = parser.getEventType();
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
                    	String[] mapRows=parser.nextText().split("\n");
                    	StringBuilder sb=new StringBuilder();
                    	for(String row : mapRows)
                    	{
                    		sb.append(row.trim()+"\n");
                    	}
                    	mapString=sb.toString();
                    }
                    break;
            }
            eventType = parser.next();
        }

	}

}