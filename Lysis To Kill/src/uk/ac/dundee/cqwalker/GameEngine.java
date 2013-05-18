package uk.ac.dundee.cqwalker;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import uk.ac.dundee.cqwalker.Model.GameState;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.content.SharedPreferences;

public class GameEngine
{
	private Random random;
	private Model model;
	private Context context;
	private SurfaceView surfaceView;
	private SoundPlayer soundPlayer;
	private ArrayList<Cell> allCells;
	private ArrayList<Blob> allBlobs;
	private Bitmap[] bitmapsGood;
	private Bitmap[] bitmapsEvil;
	private Bitmap bitmapsBlob;
	private Bitmap bitmapsQmark;
	private Paint colourGreyLine;
	private Paint colourScoreText;
	private Paint colourInfoText;
	private Paint colourInfoBackground;
	private Paint colourGameOver;
	private Paint colourLevelUp;
	private Timer timer;
	private int[] rainbow;

	public GameEngine(Activity activity, Context context, SurfaceView surfaceView)
	{
		random = new Random();
		model = new Model();
		this.context = context;
		this.surfaceView = surfaceView;
		initColours();
		loadHighScore();
	}

	private void initColours()
	{
		colourGreyLine = new Paint();
		colourGreyLine.setColor(Color.argb(255, 100, 100, 100));
		colourScoreText = new Paint();
		colourScoreText.setColor(Color.argb(255, 0, 0, 200));
		colourScoreText.setTextSize(DIPtoPt(22));
		colourScoreText.setAntiAlias(true);
		colourInfoText = new Paint();
		colourInfoText.setColor(Color.argb(255, 60, 230, 60));
		colourInfoText.setTextSize(DIPtoPt(13));
		colourInfoText.setAntiAlias(true);
		colourInfoBackground = new Paint();
		colourInfoBackground.setColor(Color.argb(255, 50, 50, 50));
		colourGameOver = new Paint();
		colourGameOver.setStrokeWidth(2f);
		colourGameOver.setTextSize(DIPtoPt(22));
		colourGameOver.setAntiAlias(true);
		colourLevelUp = new Paint();
		colourLevelUp.setStrokeWidth(2f);
		colourLevelUp.setTextSize(DIPtoPt(20));
		colourLevelUp.setAntiAlias(true);
		// initialise rainbow colours for level up / game over screens
		rainbow = new int[20];
		rainbow[0] = Color.argb(255, 255, 10, 10); // RED
		rainbow[1] = Color.argb(255, 255, 255, 10); // YELLOW
		rainbow[2] = Color.argb(255, 10, 255, 10); // GREEN
		rainbow[3] = Color.argb(255, 10, 255, 255); // CYAN
		rainbow[4] = Color.argb(255, 10, 10, 255); // BLUE
		rainbow[5] = Color.argb(255, 255, 10, 255); // PURPLE
	}

	private void loadHighScore()
	{
		SharedPreferences gameSettings = context.getSharedPreferences("LysisToKillPrefs",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = gameSettings.edit();
		if (gameSettings.getInt("High Score", -1) == -1)
		{
			prefEditor.putInt("High Score", 0);
			prefEditor.commit();
		}
		model.hiscore(gameSettings.getInt("High Score", 0));
	}

	private void saveHighScore()
	{
		SharedPreferences gameSettings = context.getSharedPreferences("LysisToKillPrefs",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = gameSettings.edit();
		prefEditor.putInt("High Score", model.hiscore());
		prefEditor.commit();
	}

	public void Init(Resources R)
	{
		// Initiate arrays to hold bitmaps for rendering
		bitmapsGood = new Bitmap[4];
		bitmapsEvil = new Bitmap[4];
		for (int bits = 1; bits < 4; bits++)
		{
			int resourceIDgood = R.getIdentifier("uk.ac.dundee.cqwalker:drawable/a_" + bits, null, null);
			int resourceIDevil = R.getIdentifier("uk.ac.dundee.cqwalker:drawable/b_" + bits, null, null);
			Bitmap good = BitmapFactory.decodeResource(surfaceView.getResources(), resourceIDgood);
			Bitmap evil = BitmapFactory.decodeResource(surfaceView.getResources(), resourceIDevil);
			bitmapsGood[bits] = good;
			bitmapsEvil[bits] = evil;
		}
		// And load up 'Blob' bitmap
		int resourceID = R.getIdentifier("uk.ac.dundee.cqwalker:drawable/blob", null, null);
		bitmapsBlob = BitmapFactory.decodeResource(surfaceView.getResources(), resourceID);
		// Question mark for info screen
		resourceID = R.getIdentifier("uk.ac.dundee.cqwalker:drawable/qmark", null, null);
		bitmapsQmark = BitmapFactory.decodeResource(surfaceView.getResources(), resourceID);
		// Now create all the cells and blobs
		model.initSprites();
		// Start a new sound player
		soundPlayer = new SoundPlayer(this.context, R);
	}

	public void Update()
	{
		// take care of flying blobs
		ArrayList<Blob> blobsToDelete;
		blobsToDelete = new ArrayList<Blob>();
		ArrayList<Blob> allBlobs = model.getBlobs();
		for (int blobIdx = 0; blobIdx < allBlobs.size(); blobIdx++)
		{
			Blob thisBlob = allBlobs.get(blobIdx);
			int xadj = model.speed();
			int yadj = model.speed();
            switch (thisBlob.direction())
            {
                case 1:
                    yadj = -yadj;
                    xadj = 0;
                    break;
                case 2:
                    yadj = 0;
                    break;
                case 3:
                    xadj = 0;
                    break;
                case 4:
                    xadj = -xadj;
                    yadj = 0;
                    break;
            }
			// Now update blob position....
			thisBlob.x(thisBlob.x() + xadj);
			thisBlob.y(thisBlob.y() + yadj);
			// And check for collisions
			if (hasBlobHitCell(thisBlob))
			{
				blobsToDelete.add(thisBlob);
				cycleCell(thisBlob.x(), thisBlob.y(), true, true);
			}
			if (isBlobOffScreen(thisBlob))
			{
				blobsToDelete.add(thisBlob);
			}
		}
		ListIterator<Blob> listIterator2 = blobsToDelete.listIterator();
		while (listIterator2.hasNext())
		{
			allBlobs.remove(listIterator2.next());
		}
		// Finally, check for level up / game over
		if (model.levelComplete())
		{
			if (model.GameState() != GameState.LEVELUP)
			{
				levelUp();
			}
		}
		if ((model.availableBlobs() == 0) && (model.getBlobs().size() == 0))
		{
			if (model.GameState() != GameState.GAMEOVER)
			{
				gameOver();
			}
		}
	}

	private void levelUp()
	{
		if (model.getBlobs().size() == 0)
		{
			model.GameState(GameState.LEVELUP);
			soundPlayer.playSound("levelup");
			model.incrementLevel();
			model.incrementsAvailableBlobs();
			model.incrementScore(100); // Level Up Bonus
			boolean allClear = true;
			for (int cellNo = 0; cellNo < model.getCells().size(); cellNo++)
			{
				Cell thisCell = model.getCells().get(cellNo);
				if (thisCell.state() != 0)
				{
					allClear = false;
				}
			}
			if (allClear)
			{
				model.incrementScore(200); // Grid clearance bonus
			}
			model.resetGame(false);
		}
	}

	private void gameOver()
	{
		if (model.getBlobs().size() == 0)
		{
			model.GameState(GameState.GAMEOVER);
			soundPlayer.playSound("gameover");
			// Quick update of hi score...
			if (model.score() > model.hiscore())
			{
				model.hiscore(model.score());
				saveHighScore();
			}
			model.resetGame(true);
		}
	}

	public void Draw(Canvas canvas)
	{
		switch (model.GameState())
		{
		case SPLASH:
			renderSPLASH(canvas);
			break;
		case PLAY:
			renderPLAY(canvas);
			break;
		case INFO:
			renderINFO(canvas);
			break;
		case GAMEOVER:
			renderGAMEOVER(canvas);
			break;
		case LEVELUP:
			renderLEVELUP(canvas);
			break;
		}
	}

	private void renderPLAY(Canvas canvas)
	{
		allCells = model.getCells();
		allBlobs = model.getBlobs();
		model.levelComplete(true);
		Cell currentCell;
		Blob currentBlob;
		Rect dest = new Rect();
		for (int thisCell = 0; thisCell < allCells.size(); thisCell++)
		{
			currentCell = allCells.get(thisCell);
			if (currentCell.state() == 0)
			{
				continue;
			}
			if ((!currentCell.friendly()) && (currentCell.state() != 0))
			{
				model.levelComplete(false);
			}
			Bitmap cellImage = currentCell.friendly() ? bitmapsGood[currentCell.state()]
					: bitmapsEvil[currentCell.state()];
			dest = new Rect();
			dest.set(currentCell.x(), currentCell.y(), currentCell.x() + model.spriteCellW() - 1, currentCell.y() + model.spriteCellH() - 1);
			canvas.drawBitmap(cellImage, null, dest, null);
		}
		for (int thisBlob = 0; thisBlob < allBlobs.size(); thisBlob++)
		{
			currentBlob = allBlobs.get(thisBlob);
			dest.set(currentBlob.x(), currentBlob.y(), currentBlob.x() + model.spriteBlobW() - 1, currentBlob.y() + model.spriteBlobH() - 1);
			canvas.drawBitmap(bitmapsBlob, null, dest, null);
		}
		blitOverlay(canvas);
	}

	private void renderINFO(Canvas canvas)
	{
		// Back colour
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), colourInfoBackground);
		// Text setup
		ArrayList<String> textLines = new ArrayList<String>();
		textLines.add("          Lysis To Kill (Ver: 1.1 by Chris Walker)");
		textLines.add("");
		textLines.add("This game was written in connection with the");
		textLines.add("iGem 2012 competition. The Dundee team");
		textLines.add("worked on producing a synthetic E-coli which");
		textLines.add("could attack (or lyse) C. diff cells.");
		textLines.add("");
		textLines.add("In this game, you can kill the C.diff cells");
		textLines.add("(in red) by bursting your E.coli cells (green).");
		textLines.add("Each time you click a green cell, it shrinks.");
		textLines.add("You can only click a certain number of times,");
		textLines.add("so use your clicks wisely!");
		textLines.add("");
		textLines.add("It's a lot easier to play than describe, so it");
		textLines.add("is probably better to just start prodding your");
		textLines.add("finger at the green cells, and you'll get the");
		textLines.add("idea behind the game soon enough :-)");
		textLines.add("");
		textLines.add("To learn more, visit our project links: ");
		textLines.add("");
		textLines.add("        http://2012.igem.org/Team:Dundee");
		textLines.add("        http://dundeeigem.blogspot.com");
		textLines.add("");
		textLines.add("This game is released under GNU GPL 3 license.");
		textLines.add("");
		textLines.add("               << tap screen to exit >>");
		int yStep = (int) (Math.abs(colourInfoText.ascent()) + Math.abs(colourInfoText.descent()));
		int y = 10 + yStep;
		for (int line = 0; line < textLines.size(); line++)
		{
			String currentLine = textLines.get(line);
			// Get text positions...
			float[] widths = new float[currentLine.length()];
			int n = colourInfoText.getTextWidths(currentLine, widths);
			float[] pos = new float[n * 2];
			float accumulatedX = 10;
			for (int i = 0; i < n; i++)
			{
				pos[i * 2 + 0] = accumulatedX;
				pos[i * 2 + 1] = y;
				accumulatedX += widths[i];
				if (accumulatedX > canvas.getWidth() - widths[i])
				{
					y += yStep;
					accumulatedX = 10;
				}
			}
			canvas.drawPosText(currentLine, pos, colourInfoText);
			y += yStep;
		}
		textLines.clear();
	}

	private void renderGAMEOVER(Canvas canvas)
	{
		if (timer == null)
		{
			timer = new Timer();
			timer.startTimer(5000);
		}
		if (timer.timerExpired())
		{
			model.GameState(GameState.PLAY);
			timer = null;
		}
		else
		{
			// Render game over screen
			Paint backgroundPaint = new Paint();
			backgroundPaint.setColor(Color.argb(255, 255, 90, 90));
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
            String text = "GAME OVER   GAME OVER";
            float textWidth = colourGameOver.measureText(text);
            int x = (int)(canvas.getWidth() - textWidth) / 2;
			for (int y = 60; y < canvas.getHeight(); y += canvas.getHeight() / 5)
			{
				colourGameOver.setColor(rainbow[getRandomInt(0, 6)]);
				canvas.drawText(text, x, y, colourGameOver);
			}
		}
	}

	private void renderLEVELUP(Canvas canvas)
	{
		if (timer == null)
		{
			timer = new Timer();
			timer.startTimer(3000);
		}
		if (timer.timerExpired())
		{
			model.GameState(GameState.PLAY);
			timer = null;
		}
		else
		{
			// Render level up screen
			Paint backgroundPaint = new Paint();
			backgroundPaint.setColor(Color.argb(255, 110, 110, 255));
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
            String text = "LEVEL UP  LEVEL UP  LEVEL UP";
            float textWidth = colourLevelUp.measureText(text);
            int x = (int)(canvas.getWidth() - textWidth) / 2;
			for (int y = 20; y < canvas.getHeight(); y += canvas.getHeight() / 15)
			{
				colourLevelUp.setColor(rainbow[getRandomInt(0, 6)]);
				canvas.drawText(text, x, y, colourLevelUp);
			}
		}
	}

	private void renderSPLASH(Canvas canvas)
	{
		if (timer == null)
		{
			timer = new Timer();
			timer.startTimer(5000);
		}
		if (timer.timerExpired())
		{
			model.GameState(GameState.PLAY);
			timer = null;
		}
		else
		{
			// Render splash screen
			Paint backgroundPaint1 = new Paint();
			Paint backgroundPaint2 = new Paint();
			backgroundPaint1.setColor(Color.argb(255, 80, 255, 80));
			backgroundPaint2.setColor(Color.argb(255, 40, 200, 40));
			Paint tempPaint = new Paint();
			tempPaint.setStrokeWidth(2f);
			tempPaint.setAntiAlias(true);
			tempPaint.setTextSize(DIPtoPt(28));
			canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint1);
			canvas.drawRect(10, 10, canvas.getWidth()-10, canvas.getHeight()-10, backgroundPaint2);
			int lineHeight = 12 + (int)Math.abs(tempPaint.ascent()) + (int)Math.abs(tempPaint.descent());
			int y = canvas.getHeight() / 3;
            int x ;
			tempPaint.setColor(rainbow[getRandomInt(0, 6)]);
            String text = "Lysis To Kill";
            float textWidth = tempPaint.measureText(text);
            x = (int)(canvas.getWidth() - textWidth) / 2;
			canvas.drawText(text, x, y, tempPaint);
			y += 2 * lineHeight;
            text = "By Chris Walker";
            textWidth = tempPaint.measureText(text);
            x = (int)(canvas.getWidth() - textWidth) / 2;
			canvas.drawText(text, x, y, tempPaint);
			y += lineHeight;
            text = "Dundee University";
            textWidth = tempPaint.measureText(text);
            x = (int)(canvas.getWidth() - textWidth) / 2;
			canvas.drawText(text, x, y, tempPaint);
			y += lineHeight;
            text="iGEM Team 2012";
            textWidth = tempPaint.measureText(text);
            x = (int)(canvas.getWidth() - textWidth) / 2;
			canvas.drawText(text, x, y, tempPaint);
		}
	}

	private void blitOverlay(Canvas canvas)
	{
		int h, w, b, vs, hs, x1, y1;
		h = model.gridHeight();
		w = model.gridWidth();
		b = model.border();
		vs = model.spacingV();
		hs = model.spacingH();
		// Render grid
		for (int row = 0; row <= w; row++)
		{
			canvas.drawLine(b, b + (row * vs), b + (w * hs), b + (row * hs), colourGreyLine);
		}
		for (int col = 0; col <= h; col++)
		{
			canvas.drawLine(b + (col * hs), b, b + (col * hs), b + (h * vs), colourGreyLine);
		}
		// Render score & available blobs
		x1 = b;
		y1 = (4 * b) + (vs * h) + (vs/2);
        int textSpacingV = (canvas.getHeight() - y1 ) / 3;
        int textSpacingH = (canvas.getWidth() / 3);
		String scoreL = "Score : ";
		String scoreR = model.score() + " [Hi : " + model.hiscore() + "]";
		canvas.drawText(scoreL, x1, y1, colourScoreText);
		canvas.drawText(scoreR, x1 + textSpacingH, y1, colourScoreText);
		String blobsToGoL = "Clicks Left : ";
		String blobsToGoR = "" + model.availableBlobs();
		canvas.drawText(blobsToGoL, x1, y1 + textSpacingV, colourScoreText);
		canvas.drawText(blobsToGoR, x1 + textSpacingH, y1 + textSpacingV, colourScoreText);
		String levelL = "Level : ";
		String levelR = "" + model.level();
		canvas.drawText(levelL, x1, y1 + (2 * textSpacingV), colourScoreText);
		canvas.drawText(levelR, x1 + textSpacingH, y1 + (2 * textSpacingV), colourScoreText);
		// Finally render info bitmap
		x1 = canvas.getWidth() - bitmapsQmark.getWidth() - 10;
		y1 = canvas.getHeight() - bitmapsQmark.getHeight() - 10;
		canvas.drawBitmap(bitmapsQmark, x1, y1, null);
	}

	public int getRandomInt(int min, int max)
	{
		return ((random.nextInt((max - min) + 1) + min));
	}

	private void cycleCell(float x, float y, boolean force, boolean fromBlob)
	{
		int row = (int) (y / model.spacingV());
		int col = (int) (x / model.spacingH());
		Cell target;
		int index = (row * model.gridWidth()) + col;
		if (index < model.getCells().size())
		{
			target = model.getCells().get(index);
			if (target.state() == 0)
			{
				// Set empty space to friendly so we can grow new cells here
				target.friendly(true);
			}
			if (target.friendly() || (force))
			{
				if (((fromBlob) && (target.state() != 0)) || (!fromBlob))
				{
					// Prevent action in between moves
					if (!fromBlob)
					{
						// User initiated fire
						if ((model.getBlobs().size() != 0) || (model.GameState() != GameState.PLAY))
						{
							return;
						}
					}
					target.state(target.state() == 0 ? 3 : target.state() - 1);
					if (target.state() != 0)
					{
						soundPlayer.playSound("sizeup");
					}
					// was it enemy? If so, increase score
					if (!target.friendly() && (target.state() == 0))
					{
						soundPlayer.playSound("extrablob");
						model.incrementScore();
						model.incrementsAvailableBlobs();
					}
				}
				if ((target.state() == 0) && (target.friendly())) // Only friendly cells spawn blobs
                //if (target.state() == 0)      // Any cells spawn blobs
				{
					soundPlayer.playSound("pop");
					model.createBlob(row, col, (short) 1);
					model.createBlob(row, col, (short) 2);
					model.createBlob(row, col, (short) 3);
					model.createBlob(row, col, (short) 4);
				}
			}
			if (target.friendly() && !fromBlob)
			{
				model.decrementAvailableBlobs();
			}
		}
	}

	public void triggerCell(float x, float y)
	{
		// Check if info graphic clicked
		if (model.GameState() == GameState.PLAY)
		{
			int infoW = bitmapsQmark.getWidth();
			int infoH = bitmapsQmark.getHeight();
			if ((x > surfaceView.getWidth() - 10 - infoW) && (y > surfaceView.getHeight() - 10 - infoH))
			{
				model.GameState(GameState.INFO);
			}
			// Check for in-game grid click
			boolean gridClick = true;
			int w = model.gridWidth();
			int h = model.gridHeight();
			int b = model.border();
			int hs = model.spacingH();
			int vs = model.spacingV();
			if ((x < b) || (y < b))
				gridClick = false;
			if ((x > b + (hs * w)) || (y > b + (vs * h)))
				gridClick = false;
			if (gridClick)
			{
				cycleCell(x, y, false, false);
			}
			return;
		}
		if (model.GameState() == GameState.INFO)
		{
			model.GameState(GameState.PLAY);
			return;
		}
	}

	private Cell getCellAtCoords(int x, int y)
	{
		Cell occupant = null;
		int radius = 10;
		boolean found;
		ArrayList<Cell> allCells = model.getCells();
		for (int cellIdx = 0; cellIdx < allCells.size(); cellIdx++)
		{
			found = true;
			Cell thisCell = allCells.get(cellIdx);
			if (Math.abs(thisCell.x() - x) > radius)
				found = false;
			if (Math.abs(thisCell.y() - y) > radius)
				found = false;
			if (found)
			{
				if (thisCell.state() != 0)
				{
					occupant = thisCell;
				}
				break;
			}
		}
		return occupant;
	}

	private boolean isBlobOffScreen(Blob blob)
	{
		if (blob.x() > model.border() + ((model.gridWidth() - 1) * model.spacingH() + (model.spacingH() / 2)))
			return true;
		if (blob.y() > model.border()
				+ ((model.gridHeight() - 1) * model.spacingV() + (model.spacingV() / 2)))
			return true;
		if ((blob.x() < model.border() - (model.spacingH() / 2))
				|| (blob.y() < model.border() - (model.spacingV() / 2)))
			return true;
		return false;
	}

	private boolean hasBlobHitCell(Blob blob)
	{
		// Stop original location from triggering hit
		if ((Math.abs(blob.xStart - blob.x()) < (model.spriteCellW() * 0.8))
				&& (Math.abs(blob.yStart - blob.y()) < (model.spriteCellH() * 0.8)))
		{
			return false;
		}
		return getCellAtCoords(blob.x(), blob.y()) != null ? true : false;
	}

	public void setCellSpacing(int screenWidth, int screenHeight)
	{
		int spacing = (screenWidth - (2 * model.border())) / 5;
		model.spacingV(spacing);
		model.spacingH(spacing);
	}

	public int spacingV()
	{
		return model.spacingV();
	}

	public int spacingH()
	{
		return model.spacingH();
	}

	private int DIPtoPt(float DIP)
	{
		float scale = this.context.getResources().getDisplayMetrics().density;
		return (int) (DIP * scale + 0.5f);
	}
}
