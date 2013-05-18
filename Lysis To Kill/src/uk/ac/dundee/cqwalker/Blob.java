package uk.ac.dundee.cqwalker;

public class Blob extends Sprite
{
	private boolean friendly;
	short direction;
	int xStart, yStart;
	
	public Blob()
	{
		direction = 1;
	}
	
	public Blob(short direction)
	{
		this.direction = direction;
	}
	
	public void friendly(boolean isFriendly)
	{
		this.friendly = isFriendly;
	}
	
	public boolean friendly()
	{
		return this.friendly;
	}
	
	public void direction(short direction)
	{
		this.direction = direction;
	}
	
	public short direction()
	{
		return this.direction;
	}
	
	public void xStart(int x)
	{
		this.xStart = x;
	}
	
	public int xStart()
	{
		return this.xStart;
	}
	
	public void yStart(int y)
	{
		this.yStart = y;
	}
	
	public int yStart()
	{
		return this.yStart;
	}
	
}
