package com.lerdorf.voidships;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import net.citizensnpcs.api.trait.trait.Equipment;

public class SpecialEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5487114141445842249l;;
	public int x, y, z;
	public float vx = 0, vy = 0, vz = 0; // m / tick
	// public double dirX, dirY, dirZ;
	// public double tDirX, tDirY, tDirZ;
	public float yaw, pitch;
	public float tYaw, tPitch;
	public double sysX, sysY, sysZ;
	public int type;

	Spaceship ship;

	public static final int TIE_FIGHTER = 0;
	public static final int ESCAPE_POD = 1;
	public static final int ESCAPE_POD_GUN = 2;
	public static final int SMALL_TURRET = 3;
	public static final int MEDIUM_TURRET = 4;
	public static final int DALEK_DRONE = 5;

	String world;
	public double turnSpeed;
	public String tag;

	public boolean turret = false;
	public boolean vehicle = false;
	public boolean drone = false;
	public boolean ride = true;
	public boolean comms = true;
	
	public String pilot = null;
	public String[] passengers;

	public int customModelData = -1;

	public double fuel = 0; // joules
	public double fuelMass = 0; // kg per joule
	public float engineEfficiency = 0f; // 1 means exactly half of the energy spent gets converted into kinetic energy
	public int radius; // m
	public int mass; // kg
	public double thrust; // m / tick^2
	public float flySpeed = 0.1f;
	
	public boolean flyFlight = false;
	public transient net.citizensnpcs.api.npc.NPC npc;
	public transient ItemStack[] inventory;
	public int air;
	
	public boolean lookUpAndDown = true;
	public String nsn; // navigational systems name

	public SpecialEntity(Entity entity, int type, Spaceship ship) {
		Location loc = entity.getLocation();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.ship = ship;
		this.type = type;
		setupShit();
		refuel();
		world = loc.getWorld().getName();
	}

	public SpecialEntity(Location loc, int type, Spaceship ship) {
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.ship = ship;
		this.type = type;
		setupShit();
		refuel();
		world = loc.getWorld().getName();
	}

	public SpecialEntity(String filepath) {
		load(filepath);
	}

	public void setup(Player p, int seat) {
		if (seat == 0) {
			inventory = Main.saveInventory(p);
			switch (type) {
				case TIE_FIGHTER:
					break;
			}
		}
		
	}
	
	public void addPassenger(Player p) {
		if (pilot == null) {
			if (flyFlight)
				p.teleport(Main.standEntities.get(tag).getLocation());
			pilot = p.getName();
			p.addScoreboardTag("flyspeed");
			if (drone) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, false, false));
				Main.spawnFakePlayer(p.getLocation(), p.getName(), this);
				LivingEntity le = ((LivingEntity) npc.getEntity());
				le.setHealth(p.getHealth());
				//((LivingEntity) npc.getEntity()).getEquipment().set(Equipment.EquipmentSlot.HELMET, p.getInventory().getHelmet());
				le.getEquipment().setChestplate(p.getInventory().getChestplate());
				le.getEquipment().setLeggings(p.getInventory().getLeggings());
				le.getEquipment().setBoots(p.getInventory().getBoots());
				le.getEquipment().setHelmet(p.getInventory().getHelmet());
				new java.util.Timer().schedule( 
            	        new java.util.TimerTask() {
            	            @Override
            	            public void run() {
            	            	Main.asyncNPCEquip.put(le, new ItemStack[] {p.getInventory().getHelmet(), p.getInventory().getChestplate(), p.getInventory().getLeggings(), p.getInventory().getBoots()});
            	            }
            	        }, 
            	        500 
            	);
				new java.util.Timer().schedule( 
            	        new java.util.TimerTask() {
            	            @Override
            	            public void run() {
            	            	Main.asyncNPCEquip.put(le, new ItemStack[] {p.getInventory().getHelmet(), p.getInventory().getChestplate(), p.getInventory().getLeggings(), p.getInventory().getBoots()});
            	            }
            	        }, 
            	        2000 
            	);
			}
			inventory = Main.saveInventory(p);
		    p.getInventory().clear();
		}
		else {
			int n = 0;
			if (passengers != null)
				n = passengers.length;
			n++;
			String[] temp = new String[n];
			if (passengers != null)
				for (int i = 0; i < passengers.length; i++)
					temp[i] = passengers[i];
			temp[temp.length-1] = p.getName();
			//passengers.add(p.getName());
		}
	}
	
	public void removePassenger(Player p) {
		if (pilot != null && pilot.equals(p.getName())) {
			Main.loadInventory(p, inventory);
			pilot = null;
			if (npc != null) {
				p.teleport(npc.getStoredLocation());
				if (npc.getEntity() != null)
					p.setHealth(((LivingEntity) npc.getEntity()).getHealth());
				else
					p.setHealth(0);
				p.setAllowFlight(false);
				npc.destroy();
				npc = null;
			}
			p.removeScoreboardTag("flyspeed");
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
			p.setWalkSpeed(0.2f);
			p.setFlySpeed(0.1f);
		}
		else {
			int n = 0;
			if (passengers != null)
				n = passengers.length;
			n--;
			String[] temp = new String[n];
			boolean removed = false;
			if (passengers != null) {
				for (int i = 0; i < passengers.length; i++) {
					if (!removed && passengers[i].equals(p.getName())) {
						removed = true;
						i++;
					}
					temp[removed ? i-1 : i] = passengers[i];
				}
			}
			//passengers.remove(p.getName());
		}
	}
	
	double clamp(double a, double b, double c) {
		return Math.min(Math.max(b, c), Math.max(a, Math.min(b, c)));
	}

	double delYaw = 0;
	double delPitch = 0;
	private transient double npcHealth = 20;
	private transient Vector prevFlyVel = new Vector(0, 0, 0);
	public Orbit orbit;
	public void update() { // Gets called 20 times per second (maybe overkill, perhaps scale that down if it shows signs of lag)
		ArmorStand v = Main.standEntities.get(tag);
		if (v != null) {
			Player p = (Player)v.getPassenger();
			if (p == null && pilot != null)
				p = Bukkit.getPlayer(pilot);
			if (p != null) {
				setTargetDirection(p.getEyeLocation().getPitch(), p.getEyeLocation().getYaw());
				if (flyFlight) {
					if (!p.isFlying() && p.getLocation().getY() > v.getLocation().getY() + 0.2) {
						p.teleport(v.getLocation().add(0, -0.2, 0));
					} else
						v.teleport(p.getLocation().add(0, 0.2, 0).toVector().toLocation(p.getLocation().getWorld()));
					if (p.getFlySpeed() != flySpeed && p.getScoreboardTags().contains("flyspeed"))
						p.setFlySpeed(flySpeed);
					if (p.getWalkSpeed() != flySpeed*1.8f && p.getScoreboardTags().contains("flyspeed")) {
						p.setWalkSpeed(flySpeed*1.8f);
						//p.setJumpHeight(0);
					}
					if (fuel > 1) 
						p.setAllowFlight(true);
					double fuelNeeded = 0;
					if (p.isFlying() && ( p.getScoreboardTags().contains("grav") || !Main.inVoid(p.getLocation()) ) )
					    fuelNeeded += 0.5f * (mass + fuel * fuelMass) * Math.pow(9.81/20, 2); // in 1 tick a player would have gained (9.81/20) m/s downwards velocity
					fuelNeeded += 0.5f * (mass + fuel * fuelMass) * p.getVelocity().subtract(prevFlyVel).lengthSquared();
					fuelNeeded /= (Math.max(engineEfficiency, 0.0001f)*0.5f);
					prevFlyVel = p.getVelocity().clone();
				}
				if (drone) {
					if (npc == null || npc.getEntity() == null) {
						removePassenger(p);
						p.damage(2000);
					} else {
						double npcHealthCurrent = ((LivingEntity)npc.getEntity()).getHealth();
						if (npcHealth > npcHealthCurrent) {
							((Player)p).sendMessage("§4Your physical body has taken damage! Your HP is now " + npcHealthCurrent);
							npcHealth = npcHealthCurrent;
						}
					}
				}
			}
			else if (vehicle && !flyFlight)
				slowDown();
			//pitch = (float)v.getHeadPose().getX() / 0.01745329f;
			//yaw = (float)v.getHeadPose().getY() / 0.01745329f;
			double newDelPitch = lookUpAndDown ? clamp(angleSubtract(tPitch, pitch), -turnSpeed, turnSpeed) : 0;
			double newDelYaw = clamp(angleSubtract(tYaw, yaw), -turnSpeed, turnSpeed);
			double fuelNeeded = 0.5 * (0.4 * (mass + fuel * fuelMass) * radius * radius) * 0.01745329* Math.pow(Math.abs(newDelPitch - delPitch) + Math.abs(newDelYaw - delYaw), 2);
			fuelNeeded /= (Math.max(engineEfficiency, 0.0001f)*0.5f);
			if (vehicle) {
				if (fuelNeeded < fuel) {
					fuel -= fuelNeeded;
					delYaw = newDelYaw;
					delPitch = newDelPitch;
				}
			} else if (turret) {
				if (ship != null) {
					if (fuelNeeded < ship.countFuel()) {
						ship.removeFuel((float)fuelNeeded);
						delYaw = newDelYaw;
						delPitch = newDelPitch;
					}
				} else {
					delYaw = newDelYaw;
					delPitch = newDelPitch;
				}
			}
			yaw += delYaw;
			pitch += delPitch;
			v.setHeadPose(new EulerAngle((float)(pitch*0.01745329f), (float)(yaw*0.01745329f), 0));
			//v.teleport(new Location(v.getWorld(), v.getLocation().getX(), v.getLocation().getY(),
			//		v.getLocation().getZ(), (float)yaw, (float)pitch));
			//System.out.println(tag + " pitch = " + pitch + "  yaw = " + yaw);
			//System.out.println("entity pitch " + v.getLocation().getPitch() + "  yaw" + v.getLocation().getYaw());
			//v.getEyeLocation().setPitch(pitch);
			//v.getEyeLocation().setYaw(yaw);
			//v.setLocation(v.getLocation().setYaw(yaw));
			//v.setPitch(pitch);
			/*
			 * v.teleport(new Location(v.getWorld(), v.getLocation().getX(), // Move this
			 * teleport function to the SpecialEntity class so that it can call it
			 * asynchronously v.getLocation().getY(), v.getLocation().getZ(),
			 * v.getEyeLocation().getPitch()+clamp(p.getEyeLocation().getPitch()-v.
			 * getEyeLocation().getPitch(), -e.turnSpeed, e.turnSpeed),
			 * v.getEyeLocation().getYaw()+clamp(p.getEyeLocation().getYaw()-v.
			 * getEyeLocation().getYaw(), -e.turnSpeed, e.turnSpeed) ));
			 */
			//if (vx != 0 || vy != 0 || vz != 0)
			//	System.out.println("Setting velocity for " + tag + " to " + vx + " " + vy + " " + vz);
			double velMag = Math.sqrt(vx*vx + vy*vy + vz*vz);
			if (collided(v.getEyeLocation(), radius, vx/velMag, vy/velMag, vz/velMag) || collided(v.getLocation(), radius, vx/velMag, vy/velMag, vz/velMag)) {
				vx = 0;
				vy = 0; 
				vz = 0;
			}
			v.setVelocity(getVelocity());
		} else {
			System.out.println("ERROR: LIVING ENTITY IS NULL FOR " + tag);
		}
	}
	
	public boolean collided(Location startLoc, int range, double dx, double dy, double dz) {
		for (int i = 0; i < range*2; i++) {

            double x = dx * i*0.5; // How this work is that the xyz of the origin vector was timed the current loop index and increases as the loop going
            double y = dy * i*0.5; // +1.5 to the eye location
            double z = dz * i*0.5;

            startLoc.add(x, y, z);
            //startLoc.getWorld().spawnParticle(Particle.FLAME, startLoc, 5);
            //System.out.println("World: " + startLoc.getWorld() + ", " + startLoc.getX() + " " + startLoc.getY() + " " + startLoc.getZ());

            // Do stuff here
    		if (!Main.isSomeAir(startLoc.getBlock())) {
    			return true;
    		}

            startLoc.subtract(x, y, z); // VERY IMPORTANT to reset the location afterwards
        }
		return false;
		
	}
	
	public double angleSubtract(double b, double a) {
		return CarlMath.minMag(b-a, CarlMath.minMag(b-360-a, b+360-a));
	}
	
	 public void shootParticle(Vector loc, Vector dir, Particle particle, double velocity) {
	        //Location location = player.getEyeLocation();
	        //Vector direction = location.getDirection();
	        Bukkit.getWorld(world).spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), 0, (float) dir.getX(), (float) dir.getY(), (float) dir.getZ(),velocity , null);
	    }

	public void addVelocity(Vector dir) {
		//dir = dir.normalize().multiply(thrust);
		double diff = 20*getVelocity().distance(dir); // velocity is in meters per tick, there are 20 ticks in 1 second
		double fuelNeeded = 0.5 * (mass + fuel * fuelMass) * diff * diff / (Math.max(engineEfficiency, 0.0001f)*0.5f);
		//System.out.println("Adding velocity " + diff + " " + fuelNeeded + "/" + fuel + "  " + dir.getX() + " " + dir.getY() + " " + dir.getZ());
		if (fuelNeeded < fuel) {
			fuel -= fuelNeeded;
			vx += dir.getX();
			vy += dir.getY();
			vz += dir.getZ();
			//Location loc = Main.standEntities.get(tag).getEyeLocation();
			//shootParticle(loc.toVector().add(getUp(loc).multiply(0.6f)).add(dir.multiply(-2)), dir.multiply(-1), Particle.FLAME, thrust*100);
		} else {
			Player rider = (Player)Main.standEntities.get(tag).getPassenger();
			rider.sendMessage("Not enough fuel: " + fuelNeeded + "/" + fuel);
			//System.out.println("Not enough fuel: " + fuelNeeded);
		}
	}
	
	public Vector getUp(Location loc) {
		Location loc2 = loc.clone();
		loc2.setPitch(loc2.getPitch()-90);
		return loc2.getDirection();
	}
	
	public void initRefs(Spaceship ship) {
		this.ship = ship;
	}

	public void setupShit() {
		switch (type) {
		case TIE_FIGHTER:
			engineEfficiency = 0.9f;
			customModelData = 1;
			turnSpeed = 7; // very fast maneuverability
			mass = 10000;
			fuelMass = 5*Math.pow(10,-8); // TIE Fighters have ion engines which run on electricity, but they still use some fuel
			thrust = 0.008;
			vehicle = true;
			turret = false;
			radius = 4;
			break;
		case MEDIUM_TURRET:
			customModelData = 2;
			turnSpeed = 2;
			thrust = 0;
			turret = true;
			vehicle = false;
			engineEfficiency = 1.5f;
			break;
		case DALEK_DRONE:
			customModelData = 3;
			turnSpeed = 4;
			thrust = 0.006;
			turret = false;
			vehicle = true;
			drone = true;
			engineEfficiency = 15f;
			ride = false;
			mass = 3129;
			flyFlight = true;
			flySpeed = 0.018f;
			lookUpAndDown = false;
			break;
		}
	}

	public void refuel() {
		switch (type) {
		case TIE_FIGHTER:
			fuel = Math.pow(10, 12);
			air = 0;
			break;
		case MEDIUM_TURRET:
			break;
		case DALEK_DRONE:
			fuel = 100;
			break;
		}
	}

	public static String[] typeNames = new String[] {
		"Tie Fighter",
		"Escape Pod",
		"Escape Pod Gun",
		"Small Turret",
		"Medium Turret",
		"Dalek Drone",
	};
	
	public static boolean isEntityTag(String s) {
		if (s.contains("entity-"))
			return true;
		for (String t : typeNames)
			if (s.contains(t.toLowerCase().replace(' ', '_') + "-"))
				return true;
		return false;
	}
	
	public String getName() {
		if (type < typeNames.length)
			return typeNames[type];
		return "";
	} //t = sqrt(2d/a)

	public void save() {
		try {
			(new File(world + "/VoidShips")).mkdirs();
			(new File(world + "/VoidShips/" + tag + ".dat")).createNewFile();
			FileOutputStream fos = new FileOutputStream(world + "/VoidShips/" + tag + ".dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			// write object to file
			oos.writeObject(this);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void slowDown() {
		Vector vel = getVelocity().multiply(-1);
		if (vel.length() > 0.01) {
			Location loc = new Location(Bukkit.getWorld(world), x, y, z);
			loc.setDirection(vel.normalize());
			if (angleSubtract(pitch, loc.getPitch()) > 0.01 || angleSubtract(yaw, loc.getYaw()) > 0.01)
				setTargetDirection(loc.getPitch(), loc.getYaw());
			else {
				doThrust(1, 0, 0);
			}
		} else {
			vx = 0;
			vy = 0;
			vz = 0;
		}
	}
	
	public void load(String filepath) {
		try (FileInputStream fis = new FileInputStream(filepath); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// write object to file
			SpecialEntity yeet = (SpecialEntity) ois.readObject();
			x = yeet.x;
			y = yeet.y;
			z = yeet.z;
			/*
			 * dirX = yeet.dirX; dirY = yeet.dirY; dirZ = yeet.dirZ; dirX = yeet.tDirX; dirY
			 * = yeet.tDirY; dirZ = yeet.tDirZ;
			 */
			pitch = yeet.pitch;
			tPitch = yeet.tPitch;
			yaw = yeet.yaw;
			tYaw = yeet.tYaw;
			sysX = yeet.sysX;
			sysY = yeet.sysY;
			sysZ = yeet.sysZ;
			ship = yeet.ship;
			type = yeet.type;
			world = yeet.world;
			turnSpeed = yeet.turnSpeed;
			tag = yeet.tag;
			turret = yeet.turret;
			vehicle = yeet.vehicle;
			fuel = yeet.fuel;
			comms = yeet.comms;
			//fuel = Math.pow(10, 12);
			vx = 0;
			vy = 0;
			vz = 0;
			passengers = yeet.passengers;
			pilot = yeet.pilot;
			flySpeed = yeet.flySpeed;
			air = yeet.air;
			setupShit();

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTargetDirection(float pitch, float yaw) {
		// TODO Auto-generated method stub
		tPitch = pitch;
		tYaw = yaw;
	}

	public Vector getVelocity() {
		// TODO Auto-generated method stub
		return new Vector(vx, vy, vz);
	}
	
	public Vector getDir() {
		Location loc = Main.standEntities.get(tag).getLocation().clone();
		loc.setPitch(pitch);
		loc.setYaw(yaw);
		return loc.getDirection();
		//return Main.standEntities.get(tag).getEyeLocation().getDirection();
		//return null;
	}

	public void doThrust(int forward, int up, int left) {
		if (turret)
			return;
		Vector dir = getDir().normalize();
		//System.out.println("Thrusting " + forward + " " + up + " " + left);
		addVelocity(dir.multiply(forward*thrust));
	}

}
