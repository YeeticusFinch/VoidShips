package com.lerdorf.voidships;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.metrics.Metrics;

//import com.sk89q.worldguard.bukkit.WGBukkit;
//import com.sk89q.worldguard.protection.regions.ProtectedRegion;
//import io.lumine.xikage.mythicmobs.MythicMobs;
//import io.lumine.xikage.mythicmobs.mobs.MobManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class Ride {
  HashMap<Player, Boolean> toggle = new HashMap<>();
  
  HashMap<Player, Entity> riding = new HashMap<>();
  
  HashMap<Player, Integer> livetick = new HashMap<>();
  
  ProtocolManager protocolManager;
  
  String onmessage;
  
  String offmessage;
  
  String failride;
  
  String occupied;
  
  String ridemessage;
  
  String npcfail;
  
  String mmfail;
  
  Boolean npcride;
  
  Boolean mmride;
  
  Boolean mera;
  
  Boolean IsWGE;
  
  Boolean dereg;
  
  Boolean deworld;
  
  String failr;
  
  Double mspeed;
  
  Double mjumpspd;
  
  Boolean ejump;
  
  Boolean fhead;
  
  List<String> dworlds = new ArrayList<>();
  
  List<String> dregs = new ArrayList<>();
  
  Plugin plugin;
  
  public Ride(Plugin plugin) {
	  this.plugin = plugin;
  }
  
  public boolean MythicE() {
    return false;
	  //return Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs");
  }
  
  public boolean Plib() {
    return Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
  }
  
  public void onEnable() {
    //Metrics metric = new Metrics(Main.instance);
    if (Plib()) {
      sendConsole("&b&m-------------------");
      sendConsole("&a     &lAuto&2&lRide");
      sendConsole("&e ProtocolLib plugin found, AutoRide &aenabled");
      sendConsole("&b&m-------------------");
    } else {
      sendConsole("&b&m-------------------");
      sendConsole("&4     &lAuto&c&lRide");
      sendConsole("&c ProtocolLib plugin NOT found, AutoRide &4disabled");
      sendConsole("&b&m-------------------");
      Bukkit.getPluginManager().disablePlugin(plugin);
    } 
    this.protocolManager = ProtocolLibrary.getProtocolManager();
    if (WGE()) {
      Bukkit.getConsoleSender().sendMessage("AutoRide > WorldGuard plugin found! Hooked!");
      this.IsWGE = Boolean.valueOf(true);
    } else {
      this.IsWGE = Boolean.valueOf(false);
    } 
    if (MythicE()) {
      this.mera = Boolean.valueOf(true);
    } else {
      this.mera = Boolean.valueOf(false);
    } 
    //Bukkit.getServer().getPluginManager().registerEvents(Main.instance, plugin);
    //Main.instance.getConfig().options().copyDefaults(true);
    //Main.instance.saveConfig();
    //FileConfiguration config = Main.instance.getConfig();
    /*this.onmessage = config.getString("ride-toggle-on-message");
    this.offmessage = config.getString("ride-toggle-off-message");
    this.failride = config.getString("player-toggle-off-try-to-ride-message");
    this.occupied = config.getString("ride-occupied");
    this.ridemessage = config.getString("player-ride");
    this.npcfail = config.getString("cant-ride-npc-message");
    this.npcride = Boolean.valueOf(config.getBoolean("disable-npc-ride"));
    this.mmride = Boolean.valueOf(config.getBoolean("disable-mythicmobs-ride"));
    this.mmfail = config.getString("fail-ride-mythicmobs");
    this.dereg = Boolean.valueOf(config.getBoolean("disable-certain-regions"));
    this.dregs = config.getStringList("disabled-regions");
    this.dworlds = config.getStringList("disabled-worlds");
    this.deworld = Boolean.valueOf(config.getBoolean("disable-certain-worlds"));
    this.failr = config.getString("failed-in-region-message");
    this.mspeed = Double.valueOf(config.getDouble("movement-steer-speed"));
    this.mjumpspd = Double.valueOf(config.getDouble("movement-jump-speed"));
    this.ejump = Boolean.valueOf(config.getBoolean("disable-entity-jump"));
    this.fhead = Boolean.valueOf(config.getBoolean("follow-player-head"));*/
    this.onmessage = "&7Re-toggled riding to &aON&7.";
    this.offmessage = "&7Re-toggled riding to &cOFF&7.";
    this.failride = "&9Use '&fride&9' to re-toggle riding.";
    this.occupied = "&cOops! &7That &a%RIDE% &7is already carrying someone!";
    this.ridemessage = "&7You've boarded a &a%RIDE%&7!";
    this.npcfail = "&cOops! &7That's an NPC!";
    this.npcride = false;
    this.mmfail = "&7That ship is too powerful to board.";
    this.mmride = true;
    this.dereg = true;
    this.dregs = new ArrayList<String>();
    this.dregs.add("PVP");
    this.dworlds = new ArrayList<String>();
    this.dworlds.add("Minigame");
    this.deworld = true;
    this.failr = "&cYou can't ride that around here..";
    this.mspeed = 0.43; //The speed when WASD
    this.mjumpspd = 0.5; //The speed of Jumping of Entity
    this.ejump = false; // disable entity jump
    this.fhead = true; // follow player head
    
    this.protocolManager.addPacketListener((PacketListener)new PacketAdapter(plugin, 
          ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.STEER_VEHICLE }) {
          public void onPacketReceiving(PacketEvent event) {
            Ride.this.Merapihkan(event);
          }
        });
  }
  
  //@EventHandler
  public void onDismount(EntityDismountEvent e) {
    try {
      Player p = (Player)e.getEntity();
      this.livetick.remove(p);
    } catch (Exception exception) {}
  }
  
  //@EventHandler
  public void onQuit(PlayerQuitEvent e) {
    try {
      Player p = e.getPlayer();
      this.livetick.remove(p);
      p.getVehicle().setPassenger(null);
      //p.setVehicle(null);
    } catch (Exception exception) {}
  }
  
  public void Merapihkan(PacketEvent event) {
    if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
      Player p = event.getPlayer();
      PacketContainer packet = event.getPacket();
      Boolean b2 = (Boolean)packet.getBooleans().read(0);
      Float type = (Float)packet.getFloat().read(0);
      Float type2 = (Float)packet.getFloat().read(1);
      if (!(p.getVehicle() instanceof ArmorStand))
    	  return;
      ArmorStand en = (ArmorStand)p.getVehicle();
      //System.out.println(1);
      if (en == null || p == null)
        return; 
      if (!p.hasPermission("autoride.steer." + en.getType()))
        return; 

		boolean vehicle = en.getScoreboardTags().contains("vehicle");
      //System.out.println(2);
      Location loc = p.getLocation();
      Location peye = p.getEyeLocation();
      Float pyaw = Float.valueOf(peye.getYaw());
      Float ppitch = Float.valueOf(peye.getPitch());
      Location enloc = en.getLocation().clone();
      enloc.setPitch(ppitch.floatValue());
      enloc.setYaw(pyaw.floatValue());
      Vector v = loc.getDirection();
      loc.setPitch(28.0F);
      int forward = 0;
      int left = 0;
      int up = 0;
      int livejump = 0;
      if (this.ejump.booleanValue()) {
          //System.out.println(3);
        if (this.livetick.get(p) == null)
          this.livetick.put(p, Integer.valueOf(0)); 
        livejump = ((Integer)this.livetick.get(p)).intValue();
        if (livejump > 0) 
          //this.livetick.put(p, Integer.valueOf(livejump - 1)); 
        	up += 1;
      } 
      Boolean b = Boolean.valueOf(en.isOnGround());
      if (b2.booleanValue() && b.booleanValue() && !this.ejump.booleanValue()) {
          //System.out.println(4);
        loc.setPitch(-87.15F);
        loc.setYaw(-292.05F);
        v = loc.getDirection();
        Lompat(en, p, v);
      } 
      if (!p.hasPermission("autoride.fly" + en.getType()))
        loc.setPitch(50.0F); 
      if (p.hasPermission("autoride.fly." + en.getType()))
        loc.setPitch(p.getLocation().getPitch()); 
      en.setFallDistance(0.0F);
      if (en.getType() == EntityType.HORSE)
        return; 
      //System.out.println(5);
      
      if (type2.floatValue() > 0.0F) { // `W` key pressed
    	/*  System.out.println("+TYPE 2");
        if (type.floatValue() > 0.0F)
          loc.setYaw(loc.getYaw() + 45.0F); 
        if (type.floatValue() < 0.0F)
          loc.setYaw(loc.getYaw() - 45.0F); 
        if (livejump > 0)
          loc.setPitch(-11.0F); 
        v = loc.getDirection();
        if ((b.booleanValue() & b2.booleanValue())) { // originally (b.booleanValue() & b2.booleanValue()) != 0
          if (this.ejump.booleanValue())
            return; 
          this.livetick.put(p, Integer.valueOf(20));
        } 
        if (!vehicle)
        	MoveEntity(en, p, this.mspeed, v);*/
    	  forward += 1;
      } 
      if (type.floatValue() > 0.0F) { // `A` key pressed
    	/*  System.out.println("+TYPE");
        loc.setYaw(ninentyit(loc.getYaw()));
        if (livejump > 0)
          loc.setPitch(-11.0F); 
        v = loc.getDirection();
        if ((b.booleanValue() & b2.booleanValue())) { // originally (b.booleanValue() & b2.booleanValue()) != 0
          if (this.ejump.booleanValue())
            return; 
          this.livetick.put(p, Integer.valueOf(20));
        } 
        MoveEntity(en, p, this.mspeed, v);*/
    	  left += 1;
      } 
      if (type.floatValue() < 0.0F) { // `D` key pressed
    	/*  System.out.println("-TYPE");
        loc.setYaw(uninentyit(loc.getYaw()));
        if ((b.booleanValue() & b2.booleanValue())) { // originally (b.booleanValue() & b2.booleanValue()) != 0
          if (this.ejump.booleanValue())
            return; 
          this.livetick.put(p, Integer.valueOf(20));
        } 
        if (livejump > 0)
          loc.setPitch(-11.0F); 
        v = loc.getDirection();
        MoveEntity(en, p, this.mspeed, v);*/
    	  left -= 1;
      } 
      if (type2.floatValue() < 0.0F) { // `S` key pressed
    	/*  System.out.println("-TYPE2");
        if (b2.booleanValue() && b.booleanValue())
          loc.setY(loc.getY() + 1.2D); 
        if (type.floatValue() > 0.0F)
          loc.setYaw(loc.getYaw() - 225.0F); 
        if (type.floatValue() < 0.0F)
          loc.setYaw(loc.getYaw() + 225.0F); 
        if ((b.booleanValue() & b2.booleanValue())) { // originally (b.booleanValue() & b2.booleanValue()) != 0
          if (this.ejump.booleanValue())
            return; 
          this.livetick.put(p, Integer.valueOf(20));
        } 
        loc.setPitch(-28.0F);
        if (livejump > 0)
          loc.setPitch(11.0F); 
        v = loc.getDirection();
        MoveEntity(en, p, Double.valueOf(this.mspeed.doubleValue() * -1.0D), v);*/
    	  forward -= 1;
      } 
      if (forward != 0 || up != 0 || left != 0)
    	  MoveVehicle(en, p, forward, up, left);
    } 
  }
  
  public float ninentyit(float f) {
    return f - 90.0F;
  }
  
  public float uninentyit(float f) {
    return f + 90.0F;
  }
  
  public void MoveEntity(Entity entity, Player p, Double speed, Vector vector) {

    entity.setVelocity(vector.normalize().multiply(speed.doubleValue()));
    //System.out.println("Moving ship");
  }
  
  public void MoveVehicle(ArmorStand entity, Player p, int forward, int up, int left) {
	  Main.getSpecialEntity(entity).doThrust(forward, up, left);
  }
  
  public void Lompat(Entity entity, Player p, Vector vector) {
    entity.setVelocity(vector.normalize().multiply(this.mjumpspd.doubleValue()));
  }
  
  public void onDisable() {}
  
  public void sendMsg(Player p, String msg) {
    p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
  }
  
  public void sendActionBar(Player p, String msg) {
    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
  }
  
  public void sendMsg(Player b, EntityType g) {
    b.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You've boarded a &a" + g + "&7!"));
  }
  
  public String ridement(String var, String ride) {
    return var.replaceAll("%RIDE%", ride);
  }
  
  public boolean WGE() {
	  return false;
    //return Main.instance.getServer().getPluginManager().isPluginEnabled("WorldGuard");
  }
  
  public void Reset() {
	  /*
    FileConfiguration config = Main.instance.getConfig();
    this.onmessage = config.getString("ride-toggle-on-message");
    this.offmessage = config.getString("ride-toggle-off-message");
    this.failride = config.getString("player-toggle-off-try-to-ride-message");
    this.occupied = config.getString("ride-occupied");
    this.ridemessage = config.getString("player-ride");
    this.npcfail = config.getString("cant-ride-npc-message");
    this.npcride = Boolean.valueOf(config.getBoolean("disable-npc-ride"));
    this.mmride = Boolean.valueOf(config.getBoolean("disable-mythicmobs-ride"));
    this.mmfail = config.getString("fail-ride-mythicmobs");
    this.dereg = Boolean.valueOf(config.getBoolean("disable-certain-regions"));
    this.dregs = config.getStringList("disabled-regions");
    this.dworlds = config.getStringList("disabled-worlds");
    this.deworld = Boolean.valueOf(config.getBoolean("disable-certain-worlds"));
    this.failr = config.getString("failed-in-region-message");*/
	  this.onmessage = "&7Re-toggled riding to &aON&7.";
	    this.offmessage = "&7Re-toggled riding to &cOFF&7.";
	    this.failride = "&9Use '&fride&9' to re-toggle riding.";
	    this.occupied = "&cOops! &7That &a%RIDE% &7is already carrying someone!";
	    this.ridemessage = "&7You've boarded a &a%RIDE%&7!";
	    this.npcfail = "&cOops! &7That's an NPC!";
	    this.npcride = false;
	    this.mmfail = "&7That mob is too powerful to ride.";
	    this.mmride = true;
	    this.dereg = true;
	    this.dregs = new ArrayList<String>();
	    this.dregs.add("PVP");
	    this.dworlds = new ArrayList<String>();
	    this.dworlds.add("Minigame");
	    this.deworld = true;
	    this.failr = "&cYou can't ride that around here..";
	    this.mspeed = 0.43; //The speed when WASD
	    this.mjumpspd = 0.5; //The speed of Jumping of Entity
	    this.ejump = false;
	    this.fhead = false;
  }
  
  //@EventHandler
  public void RightClick(PlayerInteractEntityEvent event) {
    Entity b = event.getRightClicked();
    Player player = event.getPlayer();
    EntityType g = b.getType();
    if (g == EntityType.ARMOR_STAND) {
    	System.out.println("Right clicked armor stand");
    }
    String entityName = b.getName();
    World world = player.getWorld();
    if (b.getScoreboardTags().contains("vehicle") && !b.getScoreboardTags().contains("drone")) {
    	System.out.println(2);
      if (g == EntityType.HORSE)
        return; 
      if (this.toggle.get(player) == null)
        this.toggle.put(player, Boolean.valueOf(true)); 
      if (((Boolean)this.toggle.get(player)).booleanValue()) {
    	  System.out.println(3);
        if (b.hasMetadata("NPC") && 
          this.npcride.booleanValue()) {
          sendActionBar(player, this.npcfail);
          return;
        } 
        if (this.deworld.booleanValue())
          for (String dw : this.dworlds) {
            if (dw.equalsIgnoreCase("dworlds"))
              return; 
          }  
        System.out.println(4);
        /*if (this.dereg.booleanValue() && 
          this.IsWGE.booleanValue()) {
          Location loc = player.getLocation();
          for (String dr : this.dregs) {
            for (ProtectedRegion reg : WGBukkit.getRegionManager(world).getApplicableRegions(loc)) {
              if (reg.getId().equals(dr)) {
                sendActionBar(player, this.failr);
                return;
              } 
            } 
          } 
        } */
        /*if (this.mera.booleanValue() && 
          this.mmride.booleanValue())
          try {
            MobManager mythic = MythicMobs.inst().getMobManager();
            if (mythic.getAllMythicEntities().contains(b)) {
              sendActionBar(player, this.mmfail);
              return;
            } 
          } catch (Exception e) {
            e.printStackTrace();
            sendConsole("AutoRide > An error has occured, do you have mythicmobs plugin?");
          }  */
        String bee = g.toString();
        String mess = ridement(this.occupied, entityName);
        String rides = ridement(this.ridemessage, entityName);
        if (b.getPassenger() == null) {
        	System.out.println(5);
          b.setPassenger((Entity)player);
          sendMsg(player, rides);
          return;
        } 
        sendActionBar(player, mess);
      } else {
        sendActionBar(player, this.failride);
        System.out.println(6);	
      } 
    } 
  }
  
  public void playerRightClicksAtEntity(PlayerInteractAtEntityEvent event) {
	  Entity b = event.getRightClicked();
	    Player player = event.getPlayer();
	    EntityType g = b.getType();
	    if (g == EntityType.ARMOR_STAND) {
	    	System.out.println("Right clicked armor stand");
	    }
	    String entityName = b.getName();
	    World world = player.getWorld();
	    if (b.getScoreboardTags().contains("vehicle") && !b.getScoreboardTags().contains("drone")) {
	    	System.out.println(2);
	      if (g == EntityType.HORSE)
	        return; 
	      if (this.toggle.get(player) == null)
	        this.toggle.put(player, Boolean.valueOf(true)); 
	      if (((Boolean)this.toggle.get(player)).booleanValue()) {
	    	  System.out.println(3);
	        if (b.hasMetadata("NPC") && 
	          this.npcride.booleanValue()) {
	          sendActionBar(player, this.npcfail);
	          return;
	        } 
	        if (this.deworld.booleanValue())
	          for (String dw : this.dworlds) {
	            if (dw.equalsIgnoreCase("dworlds"))
	              return; 
	          }  
	        System.out.println(4);
	        /*if (this.dereg.booleanValue() && 
	          this.IsWGE.booleanValue()) {
	          Location loc = player.getLocation();
	          for (String dr : this.dregs) {
	            for (ProtectedRegion reg : WGBukkit.getRegionManager(world).getApplicableRegions(loc)) {
	              if (reg.getId().equals(dr)) {
	                sendActionBar(player, this.failr);
	                return;
	              } 
	            } 
	          } 
	        } */
	        /*if (this.mera.booleanValue() && 
	          this.mmride.booleanValue())
	          try {
	            MobManager mythic = MythicMobs.inst().getMobManager();
	            if (mythic.getAllMythicEntities().contains(b)) {
	              sendActionBar(player, this.mmfail);
	              return;
	            } 
	          } catch (Exception e) {
	            e.printStackTrace();
	            sendConsole("AutoRide > An error has occured, do you have mythicmobs plugin?");
	          }  */
	        String bee = g.toString();
	        String mess = ridement(this.occupied, entityName);
	        String rides = ridement(this.ridemessage, entityName);
	        if (b.getPassenger() == null) {
	        	System.out.println(5);
	          b.setPassenger((Entity)player);
	          sendMsg(player, rides);
	          return;
	        } 
	        sendActionBar(player, mess);
	      } else {
	        sendActionBar(player, this.failride);
	        System.out.println(6);	
	      } 
	    } 
	}
  
  private void sendConsole(String string) {
    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', string));
  }
  
  /*
  public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
    if (sender instanceof Player) {
      Player b = (Player)sender;
      int length = args.length;
      if (cmd.getName().equalsIgnoreCase("ride")) {
        if (length == 0 && 
          b.hasPermission("riding.toggle")) {
          if (this.toggle.get(b) == null)
            this.toggle.put(b, Boolean.valueOf(true)); 
          if (!((Boolean)this.toggle.get(b)).booleanValue()) {
            this.toggle.put(b, Boolean.valueOf(true));
            sendMsg(b, this.onmessage);
            return true;
          } 
          if (((Boolean)this.toggle.get(b)).booleanValue()) {
            this.toggle.put(b, Boolean.valueOf(false));
            sendMsg(b, this.offmessage);
            return true;
          } 
        } 
        if (length >= 1 && 
          args[0].equalsIgnoreCase("reload") && 
          b.hasPermission("riding.reload")) {
          Main.instance.reloadConfig();
          Main.instance.reloadConfig();
          Reset();
          sendMsg(b, "&7The Configuration has been reloaded.");
          return true;
        } 
      } 
    } 
    return true;
  }*/
}
