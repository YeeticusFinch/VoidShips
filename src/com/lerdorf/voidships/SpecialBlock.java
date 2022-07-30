package com.lerdorf.voidships;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SpecialBlock implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5390084910044784771L;
	public int x, y, z;
	public int type;
	
	public int x2 = -1, y2 = -1, z2 = -1;

	int air = 0;
	float fuel = 0;
	boolean dead = false;
	boolean open = false;
	boolean localMap = false;
	boolean fastUpdate;

	long openTime = 0;
	
	Spaceship ship;

	public static final int AIR_TANK = 0;
	public static final int AIR_PUMP = 1;
	public static final int DOOR = 2;
	public static final int AIR_DETECTOR = 3;
	public static final int GRAVITY = 4;
	public static final int MAP = 5;
	public static final int TERMINAL = 6;
	public static final int FUEL_TANK = 7;
	public static final int AIR_WALL = 8;
	// Get the CCTV plugin

	String world;

	String[][][] materials;
	String[][][] datas;
	
	String name;
	
	public SpecialBlock(Block block, int type, Spaceship ship) {
		Location loc = block.getLocation();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.type = type;
		this.ship = ship;
		this.world = loc.getWorld().getName();
		fastUpdate = type == MAP;
		switch (type) {
		case AIR_TANK:
			air = 1000;
			break;
		case FUEL_TANK:
			fuel = (float)Math.pow(20,12);
			break;
		}
	}
	
	public SpecialBlock(int type, String world, int x, int y, int z, int x2, int y2, int z2) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.world = world;
		fastUpdate = type == MAP;
	}

	public SpecialBlock(String filepath) {
		load(filepath);
	}
	
	public void initRefs() {
		if (ship != null)
			ship = Main.getShip(ship.name);
	}

	public void update() {
		if (world == null)
			return;
		switch (type) {
		case AIR_TANK:
			if (new Location(Bukkit.getWorld(world), x, y, z).getBlock().getType() != getMaterial()) {
				if (!dead) Bukkit.getServer().broadcastMessage("Air tank destroyed, " + air + " cubic meters of air lost.");
				air = 0;
				dead = true;
			} else {
				if (dead) Bukkit.getServer().broadcastMessage("Air tank repaired");
				dead = false;
			}
			break;
		case AIR_PUMP:
			if (new Location(Bukkit.getWorld(world), x, y, z).getBlock().getType() != getMaterial()) {
				if (!dead) Bukkit.getServer().broadcastMessage("Air pump destroyed");
				dead = true;
			} else {
				if (dead) Bukkit.getServer().broadcastMessage("Air pump repaired");
				dead = false;
			}
			break;
		case DOOR:
			if (open && System.currentTimeMillis()-openTime > 2000 && !Main.playersWithin(world, x, y, z, x2, y2, z2))
				closeDoor();
			else if (open && System.currentTimeMillis()-openTime > 2000)
				openTime = System.currentTimeMillis()-1000;
			break;
		case AIR_DETECTOR:
			Location loc = new Location(Bukkit.getWorld(world), x, y, z);
			if (loc.getBlock().getType() != getMaterial()) {
				if (!dead) Bukkit.getServer().broadcastMessage("Atmosphere detector destroyed");
				dead = true;
			} else {
				if (dead) Bukkit.getServer().broadcastMessage("Atmosphere detector repaired");
				dead = false;
				Lightable light = ( (Lightable)( loc.getBlock().getBlockData() ) );
				if (Main.isAir(loc.clone().add(new Vector(1, 0, 0)).getBlock()) || Main.isAir(loc.clone().add(new Vector(-1, 0, 0)).getBlock()) ||  Main.isAir(loc.clone().add(new Vector(0, 1, 0)).getBlock()) || Main.isAir(loc.clone().add(new Vector(0, -1, 0)).getBlock()) || Main.isAir(loc.clone().add(new Vector(0, 0, 1)).getBlock()) || Main.isAir(loc.clone().add(new Vector(0, 0, -1)).getBlock()))
					light.setLit(false);
				else
					light.setLit(true);
				loc.getBlock().setBlockData(light);
					
			}
			break;
		case MAP:
			if (new Location(Bukkit.getWorld(world), x, y, z).getBlock().getType() != getMaterial()) {
				if (!dead) Bukkit.getServer().broadcastMessage("Map destroyed");
				dead = true;
			} else {
				if (dead) Bukkit.getServer().broadcastMessage("Map repaired");
				dead = false;
			}
			break;
		case TERMINAL:
			if (new Location(Bukkit.getWorld(world), x, y, z).getBlock().getType() != getMaterial()) {
				if (!dead) Bukkit.getServer().broadcastMessage("Ship terminal destroyed");
				dead = true;
			} else {
				if (dead) Bukkit.getServer().broadcastMessage("Ship terminal repaired");
				dead = false;
			}
			break;
		case FUEL_TANK:
			if (new Location(Bukkit.getWorld(world), x, y, z).getBlock().getType() != getMaterial()) {
				if (!dead) Bukkit.getServer().broadcastMessage("Fuel tank destroyed, " + fuel + " joules of fuel lost.");
				fuel = 0;
				dead = true;
			} else {
				if (dead) Bukkit.getServer().broadcastMessage("Fuel tank repaired");
				dead = false;
			}
			break;
		}
	}
	
	public void update(Player p, Spaceship ship) {
		if (type == MAP)
			mapDisplay(p, ship);
	}
	
	double zoom = 1;
	
	public void scroll(int prev, int next, Player player) {
		if (type == MAP) {
			if (prev == 8 && next < 5) prev = -1;
			else if (prev == 0  && next > 4) prev = 9;
			zoom *= Math.pow(1.6,(next-prev));
			player.sendMessage("Set map zoom to " + ((int)(zoom*100))/100.0 + "x");
		}
	}
	
	
	public void mapDisplay(Player player, Spaceship ship) {
		if (dead || !Main.playerNearby(world, x, y, z, 10))
			return;

		if (zoom < 0.001 || zoom > 1000000)
			zoom = 1;
		boolean playerNear = player != null && 6 > dist( player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), x, y, z );
		if (localMap) {
			SolarSystem s = ship.system;
			double h = 1.4;
			if (s != null && s.sun != null) {
				int[] rgb = s.sun.getColor();
				Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(rgb[0], rgb[1], rgb[2]), 0.3f);
	            Bukkit.getWorld(world).spawnParticle(Particle.REDSTONE, x+0.5, y+2*0.707*h+0.1, z+0.5, 0, 0, 0, 0, dust);
			}
			if (s.planets != null && s.planets.length > 0) {
				CosmicBody listPlanet = null;
				double listPlanetDiff = -1;
				//double maxD = 5.906423*Math.pow(10,8);
				double maxD = 0;
				
				for (CosmicBody p : s.planets)
					if (p.orbitDist > maxD)
						maxD = p.orbitDist;
				maxD /= zoom;
				for (CosmicBody p : s.planets) {
					for (int i = 0; i < 10; i++) {
						p.orbit(System.currentTimeMillis()/1000 - 864000*i);
						Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(155, 155, 155), 0.1f - 0.008f*i);
						
						double px = x + 0.5 + (h * p.x)/(maxD);
						double py = y + h*2*0.707 + 0.1 + (h * (p.y*0.707+p.z*0.707))/(maxD);
						double pz = z + 0.5 + (h * (p.z*0.707-p.y*0.707))/(maxD);
						Bukkit.getWorld(world).spawnParticle(Particle.REDSTONE, px, py, pz, 0, 0, 0, 0, dust);
					}
					p.orbit();
					Particle.DustOptions dust = new Particle.DustOptions(p.habitable ? Color.fromRGB(155, 255, 155) : Color.fromRGB(155, 155, 255), 0.2f);
					
					double px = x + 0.5 + (h * p.x)/(maxD);
					double py = y + h*2*0.707 + 0.1 + (h * (p.y*0.707+p.z*0.707))/(maxD);
					double pz = z + 0.5 + (h * (p.z*0.707-p.y*0.707))/(maxD);
					Bukkit.getWorld(world).spawnParticle(Particle.REDSTONE, px, py, pz, 0, 0, 0, 0, dust);
	                
					
					//System.out.println("dust for " + p.name + " at " + p.x + " " + p.y + " " + p.z);
	                if (playerNear) {
	                	//Ray ray = Ray.from(player);
	                	Vector dir = player.getEyeLocation().getDirection();
	                	Vector dir2 = getDir(player.getEyeLocation().getX(), player.getEyeLocation().getY(), player.getEyeLocation().getZ(), px, py, pz );
	                	double diff = dist( dir.getX(), dir.getY(), dir.getZ(), dir2.getX(), dir2.getY(), dir2.getZ() );
	                	if ((listPlanet == null || diff < listPlanetDiff) && diff < 0.5) {
	                		listPlanet = p;
	                		listPlanetDiff = diff;
	                	}
	                }
				}
				if (listPlanet != null) {
					String info = "";
					if (listPlanet.name != null) info += " [§6" + listPlanet.name + "§f]";
					if (listPlanet.knickname != null) info += " '§d" + listPlanet.knickname + "§f'";
					if (listPlanet.id != null) info += " [§b" + listPlanet.id + "§f]";
					if (listPlanet.type != null) info += " [§c" + listPlanet.type + "§f]";
					//if (listPlanet.mass != -1) info += " [Mass: " + (int)(10000*listPlanet.mass/(5.972*Math.pow(10,24)))/10000f + " M⭘]";
					//if (listPlanet.radius != -1) info += " [Radius: " + (int)(10000*listPlanet.radius/6371f)/10000f + " R⭘]";
					//info += "\n";
					//info += " [" + (int)(10000f*Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z)/(1.496*Math.pow(10,8)))/10000f + " AU from " + system.getName() + "]";
					//if (listPlanet.orbitDist != -1) info += " [" + (int)(10000*listPlanet.orbitDist/(1.496*Math.pow(10,8)))/10000f + " AU from " + s.getName() + "]";
					//if (listPlanet.orbitPeriod != -1) info += " [Period: " + listPlanet.orbitPeriod + " Earth days]";
					if (listPlanet.habitable) info += " [habitable]";
					TitleManager.sendActionBar(player, info);
				}
			}
		} else {
			CosmicBody listStar = null;
			double listStarDiff = -1;
			for (SolarSystem s : Main.systems) {
				if (s != null && s.sun != null) {
					int[] rgb = s.sun.getColor();
					Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(rgb[0], rgb[1], rgb[2]), 0.2f);
					double px = x + 0.5 + 1.2 * s.lyx/(50);
					double py = y+2 + 1.2 * s.lyy/(50);
					double pz = z + 0.5 + 1.2 * s.lyz/(50);
		            Bukkit.getWorld(world).spawnParticle(Particle.REDSTONE, px, py, pz, 0, 0, 0, 0, dust);
		            if (playerNear) {
	                	//Ray ray = Ray.from(player);
	                	Vector dir = player.getEyeLocation().getDirection();
	                	Vector dir2 = getDir(player.getEyeLocation().getX(), player.getEyeLocation().getY(), player.getEyeLocation().getZ(), px, py, pz );
	                	double diff = dist( dir.getX(), dir.getY(), dir.getZ(), dir2.getX(), dir2.getY(), dir2.getZ() );
	                	if ((listStar == null || diff < listStarDiff) && diff < 0.5) {
	                		listStar = s.sun;
	                		listStarDiff = diff;
	                	}
	                }
				}
			}
			if (listStar != null) {
				TitleManager.sendActionBar(player, listStar.name + listStar.knickname != null ? " '" + listStar.knickname + "'" : "");
			}
		}
	}
	
	public Block getBlock() {
		return new Location(Bukkit.getWorld(world), x, y, z).getBlock();
	}

	double dist(double x, double y, double z, double x2, double y2, double z2) {
		return Math.sqrt( Math.pow(x-x2,2) + Math.pow(y-y2,2) + Math.pow(z-z2,2) );
	}
	
	Vector getDir(double x, double y, double z, double x2, double y2, double z2) {
		double mag = dist(x, y, z, x2, y2, z2);
		return new Vector((x2-x)/mag, (y2-y)/mag, (z2-z)/mag);
	}
	
	public boolean checkMaterial(Material m) {
		if (type == DOOR)
			return true;
		return getMaterial() == m;
	}
	
	public boolean compareLocation(Location o) {
		if (type == DOOR) {
			int ox = o.getBlockX();
			int oy = o.getBlockY();
			int oz = o.getBlockZ();
			return ox >= Math.min(x,x2) && ox <= Math.max(x,x2) && oy >= Math.min(y,y2) && oy <= Math.max(y,y2) && oz >= Math.min(z,z2) && oz <= Math.max(z,z2);
		}
		return o.getBlockX() == x && o.getBlockY() == y && o.getBlockZ() == z;
	}
	
	public String toString() {
		String r = "";
		String t = getName();
		if (t != null) r += t;
		else r += "type:" + type;
		r += " at " + x;
		r += " " + y;
		r += " " + z;
		if (dead) r += " [broken]";
		if (x2 != -1 && y2 != -1 && z2 != -1) r += " to " + x2 + " " + y2 + " " + z2;
		if (type == DOOR) r += " " + ((open) ? "open" : "closed");
		if (type == AIR_TANK) r += " air: " + air;
		if (type == FUEL_TANK) r += " fuel: " + fuel;
		if (type == MAP) r += localMap ? " [local map]" : " [system map]";
		if (ship != null) r += " [" + ship.name + "]";
		if (world != null) r += " [" + world + "]";
		return r;
	}
	
	public void openDoor() {
		System.out.println("attempting to open door");
		if (type != DOOR)
			return;
		int minX = Math.min(x,x2);
		int maxX = Math.max(x,x2);
		int minY = Math.min(y,y2);
		int maxY = Math.max(y,y2);
		int minZ = Math.min(z,z2);
		int maxZ = Math.max(z,z2);
		materials = new String[maxX-minX+2][maxY-minY+2][maxZ-minZ+2];
		datas = new String[maxX-minX+2][maxY-minY+2][maxZ-minZ+2];
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					Location loc = new Location(Bukkit.getWorld(world), i, j, k);
					materials[i-minX][j-minY][k-minZ] = loc.getBlock().getType().name();
					datas[i-minX][j-minY][k-minZ] = loc.getBlock().getBlockData().getAsString();
					//System.out.println(loc.getBlock().getBlockData().getAsString());
					
				}
			}
		}
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					Location loc = new Location(Bukkit.getWorld(world), i, j, k);
					loc.getBlock().setType(Material.CAVE_AIR);
					loc.getWorld().spawnParticle(Particle.CRIT, loc, 5);
					loc.getWorld().playSound(loc, Sound.BLOCK_PISTON_EXTEND, 1, 0.6f);
				}
			}
		}
		openTime = System.currentTimeMillis();
		open = true;
	}
	
	public void closeDoor() {
		System.out.println("attempting to close door");
		if (type != DOOR)
			return;
		int minX = Math.min(x,x2);
		int maxX = Math.max(x,x2);
		int minY = Math.min(y,y2);
		int maxY = Math.max(y,y2);
		int minZ = Math.min(z,z2);
		int maxZ = Math.max(z,z2);
		//materials = new String[maxX-minX+1][maxY-minY+1][maxZ-minZ+1];
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				for (int k = minZ; k <= maxZ; k++) {
					Location loc = new Location(Bukkit.getWorld(world), i, j, k);
					if (Main.isSomeAir(loc.getBlock())) {
						//loc.getBlock().setType(Material.valueOf(materials[i-minX][j-minY][k-minZ]));
						if (datas[i-minX][j-minY][k-minZ].indexOf("minecraft:air") != -1 || datas[i-minX][j-minY][k-minZ].indexOf("minecraft:void_air") != -1)
							loc.getBlock().setType(Material.CAVE_AIR);
						else
							loc.getBlock().setBlockData(Bukkit.createBlockData(datas[i-minX][j-minY][k-minZ]));
						//if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.VOID_AIR) loc.getBlock().setType(Material.CAVE_AIR);
						loc.getWorld().spawnParticle(Particle.CRIT, loc, 5);
						loc.getWorld().playSound(loc, Sound.BLOCK_PISTON_CONTRACT, 1, 0.6f);
					}
				}
			}
		}
		open = false;
	}
	
	long lastMapSwitch = 0;
	
	public void rightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		System.out.println("Special block right click event");
		
		switch (type) {
		case AIR_TANK:
			player.sendMessage("Air: " + air);
			break;
		case FUEL_TANK:
			player.sendMessage("Fuel: " + fuel);
			break;
		case AIR_PUMP:
			System.out.println("Opening Air Pump menu");
			event.setCancelled(true);
			Main.openMenu(player, ship, this, 2);
			break;
		case DOOR:
			if (!open)
				openDoor();
			else if (System.currentTimeMillis()-openTime > 200)
				closeDoor();
			break;
		case MAP:
			if (System.currentTimeMillis()-lastMapSwitch > 200) {
				System.out.println("Switching map");
				localMap = !localMap;
				lastMapSwitch = System.currentTimeMillis();
			}
			break;
		case TERMINAL:
			// number must be multiple of 9
			Main.openMenu(player, ship, 0);
			break;
		}
	}

	public Material getMaterial() {
		switch (type) {
		case AIR_TANK:
			return Material.POLISHED_BASALT;
		case AIR_PUMP:
			return Material.DISPENSER;
		case AIR_DETECTOR:
			return Material.REDSTONE_LAMP;
		case MAP:
			return Material.SMOOTH_STONE_SLAB;
		case TERMINAL:
			return Material.DARK_OAK_STAIRS;
		case FUEL_TANK:
			return Material.DRIED_KELP_BLOCK;
		}
		return null;
	}

	public String getName() {
		switch (type) {
		case AIR_TANK:
			return "Air tank";
		case AIR_PUMP:
			return "Air pump";
		case DOOR:
			return "Space door";
		case AIR_DETECTOR:
			return "Atmosphere detector";
		case GRAVITY:
			return "Gravity generator";
		case MAP:
			return "Map";
		case TERMINAL:
			return "Ship terminal";
		case FUEL_TANK:
			return "Fuel tank";
		}
		return null;
	}

	public void save(String filename) {
		try {
			(new File(world + "/VoidShips")).mkdirs();
			(new File(world + "/VoidShips/" + filename)).createNewFile();
			FileOutputStream fos = new FileOutputStream(world + "/VoidShips/" + filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			// write object to file
			oos.writeObject(this);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void load(String filepath) {
		try (FileInputStream fis = new FileInputStream(filepath); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// read object from file
			SpecialBlock yeet = (SpecialBlock) ois.readObject();
			x = yeet.x;
			y = yeet.y;
			z = yeet.z;
			ship = yeet.ship;
			type = yeet.type;
			air = yeet.air;
			world = yeet.world;
			open = yeet.open;
			materials = yeet.materials;
			datas = yeet.datas;
			x2 = yeet.x2;
			y2 = yeet.y2;
			z2 = yeet.z2;
			openTime = yeet.openTime;
			dead = yeet.dead;
			localMap = yeet.localMap;
			fuel = yeet.fuel;
			//fastUpdate = yeet.fastUpdate;
			fastUpdate = type == MAP;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
