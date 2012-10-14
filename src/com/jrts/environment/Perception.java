package com.jrts.environment;

public class Perception extends Floor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int rowTraslation, colTraslation;

	public Perception(Floor floor, Position center, int sight) {
		super(sight*2+1, sight*2+1, CellType.UNKNOWN);
		rowTraslation = center.row - sight;
		colTraslation = center.col - sight;

		for (int row = 0; row < floor.rows; row++) {
			for (int col = 0; col < floor.cols; col++) {
				boolean inRange = Math.abs(center.row - row) <= sight
						&& Math.abs(center.col - col) <= sight;
				if (inRange) {
					Cell cell = floor.get(row, col);
					setWithAbsoluteCoords(row, col, new Cell(cell.type, cell.id));
				}
			}
		}
	}
	
	public void setWithAbsoluteCoords(int row, int col, Cell cell) {
		set(getRelativeRow(row), getRelativeCol(col), cell);
	}
	
	int getRelativeRow(int absoluteRow) {
		return absoluteRow - rowTraslation;
	}
	
	int getRelativeCol(int absoluteCol) {
		return absoluteCol - colTraslation;
	}
	
	int getAbsoluteRow(int relativeRow) {
		return relativeRow + rowTraslation;
	}
	
	int getAbsoluteCol(int relativeCol) {
		return relativeCol + colTraslation;
	}

}
