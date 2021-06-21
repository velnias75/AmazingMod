/*
 * Copyright 2021 by Heiko Schäfer <heiko@rangun.de>
 *
 * This file is part of AmazingMod.
 *
 * AmazingMod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * AmazingMod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with AmazingMod.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.rangun.amazing.maze;

import java.util.Random;

import de.rangun.amazing.maze.IMazeTraverser.Type;

public final class Maze {

	private static enum ORIENTATION {
		HORIZONTAL, VERTICAL
	};

	private final static Random rnd = new Random();

	private final int width;
	private final int height;
	private final boolean[][] grid;

	public Maze(int width, int height) {

		this.width = width;
		this.height = height;
		this.grid = new boolean[this.height][this.width];

		for (int h = 0; h < this.height; ++h) {
			for (int w = 0; w < this.width; ++w) {
				this.grid[h][w] = false;
			}
		}
	}

	public <W, M, N extends Number> void generate(final IBlockPlacer<W, M, N> blockPlacer, final W world,
			final N playerX, final N playerY, final N playerZ, final int height, M air, final IPattern<M> groundPattern,
			final IPattern<M> wallPattern, final IPattern<M> holePattern) {

		create((x, y, material, type) -> erect(world, blockPlacer, playerX, playerY, playerZ, height, x, y, material,
				air, type), groundPattern, wallPattern, holePattern);
	}

	private <M> void create(final IMazeTraverser<M> traverser, final IPattern<M> groundPattern,
			final IPattern<M> wallPattern, final IPattern<M> holePattern) {

		for (int x = 1; x < (width - 1); ++x) {
			for (int y = 1; y < (height - 1); ++y) {
				traverser.at(x, y, groundPattern.materialAt(x, y), Type.GROUND);
			}
		}

		divide(0, 0, width, height, chooseOrientation(width, height), traverser, wallPattern, holePattern);

		for (int i = 0; i < width; ++i) {
			traverser.at(i, 0, wallPattern.materialAt(i, 0), Type.WALL);
			traverser.at(i, height - 1, wallPattern.materialAt(i, height - 1), Type.WALL);
		}

		for (int i = 0; i < height; ++i) {
			traverser.at(0, i, wallPattern.materialAt(0, i), Type.WALL);
			traverser.at(width - 1, i, wallPattern.materialAt(width - 1, i), Type.WALL);
		}
	};

	private <W, M, N extends Number> void erect(final W world, final IBlockPlacer<W, M, N> blockPlacer, final N playerX,
			final N playerY, final N playerZ, final int height, final int x, final int y, final M material, M air,
			final Type type) {

		for (int h = 0; h < height; ++h) {

			if ((h == 0 && (type == Type.GROUND || type == Type.WALL || type == Type.HOLE))
					|| (h > 0 && type != Type.GROUND)) {
				blockPlacer.placeBlock(world, playerX, playerY, playerZ, x, y, h, material);
			} else if (h > 0 && type == Type.GROUND) {
				blockPlacer.placeBlock(world, playerX, playerY, playerZ, x, y, h, air);
			}
		}
	}

	private static int randomInt(final int min, int max, boolean even) {
		return ((int) (Math.floor(Math.min(max, (rnd.nextInt(max + 1) + min)) / 2.0d) * 2.0d)) + (even ? 0 : 1);
	}

	private <M> void divide(final int x, final int y, final int width, final int height, final ORIENTATION orientation,
			IMazeTraverser<M> traverser, IPattern<M> wallPattern, final IPattern<M> holePattern) {

		final int width_new;
		final int height_new;
		final int x_pair;
		final int y_pair;
		final int width_new_pair;
		final int height_new_pair;

		if (orientation == ORIENTATION.HORIZONTAL) {

			if (5 > height)
				return;

			final int wall = y + randomInt(2, height - 3, true);
			final int hole = x + randomInt(1, width - 2, false);

			for (int i = 0; i < width; ++i) {
				traverser.at(x + i, wall, wallPattern.materialAt(x + 1, wall), Type.WALL);
			}

			traverser.at(hole, wall, holePattern.materialAt(hole, wall), Type.HOLE);

			width_new = width;
			height_new = wall - y + 1;

			x_pair = x;
			y_pair = wall;
			width_new_pair = width;
			height_new_pair = y + height - wall;

		} else {

			if (5 > width)
				return;

			final int wall = x + randomInt(2, width - 3, true);
			final int hole = y + randomInt(1, height - 2, false);

			for (int i = 0; i < height; ++i) {
				traverser.at(wall, y + 1, wallPattern.materialAt(wall, y + 1), Type.WALL);
			}

			traverser.at(wall, hole, holePattern.materialAt(wall, hole), Type.HOLE);

			width_new = wall - x + 1;
			height_new = height;

			x_pair = wall;
			y_pair = y;
			width_new_pair = x + width - wall;
			height_new_pair = height;
		}

		divide(x, y, width_new, height_new, chooseOrientation(width_new, height_new), traverser, wallPattern,
				holePattern);
		divide(x_pair, y_pair, width_new_pair, height_new_pair, chooseOrientation(width_new_pair, height_new_pair),
				traverser, wallPattern, holePattern);

	}

	private ORIENTATION chooseOrientation(final int width, final int height) {

		if (width < height) {
			return ORIENTATION.HORIZONTAL;
		} else if (height < width) {
			return ORIENTATION.VERTICAL;
		}

		return rnd.nextInt(2) == 0 ? ORIENTATION.HORIZONTAL : ORIENTATION.VERTICAL;
	}
}
