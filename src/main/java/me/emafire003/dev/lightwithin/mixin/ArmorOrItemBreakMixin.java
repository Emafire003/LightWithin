package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.ArmorOrToolBreakEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ArmorOrItemBreakMixin extends Entity {


    public ArmorOrItemBreakMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "sendEquipmentBreakStatus", at = @At("RETURN"))
    private void onSendEquipmentBreakStatus(EquipmentSlot slot, CallbackInfo ci) {
        ItemStack breakingItemStack = ((LivingEntity) (Object) this).getEquippedStack(slot);
        ArmorOrToolBreakEvent.EVENT.invoker().brokenItem(breakingItemStack);
    }

}
