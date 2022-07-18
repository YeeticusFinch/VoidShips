package com.lerdorf.voidships;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.bukkit.Material;

//import org.bukkit.Location;

public class CosmicBody implements Serializable {

	private static final long serialVersionUID = -8222806095761827109L;
	public String name = null;
	public String knickname = null;
	public String type = null;
	public double x, y, z; // km
	public double mass; // kg
	public double radius; // km
	public double orbitDist; // km
	public double orbitPeriod; // days per year
	public boolean habitable = false;
	public CosmicBody orbiting;
	long prevTime = 0;
	public double vx, vy, vz; // m/s 1 hour or 3600 seconds ==> 50 seconds, multiply real world time by
								// 0.01389f
	String id = null;

	public static final float TIME_SCALE = 0.01389f;
	public static final float G = 0.000000000066743f; // m^3 kg^-1 s ^-2
	public static final long LIGHT_SPEED = 300000000; // m/s

	public CosmicBody(String name, String type, double x, double y, double z, double mass, double radius, CosmicBody orbiting, double orbitDist) {
		this.name = name;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.mass = mass;
		this.radius = radius;

		this.orbiting = orbiting;
		this.orbitDist = orbitDist;

		prevTime = System.currentTimeMillis();
	}

	public CosmicBody(String name, String knickname, String type, String id, double x, double y, double z, double mass, double radius, CosmicBody orbiting, double orbitDist) {
		this.name = name;
		this.knickname = knickname;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.mass = mass;
		this.radius = radius;

		this.id = id;
		this.orbiting = orbiting;
		this.orbitDist = orbitDist;

		prevTime = System.currentTimeMillis();
	}

	public CosmicBody(String name, String knickname, String type, double x, double y, double z, double mass, double radius, double orbitDist, CosmicBody orbiting, double orbitPeriod, boolean habitable) {
		this.name = name;
		this.knickname = knickname;
		//this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.mass = mass;
		this.radius = radius;
		this.orbiting = orbiting;
		this.orbitDist = orbitDist;
		this.habitable = habitable;
		this.orbitPeriod = orbitPeriod;
		this.type = type;
		prevTime = System.currentTimeMillis();
	}

	public CosmicBody(String name, String type, double x, double y, double z, double mass, double radius) {
		this.name = name;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.mass = mass;
		this.radius = radius;

		this.orbiting = null;
		this.orbitDist = -1;
		vx = 0;
		vy = 0;
		vz = 0;

		prevTime = System.currentTimeMillis();
	}
	
	public int[] getColor() {
		
		if (type.indexOf("Red") != -1 || type.indexOf("red") != -1)
			return new int[] {255, 50, 50};
		if (type.indexOf("Orange") != -1 || type.indexOf("orange") != -1)
			return new int[] {255, 150, 150};
		if (type.indexOf("Yellow") != -1 || type.indexOf("yellow") != -1)
			return new int[] {255, 255, 50};
		if (type.indexOf("White") != -1 || type.indexOf("white") != -1)
			return new int[] {255, 255, 255};
		if (type.indexOf("Blue") != -1 || type.indexOf("blue") != -1)
			return new int[] {50, 50, 255};
		if (type.indexOf("Black") != -1 || type.indexOf("black") != -1)
			return new int[] {10, 10, 10};
		if (type.indexOf("Star") != -1 || type.indexOf("star") != -1)
			return new int[] {255, 255, 155};
		
		return new int[] {155, 155, 255};
	}
	
	public void orbit() { // 1.2 * Math.pow(10,6) milliseconds in a day
		//double time = System.currentTimeMillis()/(1.2*Math.pow(10,1));
		if (orbiting == null || orbitDist == -1)
			return;
		double time = System.currentTimeMillis() / 86400000;
		x = orbitDist * Math.cos(2*Math.PI*(radius+time) / orbitPeriod);
		y = orbitDist * Math.sin(2*Math.PI*(radius+time) / orbitPeriod);
		z = 0;
		//System.out.println(orbitDist + " * cos(" + 2*Math.PI*(time) / orbitPeriod + ") = " + x);
		//System.out.println(name + " " + knickname + " " + x + " " + y);
		//System.out.println("Current time " + System.currentTimeMillis() + " " + LocalDateTime.now().toLocalTime().toSecondOfDay() + " " + time + " " + x + " " + y);
	}
	
	public void orbit(long t) { // 1.2 * Math.pow(10,6) milliseconds in a day
		//double time = System.currentTimeMillis()/(1.2*Math.pow(10,1));
		if (orbiting == null || orbitDist == -1)
			return;
		double time = t / 86400;
		x = orbitDist * Math.cos(2*Math.PI*(radius+time) / orbitPeriod); // the radius is just to add noise
		y = orbitDist * Math.sin(2*Math.PI*(radius+time) / orbitPeriod);
		z = 0;
		//System.out.println(orbitDist + " * cos(" + 2*Math.PI*(time) / orbitPeriod + ") = " + x);
		//System.out.println(name + " " + knickname + " " + x + " " + y);
		//System.out.println("Current time " + System.currentTimeMillis() + " " + LocalDateTime.now().toLocalTime().toSecondOfDay() + " " + time + " " + x + " " + y);
	}

	// Depricated
	public void update() {
		long time = System.currentTimeMillis();
		long deltaTime = time - prevTime;
		prevTime = time;
		if (orbiting != null) {
			double[] a = gravAccel(orbiting);
			vx += a[0];
			vy += a[1];
			vz += a[2];
			double dx = (vx * (deltaTime / 1000) / 1000);
			double dy = (vy * (deltaTime / 1000) / 1000);
			double dz = (vz * (deltaTime / 1000) / 1000);
			x += dx;
			y += dy;
			z += dz;
			Main.cosmicUpdate(this, new double[] { dx, dy, dz });
		}
	}

	// m/s^2
	public double[] gravAccel(CosmicBody o) {

		if (o != null) {
			double dx = orbiting.x - x;
			double dy = orbiting.y - y;
			double dz = orbiting.z - z;
			double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

			double a = G * o.mass / (dist * 1000);

			return new double[] { (a * (float) dx / dist), (a * (float) dy / dist), (a * (float) dz / dist), a };
		}

		return null;
	}
	
	// distance in km, returns acceleration due to gravity at such distance from this body
	public double gravAccel(double dist) {
		return G * mass / (dist * 1000); //  m/s^2
	}

	// Speed in m/s, distance in km
	// Returns time in seconds
	public double getTravelTime(double dist, double speed) {
		return dist * 1000 / speed;
	}

	public Material getItem() {
		// TODO Auto-generated method stub
		if (habitable && radius < 10000)
			return Material.SLIME_BALL;
		else if (habitable && radius >= 10000)
			return Material.ENDER_EYE;
		if (radius < 2000)
			return Material.CLAY_BALL;
		if (radius < 3000)
			return Material.SNOWBALL;
		if (radius < 9000)
			return Material.FIRE_CHARGE;
		if (radius > 50000)
			return Material.MAGMA_CREAM;
		if (radius > 20000)
			return Material.HEART_OF_THE_SEA;
		if (radius > 10000)
			return Material.ENDER_PEARL;
		return Material.SNOWBALL;
	}

	public CosmicBody getRootBody() {
		CosmicBody yeet = this;
		int c = 0;
		while (yeet.orbiting != null && c < 50) {
			yeet = yeet.orbiting;
			c++; // tee hee
		}
		return yeet;
	}
	
	public boolean equals(CosmicBody o) {
		if (o.name != null && name != null && !o.name.equals(name))
			return false;
		if (o.knickname != null && knickname != null && !o.knickname.equals(knickname))
			return false;
		if (o.id != null && id != null && !o.id.equals(id))
			return false;
		if (Math.abs(o.mass-mass) > 0.1 || Math.abs(o.radius-radius) > 0.1)
			return false;
		
		return true;
	}
	
	public double getDistance(CosmicBody body) { // The reason this orbit is failing is because the sun has no real position, nor period, can't call .orbit() on the sun
		// TODO Auto-generated method stub
		CosmicBody rootBody = getRootBody();
		CosmicBody otherRootBody = body.getRootBody();
		if (rootBody.equals(otherRootBody)) {

			orbit();
			//System.out.println("Calculating Orbit: " + x + " " + y + " rad= " + radius + " period= " + orbitPeriod);
			body.orbit();
			//System.out.println("Calculating Orbit: " + body.x + " " + body.y + " rad= " + body.radius + " period= " + body.orbitPeriod);
			System.out.println("Distance between " + x + " " + y + " " + z + " and " + body.z + " " + body.y + " " + body.z + " is " + (Math.pow(x-body.x, 2) + Math.pow(y-body.y, 2) + Math.pow(z-body.z, 2)));
			return Math.sqrt( Math.abs(Math.pow(x-body.x, 2) + Math.pow(y-body.y, 2) + Math.pow(z-body.z, 2)) ); // distance in km
		}
		return Main.getSystem(rootBody.getName()).getDistance(Main.getSystem(otherRootBody.getName())); // distance in light years
		//return 0;
	}

	public String getName() {
		// TODO Auto-generated method stub
		if (knickname != null)
			return knickname;
		if (name != null)
			return name;
		if (id != null)
			return id;
		return "UNKNOWN";
	}

}
