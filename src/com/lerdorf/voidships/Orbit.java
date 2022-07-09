package com.lerdorf.voidships;

import java.io.File;
import java.io.FilenameFilter;
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

class Orbit {
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
	
	// [deltaV, total time, time before launch, acceleration time, cruising time, acceleration time]
	public double[] deltaV(Orbit o, double acceleration, int transferType) { // acceleration in m/s^2
		Orbit primaryOrbit = this;
		Orbit secondaryOrbit = o;
		acceleration *= 3600*3600 / 1000; // convert to km / h^2
		if (body.orbiting != null)
			primaryOrbit = new Orbit(body.orbiting, body.orbitDist, body.orbitPeriod*24, new double[] {0,0,1});
		if (o.body.orbiting != null)
			secondaryOrbit = new Orbit(o.body.orbiting, o.body.orbitDist, o.body.orbitPeriod*24, new double[] {0,0,1});
		double u = primaryOrbit.getGravParam();
		if (transferType == HOHMANN) { // r1 = small orbit, r2 = big orbit, 
			// dist = kilometer
			// time = hour
			// speed = km / h
			// angle = radian
			double dV1 = Math.sqrt(u/primaryOrbit.radius) * ( Math.sqrt( (2*o.body.orbitDist) / (primaryOrbit.radius+o.body.orbitDist) ) - 1 );
			double dV2 = Math.sqrt(u/o.body.radius) * ( 1 - Math.sqrt( (2*primaryOrbit.radius) / (primaryOrbit.radius+o.body.orbitDist) ) );
			double tH = Math.PI * Math.sqrt( Math.pow(primaryOrbit.radius + o.body.orbitDist,3) );
			
		} else if (transferType == HOHMANN_IDEAL) {
			double dV1 = Math.sqrt(u/primaryOrbit.radius) * ( Math.sqrt( (2*o.body.orbitDist) / (primaryOrbit.radius+o.body.orbitDist) ) - 1 );
			double dV2 = Math.sqrt(u/o.body.radius) * ( 1 - Math.sqrt( (2*primaryOrbit.radius) / (primaryOrbit.radius+o.body.orbitDist) ) );
			double tH = Math.PI * Math.sqrt( Math.pow(primaryOrbit.radius + o.body.orbitDist,3) );
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
		period = 2*Math.PI / Math.sqrt( body.gravAccel(radius)*3.6 / radius );
	}
	
	public double angleDiff(double target, double current) {
		if (target > current)
			return target - current;
		else if (current > target)
			return 2*Math.PI - current % (Math.PI*2) + target;
		else return 0;
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
		//return Math.sqrt( body.gravAccel(radius)*3.6 / radius );
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
