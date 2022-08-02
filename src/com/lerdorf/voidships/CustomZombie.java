package com.lerdorf.voidships;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_16_R3.EntityGiantZombie;
import net.minecraft.server.v1_16_R3.EntityHorseAbstract;
import net.minecraft.server.v1_16_R3.EntityLiving;

//import org.bukkit.ridables.entity.RidableEntity;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.EnumMoveType;
import net.minecraft.server.v1_16_R3.IJumpable;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.TagsFluid;
import net.minecraft.server.v1_16_R3.Vec3D;
import net.minecraft.server.v1_16_R3.World;
//import net.pl3x.bukkit.ridables.entity.RidableEntity;
//import net.p16x.bukkit.ridables.entity.RidableEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CustomZombie extends EntityZombie {

	
	
	public CustomZombie(EntityTypes<? extends EntityZombie> entitytypes, World world) {
		super(EntityTypes.ZOMBIE, world.getWorld().getHandle());
		// TODO Auto-generated constructor stub
	}

	/*
	private static Field jump = ReflectionUtil.getField(EntityLiving.class, "jumping");

	
	protected double walkSpeed = 0.5F;
    protected boolean hasRider = false;
    protected boolean isFlying = false;
    protected float jumpPower = 0;
	
	@Override
    public void f(Vec3D vec3d) {
        if (!this.isVehicle()) {
            super.e(vec3d);
            return;
        }

        if (this.onGround && this.isFlying) {
            isFlying = false;
            this.fallDistance = 0;
        }

        EntityLiving passenger = (EntityLiving) this.getPassengers().get(0);

        if (this.a(TagsFluid.WATER)) {
            this.setMot(this.getMot().add(0, 0.4, 0));
        }

        // apply pitch & yaw
        this.lastYaw = (this.yaw = passenger.yaw);
        this.pitch = passenger.pitch * 0.5F;
        setYawPitch(this.yaw, this.pitch);
        this.aK = (this.aI = this.yaw);

        // get motion from passenger (player)
        double motionSideways = passenger.aZ * walkSpeed;
        double motionForward = passenger.bb;

        // backwards is slower
        if (motionForward <= 0.0F) {
            motionForward *= 0.25F;
        }
        // sideways is slower too but not as slow as backwards
        motionSideways *= 0.85F;

        float speed = 0.22222F * (1F + (5));
        double jumpHeight = jumpPower;
        ride(motionSideways, motionForward, vec3d.y, speed); // apply motion

        // throw player move event
        if (this instanceof EntityGiantZombie) {
            double delta = Math.pow(this.locX() - this.lastX, 2.0D) + Math.pow(this.locY() - this.lastY, 2.0D)
                    + Math.pow(this.locZ() - this.lastZ, 2.0D);
            float deltaAngle = Math.abs(this.yaw - lastYaw) + Math.abs(this.pitch - lastPitch);
            if (delta > 0.00390625D || deltaAngle > 10.0F) {
                Location to = getBukkitEntity().getLocation();
                Location from = new Location(world.getWorld(), this.lastX, this.lastY, this.lastZ, this.lastYaw,
                        this.lastPitch);
                if (from.getX() != Double.MAX_VALUE) {
                    Location oldTo = to.clone();
                    PlayerMoveEvent event = new PlayerMoveEvent((Player) passenger.getBukkitEntity(), from, to);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        passenger.getBukkitEntity().teleport(from);
                        return;
                    }
                    if ((!oldTo.equals(event.getTo())) && (!event.isCancelled())) {
                        passenger.getBukkitEntity().teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
                        return;
                    }
                }
            }
        }

        if (jump != null && this.isVehicle()) {
            boolean doJump = false;
            if (this instanceof IJumpable) {
                if (this.jumpPower > 0.0F) {
                    doJump = true;
                    this.jumpPower = 0.0F;
                } else if (!this.onGround && jump != null) {
                    try {
                        doJump = jump.getBoolean(passenger);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            } else {
                if (jump != null) {
                    try {
                        doJump = jump.getBoolean(passenger);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }

            if (doJump) {
                if (onGround) {
                    jumpHeight = new BigDecimal(jumpHeight).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    Double jumpVelocity = 0.8;
                    jumpVelocity = jumpVelocity == null ? 0.44161199999510264 : jumpVelocity;
                    if (this instanceof IJumpable) {
                        getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).setValue(jumpVelocity);
                    }
                    this.setMot(this.getMot().x, jumpVelocity, this.getMot().z);
                }
            }

        }
        super.a(vec3d);
    }

	private void ride(double motionSideways, double motionForward, double motionUpwards, float speedModifier) {
		double locY;
		float f2;
		float speed;
		float swimSpeed;

		if (this.b(TagsFluid.WATER)) {
			locY = this.locY();
			speed = 0.8F;
			swimSpeed = 0.02F;

			this.a(swimSpeed, new Vec3D(motionSideways, motionUpwards, motionForward));
			this.move(EnumMoveType.SELF, this.getMot());
			double motX = this.getMot().x * speed;
			double motY = this.getMot().y * 0.800000011920929D;
			double motZ = this.getMot().z * speed;
			motY -= 0.02D;
			if (this.positionChanged && this.e(this.getMot().x,
					this.getMot().y + 0.6000000238418579D - this.locY() + locY, this.getMot().z)) {
				motY = 0.30000001192092896D;
			}
			this.setMot(motX, motY, motZ);
		} else if (this.b(TagsFluid.LAVA)) {
			locY = this.locY();
			this.a(0.02F, new Vec3D(motionSideways, motionUpwards, motionForward));
			this.move(EnumMoveType.SELF, this.getMot());
			double motX = this.getMot().x * 0.5D;
			double motY = this.getMot().y * 0.5D;
			double motZ = this.getMot().z * 0.5D;
			motY -= 0.02D;
			if (this.positionChanged && this.e(this.getMot().x,
					this.getMot().y + 0.6000000238418579D - this.locY() + locY, this.getMot().z)) {
				motY = 0.30000001192092896D;
			}
			this.setMot(motX, motY, motZ);
		} else {
			float friction = 0.91F;

			speed = speedModifier * (0.16277136F / (friction * friction * friction));

			this.a(speed, new Vec3D(motionSideways, motionUpwards, motionForward));
			friction = 0.91F;

			double motX = this.getMot().x;
			double motY = this.getMot().y;
			double motZ = this.getMot().z;

			if (this.isClimbing()) {
				swimSpeed = 0.15F;
				motX = MathHelper.a(motX, -swimSpeed, swimSpeed);
				motZ = MathHelper.a(motZ, -swimSpeed, swimSpeed);
				this.fallDistance = 0.0F;
				if (motY < -0.15D) {
					motY = -0.15D;
				}
			}

			Vec3D mot = new Vec3D(motX, motY, motZ);

			this.move(EnumMoveType.SELF, mot);
			if (this.positionChanged && this.isClimbing()) {
				motY = 0.2D;
			}

			motY -= 0.08D;

			motY *= 0.9800000190734863D;
			motX *= friction;
			motZ *= friction;

			this.setMot(motX, motY, motZ);
		}

		this.aC = this.aD;
		locY = this.locX() - this.lastX;
		double d1 = this.locZ() - this.lastZ;
		f2 = MathHelper.sqrt(locY * locY + d1 * d1) * 4.0F;
		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		this.aD += (f2 - this.aD) * 0.4F;
		this.aE += this.aD;
	}*/

}
