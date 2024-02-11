package ru.chaosmine.minimessagecommand;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class MiniMessageCommandPlugin extends JavaPlugin {
	private static final MiniMessage miniMessage = MiniMessage.builder()
		.strict(true)
		.build();

	@Override
	public void onEnable() {
		requireNonNull(getCommand("mm")).setExecutor(new ChaosCooldownsCommand());
	}

	private static final class ChaosCooldownsCommand implements CommandExecutor, TabCompleter {
		private static final String permission = "minimessagecommand";
		private static final List<String> tabComplete = List.of("me", "bc", "to");

		@Override
		public boolean onCommand(
			CommandSender sender,
			Command command,
			String label,
			String[] args
		) {
			if (!sender.hasPermission(permission)) return true;

			if (args.length < 1) {
				return false;
			}

			switch (args[0]) {
				case "me" -> sender.sendMessage(miniMessage.deserialize(trailingArgs(args, 1)));
				case "bc" -> Bukkit.broadcast(miniMessage.deserialize(trailingArgs(args, 1)));
				case "to" -> {
					if (args.length < 2) {
						return false;
					}

					var player = Bukkit.getPlayerExact(args[1]);
					if (player != null) {
						player.sendMessage(miniMessage.deserialize(trailingArgs(args, 2)));
					}
				}
				default -> {
					return false;
				}
			}

			return true;
		}

		@Override
		public List<String> onTabComplete(
			CommandSender sender,
			Command command,
			String label,
			String[] args
		) {
			if (sender.hasPermission(permission)) {
				if (args.length == 0 || args.length == 1) {
					return tabComplete;
				} else if (args.length == 2) {
					if ("to".equals(args[0])) {
						return null;
					} else {
						return List.of();
					}
				}
			}
			return List.of();
		}

		private static String trailingArgs(String[] args, int from) {
			return String.join(" ", Arrays.copyOfRange(args, from, args.length));
		}
	}
}
