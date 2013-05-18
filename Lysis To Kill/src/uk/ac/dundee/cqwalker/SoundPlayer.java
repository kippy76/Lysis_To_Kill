package uk.ac.dundee.cqwalker;

import java.util.HashMap;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer
{
	private Context context;
	private Resources R;
	private SoundPool soundPool;
	private HashMap<String, Integer> soundPoolMap; // track resource name,
													// soundID
	private AudioManager audioManager;

	public SoundPlayer(Context context, Resources R)
	{
		this.context = context;
		this.R = R;
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<String, Integer>();
		audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
		initSoundBanks();
	}

	private void initSoundBanks()
	{
		int trackID;
		trackID = this.R.getIdentifier("uk.ac.dundee.cqwalker:raw/pop", null, null);
		soundPoolMap.put("pop", soundPool.load(this.context, trackID, 1));
		trackID = this.R.getIdentifier("uk.ac.dundee.cqwalker:raw/extrablob", null, null);
		soundPoolMap.put("extrablob", soundPool.load(this.context, trackID, 1));
		trackID = this.R.getIdentifier("uk.ac.dundee.cqwalker:raw/levelup", null, null);
		soundPoolMap.put("levelup", soundPool.load(this.context, trackID, 1));
		trackID = this.R.getIdentifier("uk.ac.dundee.cqwalker:raw/gameover", null, null);
		soundPoolMap.put("gameover", soundPool.load(this.context, trackID, 1));
		trackID = this.R.getIdentifier("uk.ac.dundee.cqwalker:raw/sizeup", null, null);
		soundPoolMap.put("sizeup", soundPool.load(this.context, trackID, 1));
	}

	public void playSound(String soundResourceName)
	{		
		float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		soundPool.play(soundPoolMap.get(soundResourceName), streamVolume, streamVolume, 1, 0, 1f);
	}

}
