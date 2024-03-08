package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class EditBlocksMixin {

    //Need for the forest aura effect
    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
                    //,ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=grass_block"),
                    to = @At(value = "CONSTANT", args = "stringValue=podzol")
            )
    )
    private static AbstractBlock.Settings modifyDirt(AbstractBlock.Settings settings) {
        //da "oak_log" a "muddy_mangrove_roots"
        //da "stripped_spruce_log" to "flowering_azalea_leaves"
        //"cactus", "pumpkin", "melon"
        //da "brown_mushroom_block" a "mushroom_stem"
        //da "mycelium" a "lily_pad"
        //"nether_wart_block"
        //da "warped_stem" a "stripped_warped_hyphae"
        //"warped_wart_block"
        //da "crimson_stem" a "stripped_crimson_hyphae"
        //"shroomlight"
        //"moss_block"
        //da "rooted_dirt" a "mud"
        //TODO Vedere se magari è possibile fare un mixin nel rendering dei blocchi, perché funzionalmente, funziono con le tag
        //TODO è l'estetica che è un po' meh
        //Nel caso non sia possibile, scriptino in python per generarsi tutti modelli di robe eh


        //TODO Move this to .hasDynamicBounds mixin inside the block class! Magari anche solo nel client.
        //maybe blockState.getOpacity(world, blockPos) >= 15? Poi devo capire come renderizzare le facce interne. Probabilmente un override dentro il Block anziché AbstractBlock
        return settings.dynamicBounds().solidBlock(Blocks::never);
    }
}
