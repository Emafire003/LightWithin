package me.emafire003.dev.lightwithin.blocks;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;

public class LightBlocks {

    public static final Block CLEAR_ICE = registerBlock("clear_ice",
            new ClearIce(AbstractBlock.Settings.create().ticksRandomly().dynamicBounds().strength(0.1f).nonOpaque().notSolid().slipperiness(0.9f).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.BLUE_ICE);

    public static final Block FROZEN_PLAYER_TOP_BLOCK = registerBlock("frozen_player_top",
            new FrozenPlayerTopBlock(AbstractBlock.Settings.create().strength(0.1f).slipperiness(0.9f).luminance(value -> 2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block FROZEN_PLAYER_BOTTOM_BLOCK = registerBlock("frozen_player_bottom",
            new FrozenPlayerBottomBlock(AbstractBlock.Settings.create().strength(0.1f).slipperiness(0.9f).luminance(value -> 2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block FROZEN_MOB_TOP_BLOCK = registerBlock("frozen_mob_top",
            new FrozenMobTopBlock(AbstractBlock.Settings.create().strength(0.1f).slipperiness(0.9f).luminance(value -> 2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block FROZEN_MOB_BOTTOM_BLOCK = registerBlock("frozen_mob_bottom",
            new FrozenMobBottomBlock(AbstractBlock.Settings.create().strength(0.1f).slipperiness(0.9f).luminance(value -> 2).sounds(BlockSoundGroup.GLASS)), ItemGroups.NATURAL, Items.ICE);

    public static final Block ICE_WALL = registerBlock("ice_wall",
            new WallBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GLASS).strength(0.05f).slipperiness(0.9f).sounds(BlockSoundGroup.GLASS).nonOpaque()), ItemGroups.NATURAL, Items.ICE);

    private static Block registerBlock(String name, Block block, RegistryKey<ItemGroup> tab, Item add_after) {
        Block the_block = Registry.register(Registries.BLOCK, LightWithin.getIdentifier(name), block);
        Item the_item = Registry.register(Registries.ITEM, LightWithin.getIdentifier(name), new BlockItem(block, new Item.Settings()));
        ItemGroupEvents.modifyEntriesEvent(tab).register(content -> {
            content.addAfter(add_after, the_item);
        });
        return the_block;
    }

    public static void registerBlocks() {
        LightWithin.LOGGER.debug("Registering Blocks for " + LightWithin.MOD_ID);
    }
}
