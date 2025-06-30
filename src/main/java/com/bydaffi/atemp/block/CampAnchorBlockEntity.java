package com.bydaffi.atemp.block;

import com.bydaffi.atemp.block.entity.AtempBlockEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CampAnchorBlockEntity extends BlockEntity {
    private UUID bindUuid;

    public CampAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(AtempBlockEntityTypes.CAMP_ANCHOR_BLOCK_ENTITY, pos, state);
    }

    public UUID getBindUuid() {
        return bindUuid;
    }

    public void setBindUuid(UUID uuid) {
        this.bindUuid = uuid;
        markDirty();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("BindUuid")) {
            this.bindUuid = UUID.fromString(nbt.getString("BindUuid"));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.bindUuid != null) {
            nbt.putString("BindUuid", this.bindUuid.toString());
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}