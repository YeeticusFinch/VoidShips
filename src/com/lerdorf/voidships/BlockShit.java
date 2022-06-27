package com.lerdorf.voidships;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class BlockShit implements CommandExecutor {
	/**
	 *
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = (sender instanceof Player) ? (Player) sender : null;
		if (cmd.getName().equalsIgnoreCase("door") && player != null) {
			try {
				Region r = Main.getWorldEdit().getWorldEdit().getSessionManager().get(new BukkitPlayer( Main.getWorldEdit(), player)).getSelection(new BukkitWorld(player.getWorld()));
				sender.sendMessage("Successfully aquired WorldEdit selection");
				/*int i = 0;
				SpecialBlock[] blocks = new SpecialBlock;
				for (BlockVector3 e : r) {
					i++;
				}*/
				Spaceship s = Main.getCurrentShip(player);
				BlockVector3 min = r.getMinimumPoint();
				BlockVector3 max = r.getMaximumPoint();
				SpecialBlock newBlock = new SpecialBlock(SpecialBlock.DOOR, player.getLocation().getWorld().getName(), min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
				if (s != null) {
					s.addBlock(newBlock);
					s.save();
				}
				else
					Main.blocks.add(newBlock);
				sender.sendMessage("Successfully created new door" + ((s != null) ? " and added it to " + s.name : "."));
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
				sender.sendMessage("Failed to get WorldEdit selection");
			}
		} else if (cmd.getName().equalsIgnoreCase("detector") && player != null) {
			Block b = player.getTargetBlock(null, 200);
			Spaceship s = Main.getCurrentShip(player);
			SpecialBlock newBlock = new SpecialBlock(b, SpecialBlock.AIR_DETECTOR, null);
			if (s != null) {
				s.addBlock(newBlock);
				s.save();
			}
			else
				Main.blocks.add(newBlock);
			sender.sendMessage("Successfully created new air detector" + ((s != null) ? " and added it to " + s.name : "."));
		} else if (cmd.getName().equalsIgnoreCase("gravity") && player != null) {
			try {
				Region r = Main.getWorldEdit().getWorldEdit().getSessionManager().get(new BukkitPlayer( Main.getWorldEdit(), player)).getSelection(new BukkitWorld(player.getWorld()));
				sender.sendMessage("Successfully aquired WorldEdit selection");
				/*int i = 0;
				SpecialBlock[] blocks = new SpecialBlock;
				for (BlockVector3 e : r) {
					i++;
				}*/
				Spaceship s = Main.getCurrentShip(player);
				BlockVector3 min = r.getMinimumPoint();
				BlockVector3 max = r.getMaximumPoint();
				SpecialBlock newBlock = new SpecialBlock(SpecialBlock.GRAVITY, player.getLocation().getWorld().getName(), min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
				if (s != null) {
					s.addBlock(newBlock);
					s.save();
				}
				else
					Main.blocks.add(newBlock);
				sender.sendMessage("Successfully created new gravity" + ((s != null) ? " and added it to " + s.name : "."));
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
				sender.sendMessage("Failed to get WorldEdit selection");
			}
		} else if (cmd.getName().equalsIgnoreCase("terminal") && player != null) {
			Block b = player.getTargetBlock(null, 200);
			Spaceship s = Main.getCurrentShip(player);
			SpecialBlock newBlock = new SpecialBlock(b, SpecialBlock.TERMINAL, s);
			if (s != null) {
				s.addBlock(newBlock);
				s.save();
			}
			else
				Main.blocks.add(newBlock);
			sender.sendMessage("Successfully created new terminal" + ((s != null) ? " and added it to " + s.name : "."));
		} else if (cmd.getName().equalsIgnoreCase("map") && player != null) {
			Block b = player.getTargetBlock(null, 200);
			Spaceship s = Main.getCurrentShip(player);
			SpecialBlock newBlock = new SpecialBlock(b, SpecialBlock.MAP, s);
			if (s != null) {
				s.addBlock(newBlock);
				s.save();
			}
			else
				Main.blocks.add(newBlock);
			sender.sendMessage("Successfully created new map" + ((s != null) ? " and added it to " + s.name : "."));
		} else if (cmd.getName().equalsIgnoreCase("pump") && player != null) {
			Block b = player.getTargetBlock(null, 200);
			Spaceship s = Main.getCurrentShip(player);
			SpecialBlock newBlock = new SpecialBlock(b, SpecialBlock.AIR_PUMP, s);
			if (s != null) {
				s.addBlock(newBlock);
				s.save();
			}
			else
				Main.blocks.add(newBlock);
			sender.sendMessage("Successfully created new air pump " + ((s != null) ? " and added it to " + s.name : "."));
		} else if (cmd.getName().equalsIgnoreCase("tank") && player != null) {
			Block b = player.getTargetBlock(null, 200);
			Spaceship s = Main.getCurrentShip(player);
			SpecialBlock newBlock = new SpecialBlock(b, SpecialBlock.AIR_TANK, s);
			if (s != null) {
				s.addBlock(newBlock);
				s.save();
			}
			else
				Main.blocks.add(newBlock);
			sender.sendMessage("Successfully created new air tank" + ((s != null) ? " and added it to " + s.name : "."));
		}
		return true;
	}
}
