package uk.ac.dundee.cqwalker;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class View extends SurfaceView implements SurfaceHolder.Callback
{
	long lastUpdate = 0;
	long sleepTime = 0;
	GameEngine gEngine;
	SurfaceHolder surfaceHolder;
	Context context;
	Activity activity;
	private PaintThread thread;
	int screenWidth, screenHeight;

	void InitView()
	{
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		gEngine = new GameEngine(this.activity, this.context, this);
		thread = new PaintThread(holder, context, new Handler(), gEngine);
		setFocusable(true);
		// Get screen metrics
		Context ctx = getContext();
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();		
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		gEngine.setCellSpacing(screenWidth, screenHeight);
		gEngine.Init(context.getResources());
	}

	public View(Context contextS, AttributeSet attrs, int defStyle)
	{
		super(contextS, attrs, defStyle);
		context = contextS;		
		InitView();
	}

	public View(Activity activity, Context contextS, AttributeSet attrs)
	{
		super(contextS, attrs);
		context = contextS;
		this.activity = activity;
		InitView();
	}

	// **************
	// EVENT HANDLING
	// **************
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean touched = false;
		float touched_x = event.getX();
		float touched_y = event.getY();

		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			touched = true;
			gEngine.triggerCell(touched_x, touched_y);
			break;
		case MotionEvent.ACTION_MOVE:
			touched = true;
			break;
		case MotionEvent.ACTION_UP:
			touched = false;
			break;
		case MotionEvent.ACTION_CANCEL:
			touched = false;
			break;
		case MotionEvent.ACTION_OUTSIDE:
			touched = false;
			break;
		default:
		}
		return touched; // processed
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		boolean retry = true;
		thread.state = PaintThread.PAUSED;
		while (retry)
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		if (thread.state == PaintThread.PAUSED)
		{
			thread = new PaintThread(getHolder(), context, new Handler(), gEngine);
			thread.start();
		}
		else
		{
			thread.start();
		}
	}

}