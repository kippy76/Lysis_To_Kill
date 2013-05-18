package uk.ac.dundee.cqwalker;

import android.graphics.Bitmap;

public class Sprite
{
	private int x, y;
	int width, height;
	float angle;
	private boolean friendly;
	int resourceID;
	String resourceName;
	Bitmap bitmap;
	
	public Sprite()
	{
		x = y = resourceID = width = height = 0;
		angle = 0f;	
		resourceName = "";
		friendly = true;
	}
	
	public void x(int x)
	{
		this.x = x;
	}
	
	public int x()
	{
		return this.x;
	}
	
	public void y(int y)
	{
		this.y = y;
	}
	
	public int y()
	{
		return this.y;
	}
	
	public void width(int width)
	{
		this.width = width;
	}
	
	public int width()
	{
		return this.width;
	}
	
	public void height(int height)
	{
		this.height = height;
	}
	
	public int height()
	{
		return this.height;
	}
	
	public void angle(float angle)
	{
		this.angle = angle;
	}
	
	public float angle()
	{
		return this.angle;
	}
	
	public void friendly(boolean isFriendly)
	{
		this.friendly = isFriendly;
	}
	
	public boolean friendly()
	{
		return this.friendly;
	}
	
	public void resourceID(int resourceID)
	{
		this.resourceID = resourceID;
	}
	
	public int resourceID()
	{
		return this.resourceID;
	}
	
	public void bitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}
	
	public Bitmap bitmap()
	{
		return this.bitmap;
	}
	
	public void resourceName(String resourceName)
	{
		this.resourceName = resourceName;
	}
	
	public String resourceName()
	{
		return this.resourceName;
	}
}
