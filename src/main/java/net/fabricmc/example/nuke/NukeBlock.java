package net.fabricmc.example.nuke;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class NukeBlock extends Block {
        public static final BooleanProperty UNSTABLE = Properties.UNSTABLE;
    
        public NukeBlock(Settings settings) {
            super(settings);
            this.setDefaultState((BlockState)this.getDefaultState().with(UNSTABLE, false));
        }
    
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (oldState.isOf(state.getBlock())) {
                return;
            }
            if (world.isReceivingRedstonePower(pos)) {
                NukeBlock.primeTnt(world, pos);
                world.removeBlock(pos, false);
            }
        }
    
        @Override
        public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
            if (world.isReceivingRedstonePower(pos)) {
                NukeBlock.primeTnt(world, pos);
                world.removeBlock(pos, false);
            }
        }
    
        @Override
        public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
            if (!world.isClient() && !player.isCreative() && state.get(UNSTABLE).booleanValue()) {
                NukeBlock.primeTnt(world, pos);
            }
            super.onBreak(world, pos, state, player);
        }
    
        @Override
        public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
            if (world.isClient) {
                return;
            }
            NukeEntity nukeEntity = new NukeEntity(world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, explosion.getCausingEntity());
            int i = nukeEntity.getFuse();
            nukeEntity.setFuse((short)(world.random.nextInt(i / 4) + i / 8));
            world.spawnEntity(nukeEntity);
        }
    
        public static void primeTnt(World world, BlockPos pos) {
            NukeBlock.primeTnt(world, pos, null);
        }
    
        private static void primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
            if (world.isClient) {
                return;
            }
            NukeEntity nukeEntity = new NukeEntity(world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, igniter);
            world.spawnEntity(nukeEntity);
            world.playSound(null, nukeEntity.getX(), nukeEntity.getY(), nukeEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent((Entity)igniter, GameEvent.PRIME_FUSE, pos);
        }
    
        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player2, Hand hand, BlockHitResult hit) {
            ItemStack itemStack = player2.getStackInHand(hand);
            if (itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FIRE_CHARGE)) {
                NukeBlock.primeTnt(world, pos, player2);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                Item item = itemStack.getItem();
                if (!player2.isCreative()) {
                    if (itemStack.isOf(Items.FLINT_AND_STEEL)) {
                        itemStack.damage(1, player2, player -> player.sendToolBreakStatus(hand));
                    } else {
                        itemStack.decrement(1);
                    }
                }
                player2.incrementStat(Stats.USED.getOrCreateStat(item));
                return ActionResult.success(world.isClient);
            }
            return super.onUse(state, world, pos, player2, hand, hit);
        }
    
        @Override
        public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
            if (!world.isClient) {
                BlockPos blockPos = hit.getBlockPos();
                Entity entity = projectile.getOwner();
                if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos)) {
                    NukeBlock.primeTnt(world, blockPos, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                    world.removeBlock(blockPos, false);
                }
            }
        }
    
        @Override
        public boolean shouldDropItemsOnExplosion(Explosion explosion) {
            return false;
        }
    
        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
            builder.add(UNSTABLE);
        }
    }