package uk.ac.dundee.cqwalker;

import java.util.logging.Level;
import java.util.logging.Logger;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;

public class PaintThread extends Thread
{
	private SurfaceHolder mSurfaceHolder;
	private Paint backgroundPaint;
	GameEngine gEngine;
	private long sleepTime;
	private long delay = 70;
	// state of game (Running or Paused).
	int state = 1;
	public final static int RUNNING = 1;
	public final static int PAUSED = 2;

	public PaintThread(SurfaceHolder surfaceHolder, Context context, Handler handler, GameEngine gEngineS)
	{		
		mSurfaceHolder = surfaceHolder;
		backgroundPaint = new Paint();
		backgroundPaint.setARGB(255, 200, 200, 255);
		gEngine = gEngineS;
	}

	// **************
	// MAIN GAME LOOP
	// **************
	@Override
	public void run()
	{
		// UPDATE
		while (state == RUNNING)
		{
			long beforeTime = System.nanoTime();
			gEngine.Update();
			Canvas c = null;
			try
			{
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder)
				{
					c.drawRect(0, 0, c.getWidth(), c.getHeight(), backgroundPaint);
					gEngine.Draw(c);
				}
			}
			finally
			{
				if (c != null)
				{
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
			this.sleepTime = delay - ((System.nanoTime() - beforeTime) / 1000000L);
			try
			{
				if (sleepTime > 0)
				{
					Thread.sleep(sleepTime);
				}
			}
			catch (InterruptedException ex)
			{
				Logger.getLogger(PaintThread.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}