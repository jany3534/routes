/*
 * Copyright (C) 2012 TomyLobo
 *
 * This file is part of Routes.
 *
 * Routes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.tomylobo.abstraction.platform.bukkit;

import java.util.Collection;

import eu.tomylobo.abstraction.Network;
import eu.tomylobo.abstraction.block.Sign;
import eu.tomylobo.abstraction.entity.Entity;
import eu.tomylobo.abstraction.entity.EntityType;
import eu.tomylobo.abstraction.entity.MobType;
import eu.tomylobo.abstraction.entity.Player;
import eu.tomylobo.abstraction.entity.VehicleType;
import eu.tomylobo.math.Location;
import eu.tomylobo.math.Vector;
import eu.tomylobo.routes.util.Utils;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet11PlayerPosition;
import net.minecraft.server.Packet130UpdateSign;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet32EntityLook;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet39AttachEntity;
import net.minecraft.server.Packet40EntityMetadata;

public class BukkitNetwork implements Network {
	private Packet38EntityStatus createEffectPacket(int entityId, byte effectData) {
		return new Packet38EntityStatus(entityId, effectData);
	}
/*
	@Override
	public void sendEffect(Player player, Entity entity, EntityEffect effect) {
		sendEffect(player, entity.getEntityId(), effect.getData());
	}
*/
	@Override
	public void sendEffect(Player player, int entityId, byte effectData) {
		sendPacket(player, createEffectPacket(entityId, effectData));
	}
/*
	@Override
	public void sendEffect(Collection<Player> players, Entity entity, EntityEffect effect) {
		sendEffect(players, entity.getEntityId(), effect.getData());
	}
*/
	@Override
	public void sendEffect(Collection<Player> players, int entityId, byte effectData) {
		sendPacket(players, createEffectPacket(entityId, effectData));
	}


	private Packet40EntityMetadata createSetDataPacket(int entityId, int index, Object value) {
		DataWatcher datawatcher = new DataWatcher();
		try {
			// create entry
			datawatcher.a(index, value.getClass().getConstructor(String.class).newInstance("0"));

			// mark dirty
			datawatcher.watch(index, value.getClass().getConstructor(String.class).newInstance("1"));
		}
		catch (Exception e) { }

		// put the actual data in
		datawatcher.watch(index, value);

		return new Packet40EntityMetadata(entityId, datawatcher);
	}

	@Override
	public void sendSetData(Player player, Entity entity, int index, Object value) {
		sendSetData(player, entity.getEntityId(), index, value);
	}

	@Override
	public void sendSetData(Player player, int entityId, int index, Object value) {
		sendPacket(player, createSetDataPacket(entityId, index, value));
	}

	@Override
	public void sendSetData(Collection<Player> players, Entity entity, int index, Object value) {
		sendSetData(players, entity.getEntityId(), index, value);
	}

	@Override
	public void sendSetData(Collection<Player> players, int entityId, int index, Object value) {
		sendPacket(players, createSetDataPacket(entityId, index, value));
	}


	private Packet34EntityTeleport createTeleportPacket(int entityId, double x, double y, double z, float yaw, float pitch) {
		return new Packet34EntityTeleport(
				entityId,
				MathHelper.floor(x*32.0D),
				MathHelper.floor(y*32.0D),
				MathHelper.floor(z*32.0D),
				(byte) ((int) (yaw * 256.0F / 360.0F)),
				(byte) ((int) (pitch * 256.0F / 360.0F))
		);
	}

	@Override
	public void sendTeleport(Player player, Entity entity, Location location) {
		final Vector position = location.getPosition();
		sendTeleport(player, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendTeleport(Player player, int entityId, double x, double y, double z, float yaw, float pitch) {
		sendPacket(player, createTeleportPacket(entityId, x, y, z, yaw, pitch));
	}

	@Override
	public void sendTeleport(Collection<Player> players, Entity entity, Location location) {
		final Vector position = location.getPosition();
		sendTeleport(players, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendTeleport(Collection<Player> players, int entityId, double x, double y, double z, float yaw, float pitch) {
		sendPacket(players, createTeleportPacket(entityId, x, y, z, yaw, pitch));
	}


	private Packet28EntityVelocity createVelocityPacket(int entityId, double x, double y, double z) {
		return new Packet28EntityVelocity(entityId, x, y, z);
	}

	@Override
	public void sendVelocity(Player player, Entity entity, Vector velocity) {
		sendVelocity(player, entity.getEntityId(), velocity.getX(), velocity.getY(), velocity.getZ());
	}

	@Override
	public void sendVelocity(Player player, int entityId, double x, double y, double z) {
		sendPacket(player, createVelocityPacket(entityId, x, y, z));
	}

	@Override
	public void sendVelocity(Collection<Player> players, Entity entity, Vector velocity) {
		sendVelocity(players, entity.getEntityId(), velocity.getX(), velocity.getY(), velocity.getZ());
	}

	@Override
	public void sendVelocity(Collection<Player> players, int entityId, double x, double y, double z) {
		sendPacket(players, createVelocityPacket(entityId, x, y, z));
	}


	private Packet32EntityLook createOrientationPacket(int entityId, float yaw, float pitch) {
		return new Packet32EntityLook(
				entityId,
				(byte) ((int) (yaw * 256.0F / 360.0F)),
				(byte) ((int) (pitch * 256.0F / 360.0F))
		);
	}

	@Override
	public void sendOrientation(Player player, Entity entity, Location location) {
		sendOrientation(player, entity.getEntityId(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendOrientation(Player player, int entityId, float yaw, float pitch) {
		sendPacket(player, createOrientationPacket(entityId, yaw, pitch));
	}

	@Override
	public void sendOrientation(Collection<Player> players, Entity entity, Location location) {
		sendOrientation(players, entity.getEntityId(), location.getYaw(), location.getPitch());
	}

	@Override
	public void sendOrientation(Collection<Player> players, int entityId, float yaw, float pitch) {
		sendPacket(players, createOrientationPacket(entityId, yaw, pitch));
	}


	private Packet39AttachEntity createAttachToVehiclePacket(int passengerId, int vehicleId) {
		Packet39AttachEntity p39 = new Packet39AttachEntity();
		p39.a = passengerId;
		p39.b = vehicleId;
		return p39;
	}

	@Override
	public void sendAttachToVehicle(Player player, Entity passenger, Entity vehicle) {
		sendAttachToVehicle(player, passenger.getEntityId(), vehicle == null ? -1 : vehicle.getEntityId());
	}

	@Override
	public void sendAttachToVehicle(Player player, int passengerId, int vehicleId) {
		sendPacket(player, createAttachToVehiclePacket(passengerId, vehicleId));
	}

	@Override
	public void sendAttachToVehicle(Collection<Player> players, Entity passenger, Entity vehicle) {
		sendAttachToVehicle(players, passenger.getEntityId(), vehicle == null ? -1 : vehicle.getEntityId());
	}

	@Override
	public void sendAttachToVehicle(Collection<Player> players, int passengerId, int vehicleId) {
		sendPacket(players, createAttachToVehiclePacket(passengerId, vehicleId));
	}


	private Packet29DestroyEntity createDestroyPacket(int entityId) {
		return new Packet29DestroyEntity(entityId);
	}

	@Override
	public void sendDestroyEntity(Player player, Entity entity) {
		sendDestroyEntity(player, entity.getEntityId());
	}

	@Override
	public void sendDestroyEntity(Player player, int entityId) {
		sendPacket(player, createDestroyPacket(entityId));
	}

	@Override
	public void sendDestroyEntity(Collection<Player> players, Entity entity) {
		sendDestroyEntity(players, entity.getEntityId());
	}

	@Override
	public void sendDestroyEntity(Collection<Player> players, int entityId) {
		sendPacket(players, createDestroyPacket(entityId));
	}


	private Packet24MobSpawn createSpawnMobPacket(int entityId, double x, double y, double z, float yaw, float pitch, int creatureTypeId) {
		final Packet24MobSpawn p24 = new Packet24MobSpawn();
		p24.a = entityId;
		p24.b = creatureTypeId;
		p24.c = MathHelper.floor(x * 32.0D);
		p24.d = MathHelper.floor(y * 32.0D);
		p24.e = MathHelper.floor(z * 32.0D);
		p24.f = (byte) ((int) (yaw * 256.0F / 360.0F));
		p24.g = (byte) ((int) (pitch * 256.0F / 360.0F));
		Utils.setPrivateValue(Packet24MobSpawn.class, p24, "h", new DataWatcher());
		Utils.setPrivateValue(Packet24MobSpawn.class, p24, "i", new DataWatcher());
		return p24;
	}

	private Packet23VehicleSpawn createSpawnOtherPacket(int entityId, double x, double y, double z, int typeId, int dataValue) {
		final Packet23VehicleSpawn p23 = new Packet23VehicleSpawn();
		p23.a = entityId;
		p23.b = MathHelper.floor(x * 32.0D);
		p23.c = MathHelper.floor(y * 32.0D);
		p23.d = MathHelper.floor(z * 32.0D);
		p23.h = typeId;
		p23.i = dataValue;
		return p23;
	}


	@Override
	public void sendSpawn(Player player, Entity entity, Location location, EntityType entityType) {
		if (entityType instanceof MobType) {
			sendSpawnMob(player, entity, location, (MobType) entityType);
		}
		else {
			sendSpawnOther(player, entity, location, (VehicleType) entityType);
		}
	}

	@Override
	public void sendSpawnMob(Player player, Entity entity, Location location, MobType mobType) {
		Vector position = location.getPosition();
		sendSpawnMob(player, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch(), mobType.getId());
	}

	@Override
	public void sendSpawnMob(Player player, int entityId, double x, double y, double z, float yaw, float pitch, int creatureTypeId) {
		sendPacket(player, createSpawnMobPacket(entityId, x, y, z, yaw, pitch, creatureTypeId));
	}

	@Override
	public void sendSpawnOther(Player player, Entity entity, Location location, VehicleType otherType) {
		final Vector position = location.getPosition();
		sendSpawnOther(player, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), otherType.getId(), 0);
	}

	@Override
	public void sendSpawnOther(Player player, int entityId, double x, double y, double z, int typeId, int dataValue) {
		sendPacket(player, createSpawnOtherPacket(entityId, x, y, z, typeId, dataValue));
	}

	@Override
	public void sendSpawn(Collection<Player> players, Entity entity, Location location, EntityType entityType) {
		if (entityType instanceof MobType) {
			sendSpawnMob(players, entity, location, (MobType) entityType);
		}
		else {
			sendSpawnOther(players, entity, location, (VehicleType) entityType);
		}
	}

	@Override
	public void sendSpawnMob(Collection<Player> players, Entity entity, Location location, MobType mobType) {
		Vector position = location.getPosition();
		sendSpawnMob(players, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch(), mobType.getId());
	}

	@Override
	public void sendSpawnMob(Collection<Player> players, int entityId, double x, double y, double z, float yaw, float pitch, int creatureTypeId) {
		sendPacket(players, createSpawnMobPacket(entityId, x, y, z, yaw, pitch, creatureTypeId));
	}

	@Override
	public void sendSpawnOther(Collection<Player> players, Entity entity, Location location, VehicleType otherType) {
		final Vector position = location.getPosition();
		sendSpawnOther(players, entity.getEntityId(), position.getX(), position.getY(), position.getZ(), otherType.getId(), 0);
	}

	@Override
	public void sendSpawnOther(Collection<Player> players, int entityId, double x, double y, double z, int typeId, int dataValue) {
		sendPacket(players, createSpawnOtherPacket(entityId, x, y, z, typeId, dataValue));
	}


	private Packet130UpdateSign createSignUpdatePacket(int x, int y, int z, String[] lines) {
		return new Packet130UpdateSign(x, y, z, lines);
	}

	@Override
	public void sendSignUpdate(Player player, Vector position, Sign signState) {
		sendSignUpdate(player, position, signState.getLines());
	}

	@Override
	public void sendSignUpdate(Player player, Vector position, String[] lines) {
		sendSignUpdate(player, (int) position.getX(), (int) position.getY(), (int) position.getZ(), lines);
	}

	@Override
	public void sendSignUpdate(Player player, int x, int y, int z, String[] lines) {
		sendPacket(player, createSignUpdatePacket(x, y, z, lines));
	}

	@Override
	public void sendSignUpdate(Collection<Player> players, Vector position, Sign signState) {
		sendSignUpdate(players, position, signState.getLines());
	}

	@Override
	public void sendSignUpdate(Collection<Player> players, Vector position, String[] lines) {
		sendSignUpdate(players, (int) position.getX(), (int) position.getY(), (int) position.getZ(), lines);
	}

	@Override
	public void sendSignUpdate(Collection<Player> players, int x, int y, int z, String[] lines) {
		sendPacket(players, createSignUpdatePacket(x, y, z, lines));
	}


	private Packet11PlayerPosition createPlayerPositionPacket(double x, double y, double z) {
		final Packet11PlayerPosition p11 = new Packet11PlayerPosition();
		p11.x = x;
		p11.y = y + 1.62;
		p11.stance = y;
		p11.z = z;
		return p11;
	}

	@Override
	public void sendPlayerPosition(Player player, Vector position) {
		sendPlayerPosition(player, position.getX(), position.getY(), position.getZ());
	}

	@Override
	public void sendPlayerPosition(Player player, double x, double y, double z) {
		sendPacket(player, createPlayerPositionPacket(x, y, z));
	}



	private static void sendPacket(Collection<Player> players, Packet packet) {
		for (Player player : players) {
			sendPacket(player, packet);
		}
	}

	private static void sendPacket(final Player player, final Packet packet) {
		((org.bukkit.craftbukkit.entity.CraftPlayer) BukkitUtils.unwrap(player)).getHandle().netServerHandler.sendPacket(packet);
	}
}
