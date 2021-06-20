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

package de.rangun.amazing.fabric.commands;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static net.minecraft.block.Blocks.AIR;
import static net.minecraft.block.Blocks.OAK_PLANKS;
import static net.minecraft.command.argument.BlockStateArgumentType.getBlockState;

import javax.annotation.Nonnull;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.rangun.amazing.maze.IMazeTraverser.Type;
import de.rangun.amazing.maze.IPattern;
import de.rangun.amazing.maze.Maze;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MazeCommand implements Command<ServerCommandSource> {

	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

		final int width = getInteger(context, "width");
		final int length = getInteger(context, "length");
		final int height = getInteger(context, "height");
		final BlockStateArgument blockState = getBlockState(context, "material");

		final Maze maze = new Maze(width, length);
		final Vec3d pos = context.getSource().getPlayer().getPos();

		final IPattern<BlockState> groundPattern = new IPattern<BlockState>() {

			@Override
			@Nonnull
			public BlockState materialAt(final int x, final int y) {
				return blockState.getBlockState();
			}
		};

		final IPattern<BlockState> wallPattern = new IPattern<BlockState>() {

			@Override
			@Nonnull
			public BlockState materialAt(final int x, final int y) {
				return OAK_PLANKS.getDefaultState();
			}
		};

		final IPattern<BlockState> holePattern = new IPattern<BlockState>() {

			@Override
			@Nonnull
			public BlockState materialAt(final int x, final int y) {
				return AIR.getDefaultState();
			}
		};

		maze.generate((x, y, mat, type) -> {

			for (int i = 0; i < height; ++i) {

				if ((i == 0 && (type == Type.GROUND || type == Type.WALL || type == Type.HOLE))
						|| (i > 0 && type != Type.GROUND)) {
					context.getSource().getWorld()
							.setBlockState(new BlockPos(pos.getX() + x, pos.getY() + i, pos.getZ() + y), mat);
				} else if (i > 0 && type == Type.GROUND) {
					context.getSource().getWorld().setBlockState(
							new BlockPos(pos.getX() + x, pos.getY() + i, pos.getZ() + y), AIR.getDefaultState());
				}
			}

		}, groundPattern, wallPattern, holePattern);

		return Command.SINGLE_SUCCESS;
	}
}
