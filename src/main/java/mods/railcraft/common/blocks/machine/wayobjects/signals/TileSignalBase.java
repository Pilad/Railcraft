/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.wayobjects.signals;

import mods.railcraft.api.signals.SignalAspect;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.blocks.machine.interfaces.ITileLit;
import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;
import mods.railcraft.common.blocks.machine.interfaces.ITileShaped;
import mods.railcraft.common.plugins.buildcraft.triggers.IAspectProvider;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.IOException;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

public abstract class TileSignalBase extends TileMachineBase implements IAspectProvider, ITileShaped, ITileRotate, ITileLit {

    protected static final float BOUNDS = 0.15f;
    private static final EnumFacing[] UP_DOWN_AXES = {UP, DOWN};
    private static final AxisAlignedBB BOUNDING_BOX = AABBFactory.start().box().expandHorizontally(-BOUNDS).raiseFloor(0.35).build();
    private EnumFacing facing = EnumFacing.NORTH;
    private int prevLightValue;

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (axis == UP || axis == DOWN) {
            return false;
        }
        if (facing == axis) {
            facing = axis.getOpposite();
        } else {
            facing = axis;
        }
        markBlockForUpdate();
        return true;
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return UP_DOWN_AXES;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        return BOUNDING_BOX.offset(pos);
    }

    @Override
    public void update() {
        super.update();
        if (Game.isClient(worldObj)) {
            boolean needsUpdate = false;
            int lightValue = getLightValue();
            if (prevLightValue != lightValue) {
                prevLightValue = lightValue;
                worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPos());
                needsUpdate = true;
            }
            if (needsUpdate) {
                markBlockForUpdate();
            }
        }
    }

    @Override
    public int getLightValue() {
        return getSignalAspect().getLightValue();
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    @Override
    public void onBlockPlacedBy(@Nonnull IBlockState state, @Nonnull EntityLivingBase entityLiving, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(state, entityLiving, stack);
        facing = MiscTools.getHorizontalSideFacingPlayer(entityLiving);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("Facing", (byte) facing.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        facing = EnumFacing.getFront(data.getByte("Facing"));
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeByte((byte) facing.ordinal());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        facing = EnumFacing.getFront(data.readByte());

        markBlockForUpdate();
    }

    public abstract SignalAspect getSignalAspect();

    @Override
    public SignalAspect getTriggerAspect() {
        return getSignalAspect();
    }
}
