package com.lerdorf.voidships;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.event.block.Action;

public class Main extends JavaPlugin implements Listener {

	public static ArrayList<Void> voids = new ArrayList<Void>();
	public static ArrayList<Spaceship> ships = new ArrayList<Spaceship>();
	public static ArrayList<SpecialBlock> blocks = new ArrayList<SpecialBlock>();
	public static ArrayList<SpecialEntity> entities = new ArrayList<SpecialEntity>();
	public static ArrayList<SolarSystem> systems = new ArrayList<SolarSystem>();

	ScheduledExecutorService executor;
	
	@Override
	public void onEnable() {
		System.out.println("Starting VoidShips");

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
		this.getCommand("setsystem").setExecutor(new VoidQuery());
		
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
		
		getServer().broadcastMessage("VoidShips enabled!");
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
							} else if (e2.length() > 6 && e2.substring(0, 6).equals("entity")) {
								System.out.println("Loading " + e2);
								int n = Integer.parseInt(e2.substring(6, e2.indexOf('.')));
								while (entities.size() <= n) entities.add(null);
								entities.set(n, new SpecialEntity(e + "/VoidShips/" + e2));
							} else if (e2.length() > 6 && e2.substring(0, 6).equals("system")) {
								System.out.println("Loading " + e2);
								int n = Integer.parseInt(e2.substring(6, e2.indexOf('.')));
								while (systems.size() <= n) systems.add(null);
								systems.set(n, new SolarSystem(e + "/VoidShips/" + e2));
							}
						}
					}
				}
			}
		}

		// getServer().broadcastMessage("VoidShips enabled!");
	}

	@Override
	public void onDisable() {
		System.out.println("Disabling VoidShips");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// System.out.println("Player joined");
		event.getPlayer().sendMessage("Good to be back!");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
	    if (event.getWhoClicked() instanceof Player) {
	        Player player = (Player) event.getWhoClicked();
	        if (event.getClickedInventory() != null) {
	            if (event.getView().getTitle().equals("Ship Terminal")) {
	                if (event.isRightClick()) {
	                    event.setCancelled(true);
	                    //PianoManager.play(player, event.getCurrentItem(), false);
	                }
	            }
	        }
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
	
	long lastBlockUpdate = 0;
	long lastGravUpdate = 0;

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (inVoid(p.getLocation()) && (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)) {
			
			if (System.currentTimeMillis()-lastGravUpdate > 300 && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				boolean yeet = false;
				for (Spaceship s : ships) {
					if (s != null && s.blocks != null && s.blocks.length > 0) {
						for (SpecialBlock b : s.blocks) {
							if (b != null && b.type == SpecialBlock.GRAVITY && within(p, b.world, b.x, b.y, b.z, b.x2, b.y2, b.z2)) {
								yeet = true;
							}
						}
					}
				}
				if (yeet)
					p.addScoreboardTag("grav");
				else
					p.removeScoreboardTag("grav");
			}
			
			if (p.isFlying() == p.getScoreboardTags().contains("grav")) {
				if (!p.getScoreboardTags().contains("grav") && p.getFlySpeed() != 0.022f)
					p.setFlySpeed(0.022f);
				p.setAllowFlight(!p.getScoreboardTags().contains("grav"));
				p.setFlying(!p.getScoreboardTags().contains("grav"));
				//p.sendMessage("Setting flight to " + !p.getScoreboardTags().contains("grav"));
			}
			
			if (!p.getScoreboardTags().contains("vac") && isAir(p.getLocation().getBlock())) {
				p.addScoreboardTag("vac");
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 0, false, false));
				p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100000, 9, false, false));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 3, false, false));
				p.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 100000, 1, false, false));
			} else if (!p.hasPotionEffect(PotionEffectType.WITHER) && p.getScoreboardTags().contains("vac")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 0, false, false));
				p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100000, 9, false, false));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 3, false, false));
			}
		} else if (p.getFlySpeed() != 0.1f)
			p.setFlySpeed(0.1f);
		if (p.getScoreboardTags().contains("vac")
				&& (isCaveAir(p.getLocation().getBlock()) || !inVoid(p.getLocation()))) {
			p.removeScoreboardTag("vac");
			p.removePotionEffect(PotionEffectType.BLINDNESS);
			p.removePotionEffect(PotionEffectType.WITHER);
			p.removePotionEffect(PotionEffectType.SLOW);
			p.removePotionEffect(PotionEffectType.HARM);
		}
		specialBlockUpdate();
		fastBlockUpdate(p);
	}

	public void specialBlockUpdate() {
		if (System.currentTimeMillis()-lastBlockUpdate > 300) {
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
			loc.getWorld().spawnParticle(Particle.CLOUD, loc, 5);
			loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 2);
			Material ogMat = loc.getBlock().getType();
			air--;
			loc.getBlock().setType(Material.COARSE_DIRT);
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
		if (loc.getBlock().getType() == Material.COARSE_DIRT) {
			// Material ogMat = loc.getBlock().getType();\
			loc.getBlock().setType(Material.VOID_AIR);
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
		if (loc.getBlock().getType() == Material.COARSE_DIRT) {
			// Material ogMat = loc.getBlock().getType();\
			loc.getBlock().setType(Material.CAVE_AIR);
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
	
	public static int countAir(Location loc, int l) {
		if (loc.getBlock().getType() == Material.CAVE_AIR && l > 0) {

			loc.getBlock().setType(Material.COARSE_DIRT);
			int r = 1 + countAir(loc.clone().add(new Vector(1, 0, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(-1, 0, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(0, 1, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(0, -1, 0)), l-1)
					+ countAir(loc.clone().add(new Vector(0, 0, 1)), l-1)
					+ countAir(loc.clone().add(new Vector(0, 0, -1)), l-1);

			loc.getBlock().setType(Material.CAVE_AIR);
			return r;

		}
		return -1000000;
		// System.out.println("WE GOT A FALSE");
	}
	
	public static int countVacuum(Location loc, int l) {
		if (loc.getBlock().getType() == Material.VOID_AIR && l > 0) {

			loc.getBlock().setType(Material.COARSE_DIRT);
			int r = 1 + countVacuum(loc.clone().add(new Vector(1, 0, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(-1, 0, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 1, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, -1, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, 1)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, -1)), l-1);

			loc.getBlock().setType(Material.VOID_AIR);
			return r;

		} else if (loc.getBlock().getType() == Material.AIR && l > 0) {

			loc.getBlock().setType(Material.COARSE_DIRT);
			int r = 1 + countVacuum(loc.clone().add(new Vector(1, 0, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(-1, 0, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 1, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, -1, 0)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, 1)), l-1)
					+ countVacuum(loc.clone().add(new Vector(0, 0, -1)), l-1);

			loc.getBlock().setType(Material.AIR);
			return r;

		}
		return -1000000;
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
			} else if ((isAir(event.getSourceBlock()) || isCaveAir(event.getSourceBlock()))
					&& isAir(event.getBlock())) {
				event.getBlock().getLocation().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
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

	public boolean inVoid(Location loc) {
		for (Void v : voids)
			if (loc.getWorld().getName().equals(v.world) && v.within(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
				return true;
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
		block.setType(Material.VOID_AIR);
		// block.setBlockData(Material.valueOf("VOID_AIR").createBlockData());
		// block.setBlockData(Bukkit.createBlockData("CraftBlockData{minecraft:void_air}"));
		Location loc = block.getLocation();
		loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 5);
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
        if (Math.ceil(loc.getX()) >= Math.min(x,x2) && Math.floor(loc.getX()) <= Math.max(x,x2) && Math.ceil(loc.getY()) >= Math.min(y,y2) && Math.floor(loc.getY()) <= Math.max(y,y2) && Math.ceil(loc.getZ()) >= Math.min(z,z2) && Math.floor(loc.getZ()) <= Math.max(z,z2)) {
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
}
