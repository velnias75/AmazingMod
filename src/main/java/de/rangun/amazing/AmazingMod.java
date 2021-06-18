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

package de.rangun.amazing;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.DISPATCHER;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import de.rangun.amazing.commands.MazeCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public final class AmazingMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {

			final MutableText welcomeMsg = new LiteralText("Welcome to AmazingMod, ").formatted(Formatting.AQUA)
					.append(new LiteralText(client.player.getDisplayName().asString()).formatted(Formatting.RED)
							.append(new LiteralText(" :-)").formatted(Formatting.AQUA)));

			client.inGameHud.addChatMessage(MessageType.SYSTEM, welcomeMsg, Util.NIL_UUID);
		});

		DISPATCHER.register(literal("maze").executes(new MazeCommand()));
	}

}
