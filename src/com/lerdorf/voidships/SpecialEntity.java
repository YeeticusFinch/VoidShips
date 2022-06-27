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

	public static final int AIR_DETECTOR = 0;

	String world;

	public SpecialEntity(Entity entity, int type, Spaceship ship) {
		Location loc = entity.getLocation();
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.ship = ship;
	}

	public SpecialEntity(String filepath) {
		load(filepath);
	}

	public void update() {
		switch (type) {
			case AIR_DETECTOR:
				Location[] locs = {
						new Location(Bukkit.getWorld(world), x+1, y, z),
						new Location(Bukkit.getWorld(world), x-1, y, z),
						new Location(Bukkit.getWorld(world), x, y+1, z),
						new Location(Bukkit.getWorld(world), x, y-1, z),
						new Location(Bukkit.getWorld(world), x, y, z+1),
						new Location(Bukkit.getWorld(world), x, y, z-1)
				};
				break;
		}
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

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
