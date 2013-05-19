package uk.ac.dundee.cqwalker;

import java.util.ArrayList;
import java.util.Random;

public class Model
{
	private ArrayList<Cell> cells;
	private ArrayList<Blob> blobs;
	private int gridWidth, gridHeight, border;
	private int spriteCellW, spriteCellH;
	private int spriteBlobW, spriteBlobH;
	private int score, hiscore;
	private int scoreIncrement;
	private int level;
	private int speed;
	private int spacingV, spacingH;
	private int availableBlobs;
	private boolean levelComplete;
	private GameState GAMESTATE;
	Random random;

	public enum GameState
	{
		PLAY, INFO, GAMEOVER, LEVELUP, SPLASH
	}

	public Model()
	{
		gridWidth = gridHeight = 5;
		spriteCellW = spriteCellH = spriteBlobW = spriteBlobH = 120;
		border = 10;
		score = 0;
		hiscore = 0;
		scoreIncrement = 10;
		speed = spriteCellW / 6;
		level = 1;
		levelComplete = false;
		availableBlobs = 10;
		GAMESTATE = GameState.SPLASH;
		random = new Random();
	}

	public void resetGame(boolean fullReset)
	{
		levelComplete = false;
		cells.clear();
		blobs.clear();
		initSprites();
		if (fullReset)
		{
			score = 0;
			availableBlobs = 10;
			level = 1;
		}
	}

	public void initSprites()
	{
		cells = new ArrayList<Cell>();
		for (int row = 0; row < gridHeight; row++)
		{
			for (int col = 0; col < gridWidth; col++)
			{
				createCell(row, col);
			}
		}
		blobs = new ArrayList<Blob>();
	}

	public ArrayList<Cell> getCells()
	{
		return cells;
	}

	public ArrayList<Blob> getBlobs()
	{
		return blobs;
	}

	public int gridWidth()
	{
		return gridWidth;
	}

	public int gridHeight()
	{
		return gridHeight;
	}

	public int border()
	{
		return border;
	}

	public void spacingV(int spacingV)
	{
		this.spacingV = spacingV;
	}

	public int spacingV()
	{
		return this.spacingV;
	}

	public void spacingH(int spacingH)
	{
		this.spacingH = spacingH;
	}

	public int spacingH()
	{
		return this.spacingH;
	}

	private void createCell(int row, int col)
	{
		Cell newCell = new Cell();
		// Position
		newCell.column(col);
		newCell.row(row);
		// State
        int startState;
        int stateRand = getRandomInt(0, 100);
        if (stateRand < 25)
        {
            startState = 0;
        }
        else
        {
            if (stateRand < 60)
            {
                startState = 1;
            }
            else
            {
                if (stateRand < 75)
                {
                    startState = 2;
                }
                else
                {
                    startState = 3;
                }
            }
        }
        // Difficulty tweaks
        if (getRandomInt(0, 25) <= level)
        {
            startState++;
            startState = startState > 3 ? 3 : startState;
        }
		newCell.state(startState);
		// Friendly status
		boolean friendly;
        int diffFactor = 50 - (2*level) < 0 ? 0 : 50 - (2*level);   // more enemies in further levels
		friendly = startState == 0 ? true : (getRandomInt(0, 100) < 25 + diffFactor ? true : false);
		newCell.friendly(friendly);
		// XY setup top left corner
		int startX = border + (col * spacingH);
		// now add offset to centre within grid....
		startX += (spacingH - spriteCellW) / 2;
		// same for Y....
		int startY = border + (row * spacingV);
		startY += (spacingV - spriteCellH) / 2;
		newCell.x(startX);
		newCell.y(startY);
		newCell.height(spriteCellH);
		newCell.width(spriteCellW);
		cells.add(newCell);
	}

	public void createBlob(int row, int col, short direction)
	{
		Blob newBlob = new Blob(direction);
		// X top left corner
		int startX = border + (col * spacingH);
		// Add offset to centre in grid
		startX += (spacingH - spriteBlobW) / 2;
		// Same for Y
		int startY = border + (row * spacingV);
		startY += (spacingV - spriteBlobH) / 2;
		newBlob.x(startX);
		newBlob.y(startY);
		newBlob.xStart(startX);
		newBlob.yStart(startY);
		newBlob.width(spriteBlobW);
		newBlob.height(spriteBlobH);
		blobs.add(newBlob);
	}

	public void speed(int speed)
	{
		this.speed = speed;
	}

	public int speed()
	{
		return this.speed;
	}

	public void incrementLevel()
	{
		this.level++;
	}

	public void level(int level)
	{
		this.level = level;
	}

	public int level()
	{
		return this.level;

	}

	public void incrementScore()
	{
		score += scoreIncrement;
	}

	public int score()
	{
		return this.score;
	}

	public void score(int score)
	{
		this.score = score;
	}

	public void incrementScore(int incrementBy)
	{
		this.score += incrementBy;
	}

	public boolean levelComplete()
	{
		return this.levelComplete;
	}

	public void levelComplete(boolean levelComplete)
	{
		this.levelComplete = levelComplete;
	}

	public int availableBlobs()
	{
		return this.availableBlobs;
	}

	public void availableBlobs(int availableBlobs)
	{
		this.availableBlobs = availableBlobs;
	}

	public void incrementsAvailableBlobs()
	{
		this.availableBlobs++;
	}

	public void decrementAvailableBlobs()
	{
		this.availableBlobs--;
		this.availableBlobs = this.availableBlobs < 0 ? 0 : this.availableBlobs;
	}

	public GameState GameState()
	{
		return this.GAMESTATE;
	}

	public void GameState(GameState GameState)
	{
		this.GAMESTATE = GameState;
	}

	public int spriteCellW()
	{
		return this.spriteCellW;
	}

	public void spriteCellW(int width)
	{
		this.spriteCellW = width;
	}

	public int spriteCellH()
	{
		return this.spriteCellH;
	}

	public void spriteCellH(int height)
	{
		this.spriteCellH = height;
	}

	public int spriteBlobW()
	{
		return this.spriteBlobW;
	}

	public int spriteBlobH()
	{
		return this.spriteBlobH;
	}

	public void hiscore(int high)
	{
		this.hiscore = high;
	}

	public int hiscore()
	{
		return this.hiscore;
	}

	public int getRandomInt(int min, int max)
	{
		return ((random.nextInt((max - min) + 1) + min));
	}
}
