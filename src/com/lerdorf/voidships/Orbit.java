package com.lerdorf.voidships;

import java.io.File;
import java.io.FilenameFilter;
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
  public double radius; //km
  public double period; //hours
  public double[] axis; // [x, y, z] unit vector
  
  public static final int DIRECT = 0;
  public static final int SLINGSHOT = 1;
  public static final int HOHMANN_TRANSFER = 2;
  public static final int HOHMANN_TRANSFER_IDEAL = 3;
  
  public Orbit(CosmicBody body, double radius, double period, double[] axis) {
    this.body = body;
    this.radius = radius;
    this.period = period;
    this.axis = axis;
  }
  
  // [deltaV, time]
  public double[] deltaV(Orbit o, double maxAcceleration, int transferType) {
    
  }
  
  public double getNetVelocity() {
    
  }
  
  public double getEscapeVelocity() {
    return getNetVelocity()*Math.sqrt(2);
  }
}
