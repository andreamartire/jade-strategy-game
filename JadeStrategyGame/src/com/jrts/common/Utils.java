package com.jrts.common;

import java.util.GregorianCalendar;
import java.util.Random;

import com.jrts.environment.Direction;
import com.jrts.environment.Position;
import com.jrts.environment.WorldMap;

public class Utils {
	
	public static Random random = new Random(GregorianCalendar.getInstance().getTimeInMillis());
	
	/**
	 * This function let you know in what angle of the map the unit is located
	 * @param The position of the unit
	 * @return a Direction between LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN
	 */
	public static Direction getMapAnglePosition(Position p)
	{
		if(p.getRow() <= GameConfig.WORLD_ROWS/2){
			if(p.getCol() <= GameConfig.WORLD_COLS/2)
				return Direction.LEFT_UP;
			else return Direction.RIGHT_UP;
		} else {
			if(p.getCol() <= GameConfig.WORLD_COLS/2)
				return Direction.LEFT_DOWN;
			else return Direction.RIGHT_DOWN;			
		}
	}
	
	public static Position getBattalionCenter(WorldMap map, Position cityCenter, int ray)
	{
		Direction angle = getMapAnglePosition(cityCenter);
		int startI = 1;
		int startJ = 1;
		
		if(angle.equals(Direction.LEFT_UP))
		{
			startI = cityCenter.getCol() + Utils.random.nextInt(5) + 4;
			startJ = cityCenter.getRow() + Utils.random.nextInt(5) + 4;
		} else if(angle.equals(Direction.LEFT_DOWN))
		{
			startI = cityCenter.getRow() - Utils.random.nextInt(10) - 3;
			startJ = Utils.random.nextInt(10);
		} else if(angle.equals(Direction.RIGHT_UP)){
			startI = cityCenter.getRow() + Utils.random.nextInt(5) + 3;
			startJ = GameConfig.WORLD_COLS/2 + Utils.random.nextInt(10) + 3;
		} else if(angle.equals(Direction.RIGHT_DOWN)){
			startI = GameConfig.WORLD_COLS/2 + Utils.random.nextInt(10) + 5;
			startJ = GameConfig.WORLD_ROWS/2 + Utils.random.nextInt(10) + 5;
		}
		
		int maxI = startI + GameConfig.WORLD_ROWS/2 - 1;
		int maxJ = startJ + GameConfig.WORLD_COLS/2 - 1;
		for (int i = startI; i < maxI; i++) {
			for (int j = startJ; j < maxJ; j++) {
				
				boolean stop = false;
				for (int w = i-1; w < i + ray - 1 && !stop; w++)
					for (int h = j-1; h < j + ray - 1; h++)
						if(!map.isWalkable(new Position(w, h)))
							stop = true;
				
				if(!stop)
					return new Position(i, j);
			}
		}
		
		return null;
	}
	
	/**
	 * Get the position of a random unknown cell located in one of the angles of the map
	 * @param a worldmap of a team
	 * @param a direction in witch looking for a unknown cell, must be between LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN
	 * @return the position of an unknown cell or NULL if not found
	 */
	public static Position getRandomUnknownCellPosition(WorldMap map, Direction d)
	{
		for (int i = 0; i < 20; i++) {

			int w = 0;
			int h = 0;
			if(d.equals(Direction.LEFT_UP))
			{
				w = 0;
				h = 0;
			} else if(d.equals(Direction.LEFT_DOWN))
			{
				w = 0;
				h = GameConfig.WORLD_ROWS/2;
			} else if(d.equals(Direction.RIGHT_UP)){
				w = GameConfig.WORLD_COLS/2;
				h = 0;
			} else if(d.equals(Direction.RIGHT_DOWN)){
				w = GameConfig.WORLD_COLS/2;
				h = GameConfig.WORLD_ROWS/2;
			}
			
			int x = w + getRandom().nextInt(GameConfig.WORLD_COLS/2 - 1);
			int y = h + getRandom().nextInt(GameConfig.WORLD_ROWS/2 - 1);
			if(x <= 0) x = 0;
			if(y <= 0) y = 0;
			Position pos = new Position(x, y);
			if(map.get(pos).isUnknown())
				return pos;
		}
		
		return null;
	}

	public static Random getRandom() {
		return random;
	}

	public static void setRandom(Random random) {
		Utils.random = random;
	}

	public static void checkAndFixPosition(Position position) {
		if (position.getCol() >= GameConfig.WORLD_COLS) position.setCol(GameConfig.WORLD_COLS-1);
		if (position.getCol() < 0) position.setCol(0);
		if (position.getRow() >= GameConfig.WORLD_ROWS) position.setRow(GameConfig.WORLD_ROWS-1);
		if (position.getRow() < 0) position.setRow(0);
	}

}
