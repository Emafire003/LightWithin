package me.emafire003.dev.lightwithin.blocks;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class LightBlocks {

    public static final Block CLEAR_ICE = registerBlock("clear_ice",
            new ClearIce(FabricBlockSettings.create().dynamicBounds().strength(0.1f).nonOpaque().notSolid().slipperiness(0.9f).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.BLUE_ICE);
    public static final Block FROZEN_PLAYER_TOP_BLOCK = registerBlock("frozen_player_top",
            new FrozenPlayerTopBlock(FabricBlockSettings.create().strength(0.1f).collidable(true).slipperiness(0.9f).luminance(2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block FROZEN_PLAYER_BOTTOM_BLOCK = registerBlock("frozen_player_bottom",
            new FrozenPlayerBottomBlock(FabricBlockSettings.create().strength(0.1f).collidable(true).slipperiness(0.9f).luminance(2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block FROZEN_MOB_TOP_BLOCK = registerBlock("frozen_mob_top",
            new FrozenMobTopBlock(FabricBlockSettings.create().strength(0.1f).collidable(true).slipperiness(0.9f).luminance(2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block FROZEN_MOB_BOTTOM_BLOCK = registerBlock("frozen_mob_bottom",
            new FrozenMobBottomBlock(FabricBlockSettings.create().strength(0.1f).collidable(true).slipperiness(0.9f).luminance(2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block ICE_WALL = registerBlock("ice_wall",
            new WallBlock(FabricBlockSettings.create().sounds(BlockSoundGroup.GLASS).strength(0.05f).collidable(true).slipperiness(0.9f).sounds(BlockSoundGroup.GLASS).nonOpaque()), ItemGroups.NATURAL, Items.ICE);

    private static Block registerBlock(String name, Block block, RegistryKey<ItemGroup> tab, Item add_after) {
        //TODO add to the right invenotry tab

        Block the_block = Registry.register(Registries.BLOCK, new Identifier(LightWithin.MOD_ID, name), block);
        Item the_item = Registry.register(Registries.ITEM, new Identifier(LightWithin.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(tab).register(content -> {
            content.addAfter(add_after, the_item);
        });
        return the_block;
    }

    public static void registerBlocks() {
        LightWithin.LOGGER.debug("Registering Blocks for " + LightWithin.MOD_ID);
    }
}
