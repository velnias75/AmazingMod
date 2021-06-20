package de.rangun.amazing.spigot.commands;

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
import org.bukkit.util.StringUtil;

import de.rangun.amazing.maze.IMazeTraverser.Type;
import de.rangun.amazing.maze.IPattern;
import de.rangun.amazing.maze.Maze;

public class MazeCommand implements CommandExecutor, TabCompleter {

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

						maze.generate((x, y, material, type) -> {

							for (int i = 0; i < height; ++i) {

								if ((i == 0 && (type == Type.GROUND || type == Type.WALL || type == Type.HOLE))
										|| (i > 0 && type != Type.GROUND)) {
									world.getBlockAt(pos.getBlockX() + x, pos.getBlockY() + i, pos.getBlockZ() + y)
											.setType(material);
								} else if (i > 0 && type == Type.GROUND) {
									world.getBlockAt(pos.getBlockX() + x, pos.getBlockY() + i, pos.getBlockZ() + y)
											.setType(Material.AIR);

								}
							}

						}, groundPattern, wallPattern, holePattern);

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
			final List<String> completions = new ArrayList<>(materials.size());
			return StringUtil.copyPartialMatches(args[3], materials, completions);
		}

		return null;
	}

}
