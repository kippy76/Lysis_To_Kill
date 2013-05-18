package uk.ac.dundee.cqwalker;

public class Cell extends Sprite
{
	
	private int state = 0;
	private int column = 0;
	private int row = 0;
	
	public Cell()
	{
		state = column = row = 0;
	}
	
	public void state(int currentState)
	{
		this.state = currentState;
	}
	
	public int state()
	{
		return this.state;
	}
	
	public void column(int column)
	{
		this.column = column;
	}
	
	public int column()
	{
		return this.column;
	}
	
	public void row(int row)
	{
		this.row = row;
	}
	
	public int row()
	{
		return this.row;
	}
}
