/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.charge;

import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.UP;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockFrame extends BlockRailcraft implements IPostConnection {

    public BlockFrame() {
        super(Material.IRON);
        setResistance(10);
        setHardness(5);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 6),
                "PPP",
                "I I",
                "III",
                'P', RailcraftItems.PLATE, Metal.IRON,
                'I', RailcraftItems.REBAR);
    }

    @Override
    public void initializeDefinintion() {
//                HarvestPlugin.setStateHarvestLevel(instance, "crowbar", 0);
        HarvestPlugin.setBlockHarvestLevel("pickaxe", 1, this);

        ForestryPlugin.addBackpackItem("forestry.builder", this);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == UP;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (RailcraftBlocks.WIRE.isEqual(heldItem))
            //noinspection ConstantConditions
            if (WorldPlugin.setBlockState(worldIn, pos, RailcraftBlocks.WIRE.getDefaultState().withProperty(BlockWire.ADDON, BlockWire.Addon.FRAME), 2)) {
                if (!playerIn.capabilities.isCreativeMode)
                    playerIn.setHeldItem(hand, InvTools.depleteItem(heldItem));
                return true;
            }
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EnumFacing face) {
        return ConnectStyle.TWO_THIN;
    }

}
