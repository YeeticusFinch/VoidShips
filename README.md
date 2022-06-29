# VoidShips
My first legit Minecraft plugin, spaceships with vacuum physics and antigravity and solar systems and planets and stuff.

The idea is to make a somewhat realistic spaceship pvp game.

The universe is composed of numerous solar systems, and planets (their distances and properties can be configured). Though I did simplify all orbits as circular.

Time runs just as fast as in Minecraft, one day in minecraft is 20 minutes, and thus one earth-day is 20 minutes, meaning time runs 72x faster.

![image](https://user-images.githubusercontent.com/50182007/176383163-8b14fea9-815f-4e45-a49a-8eece19a0a20.png)
![image](https://user-images.githubusercontent.com/50182007/176383190-8d1fbe99-6da6-4e01-9b4d-99382e9c6917.png)

# Features

## AsyncFill
Unfortunately the worldedit plugin would crash on my server when filling large amounts of blocks (>2.6 million blocks), and I need to fill a lot more blocks than that. So I created a function to asynchronously fill the blocks. One block per tick was too slow, a 700x255x700 block area would take 1.6 years assuming no lag. So I decided to go one vertical planar slice at a time (vertical dimension has a constraint, a maximum of 255 blocks). Thus in theory a 700x255x700 block area could be completed in 25 seconds assuming no lag. Unfortunately it would be placing 128k blocks per tick, which would lag the server during that time, and thus it isn't completely asynchronous.
![image](https://user-images.githubusercontent.com/50182007/176157768-2ac0b4ab-5609-48cd-86b9-1906c15b68df.png)
x-axis is the quantity of blocks being changed, y-axis is the amount of time in seconds it takes (on my server) for my asyncFill function to run.

# Detailed Overview

When battling another ship, the first step is to find that ship. Scans can only travel at the speed of light, so if you're tracking a ship on the opposite side of the solar system, the signal might take 15 minutes to get there, and then 15 minutes to return, so a 30 minute round trip, which would be 25 seconds in-game.
So you would know where that ship was 25 seconds ago.

If the enemy ship is in a different solar system, such as Proxima Centauri (the nearest system to ours), a signal would take 8.5 years to travel round trip, and chances are it won't even return conclusive results, you might as well just turn on a beacon so that 4.2 years from now the enemy knows where you are and can start heading over to you.

To not make this incredibly boring, I have included a couple remedies. Each solar system is equiped with a wormhole that spawns in a random spot near the star, and this wormhole can instantly teleport any vessel to any other wormhole (provided the vessel is equiped with a wormhole drive). Additionally, it is possible to equip your ship with a hyperdrive like from Star Wars, which can get you to Kepler-8 (3.3k lightyears) in an hour, or 50 seconds in-game time.

So how do you locate the enemy ship?
You send a probe through the wormhole, have it scan various systems, you send out a beacon to reveal your location and then set a trap for when the enemy ship enters the system.

Ok you found the enemy ship, what now?
The enemy ship will probably be tens of millions of kilometers away, you won't even be able to see it, how could you possibly aim a weapon at it? That's where WeaponTargetSystems comes in. WeaponTargetSystems analyzes electromagnetic noise coming from the other ship, be it their comms, their mapping scans, their SecSystems or perhaps even their WeaponTargetSystems, and match it to a ship model from its database, to display in the UI. You can choose exactly where on the ship you want to target. Perhaps you want to fire bullets and poke holes in their bulkhead to suffocate the crew, or if you've got the equipment you could light them up with a missile. The projectile you fire will be launched in such a way where it will orbit around the system's star, perhaps slingshot around a planet, in order to hit it's target.

Unfortunately, spaceships have very little in terms of physical defense against such attacks, one bullet and whatever section it hits will lose its air, one missile and it will all go up in flames. 

If you have SecSystems enabled, then you will be alerted if you were tagged by a scan, if any WeaponTargetSystems are aimed at you, or if you're being shot at. With WeaponDefenseSystems enabled, your ship will automatically fire an electrostatically-steered electron laser at incoming projectiles, but at the expense of energy, which comes from expending fuel, and it can only reduce incoming damage by so much, it won't really do anything about a missile.

Say you're 1 AU away from the enemy ship. It will take them 6.6 seconds of game time (8 real-world minutes) to lock on to your ship with WeaponTargetSystems, and you will be alerted of that as soon as it happens. You could temporarily confuse their WeaponTargetSystems by launching flares, potentially giving your ship enough time to target lock and fire, or you could wait until after they fire and change your ship's course to dodge (it will take the projectile at least 6.6 seconds to get here. Additionally, you could put your WeaponTargetSystems into trophy mode, which will automatically intercept incomming fire with it's own, and it can stop all incomming fire.

Or you could attempt to get close enough to board their ship, but good luck. At 1 AU (150 million km) you'll have 6.6 seconds (minimum) to react to any dangers, as you get closer things are going to happen a lot faster.

So what are the strategies? 
You could turn off everything and become practically invisible, and then get close enough to strike by surprise.
You could go in full offenseive mode, long-range mapping scanners plus WeaponTargetSystems, and put them on the defensive so that they can't counterattack.
Or perhaps you would prefer to alternate between defending and attacking, and hope the enemy makes a careless mistake before you do (very likely if you get close to them), or hope their fuel runs out before yours does.
Might add some dogfighting stuff.
