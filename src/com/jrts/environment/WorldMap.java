package com.jrts.environment;

public class WorldMap extends Floor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorldMap(int rows, int cols) {
		super(rows, cols, CellType.UNKNOWN);
	}

	public void update(Perception perception) {
		for (int i = 0; i < perception.rows; i++) {
			for (int j = 0; j < perception.cols; j++) {
				Cell current = perception.get(i, j);
				if (current.getType() != CellType.UNKNOWN && current.getType() != CellType.UNIT)
					set(perception.getAbsoluteRow(i), perception.getAbsoluteCol(j), current);
			}
		}
	}
	
	public Position findNearest(Position center, CellType type) {
		double distance = Double.MAX_VALUE;
		Position nearestPosition = null;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Position p = new Position(i, j);
				if (get(p).getType() == type) {
					double currentDistance = center.distance(p);
					if (currentDistance < distance) {
						nearestPosition = p;
						distance = currentDistance;
					}
				}
			}
		}
		return nearestPosition;
	}
	

}
