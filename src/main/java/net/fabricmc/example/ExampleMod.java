package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "modid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// an instance of our new item
	// public static final Item FABRIC_ITEM = new Item(new
	// FabricItemSettings().group(ItemGroup.MISC));
	public static final GrenadeItem GRENADE_ITEM = new GrenadeItem(new FabricItemSettings().group(ItemGroup.MISC));

	public static final ItemGroup GRENADE_ITEM_GROUP = FabricItemGroupBuilder.build(
			new Identifier("tutorial", "general"),
			() -> new ItemStack(ExampleMod.GRENADE_ITEM));

	public static final EntityType<GrenadeEntity> GRENADE_ENTITY_TYPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("grenade", "grenade_item"),
			FabricEntityTypeBuilder.<GrenadeEntity>create(SpawnGroup.MISC, GrenadeEntity::new)
					.dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
					.trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents
																// it from breaking, lol)
					.build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
	);

	public static final Identifier PacketID = new Identifier("grenade", "grenade_item");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		Registry.register(Registry.ITEM, new Identifier("grenade", "grenade_item"), GRENADE_ITEM);
		EntityRendererRegistry.register(GRENADE_ENTITY_TYPE, (context) -> new FlyingItemEntityRenderer(context));

		// receiveEntityPacket();

	}

	// public void receiveEntityPacket() {
	// 	ClientPlayNetworking.registerGlobalReceiver(PacketID, (client, handler, byteBuf, responseSender) -> {
	// 		EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
	// 		UUID uuid = byteBuf.readUuid();
	// 		int entityId = byteBuf.readVarInt();
	// 		Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
	// 		float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
	// 		float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
	// 		client.execute(() -> {
	// 			MinecraftClient mineClient = MinecraftClient.getInstance();
	// 			if (mineClient.world == null)
	// 				throw new IllegalStateException("Tried to spawn entity in a null world!");
	// 			Entity e = et.create(mineClient.world);
	// 			if (e == null)
	// 				throw new IllegalStateException(
	// 						"Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
	// 			e.updateTrackedPosition(pos);
	// 			e.setPos(pos.x, pos.y, pos.z);
	// 			e.setPitch(pitch);
	// 			e.setYaw(yaw);
	// 			// e.setEntityId(entityId);
	// 			e.setUuid(uuid);
	// 			mineClient.world.addEntity(entityId, e);
	// 			mineClient.close();
	// 		});

	// 	});

	// }

}
