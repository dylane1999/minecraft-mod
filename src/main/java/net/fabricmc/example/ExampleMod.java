package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.grenade.GrenadeEntity;
import net.fabricmc.example.grenade.GrenadeItem;
import net.fabricmc.example.lightsaber.LightsaberItem;
import net.fabricmc.example.nuke.NukeBlock;
import net.fabricmc.example.nuke.NukeEntity;
import net.fabricmc.example.nuke.NukeRenderer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.sound.SoundEvent;
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

	public static final NukeBlock NUKE_BLOCK = new NukeBlock(FabricBlockSettings.of(Material.STONE).hardness(4.0f));

	public static final EntityType<NukeEntity> NUKE_ENTITY_TYPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("nuke", "nuke_block"),
			FabricEntityTypeBuilder.<NukeEntity>create(SpawnGroup.MISC, NukeEntity::new)
					.dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
					.trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents
																// it from breaking, lol)
					.build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
	);

	public static final ToolItem LIGHTSABER_ITEM = new LightsaberItem(ToolMaterials.DIAMOND, 6, -2.8F, new Item.Settings().group(ItemGroup.TOOLS));

	public static final Identifier LIGHTSABER_HIT_SOUND_ID = new Identifier("lightsaber:lightsaber_hit");
    public static SoundEvent LIGHTSABER_HIT_SOUND_EVENT = new SoundEvent(LIGHTSABER_HIT_SOUND_ID);

	public static final Identifier LIGHTSABER_KILL_SOUND_ID = new Identifier("lightsaber:lightsaber_kill");
    public static SoundEvent LIGHTSABER_KILL_SOUND_EVENT = new SoundEvent(LIGHTSABER_KILL_SOUND_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		Registry.register(Registry.ITEM, new Identifier("grenade", "grenade_item"), GRENADE_ITEM);
		Registry.register(Registry.ITEM, new Identifier("lightsaber", "lightsaber_item"), LIGHTSABER_ITEM);
		Registry.register(Registry.BLOCK, new Identifier("nuke", "nuke_block"), NUKE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("nuke", "nuke_item"),
				new BlockItem(NUKE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
		EntityRendererRegistry.register(GRENADE_ENTITY_TYPE, (context) -> new FlyingItemEntityRenderer(context));

		EntityRendererRegistry.register(NUKE_ENTITY_TYPE, (context) -> {
			return new NukeRenderer(context);
		});

		Registry.register(Registry.SOUND_EVENT, LIGHTSABER_HIT_SOUND_ID, LIGHTSABER_HIT_SOUND_EVENT);
		Registry.register(Registry.SOUND_EVENT, LIGHTSABER_KILL_SOUND_ID, LIGHTSABER_KILL_SOUND_EVENT);


		// receiveEntityPacket();

	}

}
