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
import static net.minecraft.command.argument.BlockStateArgumentType.getBlockState;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.rangun.amazing.maze.IBlockPlacer;
import de.rangun.amazing.maze.IPattern;
import de.rangun.amazing.maze.Maze;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MazeCommand implements Command<ServerCommandSource>, IBlockPlacer<ServerWorld, BlockState, Double> {

	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

		final int width = getInteger(context, "width");
		final int length = getInteger(context, "length");
		final int height = getInteger(context, "height");
		final BlockStateArgument blockState = getBlockState(context, "material");
		final Vec3d pos = context.getSource().getPlayer().getPos();

		final Runnable t = new Runnable() {

			@Override
			public void run() {

				try {

					final Maze maze = new Maze(width, length);

					final IPattern<BlockState> groundPattern = new IPattern<BlockState>() {

						@Override
						public BlockState materialAt(final int x, final int y, final int h) {
							return blockState.getBlockState();
						}
					};

					final IPattern<BlockState> wallPattern = new IPattern<BlockState>() {

						@Override
						public BlockState materialAt(final int x, final int y, final int h) {
							return blockState.getBlockState();
						}
					};

					final IPattern<BlockState> holePattern = new IPattern<BlockState>() {

						@Override
						public BlockState materialAt(final int x, final int y, final int h) {
							return h == 0 ? blockState.getBlockState() : AIR.getDefaultState();
						}
					};

					maze.generate(MazeCommand.this, context.getSource().getWorld(), pos.getX(), pos.getY(), pos.getZ(),
							height, AIR.getDefaultState(), groundPattern, wallPattern, holePattern);

				} catch (IllegalArgumentException e) {
					context.getSource().sendError(new LiteralText(e.getMessage()));
				}
			}
		};

		(new Thread(t)).start();

		return Command.SINGLE_SUCCESS;
	}

	@Override
	public void placeBlock(final ServerWorld world, final Double playerX, final Double playerY, final Double playerZ,
			final int x, final int y, final int height, final BlockState material) {

		world.setBlockState(new BlockPos(playerX + x, playerY + height, playerZ + y), material);
	}
}
