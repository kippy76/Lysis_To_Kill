package uk.ac.dundee.cqwalker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Game extends Activity
{
    View game;

    protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		game = new View(this, this.getApplication().getBaseContext(), null);
		setContentView(game);
	}

    @Override
    protected void onStart()
    {
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        // user navigates back to activity
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        this.finish();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);
    }

}
