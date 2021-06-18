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

	public void generate(final IMazeTraverser traverser) {

		divide(0, 0, width, height, chooseOrientation(width, height), traverser);

		for (int i = 0; i < width; ++i) {
			traverser.at(i, 0, true);
			traverser.at(i, height - 1, true);
		}

		for (int i = 0; i < height; ++i) {
			traverser.at(0, i, true);
			traverser.at(width - 1, i, true);
		}
	};

	private void divide(final int x, final int y, final int width, final int height, final ORIENTATION orientation,
			IMazeTraverser t) {

		final int width_new;
		final int height_new;
		final int x_pair;
		final int y_pair;
		final int width_new_pair;
		final int height_new_pair;

		if (orientation == ORIENTATION.HORIZONTAL) {

			if (5 > height)
				return;

			final int wall = y + rnd.nextInt(height - 4) + 2;
			final int hole = x + rnd.nextInt(Math.max(1, width - 3)) + 1;

			for (int i = 0; i < width; ++i) {
				t.at(x + i, wall, true);
			}
			
			t.at(hole, wall, false);

			width_new = width;
			height_new = wall - y + 1;

			x_pair = x;
			y_pair = wall;
			width_new_pair = width;
			height_new_pair = y + height - wall;

		} else {

			if (5 > width)
				return;

			final int wall = x + rnd.nextInt(width - 4) + 2;
			final int hole = y + rnd.nextInt(Math.max(1, height - 3)) + 1;

			for (int i = 0; i < height; ++i) {
				t.at(wall, y + 1, true);
			}

			t.at(wall, hole, false);

			width_new = wall - x + 1;
			height_new = height;

			x_pair = wall;
			y_pair = y;
			width_new_pair = x + width - wall;
			height_new_pair = height;
		}

		divide(x, y, width_new, height_new, chooseOrientation(width_new, height_new), t);
		divide(x_pair, y_pair, width_new_pair, height_new_pair, chooseOrientation(width_new_pair, height_new_pair), t);
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
