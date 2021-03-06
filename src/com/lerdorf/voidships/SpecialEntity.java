package com.lerdorf.voidships;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

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

	String world;
	public double turnSpeed;
	public String tag;

	public boolean turret = false;
	public boolean vehicle = false;

	public int customModelData = -1;

	public double fuel = 0; // joules
	public double fuelMass = 0; // kg per joule
	public float engineEfficiency = 0f; // 1 means exactly half of the energy spent gets converted into kinetic energy
	public int radius; // m
	public int mass; // kg
	public double thrust; // m / tick^2

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

	double clamp(double a, double b, double c) {
		return Math.min(Math.max(b, c), Math.max(a, Math.min(b, c)));
	}

	double delYaw = 0;
	double delPitch = 0;

	public void update() {
		ArmorStand v = Main.standEntities.get(tag);
		if (v != null) {
			LivingEntity p = (LivingEntity)v.getPassenger();
			if (p != null)
				setTargetDirection(p.getEyeLocation().getPitch(), p.getEyeLocation().getYaw());
			else if (vehicle)
				slowDown();
			double newDelPitch = clamp(tPitch - pitch, -turnSpeed, turnSpeed);
			double newDelYaw = clamp(tYaw - yaw, -turnSpeed, turnSpeed);
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
			v.setVelocity(getVelocity());
		} else {
			System.out.println("ERROR: LIVING ENTITY IS NULL FOR " + tag);
		}
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
			break;
		case MEDIUM_TURRET:
			customModelData = 2;
			turnSpeed = 2;
			thrust = 0;
			turret = true;
			vehicle = false;
			engineEfficiency = 1.5f;
			break;
		}
	}

	public void refuel() {
		switch (type) {
		case TIE_FIGHTER:
			fuel = Math.pow(10, 12);
			break;
		case MEDIUM_TURRET:
			break;
		}
	}

	public String getName() {
		switch (type) {
		case TIE_FIGHTER:
			return "Tie Fighter";
		case ESCAPE_POD:
			return "Escape Pod";
		case ESCAPE_POD_GUN:
			return "Escape Pod Gun";
		case SMALL_TURRET:
			return "Small Turret";
		case MEDIUM_TURRET:
			return "Medium Turret";
		}
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
			if (Math.abs(pitch - loc.getPitch()) > 0.01 || Math.abs(yaw - loc.getYaw()) > 0.01)
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
			//fuel = Math.pow(10, 12);
			vx = 0;
			vy = 0;
			vz = 0;
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
