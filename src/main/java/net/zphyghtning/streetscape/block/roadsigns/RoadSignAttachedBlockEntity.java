package net.zphyghtning.streetscape.block.roadsigns;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.zphyghtning.streetscape.block.entity.ModBlockEntities;
import net.zphyghtning.streetscape.api.block.IBlockHolder;
import net.zphyghtning.streetscape.api.client.model.ExtraModelData;
import net.zphyghtning.streetscape.api.client.model.IExtraModelDataProvider;
import org.jetbrains.annotations.Nullable;

public class RoadSignAttachedBlockEntity extends BlockEntity implements IBlockHolder, IExtraModelDataProvider {
    private BlockState baseBlockState = Blocks.AIR.getDefaultState();
    private BlockState signBlockState = Blocks.AIR.getDefaultState();
    private int rotation = 0;
    private boolean creativeBroken = false;

    public boolean isCreativeBroken() {
        return this.creativeBroken;
    }

    public void setCreativeBroken(boolean creativeBroken) {
        this.creativeBroken = creativeBroken;
    }

    public RoadSignAttachedBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_SIGN_ATTACHED_BE, pos, state);
    }

    @Override
    public BlockState getHeldBlock() {
        return baseBlockState;
    }

    @Override
    public void setHeldBlock(BlockState baseBlockState) {
        this.baseBlockState = baseBlockState;
        this.markDirty();
    }

    @Override
    public ExtraModelData getExtraModelData() {
        return new ExtraModelData(baseBlockState);
    }

    public BlockState getBaseBlockState() {
        return baseBlockState;
    }

    public void setBaseBlockState(BlockState baseBlockState) {
        this.baseBlockState = baseBlockState;
        this.markDirty();
    }

    public BlockState getSignBlockState() {
        return signBlockState;
    }

    public void setSignBlockState(BlockState signBlockState) {
        this.signBlockState = signBlockState;
        this.markDirty();
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        this.markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (baseBlockState != null) {
            nbt.put("BaseState", NbtHelper.fromBlockState(baseBlockState));
        }
        if (signBlockState != null) {
            nbt.put("SignState", NbtHelper.fromBlockState(signBlockState));
        }
        nbt.putInt("Rotation", rotation);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        RegistryWrapper<net.minecraft.block.Block> blockLookup = registries.getWrapperOrThrow(RegistryKeys.BLOCK);
        if (nbt.contains("BaseState")) {
            baseBlockState = NbtHelper.toBlockState(blockLookup, nbt.getCompound("BaseState"));
        } else {
            baseBlockState = Blocks.AIR.getDefaultState();
        }
        if (nbt.contains("SignState")) {
            signBlockState = NbtHelper.toBlockState(blockLookup, nbt.getCompound("SignState"));
        } else {
            signBlockState = Blocks.AIR.getDefaultState();
        }
        rotation = nbt.getInt("Rotation");
        if (world != null && world.isClient) {
            BlockState state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, 2);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }
}
