package com.lerdorf.voidships;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class EntityShit implements CommandExecutor {
	/**
	 *
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player player = (sender instanceof Player) ? (Player) sender : null;

		
		if (cmd.getName().equalsIgnoreCase("vehicle") && player != null) {
			Spaceship ship = null;
			int type = -1;
			if (args.length != 1) {
				player.sendMessage("Expecting 1 arg, recieved " + args.length);
				return false;
			} 
			
			Location loc = player.getLocation();
			loc.setYaw(0);
			loc.setPitch(0);
			ArmorStand stand = (ArmorStand)((Player)sender).getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			
			if (args[0].equalsIgnoreCase("tie_fighter")) {
				type = SpecialEntity.TIE_FIGHTER;
			} else if (args[0].equalsIgnoreCase("escape_pod")) {
				type = SpecialEntity.ESCAPE_POD;
			} else if (args[0].equalsIgnoreCase("excape_pod_gun")) {
				type = SpecialEntity.ESCAPE_POD_GUN;
			} else if (args[0].equalsIgnoreCase("small_turret")) {
				type = SpecialEntity.SMALL_TURRET;
				ship = Main.getCurrentShip(player);
			} else if (args[0].equalsIgnoreCase("medium_turret")) {
				type = SpecialEntity.MEDIUM_TURRET;
				ship = Main.getCurrentShip(player);
			} else if (args[0].equalsIgnoreCase("dalek") || args[0].equalsIgnoreCase("dalek_drone")) {
				type = SpecialEntity.DALEK_DRONE;
				ship = Main.getCurrentShip(player);
				stand.setMarker(true);
			}
			
			SpecialEntity newEntity = new SpecialEntity(loc, type, ship);
			/*Zombie zombieEntity = (Zombie)((Player)sender).getWorld().spawnEntity(((Player)sender).getLocation(), EntityType.ZOMBIE);
			zombieEntity.setCustomName(newEntity.getName());
			zombieEntity.setCustomNameVisible(false);
			zombieEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1, true, false));
			zombieEntity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 255, true, false));
			zombieEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000, 255, true, false));
			zombieEntity.setGravity(false);
			zombieEntity.setSilent(true);
			zombieEntity.setInvulnerable(true);
			zombieEntity.setBaby(false);
			zombieEntity.addScoreboardTag("SpecialEntity");
			zombieEntity.addScoreboardTag("vehicle");
			zombieEntity.getEquipment().setHelmet(Main.createItem(Material.BRICK, "Tie Fighter Model", null, newEntity.customModelData));*/
			
			stand.setCustomName(newEntity.getName());
			stand.setCustomNameVisible(false);
			stand.setGravity(true);
			stand.setMarker(false);
			stand.setInvisible(true);
			stand.setInvulnerable(true);
			stand.setSmall(false);
			stand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
			stand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING);
			stand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING);
			stand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING);
			stand.addScoreboardTag("SpecialEntity");
			stand.addScoreboardTag("vehicle");
			stand.getEquipment().setHelmet(Main.createItem(Material.BRICK, newEntity.getName()+" Model", null, newEntity.customModelData));
			
			String specialTag = newEntity.getName().toLowerCase().replace(' ', '_') + "-" + (int)(Math.random()*Math.pow(10,1+Math.random()*10));
			//zombieEntity.addScoreboardTag(specialTag);
			stand.addScoreboardTag(specialTag);
			newEntity.tag = specialTag;
			if (ship == null) {
				Main.entities.put(specialTag, newEntity);
				newEntity.save();
			} else {
				ship.addEntity(newEntity);
			}
			Main.standEntities.put(specialTag, stand);
			
			sender.sendMessage("Successfully created new " + newEntity.getName());
		} else if (cmd.getName().equalsIgnoreCase("dalek") && player != null) {
			int type = SpecialEntity.DALEK_DRONE;
			
			Location loc = player.getLocation();
			loc.setYaw(0);
			loc.setPitch(0);
			SpecialEntity newEntity = new SpecialEntity(loc, type, null);

			ArmorStand stand = (ArmorStand)((Player)sender).getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			stand.setCustomName(newEntity.getName());
			stand.setCustomNameVisible(false);
			stand.setGravity(true);
			stand.setMarker(true);
			stand.setInvisible(true);
			stand.setInvulnerable(true);
			stand.setSmall(false);
			stand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
			stand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING);
			stand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING);
			stand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING);
			stand.addScoreboardTag("SpecialEntity");
			stand.addScoreboardTag("vehicle");
			stand.getEquipment().setHelmet(Main.createItem(Material.BRICK, newEntity.getName()+" Model", null, newEntity.customModelData));
			
			String specialTag = newEntity.getName().toLowerCase().replace(' ', '_') + "-" + (int)(Math.random()*Math.pow(10,1+Math.random()*10));
			//zombieEntity.addScoreboardTag(specialTag);
			stand.addScoreboardTag(specialTag);
			newEntity.tag = specialTag;
			//if (ship == null) {
				Main.entities.put(specialTag, newEntity);
				newEntity.save();
			//} else {
				//ship.addEntity(newEntity);
			//}
			Main.standEntities.put(specialTag, stand);
			
			newEntity.addPassenger(player);
			
			sender.sendMessage("Successfully created new " + newEntity.getName());
			
			newEntity.addPassenger(player);
		} else if (cmd.getName().equalsIgnoreCase("entities") && player != null) {
			if (Main.entities != null && Main.entities.size() > 0) {
				for (String t : Main.entities.keySet()) {
					ArmorStand stand = Main.standEntities.get(Main.entities.get(t).tag + "");
					SpecialEntity se = Main.entities.get(Main.entities.get(t).tag + "");
					if (stand == null) {
						sender.sendMessage("LIVING ENTITY IS NULL");

						sender.sendMessage(t + " = " + Main.entities.get(t).tag + " ==> " + Main.entities.get(t).getName() + " Fuel: " +  Main.entities.get(t).fuel);
					} else 
						sender.sendMessage(t + " = " + Main.entities.get(t).tag + " ==> " + Main.entities.get(t).getName() + " Fuel: " +  Main.entities.get(t).fuel + " at " + stand.getLocation().getBlockX() + " " + stand.getLocation().getBlockY() + " " + stand.getLocation().getBlockZ());
					if (se == null) {
						sender.sendMessage("SPECIAL ENTITY IS NULL");
					}
				}
			}
			for (Spaceship ship : Main.ships) {
				if (ship != null) {
					if (ship.entities != null && ship.entities.length > 0) {
						sender.sendMessage("Spaceship " + ship.name);
						for (SpecialEntity e : ship.entities) {
							if (e == null) continue;
							ArmorStand stand = Main.standEntities.get(e.tag);
							if (stand == null)
								sender.sendMessage(e.tag + " ==> " + e.getName() + " No entity attached");
							else
								sender.sendMessage(e.tag + " ==> " + e.getName() + " at " + stand.getLocation().getBlockX() + " " + stand.getLocation().getBlockY() + " " + stand.getLocation().getBlockZ());
						}
					}
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("delentity")) {
			if (args.length == 1) {
				Main.entities.remove(args[0]);
				ArmorStand le = Main.standEntities.get(args[0]);
				if (le != null) {
					le.damage(10000);
					le.remove();
				}
				Main.standEntities.remove(args[0]);
				if ((new File(player.getLocation().getWorld().getName()+"/VoidShips/"+args[0]+".dat")).delete())
					sender.sendMessage(args[0] + ".dat successfully deleted");
				else
					sender.sendMessage("Deleted " + args[0] + ".dat in memory, but couldn't find the file in storage");
			} else
				sender.sendMessage("Expecting 1 arg");
		} else if (cmd.getName().equalsIgnoreCase("spawnplayer")) {
			Main.spawnFakePlayer(player.getLocation(), args == null || args.length < 1 ? player.getName() : args[0], null);
		}
		return true;
	}
}
