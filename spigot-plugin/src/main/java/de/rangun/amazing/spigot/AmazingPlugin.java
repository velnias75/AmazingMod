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

package de.rangun.amazing.spigot;


import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion.Target;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.Website;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import de.rangun.amazing.spigot.commands.MazeCommand;

@Plugin(name = "AmazingPlugin", version = "0.0-SNAPSHOT")
@Description(value = "A plugin to generate mazes")
@Website(value = "https://github.com/velnias75/AmazingModo")
@ApiVersion(Target.v1_16)
@Author(value = "Velnias75")
@Commands(@Command(name = "maze", desc = "generates a maze", usage = "/maze <width> <length> <height> <material>"))
public class AmazingPlugin extends JavaPlugin {

	@Override
	public void onEnable() {

		final PluginCommand mazeCommand = this.getCommand("maze");

		if (mazeCommand != null) {

			final MazeCommand cmd = new MazeCommand();

			mazeCommand.setExecutor(cmd);
			mazeCommand.setTabCompleter(cmd);
		}
	}
}
