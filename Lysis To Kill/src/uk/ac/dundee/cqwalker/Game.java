package uk.ac.dundee.cqwalker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Game extends Activity
{

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View game = new View(this, this.getApplication().getBaseContext(), null);
		setContentView(game);
	}
	
	public void onStop()
	{
		super.onDestroy();
		this.finish();
	}

}
