package com.lerdorf.voidships;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SolarSystem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 106279994250822897L;
	CosmicBody sun;
	CosmicBody[] planets;
	String world;

	double lyx; // coords in light-years
	double lyy;
	double lyz;

	double whX, whY, whZ;
	
	public SolarSystem(CosmicBody sun, double lyx, double lyy, double lyz, CosmicBody[] planets, int[] distances, String world) {
		this.sun = sun;
		if (planets != null) {
			for (int i = 0; i < planets.length; i++) {
				double angle = Math.random() * 2 * Math.PI; // Let's just put it at a random spot on it's orbital trajectory
				planets[i].x = (int) (distances[i] * Math.cos(angle));
				planets[i].y = (int) (distances[i] * Math.sin(angle));
				double a = planets[i].gravAccel(sun)[3]; // Get magnitude of acceleration due to gravity
				double v = Math.sqrt(1000f * distances[i] * a); // Calculate orbital velocity based on distance
				planets[i].vx = -(v * Math.sin(angle));
				planets[i].vy = (v * Math.cos(angle));
				planets[i].orbiting = sun;
				planets[i].orbitDist = distances[i];
			}
		}
		this.planets = planets;
		this.lyx = lyx;
		this.lyy = lyy;
		this.lyz = lyz;
		this.world = world;
		whX = 57909050+1383530000*(Math.random()-0.5);
		whY = 57909050+1383530000*(Math.random()-0.5);
		whZ = 57909050+1383530000*(Math.random()-0.5);
		//this.sun.world = world;
	}
	
	public String getName() {
		if (sun != null) {
			if (sun.knickname != null)
				return sun.knickname;
			else if (sun.name != null)
				return sun.name;
			else if (sun.id != null)
				return sun.id;
		}
		return ":unnamed star:";
	}

	public SolarSystem(String filepath) {
		load(filepath);
	}
	
	public void initRefs() {
		sun.orbiting = null;
		if (planets != null && planets.length > 0)
			for (CosmicBody p : planets)
				if (p != null)
					p.orbiting = sun;
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
			SolarSystem yeet = (SolarSystem) ois.readObject();
			sun = yeet.sun;
			planets = yeet.planets;
			world = yeet.world;
			lyx = yeet.lyx;
			lyy = yeet.lyy;
			lyz = yeet.lyz;
			whX = yeet.whX;
			whY = yeet.whY;
			whZ = yeet.whZ;
			if (whX == 0 && whY == 0 && whZ == 0) {
				whX = 57909050+1383530000*(Math.random()-0.5);
				whY = 57909050+1383530000*(Math.random()-0.5);
				whZ = 57909050+1383530000*(Math.random()-0.5);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addPlanet(CosmicBody p) {
		CosmicBody[] newPlanets;
		double angle = Math.random() * 2 * Math.PI; // Let's just put it at a random spot on it's orbital trajectory
		p.x = (p.orbitDist * Math.cos(angle));
		p.y = (p.orbitDist * Math.sin(angle));
		double a = p.gravAccel(sun)[3]; // Get magnitude of acceleration due to gravity
		double v = Math.sqrt(1000f * p.orbitDist * a); // Calculate orbital velocity based on distance
		p.vx = -(v * Math.sin(angle));
		p.vy = (v * Math.cos(angle));
		p.orbiting = sun;
		if (planets == null) {
			System.out.println("No planets, creating new array with planet " + p.name);
			newPlanets = new CosmicBody[1];
			newPlanets[0] = p;
		} else {
			System.out.println("Adding planet " + p.name);
			newPlanets = new CosmicBody[planets.length + 1];
			for (int i = 0; i < planets.length; i++)
				newPlanets[i] = planets[i];
			newPlanets[newPlanets.length - 1] = p;
		}
		planets = newPlanets;
	}
	
	public void delPlanet(int index) {
		if (index >= planets.length)
			return;
		CosmicBody[] newPlanets = new CosmicBody[planets.length-1];
		for (int i = 0; i < newPlanets.length; i++) {
			if (i >= index)
				newPlanets[i] = planets[i+1];
			else
				newPlanets[i] = planets[i];
		}
		planets = newPlanets;
	}

}
