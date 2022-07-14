package com.lerdorf.voidships;

import org.bukkit.command.CommandExecutor;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VoidQuery implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("voidlist")) {

			sender.sendMessage( "List of voids:" );
			for (int i = 0; i < Main.voids.size(); i++) {
				Void v = Main.voids.get(i);
				sender.sendMessage( "void" + i + ".dat  [" + v.x1 + ", " + v.y1 + ", " + v.z1 + "]  [" + v.x2 + ", " + v.y2 + ", " + v.z2 + "] [" + v.world + "]" );
			}
			
		} else if (cmd.getName().equalsIgnoreCase("invoid")) {
			
			Location loc = player.getLocation();
			int r = 0;
			for (int i = 0; i < Main.voids.size(); i++) {
				Void v = Main.voids.get(i);
				if (v.within(loc)) {
					sender.sendMessage( "You are within void" + i + ".dat  [" + v.x1 + ", " + v.y1 + ", " + v.z1 + "]  [" + v.x2 + ", " + v.y2 + ", " + v.z2 + "] [" + v.world + "]" );
					r++;
				}
			}
			sender.sendMessage("You are within " + r + " voids");
		} else if (cmd.getName().equalsIgnoreCase("delvoid")) {
			try { 
				Main.voids.remove(Integer.parseInt(args[0]));
				if ((new File(player.getLocation().getWorld().getName()+"/VoidShips/void"+args[0]+".dat")).delete())
					sender.sendMessage("void" + args[0] + ".dat successfully deleted");
				else
					sender.sendMessage("Deleted void" + args[0] + ".dat in memory, but couldn't find the file in storage");
			} catch (Exception e) {
				sender.sendMessage("You fool, you need to pass in a valid index");
			}
		} else if (cmd.getName().equalsIgnoreCase("voidload")) {
			Main.loadSaves();
			sender.sendMessage("Loading saved files");
		} else if (cmd.getName().equalsIgnoreCase("ships")) {
			for (int i = 0; i < Main.ships.size(); i++) {
				Spaceship s = Main.ships.get(i);
				sender.sendMessage( "ship" + i + ".dat  [§6" + s.name + "§f] [" + s.world + "] [Spawn = " + s.sx + " " + s.sy + " " + s.sz + "]" );
			}
		} else if (cmd.getName().equalsIgnoreCase("planets")) {
			if (args.length != 1) {
				sender.sendMessage("Expected 1 arg, recieved " + args.length);
				return false;
			}
			for (SolarSystem s : Main.systems) {
				if (s != null && s.sun != null && ((s.sun.name != null && s.sun.name.equalsIgnoreCase(args[0].replace('_', ' '))) || (s.sun.knickname != null && s.sun.knickname.equalsIgnoreCase(args[0].replace('_', ' '))))) {
					if (s.planets == null || s.planets.length == 0) {
						sender.sendMessage("This system has no planets");
						return false;
					}
					String star = (s.sun.knickname != null ? s.sun.knickname : (s.sun.name != null ? s.sun.name : args[0].replace('_', ' ')));
					sender.sendMessage("This system has " + s.planets.length + " planets");
					for (int i = 0; i < s.planets.length; i++) {
						CosmicBody p = s.planets[i];
						String info = ".\nplanet " + i + "\n";
						//info += " [" + (int)(p.x/(1.496*Math.pow(10,8))) + " " + (int)(p.y/(1.496*Math.pow(10,8))) + " " + (int)(p.z/(1.496*Math.pow(10,8))) + "]";
						if (p != null) {
							if (p.name != null) info += " [§6" + p.name + "§f]";
							if (p.knickname != null) info += " [§d" + p.knickname + "§f]";
							if (p.id != null) info += " [§b" + p.id + "§f]";
							if (p.type != null) info += " [§c" + p.type + "§f]";
							if (p.mass != -1) info += " [Mass: " + (int)(10000*p.mass/(5.972*Math.pow(10,24)))/10000f + " M⭘]";
							if (p.radius != -1) info += " [Radius: " + (int)(10000*p.radius/6371f)/10000f + " R⭘]";
							//info += " [" + (int)(10000f*Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z)/(1.496*Math.pow(10,8)))/10000f + " AU from " + system.getName() + "]";
							if (p.orbitDist != -1) info += " [" + (int)(10000*p.orbitDist/(1.496*Math.pow(10,8)))/10000f + " AU from " + star + "]";
							if (p.orbitPeriod != -1) info += " [Period: " + p.orbitPeriod + " Earth days]";
							if (p.habitable) info += " [habitable]";
						}
						sender.sendMessage( info );
					}
					return false;
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("systems")) {
			for (int i = 0; i < Main.systems.size(); i++) {
				SolarSystem s = Main.systems.get(i);
				String info = "system" + i + ".dat\n";
				//info += " [" + s.lyx + " " + s.lyy + " " + s.lyz + "]";
				info += " [" + Math.round(10000*Math.sqrt(s.lyx*s.lyx + s.lyy*s.lyy + s.lyz*s.lyz))/10000f + " LY from Earth]";
				if (s.sun != null) {
					if (s.sun.name != null) info += " [§6" + s.sun.name + "§f]";
					if (s.sun.knickname != null) info += " [§d" + s.sun.knickname + "§f]";
					if (s.sun.id != null) info += " [§b" + s.sun.id + "§f]";
					if (s.sun.type != null) info += " [§c" + s.sun.type + "§f]";
				}
				if (s.planets != null) info += " [Planets: " + s.planets.length + "]";
				if (s.world != null) info += " [" + s.world + "]";
				sender.sendMessage( info );
			}
		} else if (cmd.getName().equalsIgnoreCase("ship")) {
			if (Main.ships.size() > 0) {
				Location loc = player.getLocation();
				Spaceship r = Main.ships.get(0);
				int i = 0;
				int j = 0;
				for (i = 0; i < Main.ships.size(); i++) {
					Spaceship s = Main.ships.get(i);
					if (dist(r.sx-loc.getX(), r.sy-loc.getY(), r.sz-loc.getZ()) > dist(s.sx-loc.getX(), s.sy-loc.getY(), s.sz-loc.getZ())) {
						r = s;
						j = i;
					}
				}
				if (dist(r.sx-loc.getX(), r.sy-loc.getY(), r.sz-loc.getZ()) < 500) {
					sender.sendMessage("Nearest ship:");
					String info =  "ship" + j + ".dat ";
					if (r.name != null) info += " [§6"+r.name+"§f]";
					if (r.system != null) info += " [§d"+r.system.getName()+"§f]";
					if (r.orbiting != null) info += " [Orbiting: "+r.orbiting.name+"]";
					if (r.world != null) info += " ["+r.world+"]";
					info += " [Spawn: "+r.sx + " " + r.sy + " " + r.sz+"]";
					sender.sendMessage( info );
				} else sender.sendMessage("No nearby ships");
			} else {
				sender.sendMessage("There are no ships");
			}
		} else if (cmd.getName().equalsIgnoreCase("planet")) {
			if (Main.ships.size() > 0) {
				Location loc = player.getLocation();
				Spaceship r = Main.ships.get(0);
				for (int i = 0; i < Main.ships.size(); i++) {
					Spaceship s = Main.ships.get(i);
					if (dist(r.sx-loc.getX(), r.sy-loc.getY(), r.sz-loc.getZ()) > dist(s.sx-loc.getX(), s.sy-loc.getY(), s.sz-loc.getZ()))
						r = s;
				}
				if (dist(r.sx-loc.getX(), r.sy-loc.getY(), r.sz-loc.getZ()) < 500) {
					
					CosmicBody p = r.orbiting;
					
					sender.sendMessage("Nearest planet:");
					String info = ".\n";
					//info += " [" + (int)(p.x/(1.496*Math.pow(10,8))) + " " + (int)(p.y/(1.496*Math.pow(10,8))) + " " + (int)(p.z/(1.496*Math.pow(10,8))) + "]";
					if (p != null) {
						String star = r.system != null ? r.system.getName() : ":unknown star:";
						if (p.name != null) info += " [§6" + p.name + "§f]";
						if (p.knickname != null) info += " [§d" + p.knickname + "§f]";
						if (p.id != null) info += " [§b" + p.id + "§f]";
						if (p.type != null) info += " [§c" + p.type + "§f]";
						if (p.mass != -1) info += " [Mass: " + (int)(10000*p.mass/(5.972*Math.pow(10,24)))/10000f + " M⭘]";
						if (p.radius != -1) info += " [Radius: " + (int)(10000*p.radius/6371f)/10000f + " R⭘]";
						//info += " [" + (int)(10000f*Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z)/(1.496*Math.pow(10,8)))/10000f + " AU from " + system.getName() + "]";
						if (p.orbitDist != -1) info += " [" + (int)(10000*p.orbitDist/(1.496*Math.pow(10,8)))/10000f + " AU from " + star + "]";
						if (p.orbitPeriod != -1) info += " [Period: " + p.orbitPeriod + " Earth days]";
						if (p.habitable) info += " [habitable]";
					}
					sender.sendMessage( info );
					
				} else sender.sendMessage("No nearby ships");
			} else {
				sender.sendMessage("There are no ships");
			}
				
		} else if (cmd.getName().equalsIgnoreCase("system")) {
			if (Main.ships.size() > 0) {
				Location loc = player.getLocation();
				Spaceship r = Main.ships.get(0);
				for (int i = 0; i < Main.ships.size(); i++) {
					Spaceship s = Main.ships.get(i);
					if (dist(r.sx-loc.getX(), r.sy-loc.getY(), r.sz-loc.getZ()) > dist(s.sx-loc.getX(), s.sy-loc.getY(), s.sz-loc.getZ()))
						r = s;
				}
				if (dist(r.sx-loc.getX(), r.sy-loc.getY(), r.sz-loc.getZ()) < 500) {
					
					SolarSystem s = r.system;
					
					sender.sendMessage("Within system:");
					String info = ".\n";
					//info += " [" + s.lyx + " " + s.lyy + " " + s.lyz + "]";
					//info += " [" + Math.round(100*Math.sqrt(s.lyx*s.lyx + s.lyy*s.lyy + s.lyz*s.lyz))/100f + " LY from Earth]";
					if (s == null) {
						sender.sendMessage("Could not retrieve system");
						return false;
					}
					info += " [" + Math.round(10000*Math.sqrt(s.lyx*s.lyx + s.lyy*s.lyy + s.lyz*s.lyz))/10000f + " LY from Earth]";
					if (s.sun != null) {
						if (s.sun.name != null) info += " [§6" + s.sun.name + "§f]";
						if (s.sun.knickname != null) info += " [§d" + s.sun.knickname + "§f]";
						if (s.sun.id != null) info += " [§b" + s.sun.id + "§f]";
						if (s.sun.type != null) info += " [§c" + s.sun.type + "§f]";
					}
					if (s.planets != null) info += " [Planets: " + s.planets.length + "]";
					if (s.world != null) info += " [" + s.world + "]";
					sender.sendMessage( info );
					
				} else sender.sendMessage("No nearby ships");
			} else {
				sender.sendMessage("There are no ships");
			}
		} else if (cmd.getName().equalsIgnoreCase("delship")) {
			try { 
				Main.ships.remove(Integer.parseInt(args[0]));
				if ((new File(player.getLocation().getWorld().getName()+"/VoidShips/ship"+args[0]+".dat")).delete())
					sender.sendMessage("ship" + args[0] + ".dat successfully deleted");
				else
					sender.sendMessage("Deleted ship" + args[0] + ".dat in memory, but couldn't find the file in storage");
			} catch (Exception e) {
				sender.sendMessage("You fool, you need to pass in a valid index");
			}
		} else if (cmd.getName().equalsIgnoreCase("delplanet")) {
			try { 
				SolarSystem s = Main.systems.get(Integer.parseInt(args[0]));
				sender.sendMessage("Deleting planet " + s.planets[Integer.parseInt(args[1])].name + " in system " + s.getName());
				s.delPlanet(Integer.parseInt(args[1]));
				s.save("system"+args[0]+".dat");
			} catch (Exception e) {
				sender.sendMessage("You fool, you need to pass in a valid index");
			}
		} else if (cmd.getName().equalsIgnoreCase("delsystem")) {
			try { 
				Main.systems.remove(Integer.parseInt(args[0]));
				if ((new File(player.getLocation().getWorld().getName()+"/VoidShips/system"+args[0]+".dat")).delete())
					sender.sendMessage("system" + args[0] + ".dat successfully deleted");
				else
					sender.sendMessage("Deleted system" + args[0] + ".dat in memory, but couldn't find the file in storage");
			} catch (Exception e) {
				sender.sendMessage("You fool, you need to pass in a valid index");
			}
		} else if (cmd.getName().equalsIgnoreCase("blocks")) {
			for (SpecialBlock b : Main.blocks) {
				if (b != null)
					sender.sendMessage(b.toString());
			}
			for (Spaceship s : Main.ships) {
				if (s != null && ((s.blocks != null && s.blocks.length > 0) || (s.airTanks != null && s.airTanks.length > 0)))
					sender.sendMessage("In ship §6" + s.name + "§f");
				int i = 0;
				if (s != null && s.blocks != null && s.blocks.length > 0)
					for (SpecialBlock b : s.blocks)
						if (b != null)
							sender.sendMessage((i++) + " " + b.toString());
				i = 0;
				if (s != null && s.airTanks != null && s.airTanks.length > 0)
					for (SpecialBlock b : s.airTanks) 
						if (b != null)
							sender.sendMessage((i++) + " " + b.toString());
			}
		} else if (cmd.getName().equalsIgnoreCase("delblock")) {
			if (args.length == 1) {
				try { 
					Main.blocks.remove(Integer.parseInt(args[0]));
					if ((new File(player.getLocation().getWorld().getName()+"/VoidShips/block"+args[0]+".dat")).delete())
						sender.sendMessage("block" + args[0] + ".dat successfully deleted");
					else
						sender.sendMessage("Deleted block" + args[0] + ".dat in memory, but couldn't find the file in storage");
				} catch (Exception e) {
					sender.sendMessage("You fool, you need to pass in a valid index");
				}
			} else if (args.length >= 2) {
				try { 
					Spaceship s = Main.ships.get(Integer.parseInt(args[0]));
					s.delBlock(Integer.parseInt(args[1]));
					s.save();
					sender.sendMessage("Deleted block " + args[1] + " from ship " + s.name);
				} catch (Exception e) {
					sender.sendMessage("You fool, you need to pass in a valid index");
				}
						
			}
		} else if (cmd.getName().equalsIgnoreCase("deltank")) {
			if (args.length == 1) {
				try { 
					Main.blocks.remove(Integer.parseInt(args[0]));
					if ((new File(player.getLocation().getWorld().getName()+"/VoidShips/tank"+args[0]+".dat")).delete())
						sender.sendMessage("tank" + args[0] + ".dat successfully deleted");
					else
						sender.sendMessage("Deleted tank" + args[0] + ".dat in memory, but couldn't find the file in storage");
				} catch (Exception e) {
					sender.sendMessage("You fool, you need to pass in a valid index");
				}
			} else if (args.length >= 2) {
				try { 
					Spaceship s = Main.ships.get(Integer.parseInt(args[0]));
					s.delAirTank(Integer.parseInt(args[1]));
					s.save();
					sender.sendMessage("Deleted tank " + args[1] + " from ship " + s.name);
				} catch (Exception e) {
					sender.sendMessage("You fool, you need to pass in a valid index");
				}
						
			}
		} else if (cmd.getName().equalsIgnoreCase("setsystem")) {
			if (args.length != 1)
				sender.sendMessage("Incorrect number of args (expected 1)");
			else {
				
				Spaceship ship = Main.getCurrentShip(player);
				
				for (SolarSystem s : Main.systems) {
					if (s != null && s.sun != null && ((s.sun.name != null && s.sun.name.equalsIgnoreCase(args[0].replace('_', ' '))) || (s.sun.knickname != null && s.sun.knickname.equalsIgnoreCase(args[0].replace('_', ' '))))) {
						String star = (s.sun.knickname != null ? s.sun.knickname : (s.sun.name != null ? s.sun.name : args[0].replace('_', ' ')));
						
						ship.system = s;
						
						sender.sendMessage("Successfully traveled to " + star);
						ship.save();
						return true;
					}
				}
				
			}
		} else if (cmd.getName().equalsIgnoreCase("shiptp") ) {
			player.sendMessage("Opening ship teleporter");
			Main.openMenu(player, null, 3);
		} else if (cmd.getName().equalsIgnoreCase("shipspawn")) {

			Spaceship ship = Main.getCurrentShip(player);

			ship.sx = player.getLocation().getBlockX();
			ship.sy = player.getLocation().getBlockY();
			ship.sz = player.getLocation().getBlockZ();

			sender.sendMessage("Set spawnpoint of " + ship.name + " to " + ship.sx + " " + ship.sy + " " + ship.sz);
		} else if (cmd.getName().equalsIgnoreCase("modship")) {
			if (args.length < 2) {
				sender.sendMessage("You fool, you forgot the args");
				return false;
			}
			Spaceship ship = Main.getCurrentShip(player);
			for (int i = 0; i < args.length; i++) { // -n [name], -m [mass], -nsn [nav systems name], -ssn [sec systems name], -wsn [weapon systems name], -dsn [defense systems name], -acn [atmosphere control name], -sn [scanner name], -cdn [cleanup debris name]
                if (args[i].equals("-n")) {
                    i++;
                    ship.name = args[i].replace('_', ' ');
                } else if (args[i].equals("-kn")) {
                    i++;
                    ship.knickname = args[i].replace('_', ' ');
                } else if (args[i].equals("-m")) {
                    i++;
                    ship.mass = Double.parseDouble(args[i]);
                } else if (args[i].equals("-psn")) {
                	i++;
                	ship.nsn = args[i].replace('_', ' ');
                } else if (args[i].equals("-ssn")) {
                	i++;
                	ship.ssn = args[i].replace('_', ' ');
                } else if (args[i].equals("-wsn")) {
                	i++;
                	ship.wsn = args[i].replace('_', ' ');
                } else if (args[i].equals("-dsn")) {
                	i++;
                	ship.dsn = args[i].replace('_', ' ');
                } else if (args[i].equals("-acn")) {
                	i++;
                	ship.acn = args[i].replace('_', ' ');
                } else if (args[i].equals("-sn")) {
                	i++;
                	ship.sn = args[i].replace('_', ' ');
                } else if (args[i].equals("-cdn")) {
                	i++;
                	ship.cdn = args[i].replace('_', ' ');
                } else if (args[i].equals("-nsn")) {
                	i++;
                	ship.nsn = args[i].replace('_', ' ');
                }
            }
			sender.sendMessage("Successfully modified " + ship.name);
			ship.save();
		}
		return true;
	}
	
	double dist(double dx, double dy, double dz) {
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
}
