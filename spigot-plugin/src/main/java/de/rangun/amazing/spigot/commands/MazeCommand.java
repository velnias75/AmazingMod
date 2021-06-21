package de.rangun.amazing.spigot.commands;

import static java.util.stream.Collectors.toList;
import static org.bukkit.Bukkit.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Collections2;

import de.rangun.amazing.maze.IBlockPlacer;
import de.rangun.amazing.maze.IPattern;
import de.rangun.amazing.maze.Maze;

public class MazeCommand implements CommandExecutor, TabCompleter, IBlockPlacer<World, Material, Integer> {

	private static MazeCommand instance;
	private final static List<String> materials = new ArrayList<>(Material.values().length);

	private MazeCommand() {
		for (final Material m : Material.values()) {
			materials.add(m.getKey().toString());
		}
	}

	public static synchronized MazeCommand getInstance() {

		if (instance == null) {
			instance = new MazeCommand();
		}

		return instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (sender instanceof Player) {

			if (args.length == 4) {

				try {

					final int width = getIntegerArg(args[0], "width", sender);
					final int length = getIntegerArg(args[1], "length", sender);
					final int height = getIntegerArg(args[2], "height", sender);
					final Material mat = Material.matchMaterial(args[3]);
					final Player player = (Player) sender;

					if (mat != null) {

						try {

							final Maze maze = new Maze(width, length);
							final Location pos = player.getLocation();
							final World world = player.getWorld();

							final IPattern<Material> groundPattern = new IPattern<Material>() {

								@Override
								public Material materialAt(final int x, final int y) {
									return mat;
								}
							};

							final IPattern<Material> wallPattern = new IPattern<Material>() {

								@Override
								public Material materialAt(final int x, final int y) {
									return mat;
								}
							};

							final IPattern<Material> holePattern = new IPattern<Material>() {

								@Override
								public Material materialAt(int x, int y) {
									return Material.AIR;
								}
							};

							maze.generate(this, world, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), height,
									Material.AIR, groundPattern, wallPattern, holePattern);

						} catch (IllegalArgumentException e) {
							sender.sendMessage(e.getMessage());
						}

					} else {
						sender.sendMessage("No such material \"" + mat + "\"");
					}

				} catch (NumberFormatException e) {
					return false;
				}

			} else {
				sender.sendMessage("Usage: " + command.getUsage());
			}

		} else {
			getLogger().info("Only a player can call the /maze command");
		}

		return true;
	}

	@Override
	public void placeBlock(final World world, final Integer playerX, final Integer playerY, final Integer playerZ,
			final int x, final int y, final int height, final Material material) {

		world.getBlockAt(playerX + x, playerY + height, playerZ + y).setType(material);
	}

	private int getIntegerArg(final String arg, final String name, final CommandSender sender)
			throws NumberFormatException {

		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			sender.sendMessage("<" + name + "> must be an integer");
			throw e;
		}
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
			final String[] args) {

		if (args.length == 4) {
			return Collections2.filter(materials, item -> item.toLowerCase().contains(args[3].toLowerCase())).stream()
					.collect(toList());
		}

		return null;
	}
}
