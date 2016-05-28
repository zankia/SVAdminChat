package fr.zankia.svadminchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SVAC extends JavaPlugin implements Listener {
	private static final String NO_PERMISSION = "Erreur : Vous n'avez pas la permission pour cette commande.";
	private String identificator;
	private String chatPrefix;
	
	public void onEnable() {
		saveDefaultConfig();
		this.identificator = getConfig().getString("identificator");
		this.chatPrefix = getConfig().getString("chatPrefix");
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Enabled");
	}
	
	public void onDisable() {
		getLogger().info("Disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
		if ((name.equalsIgnoreCase("svac")) || (name.equalsIgnoreCase("svadminchat"))) {
			if (sender.hasPermission("svadminchat.admin")) {
				if ((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
					reloadConfig();
					this.identificator = getConfig().getString("identificator");
					this.chatPrefix = getConfig().getString("chatPrefix");
					sender.sendMessage(ChatColor.RED + "SVAdminChat : " + ChatColor.GREEN + "Reload done.");
				} else
					return false;
			} else
				sender.sendMessage(ChatColor.RED + NO_PERMISSION);
			return true;
		}
		return false;
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onChat(AsyncPlayerChatEvent evt) {
		if ((evt.getMessage().startsWith(this.identificator))
				&& (evt.getPlayer().hasPermission("svadminchat.user"))) {
			String message = ChatColor.translateAlternateColorCodes('&',
					this.chatPrefix).replace("{player}", evt.getPlayer().getName())
					+ evt.getMessage().substring(this.identificator.length());
			
			for (Player p : Bukkit.getOnlinePlayers())
				if (p.hasPermission("svadminchat.user"))
					p.sendMessage(message);
			
			getLogger().info(evt.getPlayer().getName() + ": " + evt.getMessage());
			evt.setCancelled(true);
		}
	}
}
