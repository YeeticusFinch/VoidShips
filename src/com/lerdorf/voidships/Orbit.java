package com.lerdorf.voidships;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.event.block.Action;

class Orbit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -445295528426863004L;
	public CosmicBody body;
	public double radius; // km
	public double period; // hours per year
	public double[] axis; // [x, y, z] unit vector
	public double offset; // radians
	public static final int DIRECT = 0;
	public static final int OBERTH = 1;
	public static final int HOHMANN = 2;
	public static final int HOHMANN_IDEAL = 3;

	public Orbit(CosmicBody body, double radius, double period, double[] axis) {
		this.body = body;
		this.radius = radius;
		this.period = period;
		this.axis = axis;
	}

	public Orbit(CosmicBody body, double radius, double[] axis) {
		this.body = body;
		this.radius = radius;
		this.axis = axis;
		calculatePeriod();
	}
	
	// Travel to a new offset within the same orbit
	// [total deltaV, total time, acceleration time, cruising time, acceleration time]
	public double[] deltaVOrbitTraverse(double newOffset, double acceleration) { // acceleration in m/s^s
		// g = w*w * r
		// a = @ * r
		// Each orbit has a certain velocity required to maintain the orbit
		// If the velocity is too small, the ship will fall towards the planet
		// If the velocity is too large, the ship will fall away from the planet
		// The only way to allow for a different velocity at that orbit radius is to change the acceleration
		// The fastest a spaceship can travel around an orbit will be when the spaceship is pointing towards the celestial body with full thrust
		// The slowest is when the spaceship is pointing away from the celestial body and directly countering the gravity
		// Because of this, the ship can't do maximum acceleration, it averages to about half, so that's what we'll use
		// g + t = w*w * r
		// w = sqrt( (g + t) / r ) // Max angular velocity
		// w = Max(0, sqrt( g - t ) / r) // Min angular velocitty
		acceleration *= 3600*3600 / 1000; // convevrt to km / h^2 
		double alpha = 0.5 * acceleration / radius; // average angular acceleration
		double dist = angleDiff2(newOffset, offset); // shortest path
		double g = body.gravAccel(radius) * 3600*3600 / 1000; // gravity in km / h^2
		double w = Math.sqrt(g/radius); // current rotational velocity (rad/h)
		double maxW = Math.sqrt(g + acceleration) / radius; // maximum rotational velocity (rad/h)
		double accelTime = (maxW - w) / alpha; // hours
		double accelDist = 0.5*(maxW - w) * accelTime; // angular distance traveled during that time
		if (2*accelDist >= Math.abs(dist)) { // can't accelerate to full speed, distances are too close
			double fraction = dist / (2*accelDist);
			accelTime *= fraction;
			return new double[] {2*fraction*Math.abs(maxW-w), 2*accelTime, accelTime, 0, accelTime};
		}
		double cruiseDist = Math.abs(dist) - 2*accelDist;
		double cruiseTime = 0;
		if (Math.signum(dist) == Math.signum(axis[2])) // same direction
			cruiseTime = cruiseDist / (maxW - w);
		else
			cruiseTime = cruiseDist / (maxW + w); // opposite direction
		return new double[] { 2*Math.abs(maxW-w), 2*accelTime+cruiseTime, accelTime, cruiseTime, accelTime };
	}
	
	// Changes the direction of the orbit
	// [deltaV, time]
	public double[] deltaVSetDirection(int dir, double acceleration) {
		if (Math.signum(dir) == Math.signum(axis[2]))
			return new double[] {0,0};
		double alpha = 0.5 * acceleration / radius;
		double g = body.gravAccel(radius) * 3600*3600 / 1000;
		double w = Math.sqrt(g/radius);
		double accelTime = 2*w/alpha;
		return new double[] {2*w, accelTime};
	}
	
	public double[] deltaVInterplanetary(Orbit o, double acceleration, int transferType) {
		Orbit primaryOrbit = this;
		Orbit secondaryOrbit = o;
		if (body.orbiting != null)
			primaryOrbit = new Orbit(body.orbiting, body.orbitDist, body.orbitPeriod*24, new double[] {0,0,1});
		if (o.body.orbiting != null)
			secondaryOrbit = new Orbit(o.body.orbiting, o.body.orbitDist, o.body.orbitPeriod*24, new double[] {0,0,1});
		return deltaV(primaryOrbit, secondaryOrbit, acceleration, transferType);
	}
	
	// time is in hours, speed is in km/h
	// [total deltaV, total time, time before launch, acceleration time, cruising time, acceleration time]
	public double[] deltaV(Orbit a, Orbit b, double acceleration, int transferType) { // acceleration in m/s^2
		Orbit primaryOrbit = a;
		Orbit secondaryOrbit = b;
		acceleration *= 3600*3600 / 1000; // convert to km / h^2
		double u = primaryOrbit.getGravParam();
		if (transferType == HOHMANN) { // r1 = small orbit, r2 = big orbit, 
			// dist = kilometer
			// time = hour
			// speed = km / h
			// angle = radian
			double dV1 = Math.sqrt(u/primaryOrbit.radius) * ( Math.sqrt( (2*b.body.orbitDist) / (primaryOrbit.radius+b.body.orbitDist) ) - 1 );
			double dV2 = Math.sqrt(u/b.body.orbitDist) * ( 1 - Math.sqrt( (2*primaryOrbit.radius) / (primaryOrbit.radius+b.body.orbitDist) ) );
			double tH = Math.PI * Math.sqrt( Math.pow(primaryOrbit.radius + b.body.orbitDist,3) );
			
		} else if (transferType == HOHMANN_IDEAL) {
			double dV1 = Math.sqrt(u/primaryOrbit.radius) * ( Math.sqrt( (2*b.body.orbitDist) / (primaryOrbit.radius+b.body.orbitDist) ) - 1 );
			double dV2 = Math.sqrt(u/b.body.orbitDist) * ( 1 - Math.sqrt( (2*primaryOrbit.radius) / (primaryOrbit.radius+b.body.orbitDist) ) );
			double tH = Math.PI * Math.sqrt( Math.pow(primaryOrbit.radius + b.body.orbitDist,3) );
			double timeUntilOpposite = angleDiff(Math.PI, primaryOrbit.angularDiff(secondaryOrbit)) / primaryOrbit.getAngularSpeed(secondaryOrbit);
			double temp = 0;
			while (tH > timeUntilOpposite + temp)
				temp += timeUntilOpposite;
			timeUntilOpposite += temp;
			double launchTime = timeUntilOpposite - tH; // how many hours prior to launch
			return new double[] {Math.abs(dV1)+Math.abs(dV2), timeUntilOpposite, dV1 / acceleration, tH, dV2 / acceleration};
		}
		return null;
	}
	
	public void calculatePeriod() {
		period = 2*Math.PI / Math.sqrt( body.gravAccel(radius)*3.6 / (radius*1000) );
	}
	
	public double angleDiff(double target, double current) {
		if (target > current)
			return target - current;
		else if (current > target)
			return 2*Math.PI - current % (Math.PI*2) + target;
		else return 0;
	}
	
	public double angleDiff2(double target, double current) {
		double a = target - current;
		double b = 2*Math.PI - current % (Math.PI*2) + target;
		if (Math.abs(a) < Math.abs(b))
			return a;
		return b;
	}
	
	public double angularDiff(Orbit o) {
		//double b = o.getAngle();
		//double a = getAngle();
		return angleDiff(o.getAngle(), getAngle());
		//return Math.min(b-a, Math.min(b-(a+Math.PI*2), b-(a-Math.PI*2)));
	}
	
	// Gets angular speed of a body in orbit o relative to angular speed of this orbit (rad/h)
	public double getAngularSpeed(Orbit o) {
		return o.getAngularSpeed() - getAngularSpeed();
	}
	
	/* 
	 a = r * w*w
	 w = sqrt( a / r )
	 p = 2*PI / w
	 w = 2*PI / p
	 */
	// Gets angular speed of this orbit (in radians per hour)
	public double getAngularSpeed() {
		//return Math.sqrt( body.gravAccel(radius)*3.6 / (radius*1000) );
		return 2*Math.PI / period;
	}
	
	public double getAngle() {
		return getAngle(LocalDateTime.now().toLocalTime().toSecondOfDay() / 86400);
	}
	
	public double getAngle(double t) { // the current time in seconds
		double time = t / 86400;
		return (offset + 2*Math.PI*time / period)%(2*Math.PI);
	}

	public double getNetVelocity() {
		return -1;
	}

	public double getEscapeVelocity() {
		return getNetVelocity() * Math.sqrt(2);
	}
	
	public double getGravParam() {
		return CosmicBody.G * body.mass;
	}
}
