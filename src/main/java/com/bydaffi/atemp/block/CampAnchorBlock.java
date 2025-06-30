package com.bydaffi.atemp.block;

import com.bydaffi.atemp.dimension.AtempDimensions;
import com.bydaffi.atemp.item.CampingMace;
import com.bydaffi.atemp.util.Manager;
import com.bydaffi.atemp.util.Settlement;
import com.bydaffi.atemp.util.StructureSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

public class CampAnchorBlock extends Block implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D)
    );

    public CampAnchorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, net.minecraft.util.math.Direction.NORTH)
                .with(LIT, Boolean.TRUE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            ItemStack heldItem = player.getStackInHand(hand);
            
            // Check if player is holding a camping mace
            if (heldItem.getItem() instanceof CampingMace) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof CampAnchorBlockEntity anchorEntity) {
                    UUID playerUuid = serverPlayer.getUuid();
                    UUID anchorOwnerUuid = anchorEntity.getBindUuid();
                    
                    // Check if the player owns this camp anchor
                    if (anchorOwnerUuid != null && playerUuid.equals(anchorOwnerUuid)) {
                        // Delete the portal structure around the anchor block
                        StructureSpawner.deletePlayerCamp(serverPlayer, pos);
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        serverPlayer.sendMessage(Text.literal("Camp dissolved! You can now create a new camp."), false);
                        return ActionResult.SUCCESS;
                    } else {
                        serverPlayer.sendMessage(Text.literal("You don't own this camp anchor."), false);
                        return ActionResult.FAIL;
                    }
                }
            } else {
                // Normal teleportation behavior
                world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                StructureSpawner.createOrTeleportToSettlement(serverPlayer);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.CONSUME;
    }


    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CampAnchorBlockEntity(pos, state);
    }
}