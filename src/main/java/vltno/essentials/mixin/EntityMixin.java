package vltno.essentials.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vltno.essentials.UserCache;
import vltno.essentials.UserData;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Component> cir) {
        if ((Object)this instanceof ServerPlayer player) {
            UserData data = UserCache.getUser(player.getUUID());
            if (data != null && data.nickname != null) {
                cir.setReturnValue(Component.literal("~" + data.nickname).withStyle(net.minecraft.ChatFormatting.ITALIC));
            }
        }
    }
}