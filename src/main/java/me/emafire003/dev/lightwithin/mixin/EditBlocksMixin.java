package me.emafire003.dev.lightwithin.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.Direction;
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
                    from = @At(value = "CONSTANT", args = "stringValue=dirt"),
                    to = @At(value = "CONSTANT", args = "stringValue=coarse_dirt")
            )
    )
    private static AbstractBlock.Settings modifyDirt(AbstractBlock.Settings settings) {
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }

    //DONE da "oak_log" a "muddy_mangrove_roots"
    //DONE da "stripped_spruce_log" to "flowering_azalea_leaves"
    //DONE da "brown_mushroom_block" a "mushroom_stem"
    //DONE da "mycelium" a "lily_pad"
    //DONE "grass"
    //"nether_wart_block" nah
    //da "warped_stem" a "stripped_warped_hyphae" nah
    //"warped_wart_block" nah
    //da "crimson_stem" a "stripped_crimson_hyphae" nah
    //DONE "moss_block" yes
    //DONE da "rooted_dirt" ok
    //TODO Vedere se magari è possibile fare un mixin nel rendering dei blocchi, perché funzionalmente, funziono con le tag
    //TODO è l'estetica che è un po' meh
    //Nel caso non sia possibile, scriptino in python per generarsi tutti modelli di robe eh


    //TODO Move this to .hasDynamicBounds mixin inside the block class! Magari anche solo nel client.
    //maybe blockState.getOpacity(world, blockPos) >= 15? Poi devo capire come renderizzare le facce interne. Probabilmente un override dentro il Block anziché AbstractBlock


    //Need for the forest aura effect
    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/SnowyBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
                    //,ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=podzol")
            )
    )
    private static AbstractBlock.Settings modifyPodzol(AbstractBlock.Settings settings) {
        return settings.dynamicBounds().solidBlock((state, world, pos)->{
            //TODO state.getBlock().onSteppedOn("fa cose"); poi quando c'è la entity collision modifica un valore, tipo shouldBeSolid = false, e quando la entity se ne va lo rimette a true"'
                return false;
        });
    }

    @ModifyArg(method = "createLogBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PillarBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
            )
    )
    private static AbstractBlock.Settings createLogBlock(AbstractBlock.Settings settings) {
        //TODO should find a way to only allow logs. Not stripped versions. And not nether woods
        //TODO also cherry is a bamboo block apparently. :/
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }

    //Need for the forest aura effect
    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PillarBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
                    //,ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=oak_wood"),
                    to = @At(value = "CONSTANT", args = "stringValue=stripped_mangrove_wood")
            )
    )
    private static AbstractBlock.Settings modifyWoods(AbstractBlock.Settings settings) {
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }

    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/MushroomBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
                    //,ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=brown_mushroom_block"),
                    to = @At(value = "CONSTANT", args = "stringValue=mushroom_stem")
            )
    )
    private static AbstractBlock.Settings modifyMushroom(AbstractBlock.Settings settings) {
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }

    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/MyceliumBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=mycelium")
            )
    )
    private static AbstractBlock.Settings modifyMycelium(AbstractBlock.Settings settings) {
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }

    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/GrassBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=grass_block")
            )
    )
    private static AbstractBlock.Settings modifyGrass(AbstractBlock.Settings settings) {
        //TODO nope non opaque isn't enough
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }

    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/MossBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=moss_block")
            )
    )
    private static AbstractBlock.Settings modifyMoss(AbstractBlock.Settings settings) {
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }

    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/RootedDirtBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=rooted_dirt")
            )
    )
    private static AbstractBlock.Settings modifyRootedDirt(AbstractBlock.Settings settings) {
        return settings.dynamicBounds().solidBlock(Blocks::never).nonOpaque();
    }
}
