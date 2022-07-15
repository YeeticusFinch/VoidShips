package com.lerdorf.voidships;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;

public class Void implements Serializable {

	private static final long serialVersionUID = -6248821444512434093L; //2823073100515015763L
	public int x1, y1, z1, x2, y2, z2;
	public long time; // seconds
	public String world;
	public String filepath;
	public boolean voidWorld = false;
	

	public Void(int x1, int y1, int z1, int x2, int y2, int z2, World world) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
		this.world = world.getName();
	}
	
	public Void(World world) {
		voidWorld = true;
		this.world = world.getName();
	}

	public Void(String filename) {
		load(filename);
	}

	public boolean within(Location loc) {
		return within(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()) && loc.getWorld().getName().equals(world);
	}
	
	public boolean within(String world, int x, int y, int z) {
		if (this.world.equals(world) && (voidWorld || (x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)&& z >= Math.min(z1, z2) && z <= Math.max(z1, z2))))
			return true;
		return false;
	}
	

	public void save() {
		// TODO Auto-generated method stub
		try {
			
			FileOutputStream fos = new FileOutputStream(this.filepath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			// write object to file
			oos.writeObject(this);
			//this.filepath = world+"/VoidShips/" + filename;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void save(String filename) {

		try {
			(new File(world+"/VoidShips")).mkdirs();
			(new File(world+"/VoidShips/" + filename)).createNewFile();
			FileOutputStream fos = new FileOutputStream(world+"/VoidShips/" + filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			// write object to file
			oos.writeObject(this);
			this.filepath = world+"/VoidShips/" + filename;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void load(String filepath) {
		try (FileInputStream fis = new FileInputStream(filepath);
				ObjectInputStream ois = new ObjectInputStream(fis)) {

			// write object to file
			Void yeet = (Void) ois.readObject();
			x1 = yeet.x1;
			x2 = yeet.x2;
			y1 = yeet.y1;
			y2 = yeet.y2;
			z1 = yeet.z1;
			z2 = yeet.z2;
			world = yeet.world;
			this.time = yeet.time;
			if (world == null)
				world = "Space";
			voidWorld = yeet.voidWorld;
			this.filepath = filepath;
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
