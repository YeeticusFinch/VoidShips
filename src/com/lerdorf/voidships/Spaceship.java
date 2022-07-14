package com.lerdorf.voidships;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Spaceship implements Serializable {

	private static final long serialVersionUID = 3925048207418928199L;
	double x, y, z;
	Void space;
	CosmicBody orbiting;
	SolarSystem system;
	int sx, sy, sz;
	String world;
	String name;
	SpecialBlock[] airTanks;
	SpecialBlock[] blocks;
	SpecialEntity[] entities;
	String filepath;
	
	// -n [name], -m [mass], -nsn [nav systems name], -ssn [sec systems name], -wsn [weapon systems name], -dsn [defense systems name], -acn [atmosphere control name], -sn [scanner name], -cdn [cleanup debris name]
	double mass;
	String knickname;
	String nsn; // nav systems name
	String ssn; // sec systems name
	String wsn; // Weapon systems name
	String dsn; // Defense systems name
	String acn; // Atmosphere control name
	String sn; // Scanner name
	String cdn; // cleanup debris name
	
	
	public Spaceship(String name, double x, double y, double z, Void space, CosmicBody orbiting, SolarSystem system, int sx, int sy, int sz, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.space = space;
		this.orbiting = orbiting;
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
		this.name = name;
		this.world = world;
		this.system = system;
	}

	public Spaceship(String filename) {
		load(filename);
	}

	public Location getSpawnLoc() {
		return new Location(Bukkit.getWorld(world), sx, sy, sz);
	}

	public void initRefs() {
		if (system != null)
			system = Main.getSystem( system.getName() );
		if (orbiting != null && system.planets != null) {
			for (CosmicBody p : system.planets) {
				if (p != null && ((p.name != null && orbiting.name != null && p.name == orbiting.name) || (p.knickname != null && orbiting.knickname != null && p.knickname == orbiting.knickname) || (p.id != null && orbiting.id != null && p.id == orbiting.id))) {
					orbiting = p;
				}
			}
		}
		if (airTanks != null)
			for (SpecialBlock b : airTanks)
				b.initRefs();

		if (blocks != null)
			for (SpecialBlock b : blocks)
				b.initRefs();
		
		if (entities != null)
			for (SpecialEntity e : entities)
				e.initRefs(this);
	}
	
	public void save(String filename) {
		try {
			(new File(world + "/VoidShips")).mkdirs();
			(new File(world + "/VoidShips/" + filename)).createNewFile();
			FileOutputStream fos = new FileOutputStream(world + "/VoidShips/" + filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			// write object to file
			oos.writeObject(this);
			filepath = world + "/VoidShips/" + filename;

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void save() {
		save(filepath.substring(filepath.lastIndexOf('/')+1));
	}
	
	public void load(String filepath) {
		try (FileInputStream fis = new FileInputStream(filepath); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// write object to file
			Spaceship yeet = (Spaceship) ois.readObject();
			x = yeet.x;
			y = yeet.y;
			z = yeet.z;
			sx = yeet.sx;
			sy = yeet.sy;
			sz = yeet.sz;
			world = yeet.world;
			space = yeet.space;
			orbiting = yeet.orbiting;
			system = yeet.system;
			name = yeet.name;
			airTanks = yeet.airTanks;
			blocks = yeet.blocks;
			this.filepath = filepath;
			entities = yeet.entities;
			
			mass = yeet.mass;
			nsn = yeet.nsn; // nav systems name
			ssn = yeet.ssn; // sec systems name
			wsn = yeet.wsn; // Weapon systems name
			dsn = yeet.dsn; // Defense systems name
			acn = yeet.acn; // Atmosphere control name
			sn = yeet.sn; // Scanner name
			cdn = yeet.cdn; // cleanup debris name
			
			//initRefs();

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SpecialBlock[] getBlocksOfType(int type) {
		ArrayList<SpecialBlock> r = new ArrayList<SpecialBlock>();
		
		for (SpecialBlock e : blocks)
			if (e.type == type)
				r.add(e);
		
		SpecialBlock[] yeet = new SpecialBlock[r.size()];
		
		yeet = r.toArray(yeet);
		return yeet;
	}
	
	public void removeAir(int amount) {
		for (int i = 0; i < airTanks.length && amount > 0; i++) {
			airTanks[i].air -= amount;
			amount = -airTanks[i].air;
			airTanks[i].air = Math.min(airTanks[i].air, 0);
		}
	}

	public void addAir(int amount) {
		for (int i = 0; i < airTanks.length && amount > 0; i++) {
			if (airTanks[i].dead)
				continue;
			airTanks[i].air = airTanks[i].air + amount;
			amount = airTanks[i].air - 1000;
			airTanks[i].air = Math.min(airTanks[i].air, 1000);
		}
	}

	public int countAir() {
		int r = 0;
		for (int i = 0; i < airTanks.length; i++) {
			r += airTanks[i].air;
		}
		return r;
	}

	public void removeFuel(float amount) {
		for (int i = 0; i < airTanks.length && amount > 0; i++) {
			if (blocks[i].type != SpecialBlock.FUEL_TANK) continue;
			blocks[i].fuel -= amount;
			amount = -blocks[i].fuel;
			blocks[i].fuel = Math.max(blocks[i].fuel, 0);
		}
	}

	public void addFuel(float amount) {
		for (int i = 0; i < airTanks.length && amount > 0; i++) {
			if (blocks[i].type == SpecialBlock.FUEL_TANK) continue;
			if (blocks[i].dead)
				continue;
			blocks[i].fuel = blocks[i].fuel + amount;
			amount = blocks[i].fuel - (float)Math.pow(20,12);
			blocks[i].fuel = Math.min(blocks[i].fuel, (float)Math.pow(20,12));
		}
	}
	
	public float countFuel() {
		int r = 0;
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i].type == SpecialBlock.FUEL_TANK)
				r += blocks[i].fuel;
		}
		return r;
	}
	
	public String displayFuel() {
		return CarlMath.withPrefix(countFuel()) + " joules";
	}
	
	public void addBlock(SpecialBlock block) {
		SpecialBlock[] yeet;
		if (block.type == SpecialBlock.AIR_TANK) {
			if (airTanks != null) {
				yeet = new SpecialBlock[airTanks.length+1];
				for (int i = 0; i < airTanks.length; i++)
					yeet[i] = airTanks[i];
				yeet[airTanks.length] = block;
			} else yeet = new SpecialBlock[] { block };
			airTanks = yeet;
		} else {
			if (blocks != null) {
				yeet = new SpecialBlock[blocks.length+1];
				for (int i = 0; i < blocks.length; i++)
					yeet[i] = blocks[i];
				yeet[blocks.length] = block;
			} else yeet = new SpecialBlock[] { block };
			blocks = yeet;
		}
	}
	
	public void delBlock(int index) {
		if (index >= blocks.length)
			return;
		SpecialBlock[] yeet = new SpecialBlock[blocks.length-1];
		for (int i = 0; i < yeet.length; i++) {
			if (i >= index)
				yeet[i] = blocks[i+1];
			else
				yeet[i] = blocks[i];
		}
		blocks = yeet;
	}
	
	public void delAirTank(int index) {
		if (index >= airTanks.length)
			return;
		SpecialBlock[] yeet = new SpecialBlock[airTanks.length-1];
		for (int i = 0; i < yeet.length; i++) {
			if (i >= index)
				yeet[i] = airTanks[i+1];
			else
				yeet[i] = airTanks[i];
		}
		airTanks = yeet;
	}
	
	public void addEntity(SpecialEntity entity) {
		SpecialEntity[] yeet;
		if (true) {
			if (entities != null) {
				yeet = new SpecialEntity[entities.length+1];
				for (int i = 0; i < entities.length; i++)
					yeet[i] = entities[i];
				yeet[entities.length] = entity;
			} else yeet = new SpecialEntity[] { entity };
			entities = yeet;
		}
	}
	
	public void delEntity(int index) {
		if (index >= entities.length)
			return;
		SpecialEntity[] yeet = new SpecialEntity[entities.length-1];
		for (int i = 0; i < yeet.length; i++) {
			if (i >= index)
				yeet[i] = entities[i+1];
			else
				yeet[i] = entities[i];
		}
		entities = yeet;
	}
	
	public void getDistance(CosmicBody body) {
		//if (body.equals(orbiting))
		//	return orbitDistance;
		
	}
}
