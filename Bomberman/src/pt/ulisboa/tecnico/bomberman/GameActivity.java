package pt.ulisboa.tecnico.bomberman;

import java.util.ArrayList;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class GameActivity extends Activity {
	
	public final int EMPTY=0;
	public final int WALL=1;
	public final int OBSTACLE=2;
	public final int ROBOT=3;
	public final int PLAYER=4;
	
	public final String SEP_COLON=":";
	public final String SEP_PIPE="|";
	public final String SEP_COMMA=",";
	public final String SLASH="\\";
	
	public final int UP=0;
	public final int RIGHT=1;
	public final int DOWN=2;
	public final int LEFT=3;
	
	public final int RANDOM_MOVEMENT_OPTIONS=4;
	
	GridLayout gameGrid;
	int item;
	BoardInfo boardInfo;
	CellInfo[][] cellInfo;
	ArrayList<Location> wallList;
	ArrayList<Location> obstacleList;
	ArrayList<Location> robotList;
	ArrayList<Location> playersList;
	Location currentUserLocation;
	Random rand;
	Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);	
		rand=new Random();
		boardInfo=new BoardInfo(1);
		SetGridProperties();
		SetBoardDimensions(); 
		wallList=LoadInitElements(boardInfo.wallLocations);
		obstacleList=LoadInitElements(boardInfo.obstacleLocations);
		robotList=LoadInitElements(boardInfo.robotLocations);
		playersList=LoadInitElements(boardInfo.playerLocatios);
		currentUserLocation=playersList.get(0);
		handler=new Handler();
		DisplayInitialBoard();
		handler.post(robotUpdate);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	public void MoveRight(View view)
	{
		MoveElement(PLAYER,currentUserLocation.X,currentUserLocation.Y+1,currentUserLocation);
	}
	
	public void MoveLeft(View view)
	{
		MoveElement(PLAYER,currentUserLocation.X,currentUserLocation.Y-1,currentUserLocation);
	}
	
	public void MoveUp(View view)
	{
		MoveElement(PLAYER,currentUserLocation.X-1,currentUserLocation.Y,currentUserLocation);
	}
	
	public void MoveDown(View view)
	{
		MoveElement(PLAYER,currentUserLocation.X+1,currentUserLocation.Y,currentUserLocation);
	}	
	
	
	public synchronized void MoveElement(int elementType,int nextX, int nextY,Location currentLocation)
	{
		if(elementType==PLAYER)
		{
			if(nextX>(boardInfo.rowcount-1))
				nextX=0;
			else if(nextX<0)
				nextX=(boardInfo.rowcount-1);
			
			if(nextY>(boardInfo.columncount-1))
				nextY=0;
			else if(nextY<0)
				nextY=(boardInfo.columncount-1);
			
			if(cellInfo[nextX][nextY].elementType==EMPTY)
			{
				cellInfo[nextX][nextY].elementType=PLAYER;
				cellInfo[nextX][nextY].cellImg.setImageResource(R.drawable.player_1);
				cellInfo[currentLocation.X][currentLocation.Y].elementType=EMPTY;
				cellInfo[currentLocation.X][currentLocation.Y].cellImg.setImageResource(R.drawable.empty);	
				currentLocation.X=nextX;
				currentLocation.Y=nextY;
			}
			
		}
		else if(elementType==ROBOT)
		{
			if(nextX>(boardInfo.rowcount-1))
				nextX=0;
			else if(nextX<0)
				nextX=(boardInfo.rowcount-1);
			
			if(nextY>(boardInfo.columncount-1))
				nextY=0;
			else if(nextY<0)
				nextY=(boardInfo.columncount-1);
			
			if(cellInfo[nextX][nextY].elementType==EMPTY)
			{
				cellInfo[nextX][nextY].elementType=ROBOT;
				cellInfo[nextX][nextY].cellImg.setImageResource(R.drawable.robot);
				cellInfo[currentLocation.X][currentLocation.Y].elementType=EMPTY;
				cellInfo[currentLocation.X][currentLocation.Y].cellImg.setImageResource(R.drawable.empty);	
				currentLocation.X=nextX;
				currentLocation.Y=nextY;
			}
		}
		//TODO: add movements to other elements

	}
	
	public void SetGridProperties()
	{
		gameGrid = (GridLayout)findViewById(R.id.gameGrid);
		gameGrid.setColumnCount(boardInfo.columncount);
		gameGrid.setRowCount(boardInfo.rowcount);
	}
	
	public ArrayList<Location> LoadInitElements(String value)
	{
		ArrayList<Location> data=new ArrayList<Location>();
		String[] tempPipeItems=value.split(SLASH+ SEP_PIPE);
		String[] tempColonItems;
		String[] tempCommaItems;
		Location location;
		for(int i=0;i<tempPipeItems.length;i++)
		{
			tempColonItems=tempPipeItems[i].split(SEP_COLON);
			tempCommaItems=tempColonItems[1].split(SEP_COMMA);
			for(int j=0;j<tempCommaItems.length;j++)
			{
				location=new Location(Integer.parseInt(tempColonItems[0]),Integer.parseInt(tempCommaItems[j]));
				data.add(location);
			}
		}
		return data;
	}
	
	public void DisplayInitialBoard()
	{
		ImageView cell;
		cellInfo=new CellInfo[boardInfo.rowcount][boardInfo.columncount];
		CellInfo tmpCellInfo;
		
        for(int i=0;i<boardInfo.rowcount;i++)
        {
        	for(int j=0;j<boardInfo.columncount;j++)
        	{    
        		cell=new ImageView(GameActivity.this);
        		
        		tmpCellInfo=new CellInfo();
        		tmpCellInfo.cellImg=cell;        		
        	    if(CheckElementExist(wallList, i, j))
        	    {
        	    	cell.setImageResource(R.drawable.wall);
        			tmpCellInfo.elementType=WALL;
    	        	gameGrid.addView(cell,boardInfo.boardWidth/(boardInfo.columncount+1),boardInfo.boardHeight/boardInfo.rowcount);  	        	
        	    }
        		else if(CheckElementExist(obstacleList, i, j))
        		{
         			cell.setImageResource(R.drawable.obstacle);
        			tmpCellInfo.elementType=OBSTACLE;
    	        	gameGrid.addView(cell,boardInfo.boardWidth/(boardInfo.columncount+1),boardInfo.boardHeight/boardInfo.rowcount);
    	        }
        		else if(CheckElementExist(robotList, i, j))
        		{
         			cell.setImageResource(R.drawable.robot);
        			tmpCellInfo.elementType=ROBOT;
    	        	gameGrid.addView(cell,boardInfo.boardWidth/(boardInfo.columncount+1),boardInfo.boardHeight/boardInfo.rowcount);
    	        }
        		else if(CheckElementExist(playersList, i, j))
        		{
         			cell.setImageResource(R.drawable.player_1);
        			tmpCellInfo.elementType=PLAYER;
    	        	gameGrid.addView(cell,boardInfo.boardWidth/(boardInfo.columncount+1),boardInfo.boardHeight/boardInfo.rowcount);
    	        }	
        		else
        		{
        			cell.setImageResource(R.drawable.empty);
        			tmpCellInfo.elementType=EMPTY;
    	        	gameGrid.addView(cell,boardInfo.boardWidth/(boardInfo.columncount+1),boardInfo.boardHeight/boardInfo.rowcount);
        		} 
        		cellInfo[i][j]=tmpCellInfo;
        	}
        }
	}
	
	public void SetBoardDimensions()
	{
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		boardInfo.boardWidth = size.x;
		boardInfo.boardHeight = gameGrid.getLayoutParams().height;
	}

	public boolean CheckElementExist(ArrayList<Location> list,int X, int Y)
	{
		boolean isExists=false;
		for(Location item : list)
		{
			if(item.X==X && item.Y==Y )
			{
				isExists=true;
				break;
			}
			
		}
		return isExists;
	}
	
	private Runnable robotUpdate=new Runnable()
	{
		
		public void run()
		{
			ActivateRobots();
		}
		
		public void ActivateRobots()
		{	
			if(robotList.size()>0)
			{
				try
				{

					int randVal;
					for(int i=0;i<robotList.size();i++)
					{
						randVal=rand.nextInt(RANDOM_MOVEMENT_OPTIONS);
						if(randVal==UP)
						{
							MoveElement(ROBOT, robotList.get(i).X-1, robotList.get(i).Y, robotList.get(i));
							
						}
						else if(randVal==RIGHT)
						{
							MoveElement(ROBOT, robotList.get(i).X, robotList.get(i).Y+1, robotList.get(i));
							
						}
						else if(randVal==DOWN)
						{
							MoveElement(ROBOT, robotList.get(i).X+1, robotList.get(i).Y, robotList.get(i));
							
						}
						else if(randVal==LEFT)
						{
							MoveElement(ROBOT, robotList.get(i).X, robotList.get(i).Y-1, robotList.get(i));
							
						}
					
					}
				}
			
				catch(Exception ex)
				{
					//this will happen when main thread remove a robot. This is expected
					String msg=ex.getMessage();
				}
				finally
				{
					handler.postDelayed(this, boardInfo.robotSpeed);
				}		
			}
			else
			{
				handler.removeCallbacks(this);
			}
        }			 
	};

}

class Location
{
	int X;
	int Y;
	
	public Location(int X, int Y)
	{
		this.X=X;
		this.Y=Y;
	}
}

class CellInfo
{
	int elementType;
	ImageView cellImg;
}

class BoardInfo
{
	String levelName;
	int gameDuration;
	int explosionTimeOut;
	int explosionDuration;
	int explosionRange;
	int robotSpeed;
	int pointsPerRobot;
	int pointsPerOpponent;
	
	int rowcount;
	int columncount;
	int boardWidth;
	int boardHeight;
	String obstacleLocations;
	String robotLocations;
	String playerLocatios;
	String wallLocations;
		
	public BoardInfo(int level)
	{
		LoadDataConfig(level);
	}
	
	public void LoadDataConfig(int level)
	{
		//TODO: these data should be filled from a config file.
		robotSpeed=1000; //this is in milli seconds
		
		rowcount=9;
		columncount=9;
		wallLocations="1:1,3,5|3:1,3,5|5:1,3,4|7:1,3,5,7";
		obstacleLocations="0:3,5|1:4,6,8|2:0,1,5,8|3:2,4|4:1,8|5:0,2|6:1,2,3|7:6";
		robotLocations="2:3|3:0|4:2";
		playerLocatios="0:0";
	}	
}



