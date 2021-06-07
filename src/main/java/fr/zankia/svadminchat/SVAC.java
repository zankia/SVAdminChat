package fr.zankia.svadminchat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SVAC extends JavaPlugin implements Listener {
    private static final String NO_PERMISSION = "Erreur : Vous n'avez pas la permission pour cette commande.";
    private String identificator;
    private String chatPrefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.identificator = getConfig().getString("identificator");
        this.chatPrefix = getConfig().getString("chatPrefix");
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String name, String[] args) {
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

    @EventHandler
    public void onChat(AsyncChatEvent evt) {
        String message = ((TextComponent) evt.message()).content();
        if (message.startsWith(this.identificator)
                && (evt.getPlayer().hasPermission("svadminchat.user"))) {
            evt.setCancelled(true);
            sendAdminMessage(evt.getPlayer().getName(), message);

            getLogger().info(evt.getPlayer().getName() + ": " + message);
        }
    }

    @EventHandler
    public void onDeprecatedChat(AsyncPlayerChatEvent evt) {
        if (evt.getMessage().startsWith(this.identificator) && evt.getPlayer().hasPermission("svadminchat.user")) {
            evt.setCancelled(true);
        }
    }

    private void sendAdminMessage(String playerName, String message) {
        String newMessage = ChatColor.translateAlternateColorCodes('&',
                this.chatPrefix).replace("{player}", playerName)
                + message.substring(this.identificator.length());

        for (Player p : Bukkit.getOnlinePlayers())
            if (p.hasPermission("svadminchat.user"))
                p.sendMessage(newMessage);
    }
}
