package com.lerdorf.voidships;

import org.bukkit.command.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Air implements CommandExecutor  {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("airsource") || cmd.getName().equalsIgnoreCase("air") ) {
			sender.sendMessage("Attempting to fill region with air");
			if (Main.airSource(Integer.parseInt(args[0]), player.getLocation(), null))
				sender.sendMessage("Successfully filled region with air");
			else
				sender.sendMessage("Failed to fill region with air");
		}
		return false;
	}
	
}
