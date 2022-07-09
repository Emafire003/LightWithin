package me.emafire003.dev.lightwithin.blocks;

import me.emafire003.dev.lightwithin.LightWithin;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LightBlocks {


    public static final Block FROZEN_PLAYER_TOP_BLOCK = registerBlock("frozen_player_top",
            new FrozenPlayerTopBlock(FabricBlockSettings.of(Material.ICE).strength(0.1f).collidable(true).slipperiness(0.9f).luminance(2).sounds(BlockSoundGroup.GLASS)), ItemGroup.DECORATIONS);

    public static final Block FROZEN_PLAYER_BOTTOM_BLOCK = registerBlock("frozen_player_bottom",
            new FrozenPlayerBottomBlock(FabricBlockSettings.of(Material.ICE).strength(0.1f).collidable(true).slipperiness(0.9f).luminance(2).sounds(BlockSoundGroup.GLASS)), ItemGroup.DECORATIONS);

    public static final Block ICE_WALL = registerBlock("ice_wall",
            new WallBlock(FabricBlockSettings.of(Material.ICE).strength(0.1f).collidable(true).slipperiness(0.9f).sounds(BlockSoundGroup.GLASS).nonOpaque()), ItemGroup.DECORATIONS);

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, new Identifier(LightWithin.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        return Registry.register(Registry.ITEM, new Identifier(LightWithin.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(tab)));
    }

    public static void registerBlocks() {
        LightWithin.LOGGER.debug("Registering Blocks for " + LightWithin.MOD_ID);
    }
}
