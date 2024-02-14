package ru.chaosmine.minimessagecommand;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class MiniMessageCommandPlugin extends JavaPlugin implements Listener {
	private static final MiniMessage miniMessage = MiniMessage.builder()
		.strict(true)
		.build();
	private final ChaosCooldownsCommand command = new ChaosCooldownsCommand();

	@Override
	public void onEnable() {
		requireNonNull(getCommand("mm")).setExecutor(this.command);
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void event(ServerCommandEvent e) {
		var message = e.getCommand();

		if ("mm".equals(extractCommandName(message, false))) {
			var result = this.command.handle(e.getSender(), message);
			if (result) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void event(PlayerCommandPreprocessEvent e) {
		var message = e.getMessage();

		if ("mm".equals(extractCommandName(message, true))) {
			var result = this.command.handle(e.getPlayer(), message);
			if (result) {
				e.setCancelled(true);
			}
		}
	}

	private static final class ChaosCooldownsCommand implements CommandExecutor, TabCompleter {
		private static final String permission = "minimessagecommand";
		private static final List<String> tabComplete = List.of("me", "bc", "to");

		private boolean handle(CommandSender sender, String message) {
			if (!sender.hasPermission(permission)) return false;

			var args = message.split(" ", 3);

			if (args.length < 3) {
				return false;
			}

			switch (args[1]) {
				case "me" -> sender.sendMessage(miniMessage.deserialize(args[2]));
				case "bc" -> Bukkit.broadcast(miniMessage.deserialize(args[2]));
				case "to" -> {
					args = message.split(" ", 4);

					if (args.length < 4) {
						return false;
					}

					var player = Bukkit.getPlayerExact(args[2]);
					if (player != null) {
						player.sendMessage(miniMessage.deserialize(args[3]));
					}
				}
				default -> {
					return false;
				}
			}

			return true;
		}

		@Override
		public boolean onCommand(
			CommandSender sender,
			Command command,
			String label,
			String[] args
		) {
			return false;
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
	}

	private static String extractCommandName(String message, boolean stripSlash) {
		var strip = stripSlash ? 1 : 0;
		var indexOfSpace = message.indexOf(' ', strip);
		return message.substring(strip, indexOfSpace == -1 ? message.length() : indexOfSpace);
	}
}
