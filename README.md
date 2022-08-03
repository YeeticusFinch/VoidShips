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

## Block-Ships
Larger ships made out of blocks, you can walk around in them, build in them, and most of the plugin revolves around them.
To create a new spaceship, use ```/newship [name]``` while standing in the location within the ship you wish to be the player spawnpoint for that ship. The ship will be placed in a random solar system. Use ```/setsystem [name of star]``` to change the location of the ship.
Do ```/ships``` to see a list of all spaceships on your server.

### Ship Terminal
An inventory menu featuring the controls for the ship, allowing access to Navigation, Scanning, Atmosphere, Weapons, Defense, Security Systems (I'm a Systems Engineering Student, everything must be a 'system'). Currently the only thing that works is Atmosphere Systems, which allows the user to pressurize or depressurize any room in the ship (so long as there is an oxygen pump pointing in to that room).
To create a terminal, use ```/terminal``` while looking at dark oak stairs, you need to be within a spaceship to do this.

### Oxygen Pump
Looks like a dispensor, right-click to pressurize or depressurize the room. It will automatically connect to the ship's terminal via Atmosphere Systems (unless the signal is jammed), allowing for remote access from the terminal.
To create a pump, use ```/pump [room name]``` while looking at a dispensor, you need to be within a spaceship to do this, and the dispensor needs to be facing an empty air block.

### Atmosphere Detector
A redstone lamp that detects for an atmosphere in the adjacent blocks. If no adjacent blocks are vacuums, then the lamp will be lit.
To create an atmosphere detector, use ```/detector``` while looking at a redstone lamp.

### Cosmic Map
A smooth stone slab that displays a 3D holographic map above. Right clicking the slab will switch the display between displaying the current solar system, and displaying a map of the nearby stars. Scrolling between inventory slots while looking at the slab will zoom in or zoom out on the map.
To create a cosmic map display, use ```/map``` while looking at a smooth stone slab to make a map, you need to be within a spaceship to do this.

### Anti Gravity and Artificial Gravity
There is no gravity in space, so gravity is disabled by default (except if you are in creative mode).
To add artificial gravity to your ship, make a world edit selection around the parts you wish to contain gravity, and do ```/gravity```

### Door
A door is a region of blocks that will disapear for 2 seconds upon right click.
To create a door, make a WorldEdit selection and do ```/door```
The door will only overwrite air blocks when closing, so any blocks placed while the door is open will become incorporated into the door. (I might fix this)

## Cosmology

### Solar Systems
Do ```/systems``` to see a list of all solar systems on your server.
Do ```/newsystem [args]``` to create a new solar system. Use ```/help newsystem``` for a list of the possible args. For example, ```/newsystem -n 40_Eridani_A -kn Keid -d 16.34 -m 0.84 -r 0.81``` will create a new solar system with a star named "40 Eridani A" (underscores are replaced by spaces), a knickname of "Keid", a distance of 16.34 light years from our sun, a solar mass of 0.84 (our sun has a solar mass of 1), and a solar radius of 0.81 (our sun has a solar radius of 1). Any details that aren't provided will be randomly generated based on the provided information. Returning to the provided example, it will probably choose the star to be a main sequence star such as a yellow/orange dwarf or a red giant, because those are likely options given the provided radius and mass (40 Eridani A is an orange dwarf). 

### Planets
Do ```/planets [name of system]``` to get a list of planets in the provided solar system. The name of the system is the same as the name of the star, or the id, or the knickname, whichever identifier is provided, they will (should) all work. Do ```/newplanet [args]``` to create a new planet. Same deal as for creating a new solar system, except the ```-s [name of system]``` arg is mandatory, there is no support for rogue planets (at least not yet). If you really want a rogue planet, you can use the ```-t [type]``` argument on the ```/newsystem``` to set the type as a planet, or rogue planet. For example ```/newsystem -n Look_ma,_no_star! -t Rogue_planet```

## Entity-Ships
Tiny ships, such as escape pods and tie fighters, zip around and dogfight, or zoom around an enemy blockship and try to pierce the hull from up close, maybe even land in the dock and board the ship. Block-Ships will also have turrets that are similar in principle to entity-ships, except they don't fly around and are tied to a block.
Do ```/vehicle tie_fighter``` to spawn a new tie fighter (more vehicles will be added in the future). Currently the tie fighter only has forward thrust capabilities, and it can obviously spin. I gave entity-ships newtonian physics appropriate for a vacuum, velocities can stay constant due to a lack of air resistance, and all movement requires fuel. Fuel is measured in jules, for an acceleration maneuvre the fuel required equals at least double the resulting change in kinetic energy (depending on the engine efficiency). That's right, turning counts as an acceleration, although I have approximated all entity-ships as spheres for the rotational inertia calculations.

# Planned Features

ToDo List: https://docs.google.com/document/d/1mwkHu0kTy9LCB7z1kaila9Fu1oxkFkfhTGh7txD13RU/edit?usp=sharing

# Crazy Bugs

## Entity-ship Rotation

An entity-ship is a tiny ship not made out of blocks, but made out of invisible armorstands carrying 3d models on their heads. Players can ride them around, use them to board or attack other ships, or defend their own ships... My first entity ship was the tie-fighter, the second one being a Dalek (a remote controllable drone). Each entity ship would be facing towards positive Z upon spawning, at the location of the player that spawned the ship. Take the tie fighter as an example, it always seeks to point in the direction the player is facing, though it can't turn as fast as the player can turn, it uses attitude thrusters to accelerate rotation, then decelerate. The first issue was that whenever the pilot looked directly towards the negative Z direction, the tie fighter would do a full 360 spin in the opposite direction. This is because minecraft's yaw values go from -180 to 180, meaning if you pass the negative Z mark your yaw direction jumps from 180 to -180. My ship rotation code orriginaly spun the ship in the direction corresponding to the numerically-shortest path to the target. The solution was to substitute that for the numerically-shortest path to the sum of the target and a multiple of 360.

The next issue was that the rotation didn't work for all ships. A ship's rotation could be 'broken' upon spawning. It would always rotate with the player, in the correct direction too, but to the wrong spot, as if it didn't know where the "front" of the ship was. The issue was that it was just the armorstand's head that was rotating, and not the body. But the Location object carries a rotation, so the initial spawning of the armorstand would carry rotation. Of course the head's rotation is relative to the entity's rotation, meaning that if the entity were rotated it would throw off the head's rotation.

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
Perhaps if you can sneak something small past their radar, you can send in a starfighter to attack the enemy ship.
