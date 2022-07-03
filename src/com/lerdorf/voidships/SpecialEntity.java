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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class SpecialEntity implements Serializable {

	public int x, y, z;
	public int vx, vy, vz; // m / tick
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
	}

	public SpecialEntity(Location loc, int type, Spaceship ship) {
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.ship = ship;
		this.type = type;
		setupShit();
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
		LivingEntity v = Main.livingEntities.get(tag);
		if (v != null) {
			double newDelPitch = clamp(tPitch - pitch, -turnSpeed, turnSpeed);
			double newDelYaw = clamp(tYaw - yaw, -turnSpeed, turnSpeed);
			double fuelNeeded = 0.5 * (0.4 * (mass + fuel * fuelMass) * radius * radius) * 0.01745329* Math.pow(Math.abs(newDelPitch - delPitch) + Math.abs(newDelYaw - delYaw), 2);
			if (fuelNeeded < fuel) {
				fuel -= fuelNeeded;
				delYaw = newDelYaw;
				delPitch = newDelPitch;
			}
			yaw += delYaw;
			pitch += delPitch;
			v.teleport(new Location(v.getWorld(), v.getLocation().getX(), v.getLocation().getY(),
					v.getLocation().getZ(), pitch, yaw));
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
			
			v.setVelocity(getVelocity());
		}
	}

	public void addVelocity(Vector dir) {
		dir = dir.normalize().multiply(thrust);
		double diff = 20*getVelocity().distance(dir); // velocity is in meters per tick, there are 20 ticks in 1 second
		double fuelNeeded = 0.5 * (mass + fuel * fuelMass) * diff * diff;
		if (fuelNeeded < fuel) {
			fuel -= fuelNeeded;
			vx += dir.getX();
			vy += dir.getY();
			vz += dir.getZ();
		}
	}
	
	public void initRefs(Spaceship ship) {
		this.ship = ship;
	}

	public void setupShit() {
		switch (type) {
		case TIE_FIGHTER:
			customModelData = 1;
			turnSpeed = 5;
			mass = 10000;
			break;
		case MEDIUM_TURRET:
			customModelData = 2;
			turnSpeed = 2;
			break;
		}
	}

	public void refuel() {
		switch (type) {
		case TIE_FIGHTER:
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

}
