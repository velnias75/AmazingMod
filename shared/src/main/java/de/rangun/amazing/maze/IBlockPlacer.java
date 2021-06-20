package de.rangun.amazing.maze;

@FunctionalInterface
public interface IBlockPlacer<W, M, N extends Number> {
	void placeBlock(W world, N playerX, N playerY, N playerZ, int x, int y, int height, M material);
}