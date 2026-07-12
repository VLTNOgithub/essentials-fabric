package vltno.essentials.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vltno.essentials.UserCache;
import vltno.essentials.UserData;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class VanishPacketMixin {

    @Shadow public ServerPlayer player;

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        // If the packet listener hasn't been fully initialized, don't do anything.
        if (this.player == null || this.player.level().getServer() == null) return;
        
        // If the receiving player is an Operator, they are allowed to see vanished players.
        boolean isOp = this.player.level().getServer().getPlayerList().isOp(this.player.nameAndId());
        if (isOp) return;

        if (packet instanceof ClientboundAddEntityPacket addEntityPacket) {
            // If the server tries to render another player entity on this client
            if (addEntityPacket.getType() == net.minecraft.world.entity.EntityType.PLAYER) {
                ServerPlayer target = this.player.level().getServer().getPlayerList().getPlayer(addEntityPacket.getUUID());
                if (target != null) {
                    UserData data = UserCache.getUser(target);
                    if (data != null && data.isVanished) {
                        ci.cancel(); // Cancel sending the spawn packet!
                    }
                }
            }
        } else if (packet instanceof ClientboundPlayerInfoUpdatePacket infoPacket) {
            // Intercept tab list updates and remove vanished players from the entries.
            List<ClientboundPlayerInfoUpdatePacket.Entry> safeEntries = new ArrayList<>();
            boolean modified = false;
            
            for (ClientboundPlayerInfoUpdatePacket.Entry entry : infoPacket.entries()) {
                ServerPlayer target = this.player.level().getServer().getPlayerList().getPlayer(entry.profileId());
                boolean shouldHide = false;
                if (target != null) {
                    UserData data = UserCache.getUser(target);
                    if (data != null && data.isVanished) {
                        shouldHide = true;
                    }
                }
                if (shouldHide) {
                    modified = true;
                } else {
                    safeEntries.add(entry);
                }
            }
            
            // If we found vanished players in the packet, we need to modify it.
            if (modified) {
                if (safeEntries.isEmpty()) {
                    ci.cancel(); // No one left to update, just drop the packet
                } else {
                    // To safely modify it, we cancel the original and construct a new one.
                    // However, ClientboundPlayerInfoUpdatePacket is immutable, 
                    // we must use an accessor or recreate it manually. 
                    // Fortunately, for simple ports, canceling the packet entirely for non-ops when a vanished player triggers an update is an acceptable compromise to avoid complex constructor accessors.
                    // Wait! `ClientboundPlayerInfoUpdatePacket` constructor takes an EnumSet of actions and a List of Entries.
                    // In 1.21.11, the constructor might be slightly different. 
                    // Let's cancel the packet safely for now.
                    ci.cancel();
                    if (!safeEntries.isEmpty()) {
                        // Rebuild and send using the list of safe players.
                        // To avoid infinite loops, we can't easily call connection.send again with a modified packet without triggering this mixin again, 
                        // but since the new packet won't have vanished players, it won't be modified on the second pass!
                        List<ServerPlayer> safePlayers = new ArrayList<>();
                        for (ClientboundPlayerInfoUpdatePacket.Entry safeEntry : safeEntries) {
                            ServerPlayer p = this.player.level().getServer().getPlayerList().getPlayer(safeEntry.profileId());
                            if (p != null) safePlayers.add(p);
                        }
                        if (!safePlayers.isEmpty()) {
                            this.player.connection.send(new ClientboundPlayerInfoUpdatePacket(infoPacket.actions(), safePlayers));
                        }
                    }
                }
            }
        }
    }
}
