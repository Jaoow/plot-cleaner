package com.joaolucas.cleaner.command;

import com.joaolucas.cleaner.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCleaner implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!sender.hasPermission("limpador.admin")) {
            sender.sendMessage("§cAcesso negado.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("§cUtilize: /givelimpador <jogador>.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cJogador não encontrado.");
            return true;
        }

        if (sender instanceof Player) {
            sender.sendMessage("§aVocê enviou um limpador de terreno para o jogador §7'" + target.getName() + "'§a.");
        }

        target.getInventory().addItem(Settings.ITEM_CLEANER.complete());
        return false;
    }
}
