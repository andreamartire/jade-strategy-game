package com.jrts.common;

public class GameConfig {
	public static final int WORLD_ROWS = 50;
	public static final int WORLD_COLS = 50;
	
	public static final int WORKER_LIFE = 10;
	public static final int SOLDIER_LIFE = 20;
	
	public static final int WORKER_DAMAGES = 1;
	public static final int SOLDIER_DAMAGES = 3;
	
	public static final int WORKER_SPEED = 15;
	public static final int SOLDIER_SPEED = 7;
	
	public static final int WORKER_SIGHT = 40; //TODO change
	public static final int SOLDIER_SIGHT = 4;
	public static final int CITY_CENTER_SIGHT = 5;
	
	public static final int WORKER_KNAPSACK_CAPACITY = 10;
	
	public static final int UNIT_MOVING_ATTEMPTS = 20;
	
	public static final int DEFAULT_REFRESH_TIME = 5000;
	public static final int MIN_REFRESH_TIME = 1000;
	public static final int MAX_REFRESH_TIME = 8000;
	
	public static final float VERTICAL_OVERLAP = 0.8f;
	public static final float HORIZONTAL_OVERLAP = 1.00f;
	
	public static final int ATTACKS_REFRESH = 50;
	public static final int PERCEPTION_REFRESH = 500;
}
