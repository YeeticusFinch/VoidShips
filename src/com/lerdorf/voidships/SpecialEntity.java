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
import org.bukkit.util.Vector;

public class SpecialEntity implements Serializable {

	public int x, y, z;
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

	public SpecialEntity(Entity entity, int type, Spaceship ship) {
		Location loc = entity.getLocation();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.ship = ship;
		this.type = type;
	}
	
	public SpecialEntity(Location loc, int type, Spaceship ship) {
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.ship = ship;
		this.type = type;
	}

	public SpecialEntity(String filepath) {
		load(filepath);
	}

	public void update() {
		
	}

	public void initRefs(Spaceship ship) {
		this.ship = ship;
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
			ship = yeet.ship;
			type = yeet.type;
			world = yeet.world;
			turnSpeed = yeet.turnSpeed;
			tag = yeet.tag;
			turret = yeet.turret;
			vehicle = yeet.vehicle;

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTargetDirection(Vector f) {
		// TODO Auto-generated method stub
		
	}

	public Vector getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

}
