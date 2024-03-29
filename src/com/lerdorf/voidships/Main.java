package com.lerdorf.voidships;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.mojang.authlib.GameProfile;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;

import org.bukkit.event.block.Action;

public class Main extends JavaPlugin implements Listener {

	public static Main instance;
	
	public static ArrayList<Void> voids = new ArrayList<Void>();
	public static ArrayList<Spaceship> ships = new ArrayList<Spaceship>();
	public static ArrayList<SpecialBlock> blocks = new ArrayList<SpecialBlock>();
	public static HashMap<String, SpecialEntity> entities = new HashMap<String, SpecialEntity>();
	public static HashMap<String, ArmorStand> standEntities = new HashMap<String, ArmorStand>();
	public static ArrayList<SolarSystem> systems = new ArrayList<SolarSystem>();
	
	public static HashMap<Player, SpecialBlock> interact = new HashMap<Player, SpecialBlock>();
	
	public static HashMap<Block, Integer> blockTags = new HashMap<Block, Integer>();
	
	public static HashMap<Location[], Material> asyncFill = new HashMap<Location[], Material>();

	public static HashMap<Player, Location> asyncTP = new HashMap<Player, Location>();
	
	ScheduledExecutorService executor;
	
	Ride ride;
	
	public static long timeOffset = -1;
	
	@Override
	public void onEnable() {
		System.out.println("Starting VoidShips");
		
		ride = new Ride((Plugin)this);
		ride.onEnable();

		getServer().getPluginManager().registerEvents(this, this);

		// Register our command "kit" (set an instance of your command class as
		// executor)
		/*
		 * this.getCommand("wr").setExecutor(new WorldReset());
		 * this.getCommand("worldreset").setExecutor(new WorldReset());
		 * this.getCommand("sg").setExecutor(new SurvivalGames());
		 * this.getCommand("survivalgames").setExecutor(new SurvivalGames());
		 * this.getCommand("heal").setExecutor(new Heal());
		 * this.getCommand("h").setExecutor(new Heal());
		 * this.getCommand("healall").setExecutor(new Heal());
		 * this.getCommand("ha").setExecutor(new Heal());
		 */
		this.getCommand("voidset").setExecutor(new VoidSet());
		this.getCommand("invoid").setExecutor(new VoidQuery());
		this.getCommand("voidlist").setExecutor(new VoidQuery());
		this.getCommand("delvoid").setExecutor(new VoidQuery());
		this.getCommand("voidload").setExecutor(new VoidQuery());
		this.getCommand("airsource").setExecutor(new Air());
		this.getCommand("air").setExecutor(new Air());
		
		this.getCommand("newship").setExecutor(new VoidSet());
		this.getCommand("newsystem").setExecutor(new VoidSet());
		this.getCommand("newplanet").setExecutor(new VoidSet());
		
		this.getCommand("delship").setExecutor(new VoidQuery());
		this.getCommand("delsystem").setExecutor(new VoidQuery());
		this.getCommand("delplanet").setExecutor(new VoidQuery());
		
		this.getCommand("ships").setExecutor(new VoidQuery());
		this.getCommand("systems").setExecutor(new VoidQuery());
		this.getCommand("planets").setExecutor(new VoidQuery());

		this.getCommand("ship").setExecutor(new VoidQuery());
		this.getCommand("system").setExecutor(new VoidQuery());
		this.getCommand("planet").setExecutor(new VoidQuery());
		
		this.getCommand("door").setExecutor(new BlockShit());

		this.getCommand("blocks").setExecutor(new VoidQuery());
		this.getCommand("detector").setExecutor(new BlockShit());
		this.getCommand("delblock").setExecutor(new VoidQuery());
		this.getCommand("deltank").setExecutor(new VoidQuery());
		
		this.getCommand("gravity").setExecutor(new BlockShit());
		this.getCommand("terminal").setExecutor(new BlockShit());
		this.getCommand("map").setExecutor(new BlockShit());
		this.getCommand("pump").setExecutor(new BlockShit());
		this.getCommand("tank").setExecutor(new BlockShit());
		this.getCommand("fuel").setExecutor(new BlockShit());
		this.getCommand("setsystem").setExecutor(new VoidQuery());
		this.getCommand("refuel").setExecutor(new BlockShit());
		this.getCommand("asyncFill").setExecutor(new VoidSet());
		this.getCommand("voidcreate").setExecutor(new VoidSet());

		this.getCommand("shiptp").setExecutor(new VoidQuery());
		this.getCommand("shipspawn").setExecutor(new VoidQuery());
		this.getCommand("vehicle").setExecutor(new EntityShit());
		this.getCommand("entities").setExecutor(new EntityShit());
		this.getCommand("delentity").setExecutor(new EntityShit());

		this.getCommand("modship").setExecutor(new VoidQuery());
		
		this.getCommand("airwall").setExecutor(new BlockShit());
		
		this.getCommand("dalek").setExecutor(new EntityShit());
		this.getCommand("spawnplayer").setExecutor(new EntityShit());
		
		loadSaves();

		/*BukkitRunnable mainLoop = new BukkitRunnable() {
		    public void run() {
		    	try {
					specialBlockUpdate();
					fastBlockUpdate(null);
					//System.out.println("Main loop");
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		}; */
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			  @Override
			  public void run() {
				  try {
						specialBlockUpdate();
						fastBlockUpdate(null);
						asyncFillUpdate();
						entityUpdate();
						//System.out.println("Main loop");
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    	}
			  }
			}, 0L, 1L);

		//executor = Executors.newScheduledThreadPool(1);
		//executor.scheduleAtFixedRate(mainLoop, 0, 50, TimeUnit.MILLISECONDS);
		//mainLoop.runTaskTimer(this, 2L, 3L);
		//execut
		instance = this;
		getServer().broadcastMessage("VoidShips enabled!");
	}
	
	/*
	public static Plugin getPlugin() {
		return (Plugin)this;
	}*/
	
	 @EventHandler
	 public void onDismount(EntityDismountEvent e) {
		 ride.onDismount(e);
	 }
	 
	 @EventHandler
	  public void onQuit(PlayerQuitEvent e) {
		 ride.onQuit(e);
	 }
	
	public static WorldEditPlugin getWorldEdit() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (p instanceof WorldEditPlugin) return ((WorldEditPlugin) p);
		p = Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
		if (p instanceof WorldEditPlugin) return ((WorldEditPlugin) p);
		return null;
	}

	public static void loadSaves() {
		System.out.println("Attempting to load saved files");
		/*
		 * File f = new File("plugins/VoidShips/"); if (f != null && f.exists()) {
		 * String[] list = f.list(); System.out.println("list = " + list); if (list !=
		 * null) { for (String e : list) { System.out.println("File: " + e + " " +
		 * e.substring(0,4)); if (e.length() > 4 && e.substring(0,4).equals("void")) {
		 * System.out.println("Loading " + e); voids.add(new Void(e)); } } } }
		 */

		for (World world : Bukkit.getWorlds()) {
			List<Entity> mobs = world.getEntities();
			for (Entity mob : mobs) {
				if (mob.getScoreboardTags().contains("SpecialEntity")) {
					String[] tags = new String[mob.getScoreboardTags().size()];
					tags = mob.getScoreboardTags().toArray(tags);
					for (String s : tags) {
						if (SpecialEntity.isEntityTag(s)) {
							standEntities.put(s, (ArmorStand)mob);
						}
					}
				}
			}
		}
		
		/*File f = new File("plugins/VoidShips/");
		f.mkdirs();
		String[] list3 = f.list();
		for (String e3 : list3) {
			if (e3.equals(""))
		}*/
		
		File f = new File("plugins/.."); // How else do you get the current directory?
		if (f != null) {
			String[] list = f.list();
			System.out.println("list = " + list);
			if (list != null) {
				for (String e : list) {
					//System.out.println("File: " + e);
					File g = new File(e + "/VoidShips/");
					if (g.exists() && !e.equalsIgnoreCase("plugins")) {
						System.out.println("Found " + e + "/VoidShips/");
						String[] list2 = g.list();
						for (String e2 : list2) {
							System.out.println("File: " + e2 + " " + e2.substring(0, 4));
							if (e.length() > 4 && e2.substring(0, 4).equals("void")) {
								System.out.println("Loading " + e + "/VoidShips/" + e2);
								int n = Integer.parseInt(e2.substring(4, e2.indexOf('.')));
								while (voids.size() <= n) voids.add(null);
								voids.set(n, new Void(e + "/VoidShips/" + e2));
							} else if (e2.length() > 4 && e2.substring(0, 4).equals("ship")) {
								System.out.println("Loading " + e2);
								int n = Integer.parseInt(e2.substring(4, e2.indexOf('.')));
								while (ships.size() <= n) ships.add(null);
								ships.set(n, new Spaceship(e + "/VoidShips/" + e2));
							} else if (e2.length() > 5 && e2.substring(0, 5).equals("block")) {
								System.out.println("Loading " + e2);
								int n = Integer.parseInt(e2.substring(5, e2.indexOf('.')));
								while (blocks.size() <= n) blocks.add(null);
								blocks.set(n, new SpecialBlock(e + "/VoidShips/" + e2));
							} else if (e2.length() > 6 && e2.substring(0, 7).equals("entity-")) {
								System.out.println("Loading " + e2);
									
								String tag = e2.substring(0, e2.indexOf(".dat"));
								//LivingEntity le = livingEntities.get(tag);
								entities.put(tag, new SpecialEntity(e + "/VoidShips/" + e2));
								
							} else if (e2.length() > 6 && e2.substring(0, 6).equals("system")) {
								System.out.println("Loading " + e2);
								int n = Integer.parseInt(e2.substring(6, e2.indexOf('.')));
								while (systems.size() <= n) systems.add(null);
								SolarSystem sys = new SolarSystem(e + "/VoidShips/" + e2);
								sys.initRefs();
								systems.set(n, sys);
							}
						}
					}
				}
			}
		}
		
		for (Spaceship ship : ships)
			ship.initRefs();

		// getServer().broadcastMessage("VoidShips enabled!");
	}
	
	public static SpecialEntity getSpecialEntity(Entity e) {
		if (e.getScoreboardTags().contains("SpecialEntity")) {
			String[] tags = new String[e.getScoreboardTags().size()];
			tags = e.getScoreboardTags().toArray(tags);
			for (String s : tags) {
				if (SpecialEntity.isEntityTag(s)) {
					//System.out.println("Found tag! " + s);
					return entities.get(s);
				}
			}
		}
		return null;
	}

	@EventHandler
	public void playerRightClicksAtEntity(PlayerInteractAtEntityEvent event) {
		ride.playerRightClicksAtEntity(event);
	}
	
	@Override
	public void onDisable() {
		System.out.println("Stopping VoidShips");
		for (Spaceship ship : ships)
			ship.save();
		for (SpecialEntity entity : entities.values())
			entity.save();
		for (SolarSystem system : systems)
			system.save();
		for (Void v : voids)
			v.save();
		
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// System.out.println("Player joined");
		event.getPlayer().sendMessage("Good to be back!");
	}
	
	@EventHandler
	public void RightClick(PlayerInteractEntityEvent event) {
		ride.RightClick(event);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		Player player = e.getEntity();
	     if(player.getScoreboardTags().contains("vac") && (e.getDeathMessage().contains("killed by magic") || e.getDeathMessage().contains("out of the world"))) {
	    	 switch((int)(Math.random()*8)) {
	    	 case 0: 
	    		 e.setDeathMessage(player.getName()+" fell off the void");
	    		 break;
	    	 case 1:
	    		 e.setDeathMessage(player.getName()+" was sent to Stalin");
	    		 break;
	    	 case 2:
	    		 e.setDeathMessage(player.getName()+" took inspiration from Warrior24");
	    		 break;
	    	 case 3:
	    		 e.setDeathMessage(player.getName()+" forgot a spacesuit");
	    		 break;
	    	 case 4:
	    		 e.setDeathMessage(player.getName()+" believed nudity was the best spacesuit");
	    		 break;
	    	 case 5:
	    		 e.setDeathMessage(player.getName()+" is a bald boi");
	    		 break;
			 case 6:
				 e.setDeathMessage(player.getName()+" got spaced"); 
				 break;
			 case 7:
				 e.setDeathMessage(player.getName()+" thought this was spelljammer"); 
	    	 }
	     }
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
	    if (event.getWhoClicked() instanceof Player) {
	        Player player = (Player) event.getWhoClicked();
	        if (event.getClickedInventory() != null) {
	            if (event.getView().getTitle().indexOf("Ship Terminal") != -1) { ////////// SHIP TERMINAL
	                if (event.isRightClick() || event.isLeftClick()) {
	                    event.setCancelled(true);
	                    ItemStack item = event.getCurrentItem();
	                    if (item.getItemMeta().getDisplayName().indexOf("Atmosphere Control") != -1) {
	                    	openMenu(player, getCurrentShip(player), 1);
	                    	System.out.println("Opened Atmosphere Control");
	                    } else if (item.getType() == Material.DISPENSER && event.getView().getTitle().indexOf("Atmosphere Control") != -1) { // Air Pumps
	                    	Spaceship ship = getCurrentShip(player);
	                    	SpecialBlock pump = ship.getBlocksOfType(SpecialBlock.AIR_PUMP)[event.getSlot()-9];
	                    	openMenu(player, ship, pump, 2);
	                    } else if (item.getItemMeta().getDisplayName().indexOf("Cleanup Debris") != -1) {
	                    	player.sendMessage("�oDeleting broken blocks from the systems...");
	                    	int c = 0;
	                    	int d = 0;
	                    	Spaceship s = getCurrentShip(player);
							if (s != null) {
								if (s.blocks != null && s.blocks.length > 0) {
									for (int i = 0; i < s.blocks.length; i++) {
										if (s.blocks[i] != null && s.blocks[i].dead) {
											s.delBlock(i);
											c++;
										}
									}
								}
								if (s.airTanks != null && s.airTanks.length > 0) {
									for (int i = 0; i < s.airTanks.length; i++) {
										if (s.airTanks[i] != null && s.airTanks[i].dead) {
											s.delAirTank(i);
											d++;
										}
									}
								}
							}
							player.sendMessage("Deleted " + c + " blocks and " + d + " air tanks");
	                    } else if (item.getItemMeta().getDisplayName().indexOf("Navigation Systems") != -1) {
	                    	openMenu(player, getCurrentShip(player), 4);
	                    	System.out.println("Opened Navigation Control");
	                    }
	                    //PianoManager.play(player, event.getCurrentItem(), false);
	                }
	            } else if (event.getView().getTitle().indexOf("Air Pump") != -1) {
	                if (event.isRightClick() || event.isLeftClick()) {
	                    event.setCancelled(true);
	                    ItemStack item = event.getCurrentItem();
	                    if (item.getItemMeta().getDisplayName().indexOf("Depressurize") != -1) {
	                    	player.sendMessage("Attempting to depressurize");
	                    	Spaceship ship = getCurrentShip(player);
	                    	Directional directional = (Directional) interact.get(player).getBlock().getBlockData();
	                    	Location loc = interact.get(player).getBlock().getLocation().clone().add(directional.getFacing().getDirection());
	                    	int a = countAir(loc, 200);
	                    	player.sendMessage("Regaining " + a + " cubic meters of air");
	                    	replace(loc, 2000, Material.COARSE_DIRT, Material.VOID_AIR, 0);
	                    	//setVoidAir(loc.getBlock());
	                    	ship.addAir(a);
	                    	player.sendMessage("Successfully reclaimed " + a + " cubic meters of air");
	                    	//player.getLocation 
	                    } else if (item.getItemMeta().getDisplayName().indexOf("Pressurize") != -1) {	
	                    	player.sendMessage("Attempting to pressurize");
	                    	Spaceship ship = getCurrentShip(player);
	                    	Directional directional = (Directional) interact.get(player).getBlock().getBlockData();
	                    	Location loc = interact.get(player).getBlock().getLocation().clone().add(directional.getFacing().getDirection());
	                    	int a = countVacuum(loc, 200);
	                    	player.sendMessage("Expending " + a + " cubic meters of air");
	                        //directional.getFacing();
	                    	if (a <= ship.countAir()) {
	                    		//if (airSource(2000, interact.get(player).getBlock().getLocation().clone().add(directional.getFacing().getDirection()), null)) {
		                    		player.sendMessage("Successfully pressurized room");

		                    	replace(loc, 200, Material.COARSE_DIRT, Material.CAVE_AIR, 0);
		                    		ship.removeAir(a);
	                    		//} else
	                    		//	player.sendMessage("Fatal pressurization error");
	                    	} else {

		                    	replace(loc, 200, Material.COARSE_DIRT, Material.VOID_AIR, 0);
		                    	player.sendMessage("Fatal pressurization error"); 
	                    	}
	                    } 
	                    //PianoManager.play(player, event.getCurrentItem(), false);
	                }
	            } else if (event.getView().getTitle().indexOf("Ship Selector") != -1) {
	            	Spaceship ship = ships.get(event.getSlot());
	            	player.sendMessage("Teleporting to " + ship.name);
	            	Location loc = new Location(Bukkit.getWorld(ship.world), ship.sx, ship.sy, ship.sz);
	            	player.teleport(loc);
	            	player.setBedSpawnLocation(loc, true);
	            	new java.util.Timer().schedule( 
	            	        new java.util.TimerTask() {
	            	            @Override
	            	            public void run() {
	            	            	asyncTP.put(player, loc);
	            	            }
	            	        }, 
	            	        500 
	            	);
	            }
	        }
	    }
	}
	
	/*
	 * Terminal
	 * 	- Ship Status
	 * 	- Atmosphere (control for each pump, how many tanks, how much air remains)
	 * 	- SecSystems (on/off)
	 * 	- WeaponDefenseSystems (on/off)
	 *		- Connect to drone ships and drone turrets
	 * 	- Scanners (on/off + list of scanned ships)
	 * 	- PilotSystems
	 * 		- Set Course (list of planets to orbit, and how much time and fuel required to get there)
	 * 		- Manual Control
	 * 	- WeaponTargetSystems
	 * 		- Attack
	 *		- Launch missile
	 *		- Launch drone ship
	 */
	
	public static void openMenu(Player player, Spaceship ship, int n) {
		ship = getCurrentShip(player);
		if (n == 0) { // Main Terminal
			Inventory inventory = Bukkit.createInventory(null, 1*9, "Ship Terminal");
			
			inventory.setItem(0, createItem(Material.IRON_BLOCK, "Ship Status", Arrays.asList("�6"+ship.name+"�f", "�c"+ship.countAir()+"�f cubic meters of air", "�c"+ship.displayFuel()+"�f of fuel", "�dOrbiting " + ship.orbiting.getName())));
			inventory.setItem(2, createItem(Material.DISPENSER, "�fAtmosphere Control", Arrays.asList(ship.acn != null ? "�f�o"+ship.acn : "�f�oAtmosphere Systems","�c"+ship.countAir()+"�f cubic meters of air", "Click to access Atmosphere Control", "�7�oFill a room with oxygen,", "�7�oor turn a room into a vacuum")));
			inventory.setItem(3, createItem(Material.OBSERVER, "�3Security Systems", Arrays.asList(ship.ssn != null ? "�3�o"+ship.ssn : "�3�oSecurity Systems","Click to access SecSystems", "�7�oAlerts and alarms regarding scans, target locks,", "�7�oand incomming attacks")));
			inventory.setItem(4, createItem(Material.BREWING_STAND, "�2Defense Systems", Arrays.asList(ship.dsn != null ? "�2�o"+ship.dsn : "�2�oWeapon Defense Systems","Click to toggle WeaponDefenseSystems", "�7�oProtection against exterior environment", "�7�oAutonomous defense against light weaponry")));
			inventory.setItem(5, createItem(Material.TARGET, "�4�lWeapon Systems", Arrays.asList(ship.wsn != null ? "�4�o"+ship.wsn : "�b�oWeapon Target Systems","�fClick to access WeaponTargetSystems", "�7�oTarget locking, defense against heavy weaponry")));
			inventory.setItem(6, createItem(Material.DAYLIGHT_DETECTOR, "�bScaners", Arrays.asList(ship.sn != null ? "�b�o"+ship.sn : "�b�oLong-Range Mapping Scanner", "�fClick to access the Scanning Systems", "�7�oScan for other ships within your system,", "�7�oor send a probe to scan another system")));
			inventory.setItem(7, createItem(Material.NETHER_STAR, "�dNavigation Systems", Arrays.asList(ship.nsn != null ? "�d�o"+ship.nsn : "�d�oBotPilot","�fClick to access Navigational Systems", "�7�oSet course for a destination,", "�7�oor pilot the ship manually")));
			inventory.setItem(8, createItem(Material.HOPPER, "�4Cleanup Debris", Arrays.asList(ship.cdn != null ? "�4�o"+ship.cdn : "�b�oGarbage Collector","�fClick to cleanup broken modules", "�7�oAll broken special blocks will", "�7�obe deleted from the system")));
			
			player.openInventory(inventory);
		} 
		else if (n == 1) {
			SpecialBlock[] pumps = ship.getBlocksOfType(SpecialBlock.AIR_PUMP);
			
			Inventory inventory = Bukkit.createInventory(null, Math.min((int)(Math.ceil(pumps.length/9.0)+2)*9, 54), "Ship Terminal: Atmosphere Control");
			
			inventory.setItem(4, createItem(Material.POLISHED_BASALT, Math.max(1,ship.airTanks.length), "Oxygen Tanks", Arrays.asList("�c"+ship.countAir()+"�f cubic meters of air")));
			
			for (int i = 0; i < pumps.length; i++) {
				inventory.setItem(9+i, createItem(Material.DISPENSER, "�b"+pumps[i].name, Arrays.asList("pump:" + i, "Click to open pump controls")));
			}
			
			player.openInventory(inventory);
		}
		else if (n == 3) {
			Inventory inventory = Bukkit.createInventory(null, Math.min((int)(Math.ceil(ships.size()/9.0))*9, 54), "Ship Selector");
			
			//inventory.setItem(4, createItem(Material.POLISHED_BASALT, Math.max(1,ship.airTanks.length), "Oxygen Tanks", Arrays.asList("�c"+ship.countAir()+"�f cubic meters of air")));
			
			for (int i = 0; i < ships.size(); i++) {
				inventory.setItem(i, createItem(Material.DRAGON_HEAD, "�6"+ships.get(i).name, Arrays.asList("�7�oClick to teleport")));
			}
			
			player.openInventory(inventory);
		} else if (n == 4) {
			Inventory inventory = Bukkit.createInventory(null, Math.min(54, (int)(ship.system == null || ship.system.planets == null ? 1 : Math.ceil(ship.system.planets.length/9.0)+2)*9), "Navigation Systems");
			inventory.setItem(4, createItem(Material.NETHER_STAR, "�6" + ship.system.getName()+"�f", Arrays.asList("�d"+ship.system.sun.type, ship.system.sun.equals(ship.orbiting) ? "�d�oORBITING" : "", "�d�o"+CarlMath.withPrefix(ship.getDistance(ship.system.sun))+" km")));
			if (ship.system != null && ship.system.planets != null)
				for (int i = 0; i < ship.system.planets.length; i++)
					inventory.setItem(9+i, createItem(ship.system.planets[i].getItem(), (ship.system.planets[i].habitable ? "�2" : "�f") + ship.system.planets[i].getName(), Arrays.asList(ship.system.planets[i].equals(ship.orbiting) ? "�d�oORBITING" : "", "�d�o"+CarlMath.withPrefix(ship.getDistance(ship.system.planets[i]))+" km")));

			player.openInventory(inventory);
		}
	}
	
	public static void openMenu(Player player, Spaceship ship, SpecialEntity e, int n) {
		Spaceship nearestShip = getCurrentShip(player);
		if (n == 5) {
			Inventory inventory = Bukkit.createInventory(null, 1*9, "Flight Control");
			
			if (!e.comms) {
				inventory.setItem(0, createItem(Material.REDSTONE_BLOCK, "Mothership", Arrays.asList("Can't connect to �6"+ship.name+"�f", "�fYour comms are disabled")));
				inventory.setItem(1, createItem(Material.REDSTONE_BLOCK, "Nearest Ship", Arrays.asList("Can't connect to �6"+nearestShip.name+"�f", "�fYour comms are disabled")));
			} else if (!ship.comms)
				inventory.setItem(0, createItem(Material.REDSTONE_BLOCK, "Mothership", Arrays.asList("Can't connect to �6"+ship.name+"�f", "�6"+ship.name+"�f's comms are disabled")));
			else
				inventory.setItem(0, createItem(Material.IRON_BLOCK, "Mothership", Arrays.asList("�6"+ship.name+"�f", "�c"+ship.countAir()+"�f cubic meters of air", "�c"+ship.displayFuel()+"�f of fuel", "�dOrbiting " + ship.orbiting.getName(), "�fClick to access Mothership Terminal")));
			if (e.comms && !nearestShip.comms)
				inventory.setItem(1, createItem(Material.REDSTONE_BLOCK, "Nearest Ship", Arrays.asList("Can't connect to �6"+nearestShip.name+"�f", "�6"+nearestShip.name+"�'s comms are disabled")));
			else if (e.comms && nearestShip.comms)
				inventory.setItem(1, createItem(Material.GOLD_BLOCK, "Nearest Ship", Arrays.asList("�6"+nearestShip.name+"�f", "�c"+nearestShip.countAir()+"�f cubic meters of air", "�c"+nearestShip.displayFuel()+"�f of fuel", "�dOrbiting " + nearestShip.orbiting.getName(), "�fClick to hack �6"+nearestShip.name+"�'s terminal")));
			inventory.setItem(2, createItem(Material.DIAMOND_BLOCK, "�fShip Status", Arrays.asList("�6"+e.type+"�f", "�3"+e.tag, "�c"+e.air+"�f cubic meters of air", "�c"+e.fuel+"�f joules of fuel", "�dOrbiting " + e.orbit.body.getName())));
			inventory.setItem(3, createItem(Material.BREWING_STAND, "�2Defense Systems", Arrays.asList(ship.dsn != null ? "�2�o"+ship.dsn : "�2�oWeapon Defense Systems","Click to toggle WeaponDefenseSystems", "�7�oProtection against exterior environment", "�7�oAutonomous defense against light weaponry")));
			inventory.setItem(4, createItem(Material.TARGET, "�4�lWeapon Systems", Arrays.asList(ship.wsn != null ? "�4�o"+ship.wsn : "�b�oWeapon Target Systems","�fClick to access WeaponTargetSystems", "�7�oTarget locking, defense against heavy weaponry")));
			inventory.setItem(5, createItem(Material.DAYLIGHT_DETECTOR, "�bScaners", Arrays.asList(ship.sn != null ? "�b�o"+ship.sn : "�b�oLong-Range Mapping Scanner", "�fClick to access the Scanning Systems", "�7�oScan for other ships within your system,", "�7�oor send a probe to scan another system")));
			inventory.setItem(6, createItem(Material.NETHER_STAR, "�dNavigation Systems", Arrays.asList(e.nsn != null ? "�d�o"+e.nsn : "�d�oBotPilot","�fClick to access Navigational Systems", "�7�oSet course for a destination,", "�7�oor pilot the ship manually")));
			inventory.setItem(7, createItem(Material.IRON_DOOR, "�4Eject", Arrays.asList("�fClick to leave ship")));
			inventory.setItem(8, createItem(Material.BARRIER, "�4�lScuttle Ship", Arrays.asList("�fClick to self destruct", "�7�oChoose whether or not", "�7�othe ship should explode")));
			
			player.openInventory(inventory);
		}
	}
	
	public static void openMenu(Player player, Spaceship ship, SpecialBlock b, int n) {

		ship = getCurrentShip(player);
		if (interact.keySet().contains(player))
			interact.remove(player);
		interact.put(player, b);
		if (n == 2) {
			Inventory inventory = Bukkit.createInventory(null, 1*9, b.name + " Air Pump");
			
			inventory.setItem(0, createItem(Material.POLISHED_BASALT, Math.max(1,ship.airTanks.length), "Oxygen Tanks", Arrays.asList("�c"+ship.countAir()+"�f cubic meters of air")));
			inventory.setItem(3, createItem(Material.RED_STAINED_GLASS, "�4Depressurize", Arrays.asList("�7�oClick to turn the room into a vacuum")));
			inventory.setItem(5, createItem(Material.LIME_STAINED_GLASS, "�2Pressurize", Arrays.asList("�7�oClick to fill the room with air")));
			
			player.openInventory(inventory);
		}
	}
	
	public static ItemStack createItem(Material mat, String name, List<String> lore) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (lore != null)
			meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material mat, int count, String name, List<String> lore) {
		ItemStack item = new ItemStack(mat, count);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (lore != null)
			meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material mat, String name, List<String> lore, int customModelData) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (lore != null)
			meta.setLore(lore);
		meta.setCustomModelData(customModelData);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack[] saveInventory(Player p) {
        return p.getInventory().getContents();
       
    }
   
	public static void loadInventory(Player p, ItemStack[] items) {
       p.getInventory().clear();
    	for (ItemStack item : items) {
    		if (item != null)
    			p.getInventory().addItem(item);
    	}

    }
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Location loc = event.getBlock().getLocation();
		if (inVoid(loc)) {
			// System.out.println("Block broken");
			// loc.getBlock().setType(Material.valueOf("CAVE_AIR"));
			// getServer().broadcastMessage("BlockBreakEvent");
			// loc.getBlock().setBlockData(new
			// BlockData("CraftBlockData{minecraft:cave_air}"));
			if (Main.isAir(loc.clone().add(new Vector(1, 0, 0)).getBlock()) || Main.isAir(loc.clone().add(new Vector(-1, 0, 0)).getBlock()) ||  Main.isAir(loc.clone().add(new Vector(0, 1, 0)).getBlock()) || Main.isAir(loc.clone().add(new Vector(0, -1, 0)).getBlock()) || Main.isAir(loc.clone().add(new Vector(0, 0, 1)).getBlock()) || Main.isAir(loc.clone().add(new Vector(0, 0, -1)).getBlock()))
			{	
				if (isCaveAir(loc.clone().add(new Vector(1, 0, 0)).getBlock()) || isCaveAir(loc.clone().add(new Vector(-1, 0, 0)).getBlock()) ||  isCaveAir(loc.clone().add(new Vector(0, 1, 0)).getBlock()) || isCaveAir(loc.clone().add(new Vector(0, -1, 0)).getBlock()) || isCaveAir(loc.clone().add(new Vector(0, 0, 1)).getBlock()) || isCaveAir(loc.clone().add(new Vector(0, 0, -1)).getBlock())) {
					getServer().broadcastMessage("Breach in the hull!");
					loc.getWorld().spawnParticle(Particle.FLAME, loc, 5);
					new java.util.Timer().schedule( 
					        new java.util.TimerTask() {
					            @Override
					            public void run() {
					                // your code here
					            	List<Entity> near = loc.getWorld().getEntities();
					        		for(Entity entity : near) {
					        			double dx = loc.getX() - entity.getLocation().getX();
				        		        double dy = loc.getY() - entity.getLocation().getY();
				        		        double dz = loc.getZ() - entity.getLocation().getZ();
				        		        double d = Math.sqrt(dx*dz + dy*dy + dz*dz);
					        		    if(d < 5 && !entity.getScoreboardTags().contains("SpecialEntity") && entity.getType() != EntityType.ARMOR_STAND && ( isAir(entity.getLocation().getBlock()) || isAir(entity.getLocation().clone().add(new Vector(0, 1, 0)).getBlock() ))) {
					        		        double v = 0.02;
					        		        dx = v*dx/d;
					        		        dy = v*dy/d;
					        		        dz = v*dz/d;
					        		        entity.setVelocity(entity.getVelocity().add(new Vector(dx, dy, dz)));
					        		    }
					        		}
					            }
					        }, 
					        200 
					);
				}
			}
		}
		// event.getPlayer().sendMessage( "Welcome to the Yeet Squad Minecraft
		// Server!\nEnjoy your stay!" );
	}

	@EventHandler
	public void onUseEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
		} else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			boolean yeeted = false;
			Block clicked = event.getClickedBlock();
			if (inVoid(clicked.getLocation())) {
				for (SpecialBlock b : blocks) {
					if (b != null && b.checkMaterial(clicked.getType()) && b.compareLocation(clicked.getLocation())) {
						b.rightClick(event);
						yeeted = true;
					}
				}
				if (yeeted == false) {
					for (Spaceship s : ships) {
						if (s != null && s.blocks != null && s.blocks.length > 0)
							for (SpecialBlock b : s.blocks)
								if (b != null && b.checkMaterial(clicked.getType()) && b.compareLocation(clicked.getLocation()))
									b.rightClick(event);
					}
				}
			}
		}
	}
	

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (inVoid(player.getLocation())) {
			boolean yeeted = false;
			Block target = player.getTargetBlock(null, 10);
			for (SpecialBlock b : blocks) {
				if (b != null && b.checkMaterial(target.getType()) && b.compareLocation(target.getLocation())) {
					b.scroll(event.getPreviousSlot(), event.getNewSlot(), player);
					yeeted = true;
				}
			}
			if (yeeted == false) {
				for (Spaceship s : ships) {
					if (s != null && s.blocks != null && s.blocks.length > 0)
						for (SpecialBlock b : s.blocks)
							if (b != null && b.checkMaterial(target.getType()) && b.compareLocation(target.getLocation()))
								b.scroll(event.getPreviousSlot(), event.getNewSlot(), player);
				}
			}
		}
	}
	
	public double clamp(double a, double b, double c) {
		return Math.min(Math.max(b, c), Math.max(a, Math.min(b, c)));
	}
	
	long lastBlockUpdate = 0;
	long lastGravUpdate = 0;
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (p.getVehicle() != null) {
			//event.setCancelled(true);
			//getFrom(); // Location the player moved from
			//getTo(); // Location the player moved to
			//System.out.println("Player steered vehicle");
			ArmorStand v = (ArmorStand) p.getVehicle();
			String[] tags = new String[v.getScoreboardTags().size()];
			tags = v.getScoreboardTags().toArray(tags);
			int tagIndex = -1;
			for (int i = 0; i < tags.length; i++)
				if (SpecialEntity.isEntityTag(tags[i]))
					tagIndex = i;
			if (tagIndex > -1) {
				//System.out.println("Found entity tag, setting pitch to " + p.getEyeLocation().getPitch() + " and yaw to " + p.getEyeLocation().getYaw());
				entities.get(tags[tagIndex]).setTargetDirection(p.getEyeLocation().getPitch(), p.getEyeLocation().getYaw());
				p.setFallDistance(0.0F);
				v.setFallDistance(0.0F);
			}
		}
		else if (inVoid(p.getLocation())) {
			Spaceship currentShip = getCurrentShip(p);
			if (currentShip.fastLadders) {
				if (p.getLocation().getBlock().getType() == Material.LADDER) {
					Vector vel = p.getVelocity();
					//Location newLoc = p.getLocation().add(0, Math.signum(vel.getY()), 0);
					//if (vel.getY() > 0 && newLoc.getBlock().getType() == Material.LADDER && newLoc.add(0, 1, 0).getBlock().getType() == Material.LADDER)
					//p.teleport(newLoc);
					if (!p.isSneaking()) {
						if (vel.getY() > 0) {
							vel.setY(0.5);
							p.setVelocity(vel);
						} /*else if (vel.getY() < 0) {
							Location newLoc = p.getLocation().add(0, -1, 0);
							if (newLoc.getBlock().getType() == Material.LADDER && newLoc.add(0, 1, 0).getBlock().getType() == Material.LADDER)
								p.teleport(newLoc.add(0, -1, 0));
						}*/
					}
				}
			}
			if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
				if (System.currentTimeMillis() - lastGravUpdate > 300
						&& !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					boolean yeet = false;
					// for (Spaceship s : ships) {
					if (currentShip != null && currentShip.blocks != null && currentShip.blocks.length > 0) {
						for (SpecialBlock b : currentShip.blocks) {
							if (b != null && b.type == SpecialBlock.GRAVITY && !b.dead
									&& within(p, b.world, b.x, b.y, b.z, b.x2, b.y2, b.z2)) {
								yeet = true;
							}
						}
					}
					// }
					if (yeet)
						p.addScoreboardTag("grav");
					else
						p.removeScoreboardTag("grav");
				}

				if (p.isFlying() == p.getScoreboardTags().contains("grav") && !(p.getScoreboardTags().contains("grav") && p.getScoreboardTags().contains("flyspeed"))) {
					if (!p.getScoreboardTags().contains("grav") && !p.getScoreboardTags().contains("flyspeed")
							&& p.getFlySpeed() != 0.022f)
						p.setFlySpeed(0.022f);
					p.setAllowFlight(!p.getScoreboardTags().contains("grav"));
					p.setFlying(!p.getScoreboardTags().contains("grav"));
					// p.sendMessage("Setting flight to " +
					// !p.getScoreboardTags().contains("grav"));
				}

				if (spacesuit(p) < 4 && !p.hasPotionEffect(PotionEffectType.INVISIBILITY) && !p.getScoreboardTags().contains("vac") && (isAir(p.getLocation().clone().add(new Vector(0, 1, 0)).getBlock()) || isAir(p.getLocation().getBlock()))) {
					p.addScoreboardTag("vac");
					p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100000, 1, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 0, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100000, 10, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 2, false, false));
					// p.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 100000, 0, false,
					// false));
				} else if (spacesuit(p) < 4 && !p.hasPotionEffect(PotionEffectType.WITHER)
						&& p.getScoreboardTags().contains("vac")) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100000, 1, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 0, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100000, 10, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 2, false, false));
				}
			}
		} else if (p.getFlySpeed() != 0.1f && !p.getScoreboardTags().contains("flyspeed"))
			p.setFlySpeed(0.1f);
		if (p.getScoreboardTags().contains("vac") && (spacesuit(p) >= 4 || isCaveAir(p.getLocation().getBlock()) || isCaveAir(p.getLocation().clone().add(new Vector(0, 1, 0)).getBlock()) || !inVoid(p.getLocation()))) {
			p.removeScoreboardTag("vac");
			p.removePotionEffect(PotionEffectType.WEAKNESS);
			p.removePotionEffect(PotionEffectType.BLINDNESS);
			p.removePotionEffect(PotionEffectType.WITHER);
			p.removePotionEffect(PotionEffectType.SLOW);
			//p.removePotionEffect(PotionEffectType.HARM);
		}
		specialBlockUpdate();
		fastBlockUpdate(p);
	}

	public void entityUpdate() {
		for (Spaceship s : ships) {
			if (s.entities != null && s.entities.length > 0) {
				for (SpecialEntity e : s.entities) {
					if (e != null) {
						e.update();
					}
				}
			}
		}
		if (entities != null && entities.size() > 0) {
			for (SpecialEntity e : entities.values()) {
				if (e != null) {
					e.update();
				}
			}
		}
	}
	
	public static HashMap<LivingEntity, ItemStack[]> asyncNPCEquip = new HashMap<LivingEntity, ItemStack[]>();
	
	public void specialBlockUpdate() {
		if (System.currentTimeMillis()-lastBlockUpdate > 300) {
			if (asyncTP.size() > 0) {
				for (Player player : asyncTP.keySet()) {
					player.teleport(asyncTP.get(player));
					asyncTP.remove(player);
				}
			}
			if (asyncNPCEquip.size() > 0) {
				for (LivingEntity npc : asyncNPCEquip.keySet()) {
					//npc.teleport(asyncNPCEquip.get(player));
					npc.getEquipment().setHelmet(asyncNPCEquip.get(npc)[0]);
					npc.getEquipment().setChestplate(asyncNPCEquip.get(npc)[1]);
					npc.getEquipment().setLeggings(asyncNPCEquip.get(npc)[2]);
					npc.getEquipment().setBoots(asyncNPCEquip.get(npc)[3]);
					asyncNPCEquip.remove(npc);
				}
			}
			lastBlockUpdate = System.currentTimeMillis();
			for (SpecialBlock b : blocks) {
				if (b != null)
					b.update();
			}
			for (Spaceship s : ships) {
				if (s != null && s.blocks != null && s.blocks.length > 0)
					for (SpecialBlock b : s.blocks)
						if (b != null)
							b.update();
				if (s != null && s.airTanks != null && s.airTanks.length > 0)
					for (SpecialBlock b : s.airTanks) 
						if (b != null)
							b.update();
			}
		}
	}
	
	public void fastBlockUpdate(Player p) {
		for (SpecialBlock b : blocks) {
			if (b != null && b.fastUpdate)
				b.update(p, null);
		}
		for (Spaceship s : ships) {
			if (s != null && s.blocks != null && s.blocks.length > 0)
				for (SpecialBlock b : s.blocks)
					if (b != null && b.fastUpdate)
						b.update(p, s);
			if (s != null && s.airTanks != null && s.airTanks.length > 0)
				for (SpecialBlock b : s.airTanks) 
					if (b != null && b.fastUpdate)
						b.update(p, s);
		}
	}
	
	public static boolean airSource(int air, Location loc, Spaceship ship) {
		if (airSource(air, loc)) {
			int r = successAirSource(loc);
			System.out.println("Great Success");
			if (ship != null)
				ship.removeAir(r);
			return true;
		} else {
			int r = failedAirSource(loc);
			System.out.println("Epic Fail");
			if (ship != null)
				ship.removeAir(r);
			return false;
		}
	}

	public static boolean findHole(int c, Location loc) {
		if (isSomeAir(loc.getBlock())) {
			if (c < 1)
				return true;
			// boolean r = findHole(c-1, loc.clone)

		}
		return false;
	}

	public static boolean airSource(int air, Location loc) {
		if (isSomeAir(loc.getBlock())) {
			if (air < 1)
				return false;
			for (Spaceship s : ships) {
				if (s != null && s.blocks != null && s.blocks.length > 0) {
					for (SpecialBlock b : s.blocks) {
						if (b != null && b.dead == false && b.type == SpecialBlock.AIR_WALL && within(loc, b.world, b.x, b.y, b.z, b.x2, b.y2, b.z2)) {
							return true;
						}
					}
				}
			}
			loc.getWorld().spawnParticle(Particle.CLOUD, loc, 5);
			loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 2);
			Material ogMat = loc.getBlock().getType();
			air--;
			loc.getBlock().setType(Material.COARSE_DIRT);
			blockTags.put(loc.getBlock(), 0);
			if (airSource(air, loc.clone().add(new Vector(1, 0, 0)))
					&& airSource(air, loc.clone().add(new Vector(-1, 0, 0)))
					&& airSource(air, loc.clone().add(new Vector(0, 1, 0)))
					&& airSource(air, loc.clone().add(new Vector(0, -1, 0)))
					&& airSource(air, loc.clone().add(new Vector(0, 0, 1)))
					&& airSource(air, loc.clone().add(new Vector(0, 0, -1)))) {
				// setCaveAir(loc.getBlock());
				// loc.getBlock().setType(Material.STONE);
				return true;
			} else {
				failedAirSource(loc.clone().add(new Vector(1, 0, 0)));
				failedAirSource(loc.clone().add(new Vector(-1, 0, 0)));
				failedAirSource(loc.clone().add(new Vector(0, 1, 0)));
				failedAirSource(loc.clone().add(new Vector(0, -1, 0)));
				failedAirSource(loc.clone().add(new Vector(0, 0, 1)));
				failedAirSource(loc.clone().add(new Vector(0, 0, -1)));
			}
			loc.getBlock().setType(ogMat);
			// System.out.println("WE GOT A FALSE");
			return false;
		}
		// System.out.println("WE GOT A FALSE");
		return true;
	}

	public static int failedAirSource(Location loc) {
		if (loc.getBlock().getType() == Material.COARSE_DIRT && blockTags.keySet().contains(loc.getBlock()) && blockTags.get(loc.getBlock()) == 0) {
			// Material ogMat = loc.getBlock().getType();\
			loc.getBlock().setType(Material.VOID_AIR);
			blockTags.remove(loc.getBlock());
			return 1 + failedAirSource(loc.clone().add(new Vector(1, 0, 0)))
					+ failedAirSource(loc.clone().add(new Vector(-1, 0, 0)))
					+ failedAirSource(loc.clone().add(new Vector(0, 1, 0)))
					+ failedAirSource(loc.clone().add(new Vector(0, -1, 0)))
					+ failedAirSource(loc.clone().add(new Vector(0, 0, 1)))
					+ failedAirSource(loc.clone().add(new Vector(0, 0, -1)));

			// loc.getBlock().setType(ogMat);
			// System.out.println("WE GOT A FALSE");
			// return false;
		}
		return 0;
		// System.out.println("WE GOT A FALSE");
	}

	public static int successAirSource(Location loc) {
		if (loc.getBlock().getType() == Material.COARSE_DIRT && blockTags.keySet().contains(loc.getBlock()) && blockTags.get(loc.getBlock()) == 0) {
			// Material ogMat = loc.getBlock().getType();\
			loc.getBlock().setType(Material.CAVE_AIR);
			blockTags.remove(loc.getBlock());
			return 1 + successAirSource(loc.clone().add(new Vector(1, 0, 0)))
					+ successAirSource(loc.clone().add(new Vector(-1, 0, 0)))
					+ successAirSource(loc.clone().add(new Vector(0, 1, 0)))
					+ successAirSource(loc.clone().add(new Vector(0, -1, 0)))
					+ successAirSource(loc.clone().add(new Vector(0, 0, 1)))
					+ successAirSource(loc.clone().add(new Vector(0, 0, -1)));

			// loc.getBlock().setType(ogMat);
			// System.out.println("WE GOT A FALSE");
			// return false;
		}
		return 0;
		// System.out.println("WE GOT A FALSE");
	}
	
	public static int replace(Location loc, int l, Material mat1, Material mat2) {
		if (loc.getBlock().getType() == mat1 && l > 0) {
			loc.getBlock().setType(mat2);
			return 1 + replace(loc.clone().add(new Vector(1, 0, 0)), l-1, mat1, mat2)
			+ replace(loc.clone().add(new Vector(-1, 0, 0)), l-1, mat1, mat2)
			+ replace(loc.clone().add(new Vector(0, 1, 0)), l-1, mat1, mat2)
			+ replace(loc.clone().add(new Vector(0, -1, 0)), l-1, mat1, mat2)
			+ replace(loc.clone().add(new Vector(0, 0, 1)), l-1, mat1, mat2)
			+ replace(loc.clone().add(new Vector(0, 0, -1)), l-1, mat1, mat2);
		}
		return 0;
	}
	
	public static int replace(Location loc, int l, Material mat1, Material mat2, int tag) {
		if (loc.getBlock().getType() == mat1 && l > 0 && blockTags.keySet().contains(loc.getBlock()) && blockTags.get(loc.getBlock()) == tag) {
			blockTags.remove(loc.getBlock());
			loc.getBlock().setType(mat2);
			return 1 + replace(loc.clone().add(new Vector(1, 0, 0)), l-1, mat1, mat2, tag)
			+ replace(loc.clone().add(new Vector(-1, 0, 0)), l-1, mat1, mat2, tag)
			+ replace(loc.clone().add(new Vector(0, 1, 0)), l-1, mat1, mat2, tag)
			+ replace(loc.clone().add(new Vector(0, -1, 0)), l-1, mat1, mat2, tag)
			+ replace(loc.clone().add(new Vector(0, 0, 1)), l-1, mat1, mat2, tag)
			+ replace(loc.clone().add(new Vector(0, 0, -1)), l-1, mat1, mat2, tag);
		}
		return 0;
	}
	
	public static int countAir(Location loc, int l) {
		if (loc.getBlock().getType() == Material.CAVE_AIR && l > 0) {
			
			loc.getWorld().spawnParticle(Particle.DOLPHIN, loc, 5);
			//System.out.println(l);
			loc.getBlock().setType(Material.COARSE_DIRT);
			blockTags.put(loc.getBlock(), 0);
			int r = 1 + countAir(loc.clone().add(new Vector(1, 0, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(-1, 0, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(0, 1, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(0, -1, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(0, 0, 1)), l-1)
					+ countAir(loc.clone().add(new Vector(0, 0, -1)), l-1);

			//loc.getBlock().setType(Material.CAVE_AIR);
			return r;

		}
		//System.out.println("WE GOT A FALSE");
		return 0;
	}
	
	public static int countVacuum(Location loc, int l) {
		if (loc.getBlock().getType() == Material.VOID_AIR && l > 0) {

			loc.getBlock().setType(Material.COARSE_DIRT);
			blockTags.put(loc.getBlock(), 0);
			int r = 1 + countVacuum(loc.clone().add(new Vector(1, 0, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(-1, 0, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 1, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, -1, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, 1)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, -1)), l-1);

			//loc.getBlock().setType(Material.VOID_AIR);
			return r;

		} else if (loc.getBlock().getType() == Material.AIR && l > 0) {

			loc.getBlock().setType(Material.COARSE_DIRT);
			blockTags.put(loc.getBlock(), 0);
			int r = 1 + countVacuum(loc.clone().add(new Vector(1, 0, 0)), l - 1)
					+ countVacuum(loc.clone().add(new Vector(-1, 0, 0)), l - 1)
					+ countVacuum(loc.clone().add(new Vector(0, 1, 0)), l - 1)
					+ countVacuum(loc.clone().add(new Vector(0, -1, 0)), l - 1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, 1)), l - 1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, -1)), l - 1);

			//loc.getBlock().setType(Material.AIR);
			return r;

		}
		return 0;
		// System.out.println("WE GOT A FALSE");
	}

	@EventHandler
	public void onBlockPhysics(final BlockPhysicsEvent event) {
		if (inVoid(event.getBlock().getLocation())) {
			// getServer().broadcastMessage("Block: " + locStr(event.getBlock()) + " Source
			// Block: " + locStr(event.getSourceBlock()) + " Changed Type: " +
			// event.getChangedType().name());
			// getServer().broadcastMessage("BlockData: " +
			// event.getBlock().getBlockData());

			if (isAir(event.getSourceBlock()) && !isVoidAir(event.getSourceBlock()) && isCaveAir(event.getBlock())) {
				setCaveAir(event.getSourceBlock());
			} else if ((isAir(event.getSourceBlock()) || isCaveAir(event.getSourceBlock())) && isAir(event.getBlock())) {
				setVoidAir(event.getSourceBlock());
			} else if (isVoidAir(event.getSourceBlock()) && isCaveAir(event.getBlock())) {
				setVoidAir(event.getBlock());
			}

			//specialBlockUpdate();
		}
	}

	public String locStr(Block block) {
		Location loc = block.getLocation();
		return block.getType().name() + " - " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
	}

	public static boolean inVoid(Location loc) {
		for (Void v : voids) {
			if (loc.getWorld().getName().equals(v.world)) {
				if (v.voidWorld || (loc.getWorld().getName().equals(v.world) && v.within(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))) {
					/*if (timeOffset == -1)
						timeOffset = v.time - System.currentTimeMillis();
					else
						v.time = System.currentTimeMillis();*/
					return true;
				}
			}
		}
		return false;
	}

	public static void cosmicUpdate(CosmicBody yeet, double[] ds) {
		for (Spaceship e : ships) {
			if (e.orbiting == yeet) {
				e.x += ds[0];
				e.y += ds[1];
				e.z += ds[2];
			}
		}
	}

	public void setVoidAir(Block block) {
		/*
		 * try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		// getServer().broadcastMessage("Creating void air!");
		
		/*ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = new Runnable() {
			public void run() {
				block.setType(Material.VOID_AIR);
				Location loc = block.getLocation();
				loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
				loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 5);
			}
		};

		int delay = 100;
		scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
		scheduler.shutdown();*/
		
		block.setType(Material.VOID_AIR);
		Location loc = block.getLocation();
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
		loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 5);
		
		// block.setBlockData(Material.valueOf("VOID_AIR").createBlockData());
		// block.setBlockData(Bukkit.createBlockData("CraftBlockData{minecraft:void_air}"));
		// Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " +
		// loc.getWorld().getName() + " run setblock " + loc.getBlockX() + " " +
		// loc.getBlockY() + " " + loc.getBlockZ() + " void_air");
	}

	public static void setCaveAir(Block block) {
		/*
		 * try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		// getServer().broadcastMessage("Creating cave air!");
		block.setType(Material.CAVE_AIR);
		// block.setBlockData(Material.valueOf("AIR").createBlockData());
		// block.setBlockData(Bukkit.createBlockData("CraftBlockData{minecraft:cave_air}"));
		Location loc = block.getLocation();
		loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 5);
		// Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " +
		// loc.getWorld().getName() + " run setblock " + loc.getBlockX() + " " +
		// loc.getBlockY() + " " + loc.getBlockZ() + " cave_air");
	}

	public void setAir(Block block) {
		/*
		 * try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		// getServer().broadcastMessage("Creating air!");
		block.setType(Material.VOID_AIR);
		// block.setBlockData(Material.valueOf("AIR").createBlockData());
		// block.setBlockData(Bukkit.createBlockData("CraftBlockData{minecraft:air}"));
		Location loc = block.getLocation();
		loc.getWorld().spawnParticle(Particle.CRIT, loc, 5);
		// Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " +
		// loc.getWorld().getName() + " run setblock " + loc.getBlockX() + " " +
		// loc.getBlockY() + " " + loc.getBlockZ() + " air");
	}

	public static boolean isSomeAir(Block block) {
		return block.getType() == Material.VOID_AIR ||block.getType() == Material.CAVE_AIR || block.getType() == Material.AIR;
	}

	public static boolean isAir(Block block) {
		return block.getType() == Material.AIR
				|| block.getType() == Material.VOID_AIR ;
	}

	public boolean isVoidAir(Block block) {
		return block.getType() == Material.VOID_AIR ;
	}

	public boolean isCaveAir(Block block) {
		return block.getType() == Material.CAVE_AIR ;
	}
	
	public static SolarSystem getSystem(String name) {
		if (Main.systems.size() > 0) {
			for (SolarSystem s : Main.systems) {
				if (s != null && (s.sun != null && ((s.sun.name != null && s.sun.name.equals(name)) || (s.sun.knickname != null && s.sun.knickname.equals(name)) || (s.sun.id != null && s.sun.id.equals(name))))) {
					return s;
				}
			}
		}
		return null;
	}
	
	public static Spaceship getShip(String name) {
		if (Main.ships.size() > 0) {
			for (Spaceship s : Main.ships) {
				if (s != null && s.name.equals(name)) {
					return s;
				}
			}
		}
		return null;
	}
	
	public static Spaceship getCurrentShip(Player player) {
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
				//player.sendMessage("Nearest ship:");
				return r;
			} //else No nearby ships
		} // else there are no ships;
		return null;
	}
	static double dist(double dx, double dy, double dz) {
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public static boolean within(Player p, String world, int x, int y, int z, int x2, int y2, int z2) {
        Location loc = p.getLocation();
        if (!loc.getWorld().getName().equalsIgnoreCase(world)) return false;
        if (Math.round(loc.getX()) >= Math.min(x,x2) && Math.round(loc.getX()) <= Math.max(x,x2) && Math.ceil(loc.getY()+0.5) >= Math.min(y,y2) && Math.floor(loc.getY()+0.5) <= Math.max(y,y2) && Math.round(loc.getZ()) >= Math.min(z,z2) && Math.round(loc.getZ()) <= Math.max(z,z2)) {
        	return true;
        }
    	return false;
	}
	
	public static boolean within(Location loc, String world, int x, int y, int z, int x2, int y2, int z2) {
        if (!loc.getWorld().getName().equalsIgnoreCase(world)) return false;
        if (Math.round(loc.getX()) >= Math.min(x,x2) && Math.round(loc.getX()) <= Math.max(x,x2) && Math.ceil(loc.getY()+0.5) >= Math.min(y,y2) && Math.floor(loc.getY()+0.5) <= Math.max(y,y2) && Math.round(loc.getZ()) >= Math.min(z,z2) && Math.round(loc.getZ()) <= Math.max(z,z2)) {
        	return true;
        }
    	return false;
	}
	
	public static boolean playerNearby(String world, int x, int y, int z, int dist) {
		List<Player> near = Bukkit.getWorld(world).getPlayers();
		for(Entity entity : near) {
		    if(entity instanceof Player) {
		        Player p = (Player) entity;
		        if (dist(p.getLocation().getX()-x, p.getLocation().getY()-y, p.getLocation().getZ()-z) <= dist) {
		        	return true;
		        }
		    }
		}
		return false;
	}
	
	public static boolean playersWithin(String world, int x, int y, int z, int x2, int y2, int z2) {
		//Location loc = new Location( Bukkit.getWorld(world), (x+x2)/2, (y+y2)/2, (z+z2)/2 );
		List<Player> near = Bukkit.getWorld(world).getPlayers();
		for(Entity entity : near) {
		    if(entity instanceof Player) {
		        Player p = (Player) entity;
		        if (within(p, world, x, y, z, x2, y2, z2)) {
		        	return true;
		        }
		    }
		}
		return false;
	}
	
	void asyncFillUpdate() {
		if (asyncFill.size() > 0) {
			/*for (Location[] key : asyncFill.keySet()) {
				if (key[1].getBlockX() <= key[2].getBlockX()) {
					key[1].getBlock().setType(asyncFill.get(key));
					key[1].add(new Vector(1, 0, 0));
				} else if (key[1].getBlockY() < key[2].getBlockY()) {
					key[1] = new Location(key[1].getWorld(), key[0].getBlockX(), key[1].getBlockY()+1, key[1].getBlockZ());
				} else if (key[1].getBlockZ() < key[2].getBlockZ()) {
					key[1] = new Location(key[1].getWorld(), key[0].getBlockX(), key[0].getBlockY(), key[1].getBlockZ()+1);
				} else {
					getServer().broadcastMessage("Async fill complete!");
					asyncFill.remove(key);
				}
			} */
			
			for (Location[] key : asyncFill.keySet()) {
				while (key[1].getBlockY() <= key[2].getBlockY()) {
					if (key[1].getBlockX() <= key[2].getBlockX()) {
						key[1].getBlock().setType(asyncFill.get(key));
						key[1].add(new Vector(1, 0, 0));
					} else
						key[1] = new Location(key[1].getWorld(), key[0].getBlockX(), key[1].getBlockY()+1, key[1].getBlockZ());

				}
				if (key[1].getBlockZ() < key[2].getBlockZ()) {
					key[1] = new Location(key[1].getWorld(), key[0].getBlockX(), key[0].getBlockY(), key[1].getBlockZ()+1);
				} else {
					getServer().broadcastMessage("Async fill complete!");
					asyncFill.remove(key);
				}
				//break;
			}
		}
	}
	
	public static void spawnFakePlayer(Location loc, String displayname, SpecialEntity entity) {
		
		NPCRegistry registry = CitizensAPI.createNamedNPCRegistry("myown-registry", new MemoryNPCDataStore());
		net.citizensnpcs.api.npc.NPC npc = registry.createNPC(EntityType.PLAYER, displayname);
		// here to can manage skin for example
		npc.spawn(loc, SpawnReason.CREATE);
		// now it's spawned, you can add item in hand like that :
		// npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.HAND, itemInHand);
		npc.setProtected(false);
		if (entity != null)
			entity.npc = npc;
		//npc.setUseMinecraftAI(true);
		
        /*MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
       
        Player target = Bukkit.getServer().getPlayer(displayname);
        EntityPlayer npc;
        if (target != null) {
            npc = new EntityPlayer(server, world, new GameProfile(target.getUniqueId(), target.getName()), new PlayerInteractManager(world));
        } else {
            OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(displayname);
            npc = new EntityPlayer(server, world, new GameProfile(op.getUniqueId(), displayname), new PlayerInteractManager(world));
        }
        Location loc = player.getLocation();
        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        for(Player all : Bukkit.getOnlinePlayers()){
            PlayerConnection connection = ((CraftPlayer)all).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        }*/
    }
	
	public static void fillAsync(String world, int x1, int y1, int z1, int x2, int y2, int z2, Material mat) {

		System.out.println("Commencing asyc fill for " + (Math.abs(x1-x2)*Math.abs(y1-y2)*Math.abs(z1-z2)) + " blocks, " + Math.abs(x1-x2) + "x" + Math.abs(y1-y2) + "x" + Math.abs(z1-z2));
		World w = Bukkit.getWorld(world);
		asyncFill.put(new Location[] {new Location(w, Math.min(x1,x2), Math.min(y1,y2), Math.min(z1,z2)), new Location(w, Math.min(x1,x2), Math.min(y1,y2), Math.min(z1,z2)), new Location(w, Math.max(x1,x2), Math.max(y1,y2), Math.max(z1,z2))}, mat);
	}
	
	public static long getTimeMillis() {
		return System.currentTimeMillis() + timeOffset;
	}
	
	public static long getTimeSec() {
		return (long)((System.currentTimeMillis() + timeOffset)/1000f);
	}
	
	private String[] space_chestplates = {
			"Stormtrooper Pilot Chestplate",
			"Space Armor Chestplate",
			"Apollo Chestplate"
	};

	private String[] space_helmets = {
			"Stormtrooper Pilot Helmet",
			"Space Armor Helmet",
			"Apollo Helmet"
	};

	private String[] space_leggings = {
			"Stormtrooper Pilot Leggings",
			"Space Armor Leggings",
			"Apollo Leggings"
	};

	private String[] space_boots = {
			"Stormtrooper Pilot Boots",
			"Space Armor Boots",
			"Apollo Boots"
	};
	
	public int spacesuit(Player player) {
		int r = 0;
		if (player != null && player.getInventory().getChestplate() != null) {
			String name = player.getInventory().getChestplate().getItemMeta().getDisplayName();
			for (String s : space_chestplates) {
				if (name.equalsIgnoreCase(s)) {
					r++;
					break;
				}
			}
		}
		if (player != null && player.getInventory().getHelmet() != null) {
			String name = player.getInventory().getHelmet().getItemMeta().getDisplayName();
			for (String s : space_helmets) {
				if (name.equalsIgnoreCase(s)) {
					r++;
					break;
				}
			}
		}
		if (player != null && player.getInventory().getBoots() != null) {
			String name = player.getInventory().getBoots().getItemMeta().getDisplayName();
			for (String s : space_boots) {
				if (name.equalsIgnoreCase(s)) {
					r++;
					break;
				}
			}
		}
		if (player != null && player.getInventory().getLeggings() != null) {
			String name = player.getInventory().getLeggings().getItemMeta().getDisplayName();
			for (String s : space_leggings) {
				if (name.equalsIgnoreCase(s)) {
					r++;
					break;
				}
			}
		}
		//player.sendMessage("spacesuit = " + r);
		return r;
	}
	
	
}
