package me.emafire003.dev.lightwithin.mixin;

import me.emafire003.dev.lightwithin.events.PlayerFirstJoinEvent;
import me.emafire003.dev.lightwithin.events.PlayerJoinEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerJoinMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect", cancellable = true)
    private void injectPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        PlayerJoinEvent.EVENT.invoker().joinServer(player, player.getServer());
        if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) < 1) {
            PlayerFirstJoinEvent.EVENT.invoker().joinServerForFirstTime(player, player.getServer());
        }

        ActionResult result1 = PlayerJoinEvent.EVENT.invoker().joinServer(player, player.getServer());
        if (result1 == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
