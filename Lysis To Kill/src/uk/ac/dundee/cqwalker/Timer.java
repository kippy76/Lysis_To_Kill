package uk.ac.dundee.cqwalker;

public class Timer
{
	private int delayms;
	private long startTime;
	
	public Timer()
	{
		delayms = 0;
		startTime = 0;
	}
		
	public void startTimer(int ms)
	{
		startTime = System.nanoTime();
		delayms = ms;
	}
	
	public boolean timerExpired()
	{
		if (delayms == 0)
		{
			return true;
		}
		if (((System.nanoTime() - startTime) / 1000000L) > delayms)
		{
			delayms = 0;
			startTime = 0;			
			return true;
		}
		return false;
	}
}
