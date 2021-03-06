package com.jrts.messages;

import jade.util.leap.Serializable;

import com.jrts.common.GameConfig;
import com.jrts.common.GoalPriority;

public class GoalLevels implements Serializable {

	private static final long serialVersionUID = 1L;
	
	GoalPriority resources;
	GoalPriority attack;
	GoalPriority defence;
	GoalPriority exploration;

	public GoalLevels() {
		// TODO Auto-generated constructor stub
	}
	
	public GoalLevels(GoalPriority resources, GoalPriority attack,
			GoalPriority defence, GoalPriority exploration) {
		super();
		this.resources = resources;
		this.attack = attack;
		this.defence = defence;
		this.exploration = exploration;
	}

	public int extimateResourceUnits() {
		
		switch (resources) {
		case LOW:
			return GameConfig.LOW_PRIORITY_RESOURCES_UNITS;
		case MEDIUM:
			return GameConfig.MEDIUM_PRIORITY_RESOURCES_UNITS;
		case HIGH:
			return GameConfig.HIGH_PRIORITY_RESOURCES_UNITS;
		default:
			return 0;
		}
	}
	
	public int extimateFightingUnits() {
		
		switch (attack) {
		case LOW:
			return GameConfig.LOW_PRIORITY_ATTACKS_UNITS;
		case MEDIUM:
			return GameConfig.MEDIUM_PRIORITY_ATTACKS_UNITS;
		case HIGH:
			return GameConfig.HIGH_PRIORITY_ATTACKS_UNITS;
		default:
			return 0;
		}
	}
	
	public int extimatePatrolingUnits()
	{
		switch (defence) {
		case LOW:
			return GameConfig.LOW_PRIORITY_PATROLING_UNITS;
		case MEDIUM:
			return GameConfig.MEDIUM_PRIORITY_PATROLING_UNITS;
		case HIGH:
			return GameConfig.HIGH_PRIORITY_PATROLING_UNITS;
		default:
			return 0;
		}
	}

	public int extimateExplorationUnits()
	{	
		switch (exploration) {
		case LOW:
			return GameConfig.LOW_PRIORITY_EXPLORATION_UNITS;
		case MEDIUM:
			return GameConfig.MEDIUM_PRIORITY_EXPLORATION_UNITS;
		case HIGH:
			return GameConfig.HIGH_PRIORITY_EXPLORATION_UNITS;
		default:
			return 0;
		}
	}
	
	public GoalPriority getResources() {
		return resources;
	}

	public void setResources(GoalPriority resources) {
		this.resources = resources;
	}

	public GoalPriority getAttack() {
		return attack;
	}

	public void setAttack(GoalPriority attack) {
		this.attack = attack;
	}

	public GoalPriority getDefence() {
		return defence;
	}

	public void setDefence(GoalPriority defence) {
		this.defence = defence;
	}

	public GoalPriority getExploration() {
		return exploration;
	}

	public void setExploration(GoalPriority exploration) {
		this.exploration = exploration;
	}
	
	public int extimateNeededUnits() {
		return extimateExplorationUnits() + extimateFightingUnits()
				+ extimatePatrolingUnits() + extimateResourceUnits();
	}

}
