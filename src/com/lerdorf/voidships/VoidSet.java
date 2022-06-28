package com.lerdorf.voidships;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class VoidSet implements CommandExecutor {

	/*
	 * public boolean onCommand(CommandSender sender, Command cmd, String label,
	 * String[] args) { Player player = (Player) sender; if
	 * (cmd.getName().equalsIgnoreCase("heal") ||
	 * cmd.getName().equalsIgnoreCase("h")) {
	 * 
	 * 
	 * //Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "effect give " + player.getName() + " minecraft:saturation 3 255");
	 * //Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "effect give " + player.getName() + " minecraft:instant_health 3 255");
	 * player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 3, 200));
	 * player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3,
	 * 200)); Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "execute as " + player.getName() +
	 * " at @s run particle minecraft:end_rod ~ ~1 ~ 0.1 0.1 0.1 0.5 100 force");
	 * Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as " +
	 * player.getName() +
	 * " at @s run playsound minecraft:block.end_portal.spawn master @a ~ ~ ~ 1 2");
	 * 
	 * Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say " +
	 * player.getName() + " has been healed");
	 * 
	 * } else if (cmd.getName().equalsIgnoreCase("healall") ||
	 * cmd.getName().equalsIgnoreCase("ha")) {
	 * 
	 * //Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "effect give @a minecraft:saturation 3 255");
	 * //Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "effect give @a minecraft:instant_health 3 255"); Collection<? extends
	 * Player> players = sender.getServer().getOnlinePlayers(); Iterator<? extends
	 * Player> iterator = players.iterator(); while (iterator.hasNext()) { Player p
	 * = iterator.next(); p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL,
	 * 3, 200)); p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3,
	 * 200)); } Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "execute as @a at @s run particle minecraft:end_rod ~ ~1 ~ 0.1 0.1 0.1 0.5 100 force"
	 * ); Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "execute as @a at @s run playsound minecraft:block.end_portal.spawn master @s ~ ~ ~ 1 2"
	 * );
	 * 
	 * Bukkit.dispatchCommand(player.getServer().getConsoleSender(),
	 * "say All players have been healed");
	 * 
	 * } return false; }
	 */

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("voidset")) {
			if (args.length == 0) {
				try {
					Region r = Main.getWorldEdit().getWorldEdit().getSessionManager().get(new BukkitPlayer( Main.getWorldEdit(), player)).getSelection(new BukkitWorld(player.getWorld()));
					
					sender.sendMessage("Successfully aquired WorldEdit selection");
		
					BlockVector3 min = r.getMinimumPoint();
					BlockVector3 max = r.getMaximumPoint();
					
					Main.voids.add(new Void(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), player.getLocation().getWorld()));
					Main.voids.get(Main.voids.size()-1).save("void" + (Main.voids.size()-1) + ".dat");
				} catch (Exception e) {
					sender.sendMessage("Ruh roh something went wrong (either make a worldedit selection, or pass in coords)");
				}
			
			} else if (args.length == 1) {
				Main.voids.add(new Void(Bukkit.getWorld(args[0])));
				Main.voids.get(Main.voids.size()-1).save("void" + (Main.voids.size()-1) + ".dat");
			} else if (args.length != 6)
					player.sendMessage("Incorrect number of args for /voidset (expected 6)");
			else {
				try {
					Main.voids.add(new Void(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), player.getLocation().getWorld()));
					Main.voids.get(Main.voids.size()-1).save("void" + (Main.voids.size()-1) + ".dat");
				} catch (Exception e) {
					player.sendMessage("Something went wrong:\n" + e.getMessage() + "\n" + e.getStackTrace());
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("voidcreate")) {
			if (args.length == 0) {
				try {
					Region r = Main.getWorldEdit().getWorldEdit().getSessionManager().get(new BukkitPlayer( Main.getWorldEdit(), player)).getSelection(new BukkitWorld(player.getWorld()));
					
					sender.sendMessage("Successfully aquired WorldEdit selection");
		
					BlockVector3 min = r.getMinimumPoint();
					BlockVector3 max = r.getMaximumPoint();
					
					//Vector min = new Vector(Math.min(min2.getX(), max2.getX()), Math.min(min2.getY(), max2.getY()), Math.min(min2.getZ(), max2.getZ()));
					//Vector max = new Vector(Math.max(min2.getX(), max2.getX()), Math.max(min2.getY(), max2.getY()), Math.max(min2.getZ(), max2.getZ()));
					
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX()+1, min.getY()+1, min.getZ()+1, max.getX()-1, max.getY()-1, max.getZ()-1, Material.AIR);
					
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), min.getZ(), Material.BARRIER);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX(), min.getY(), min.getZ(), min.getX(), max.getY(), max.getZ(), Material.BARRIER);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX(), min.getY(), min.getZ(), max.getX(), min.getY(), max.getZ(), Material.BARRIER);
					Main.fillAsync(player.getLocation().getWorld().getName(), max.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), Material.BARRIER);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX(), max.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), Material.BARRIER);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX(), min.getY(), max.getZ(), max.getX(), max.getY(), max.getZ(), Material.BARRIER);

					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX()-1, min.getY()-1, min.getZ()-1, max.getX()+1, max.getY()+1, min.getZ()-1, Material.BLACK_CONCRETE);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX()-1, min.getY()-1, min.getZ()-1, min.getX()-1, max.getY()+1, max.getZ()+1, Material.BLACK_CONCRETE);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX()-1, min.getY()-1, min.getZ()-1, max.getX()+1, min.getY()-1, max.getZ()+1, Material.BLACK_CONCRETE);
					Main.fillAsync(player.getLocation().getWorld().getName(), max.getX()+1, min.getY()-1, min.getZ()-1, max.getX()+1, max.getY()+1, max.getZ()+1, Material.BLACK_CONCRETE);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX()-1, max.getY()+1, min.getZ()-1, max.getX()+1, max.getY()+1, max.getZ()+1, Material.BLACK_CONCRETE);
					Main.fillAsync(player.getLocation().getWorld().getName(), min.getX()-1, min.getY()-1, max.getZ()+1, max.getX()+1, max.getY()+1, max.getZ()+1, Material.BLACK_CONCRETE);
					
					Main.voids.add(new Void(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), player.getLocation().getWorld()));
					Main.voids.get(Main.voids.size()-1).save("void" + (Main.voids.size()-1) + ".dat");
				} catch (Exception e) {
					sender.sendMessage("Ruh roh something went wrong (either make a worldedit selection, or pass in coords)");
				}
			}
			else if (args.length != 6)
				player.sendMessage("Incorrect number of args for /voidset (expected 6)");
			else {
				try {
					Main.voids.add(new Void(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), player.getLocation().getWorld()));
					Void v = Main.voids.get(Main.voids.size()-1);
					v.save("void" + (Main.voids.size()-1) + ".dat");
				} catch (Exception e) {
					player.sendMessage("Something went wrong:\n" + e.getMessage() + "\n" + e.getStackTrace());
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("asyncFill")) { 
			if (args.length != 1) {
				sender.sendMessage("Expected 1 arg");
				return false;
			}
			try {
				Region r = Main.getWorldEdit().getWorldEdit().getSessionManager().get(new BukkitPlayer( Main.getWorldEdit(), player)).getSelection(new BukkitWorld(player.getWorld()));
			
				sender.sendMessage("Successfully aquired WorldEdit selection");
	
				BlockVector3 min = r.getMinimumPoint();
				BlockVector3 max = r.getMaximumPoint();
				Main.fillAsync(player.getLocation().getWorld().getName(), min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), Material.valueOf(args[0].toUpperCase()));
				sender.sendMessage("Started async fill");
			} catch (IncompleteRegionException e) {
				sender.sendMessage("Incomplete worldedit selection");
			}
		} else if (cmd.getName().equalsIgnoreCase("newship")) {
			if (args.length != 1) {
				player.sendMessage("A name needs to be provided");
				return false;
			}
			Location loc = player.getLocation();
			Void v = null;
			for (int i = 0; i < Main.voids.size(); i++) {
				v = Main.voids.get(i);
				if (v.within(loc))
					break;
				if (i == Main.voids.size()-1)
					v = null;
			}
			if (v == null) {
				player.sendMessage("You need to make your ship within a void region, create a new void region with /voidset x1 y1 z1 x2 y2 z2");
				return false;
			} // Spaceship(String name, double x, double y, double z, Void space, CosmicBody orbiting, SolarSystem system, int sx, int sy, int sz, String world)
			SolarSystem s = Main.systems.get((int)(Math.random()*Main.systems.size()));
			Main.ships.add(new Spaceship(args[0].replace('_', ' '), (Math.random()-0.5)*5.9091*Math.pow(10,9), (Math.random()-0.5)*5.9091*Math.pow(10,9), (Math.random()-0.5)*5.9091*Math.pow(10,9), v, null, s, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName() ));
			Main.ships.get(Main.ships.size()-1).save("ship" + (Main.voids.size()-1) + ".dat");
			player.sendMessage("Successfully created ship " + args[0].replace('_', ' ') + " in system " + s.getName());
		} else if (cmd.getName().equalsIgnoreCase("newplanet")) { 
            String name = null;
            String knickname = null;
            String id = null;
            String type = null;
            double dist = -1; // distance to star in km
            double rad = -1; // earth radii
            double mass = -1; // earth masses
            double period = -1; //days per year
            boolean habitable = false;
            CosmicBody star = null;
            SolarSystem system = null;
            int systemIndex = -1;
            for (int i = 0; i < args.length; i++) { // -n [name], -kn [knickname], -d [distance from star in km], -s [star (name)], -o [orbital period (days-per-year)], -r [radius in relative to earth], -m [math (relative to earth], -h (if habitable)
                if (args[i].equals("-n")) {
                    i++;
                    name = args[i].replace('_', ' ');
                } else if (args[i].equals("-kn")) {
                    i++;
                    knickname = args[i].replace('_', ' ');
                } else if (args[i].equals("-d")) {
                    i++;
                    dist = Double.parseDouble(args[i]);
                } else if (args[i].equals("-s")) {
                    i++;
                    for (int j = 0; j < Main.systems.size(); j++) {
                    	SolarSystem s = Main.systems.get(j);
                        if (s.sun.name.equalsIgnoreCase(args[i].replace('_', ' '))) {
                            star = s.sun;
                            system = s;
                            systemIndex = j;
                            System.out.println("Found star!");
                            break;
                        }
                    }
                } else if (args[i].equals("-t")) {
                	i++;
                	type = args[i].replace('_', ' ');
                } else if (args[i].equals("-o")) {
                    i++;
                    period = Double.parseDouble(args[i]);
                } else if (args[i].equals("-r")) {
                    i++;
                    rad = Double.parseDouble(args[i]);
                } else if (args[i].equals("-m")) {
                    i++;
                    mass = Double.parseDouble(args[i]);
                } else if (args[i].equals("-h")) {
                    habitable = true;
                }
                
            }
            if (mass == -1) {
            	if (rad == -1)
            		mass = 0.00218 + Math.random()*Math.random()*(200);
            	else
            		mass = Math.pow(rad, 1/0.55);
            }
            if (rad == -1) {
            	if (mass == -1)
            		rad = 0.186 + Math.random()*Math.random()*(15);
            	else
            		rad = Math.pow(mass, 0.55);
            }
            if (dist == -1) {
                dist = 0.0033 + Math.random()*Math.random()*(100);
                //dist /= 1.496*Math.pow(10,8);
            } // CosmicBody(String name, String knickname, double x, double y, double z, double mass, double radius, double orbitDist, CosmicBody orbiting, double orbitPeriod, boolean habitable)
            if (system == null)
            	sender.sendMessage("Missing required arg: -s\n(Perhaps there is a spelling mistake in the name of the star");
            else {
	            CosmicBody p = new CosmicBody(name, knickname, type, 0, 0, 0, mass*5.972*Math.pow(10,24), rad*6371, dist*1.496*Math.pow(10,8), star, period, habitable);
	            system.addPlanet( p );
	            Main.systems.get(systemIndex).save("system" + (systemIndex) + ".dat");
	            sender.sendMessage("Successfully created planet " + name);
	            
	            String info = ".\n";
				//info += " [" + (int)(p.x/(1.496*Math.pow(10,8))) + " " + (int)(p.y/(1.496*Math.pow(10,8))) + " " + (int)(p.z/(1.496*Math.pow(10,8))) + "]";
				if (p != null) {
					if (p.name != null) info += " [§6" + p.name + "§f]";
					if (p.knickname != null) info += " [§d" + p.knickname + "§f]";
					if (p.id != null) info += " [§b" + p.id + "§f]";
					if (p.type != null) info += " [§c" + p.type + "§f]";
					if (p.mass != -1) info += " [Mass: " + (int)(10000*p.mass/(5.972*Math.pow(10,24)))/10000f + " M⭘]";
					if (p.radius != -1) info += " [Radius: " + (int)(10000*p.radius/6371f)/10000f + " R⭘]";
					//info += " [" + (int)(10000f*Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z)/(1.496*Math.pow(10,8)))/10000f + " AU from " + system.getName() + "]";
					if (p.orbitDist != -1) info += " [" + (int)(10000*p.orbitDist/(1.496*Math.pow(10,8)))/10000f + " AU from " + system.getName() + "]";
					if (p.orbitPeriod != -1) info += " [Period: " + p.orbitPeriod + " Earth days]";
					if (p.habitable) info += " [habitable]";
				}
				sender.sendMessage( info );
            }
        } else if (cmd.getName().equalsIgnoreCase("newsystem")) {
			String name = null;
			String knickname = null;
			String id = null;
			double lyx = -1, lyy = 1, lyz = -1;
			double dist = -1;
			String type = null;
			double rad = -1;
			double mass = -1;
            String ttype = null;
			for (int i = 0; i < args.length; i++) { // -n [name], -kn [knickname], -id [system id], -c [x y z coords in lightyears], -d [distance from sun in lightyears], -t [type of star], -r [solar-radius of star], -m [solar-mass of star]
				if (args[i].equals("-n")) {
					i++;
					name = args[i].replace('_', ' ');
				} else if (args[i].equals("-kn")) {
					i++;
					knickname = args[i].replace('_', ' ');
				} else if (args[i].equals("-id")) {
					i++;
					id = args[i].replace('_', ' ');
				} else if (args[i].equals("-c")) {
					i++;
					lyx = Double.parseDouble(args[i]);
					i++;
					lyy = Double.parseDouble(args[i]);
					i++;
					lyz = Double.parseDouble(args[i]);
				} else if (args[i].equals("-d")) {
					i++;
					dist = Double.parseDouble(args[i]);
				} else if (args[i].equals("-t")) {
					i++;
					type = args[i].replace('_', ' ');
                    ttype = type;
				} else if (args[i].equals("-r")) {
					i++;
					rad = Double.parseDouble(args[i]) * 695700;
				} else if (args[i].equals("-m")) {
					i++;
					mass = Double.parseDouble(args[i]) * 1.989 * Math.pow(10, 30);
				}
					
			}
			if (dist == -1)
				dist = 10+10000*Math.random();
			if (lyx == -1 && lyy == 1 && lyz == -1) {
				lyx = Math.random();
				lyy = Math.random();
				lyz = Math.random();
				double lyt = Math.sqrt( lyx*lyx + lyy*lyy + lyz*lyz);
				lyx = lyx * dist / lyt;
				lyy = lyy * dist / lyt;
				lyz = lyz * dist / lyt;
			}
			if (type == null || rad == -1 || mass == -1) {
				double typeMassMin = -1; // solar mass
				double typeMassMax = -1;
				double typeRadiusMin = -1; // solar radius
				double typeRadiusMax = -1;
				int count = 0;
                do {
	                switch((int)(25 * Math.random())) {
					case 0:
						type = "Solar analog star";
						typeMassMin = 0.8;
	                    typeMassMax = 1.2;
	                    break;
					case 1:
						type = "Hot blue star";
						typeMassMin = 3;
						typeMassMax = 10;
	                    typeRadiusMin = 1.4;
	                    typeRadiusMax = 6;
						break;
					case 2:
						type = "Orange dwarf star";
						typeMassMin = 0.5;
						typeMassMax = 0.8;
	                    typeRadiusMin = 0.9;
	                    typeRadiusMax = 9;
						break;
					case 3:
					case 4:
						type = "Red dwarf star";
						typeMassMin = 0.075;
						typeMassMax = 0.6;
	                    typeRadiusMin = 0.09;
	                    typeRadiusMax = 0.7;
						break;
					case 5:
						type = "Red giant star";
						typeMassMin = 0.3;
	                    typeMassMax = 8;
	                    typeRadiusMin = 50;
	                    typeRadiusMax = 100;
	                    break;
					case 6:
						type = "White dwarf star";
						typeRadiusMin = 0.009;
	                    typeRadiusMax = 0.009248;
	                    typeMassMin = 0.15;
	                    typeMassMax = 1.4;
	                    break;
					case 7:
						type = "Neutron star";
						typeMassMin = 10;
	                    typeMassMax = 24;
	                    typeRadiusMin = 0.0000114943;
	                    typeRadiusMax = 0.0000229885;
	                    break; // 8-16 km, 1 solar radius = 695700 km
					case 8:
						type = "Stellar-mass black hole";
	                    typeMassMin = 5;
	                    typeMassMax = 2000;
						typeRadiusMin = 2.156*Math.pow(10,-5);
	                    typeRadiusMax = 0.0028748;
	                    break;
					case 9:
						type = "Protostar";
						typeMassMin = 0.08;
	                    typeMassMax = 200;
	                    break;
					case 11:
					case 12:
						type = "Main sequence star";
						typeMassMin = 0.1;
						typeMassMax = 200;
						break;
					case 13:
						type = "Yellow dwarf star";
						typeMassMin = 0.7;
						typeMassMax = 1;
						break;
					case 14:
						type = "Blue giant star";
						typeRadiusMin = 5;
						typeRadiusMax = 10;
						typeMassMin = 7;
						typeMassMax = 15;
						break;
					case 15:
						type = "Red supergiant";
						typeMassMin = 8;
	                    typeMassMax = 40;
	                    typeRadiusMin = 100;
	                    typeRadiusMax = 10000;
	                    break;
					case 16:
						type = "Blue supergiant";
						typeMassMin = 15;
						typeMassMax = 25;
	                    typeRadiusMin = 10;
	                    typeRadiusMax = 100;
						break;
					case 17:
						type = "Brown dwarf star";
						typeMassMin = 0;
	                    typeMassMax = 0.08;
	                    break;
					case 19:
						type = "Cepheid variable star";
						typeMassMin = 4;
	                    typeMassMax = 20;
	                    break;
	                case 20:
	                    type = "White hole";
	                    typeMassMin = 0;
	                    typeMassMax = Math.pow(10,900);
	                    typeRadiusMin = 0;
	                    typeRadiusMax = 0.0001;
	                    break;
	                case 21:
	                    type = "Quark star";
	                    typeMassMin = 24;
	                    typeMassMax = 30;
	                    typeRadiusMin = 2.156*Math.pow(10,-5);
	                    typeRadiusMax = 0.0000229885;
	                    break;
	                case 22:
	                    type = "Strange star";
	                    typeMassMin = 10;
	                    typeMassMax = 24;
	                    typeRadiusMin = 0.0000114943;
	                    typeRadiusMax = 0.0000229885;
	                    break;
	                case 23:
	                    type = "Supermassive black hole";
	                    typeMassMin = Math.pow(10,5);
	                    typeMassMax = Math.pow(10,10);
	                    typeRadiusMin = 0.001;
	                    typeRadiusMax = 400;	
	                    break;
	                case 24:
	                    type = "Micro black hole";
	                    typeMassMin = 0;
	                    typeMassMax = 3.69223*Math.pow(10,-9);
	                    typeRadiusMin = 0;
	                    typeRadiusMax = 0.0014368;
					}
	                count++;
                } while ( ((rad > -1 && (rad < typeRadiusMin || rad > typeRadiusMax)) || (mass > -1 && (mass < typeMassMin || mass > typeMassMax)) || (type != null && ttype != null && !type.equalsIgnoreCase(ttype))) && count < 100  );
                if (ttype != null)
                	type = ttype;
                if (typeRadiusMin == -1 && typeMassMin > -1)
                    typeRadiusMin = Math.pow(typeMassMin, 0.8);
                if (typeRadiusMax == -1 && typeMassMax > -1)
                    typeRadiusMax = Math.pow(typeMassMax, 0.8);
                if (typeMassMin > -1 && typeMassMax > -1) {
                    if (mass == -1 && rad == -1)
                        mass = typeMassMin + Math.random()*(typeMassMax-typeMassMin);
                    if (rad == -1)
                        rad = Math.min(typeRadiusMin, Math.max(Math.pow(mass,0.8), typeRadiusMax));
                    if (mass == -1)
                        mass = Math.min(typeMassMin, Math.max(Math.pow(rad,1/0.8), typeMassMax));
                }
			}
            CosmicBody star = new CosmicBody(name, knickname, type, id, 0, 0, 0, mass*1.989*Math.pow(10,30), rad*695700, null, -1);
            Main.systems.add(new SolarSystem(star, lyx, lyy, lyz, null, null, player.getLocation().getWorld().getName()));
            Main.systems.get(Main.systems.size()-1).save("system" + (Main.systems.size()-1) + ".dat");
            sender.sendMessage("Successfully created new system " + Main.systems.get(Main.systems.size()-1).getName());
		}
		return false;
	}

}
