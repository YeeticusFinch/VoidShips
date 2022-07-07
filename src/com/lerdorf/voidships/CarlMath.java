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

class CarlMath {
	
  // Displays a number in scientific notation 2000000 ==> 2e+6
  public static String sciNot(double num) {
    int exp = (int)Math.log10(Math.abs(num));
    if (Math.abs(exp) > 3) {
	    num /= Math.pow(10,exp);
	    return Math.round(num*10000)/10000f + "e" + (exp > 0 ? "+" : "-") + Math.abs(exp);
    } else return Math.round(num*10000)/10000f + "";
  }
  
  // Displays a number with a prefix 2000000 ==> 2 mega
  public static String withPrefix(double num) {
    String pre = "";
    if (num > Math.pow(10,18)) {
	pre = "exa";
	num /= Math.pow(10,18);
    } else if (num > Math.pow(10,15)) {
	pre = "peta";
	num /= Math.pow(10,15);
    } else if (num > Math.pow(10,12)) {
	pre = "tera";
	num /= Math.pow(10,12);
    } else if (num > Math.pow(10,9)) {
	pre = "giga";
	num /= Math.pow(10,9);
    } else if (num > Math.pow(10,6)) {
	pre = "mega";
	num /= Math.pow(10,6);
    } else if (num > Math.pow(10,3)) {
	pre = "kilo";
	num /= Math.pow(10,3);
    } else if (num >= 0) { // Don't do anything, no prefix required
      pre = "";
    } else if (num > Math.pow(10,-2)) {
      pre = "centi";
      num /= Math.pow(10,-2);
    } else if (num > Math.pow(10,-4)) {
      pre = "milli";
      num /= Math.pow(10,-3);
    } else if (num > Math.pow(10,-7)) {
      pre = "micro";
      num /= Math.pow(10,-6);
    } else if (num > Math.pow(10,-10)) {
      pre = "nano";
      num /= Math.pow(10,-9);
    } else if (num > Math.pow(10,-13)) {
      pre = "pico";
      num /= Math.pow(10,-12);
    } else {
      pre = "femto";
      num /= Math.pow(10,-15);
    }
    return Math.round(num*10000)/10000f + " " + pre; 
  }
  
}
