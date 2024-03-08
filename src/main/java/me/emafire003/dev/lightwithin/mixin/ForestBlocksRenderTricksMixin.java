package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.LightWithin;
import net.minecraft.block.Block;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class ForestBlocksRenderTricksMixin implements ToggleableFeature {

    @Inject(method = "hasDynamicBounds", at = @At("HEAD"), cancellable = true)
    public void modifyDynamicBounds(CallbackInfoReturnable<Boolean> cir) {

        //TODO viene chimato solo all'inizio quindi, forse Injectando dentro AbstractBlock funzina, ma piu che altro mi servirebbe già la Tag fatta, ma viene fatto dopo. Quindi o la precompilo o boh. Mixinno in tutti block constructor :/
        if(((Block) (Object) this).getDefaultState().isIn(LightWithin.FOREST_AURA_BLOCKS)){
            LightWithin.LOGGER.info("Modifying the thing to true, the block is: " + ((Block) (Object) this).getDefaultState());
            cir.setReturnValue(true);
        }else{
            LightWithin.LOGGER.info("Nope, The block is: " + ((Block) (Object) this).getDefaultState());
        }
    }


}
