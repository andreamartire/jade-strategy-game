package com.jrts.behaviours;

import com.jrts.agents.Soldier;
import com.jrts.common.AgentStatus;
import com.jrts.environment.Direction;
import com.jrts.environment.Position;
import com.jrts.environment.WorldMap;

@SuppressWarnings("serial")
public class PatrolBehaviour extends UnitBehaviour {
	
	private int distance = DISTANCE_LITTLE;
	
	public static final int DISTANCE_LITTLE = 4;
	public static final int DISTANCE_MEDIUM = 6;
	public static final int DISTANCE_BIG = 8;

	Soldier soldier;
	Position p1, p2;
	boolean go1 = true;
	/**
	 * 
	 * @param soldier
	 * @param direction: TOP | RIGHT | DOWN | LEFT 
	 */
	public PatrolBehaviour(Soldier soldier, Direction direction, int distance, WorldMap worldMap) {
		super(AgentStatus.PATROLING, false);
		
		this.soldier = soldier;		
		this.distance = distance;
		
		if(direction == Direction.LEFT)
		{
			p1 = worldMap.bigStep(soldier.getCityCenter(), Direction.LEFT_UP, this.distance);
			p2 = worldMap.bigStep(soldier.getCityCenter(), Direction.LEFT_DOWN, this.distance);
		} else if(direction == Direction.UP)
		{
			p1 = worldMap.bigStep(soldier.getCityCenter(), Direction.LEFT_UP, this.distance);
			p2 = worldMap.bigStep(soldier.getCityCenter(), Direction.RIGHT_UP, this.distance);
		} else if(direction == Direction.RIGHT)
		{
			p1 = worldMap.bigStep(soldier.getCityCenter(), Direction.RIGHT_UP, this.distance);
			p2 = worldMap.bigStep(soldier.getCityCenter(), Direction.RIGHT_DOWN, this.distance);
		} else if(direction == Direction.DOWN)
		{
			p1 = worldMap.bigStep(soldier.getCityCenter(), Direction.LEFT_DOWN, this.distance);
			p2 = worldMap.bigStep(soldier.getCityCenter(), Direction.RIGHT_DOWN, this.distance);
		}
	}

	@Override
	public void myAction() {
		if(go1)
			this.soldier.goThere(p2);
		else
			this.soldier.goThere(p1);
		go1 = !go1;
	}

	@Override
	public boolean done() {
		return false;
	}
}
