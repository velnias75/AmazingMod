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

package de.rangun.amazing.commands;

import static net.minecraft.block.Blocks.AIR;
import static net.minecraft.block.Blocks.STONE;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.rangun.amazing.maze.Maze;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MazeCommand implements Command<FabricClientCommandSource> {

	@Override
	public int run(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {

		final Maze maze = new Maze(100, 150);
		final Vec3d pos = context.getSource().getPlayer().getPos();

		maze.generate((x, y, b) -> {
			for (int i = 0; i < 5; ++i) {
				context.getSource().getWorld()
						.setBlockState(new BlockPos(pos.getX() + x, pos.getY() + i, pos.getZ() + y),
								b ? STONE.getDefaultState() : AIR.getDefaultState());
			}
		});

		return Command.SINGLE_SUCCESS;
	}
}
