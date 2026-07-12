package vltno.essentials;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import vltno.essentials.commands.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class EssentialsCommands {
    public static class HomePosition {
        public final double x, y, z;
        public final float yaw, pitch;
        public final String dimension;

        public HomePosition(double x, double y, double z, float yaw, float pitch, String dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.dimension = dimension;
        }
    }

    public static final java.util.Map<String, HomePosition> offlinePositions = new java.util.HashMap<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final java.util.Map<String, KitData> KITS = new java.util.HashMap<>();
    public static final java.util.Map<String, HomePosition> JAILS = new java.util.HashMap<>();
    public static final java.util.Map<String, HomePosition> WARPS = new java.util.HashMap<>();
    public static final java.util.Map<java.util.UUID, java.util.UUID> replyMap = new java.util.HashMap<>();
    public static final java.util.Map<java.util.UUID, HomePosition> backPositions = new java.util.HashMap<>();
    public static final java.util.Map<java.util.UUID, TeleportRequest> pendingRequests = new java.util.HashMap<>();
    public static final java.util.Set<java.util.UUID> afkPlayers = new java.util.HashSet<>();
    public static final java.util.Map<String, Double> itemWorth = new java.util.HashMap<>();

    public static class KitData {
        public int delay;
        public java.util.List<String> items = new java.util.ArrayList<>();
    }

    public static class TeleportRequest {
        public final java.util.UUID sender;
        public final boolean isTpaHere;
        public final long timestamp;

        public TeleportRequest(java.util.UUID sender, boolean isTpaHere) {
            this.sender = sender;
            this.isTpaHere = isTpaHere;
            this.timestamp = System.currentTimeMillis();
        }
    }

        public static File getConfigFile(String name) {
        File dir = new File("config/essentials-fabric");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, name);
    }

    public static File getKitsFile() { return getConfigFile("kits.json"); }
    public static File getJailsFile() { return getConfigFile("jails.json"); }
    public static File getWarpsFile() { return getConfigFile("warps.json"); }
    public static File getDataFile() { return getConfigFile("offline_data.json"); }
    public static File getWorthFile() { return getConfigFile("worth.json"); }

    public static void loadWorth() {
        File file = getWorthFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, Double>>(){}.getType();
                java.util.Map<String, Double> loaded = GSON.fromJson(reader, type);
                if (loaded != null) { itemWorth.clear(); itemWorth.putAll(loaded); }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void saveWorth() {
        try (FileWriter writer = new FileWriter(getWorthFile())) {
            GSON.toJson(itemWorth, writer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void loadKits() {
        File file = getKitsFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, KitData>>() {
                }.getType();
                java.util.Map<String, KitData> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    KITS.clear();
                    KITS.putAll(loaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveKits() {
        try (FileWriter writer = new FileWriter(getKitsFile())) {
            GSON.toJson(KITS, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadJailsWarps() {
        if (getJailsFile().exists()) {
            try (FileReader reader = new FileReader(getJailsFile())) {
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, new TypeToken<java.util.Map<String, HomePosition>>() {
                }.getType());
                if (loaded != null) {
                    JAILS.clear();
                    JAILS.putAll(loaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getWarpsFile().exists()) {
            try (FileReader reader = new FileReader(getWarpsFile())) {
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, new TypeToken<java.util.Map<String, HomePosition>>() {
                }.getType());
                if (loaded != null) {
                    WARPS.clear();
                    WARPS.putAll(loaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveJailsWarps() {
        try (FileWriter writer = new FileWriter(getJailsFile())) {
            GSON.toJson(JAILS, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(getWarpsFile())) {
            GSON.toJson(WARPS, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadData(net.minecraft.server.MinecraftServer server) {
        File file = getDataFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, HomePosition>>() {
                }.getType();
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    offlinePositions.clear();
                    offlinePositions.putAll(loaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData(net.minecraft.server.MinecraftServer server) {
        try (FileWriter writer = new FileWriter(getDataFile())) {
            GSON.toJson(offlinePositions, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onPlayerDisconnect(net.minecraft.server.network.ServerGamePacketListenerImpl handler, net.minecraft.server.MinecraftServer server) {
        ServerPlayer player = handler.player;
        String dim = player.level().dimension().identifier().toString();
        offlinePositions.put(player.getName().getString().toLowerCase(), new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim));
        saveData(server);
    }

    public static void saveBackLocation(ServerPlayer player) {
        backPositions.put(player.getUUID(), new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString()));
    }

    public static java.util.function.Predicate<CommandSourceStack> require(String node, int defaultLevel) {
        return source -> me.lucko.fabric.api.permissions.v0.Permissions.check(source, node, defaultLevel);
    }

    public static void registerEvents() {
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            UserData data = UserCache.getUser(player.getUUID());
            if (data.jail != null && !me.lucko.fabric.api.permissions.v0.Permissions.check(player, "essentials.jail.bypass", 4)) {
                if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("You cannot break blocks while jailed.").withStyle(net.minecraft.ChatFormatting.RED));
                return false;
            }
            return true;
        });
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            UserData data = UserCache.getUser(player.getUUID());
            if (data.jail != null && !me.lucko.fabric.api.permissions.v0.Permissions.check(player, "essentials.jail.bypass", 4)) {
                if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("You cannot interact while jailed.").withStyle(net.minecraft.ChatFormatting.RED));
                return net.minecraft.world.InteractionResult.FAIL;
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClientSide() && player instanceof ServerPlayer sp) {
                UserData data = UserCache.getUser(sp.getUUID());
                if (data.powertoolEnabled && !data.powertools.isEmpty()) {
                    net.minecraft.world.item.ItemStack stack = sp.getItemInHand(hand);
                    if (!stack.isEmpty()) {
                        String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                        String cmd = data.powertools.get(itemId);
                        if (cmd != null) {
                            if (cmd.contains("{player}")) {
                                // requires clicking a player, this is just right clicking air/block so ignore {player} macros for now
                            } else {
                                sp.level().getServer().getCommands().performPrefixedCommand(sp.createCommandSourceStack(), cmd);
                            }
                        }
                    }
                }
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide() && player instanceof ServerPlayer sp && entity instanceof ServerPlayer targetPlayer) {
                UserData data = UserCache.getUser(sp.getUUID());
                if (data.powertoolEnabled && !data.powertools.isEmpty()) {
                    net.minecraft.world.item.ItemStack stack = sp.getItemInHand(hand);
                    if (!stack.isEmpty()) {
                        String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                        String cmd = data.powertools.get(itemId);
                        if (cmd != null && cmd.contains("{player}")) {
                            cmd = cmd.replace("{player}", targetPlayer.getName().getString());
                            sp.level().getServer().getCommands().performPrefixedCommand(sp.createCommandSourceStack(), cmd);
                        }
                    }
                }
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            UserData data = UserCache.getUser(player.getUUID());

            // Restore God Mode
            player.setInvulnerable(data.godMode);

            // Restore Fly State
            player.getAbilities().mayfly = data.isFlying;
            if (!data.isFlying) {
                player.getAbilities().flying = false;
            }

            // Restore Speeds
            player.getAbilities().setFlyingSpeed(data.flySpeed);
            player.getAbilities().setWalkingSpeed(data.walkSpeed);
            net.minecraft.world.entity.ai.attributes.AttributeInstance walkAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
            if (walkAttr != null) {
                walkAttr.setBaseValue(data.walkSpeed);
            }

            // Sync to client immediately
            player.onUpdateAbilities();

            // If they were vanished, resend the vanish packets to people without permissions
            if (data.isVanished) {
                net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket packet = new net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket(java.util.Collections.singletonList(player.getUUID()));
                for (ServerPlayer other : server.getPlayerList().getPlayers()) {
                    if (other != player && !server.getPlayerList().isOp(other.nameAndId())) {
                        other.connection.send(packet);
                    }
                }
            }
        });
        net.fabricmc.fabric.api.message.v1.ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            UserData data = UserCache.getUser(sender.getUUID());
            if (data.isMuted) {
                if (data.muteTimeout > 0 && System.currentTimeMillis() > data.muteTimeout) {
                    data.isMuted = false;
                    data.muteTimeout = 0;
                    return true;
                }
                sender.sendSystemMessage(Component.literal("You cannot speak because you are muted.").withStyle(net.minecraft.ChatFormatting.RED));
                return false;
            }
            return true;
        });
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);
        registerEvents();
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register(EssentialsCommands::onPlayerDisconnect);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            loadData(server);
            loadKits();
            loadJailsWarps();
            loadWorth();
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            saveData(server);
            saveKits();
            saveJailsWarps();
            saveWorth();
        });

    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        CommandSethealth.register(dispatcher, registryAccess);
        CommandSethunger.register(dispatcher, registryAccess);
        CommandGethealth.register(dispatcher, registryAccess);
        CommandGethunger.register(dispatcher, registryAccess);
        CommandTpa.register(dispatcher, registryAccess);
        CommandTpahere.register(dispatcher, registryAccess);
        CommandTpall.register(dispatcher, registryAccess);
        CommandTpo.register(dispatcher, registryAccess);
        CommandTpohere.register(dispatcher, registryAccess);
        CommandTpoffline.register(dispatcher, registryAccess);
        CommandAfk.register(dispatcher, registryAccess);
        CommandAntioch.register(dispatcher, registryAccess);
        CommandAnvil.register(dispatcher, registryAccess);
        CommandBack.register(dispatcher, registryAccess);
        CommandBackup.register(dispatcher, registryAccess);
        CommandBalance.register(dispatcher, registryAccess);
        CommandBalancetop.register(dispatcher, registryAccess);
        CommandBan.register(dispatcher, registryAccess);
        CommandBanip.register(dispatcher, registryAccess);
        CommandBeezooka.register(dispatcher, registryAccess);
        CommandBook.register(dispatcher, registryAccess);
        CommandBottom.register(dispatcher, registryAccess);
        CommandBreak.register(dispatcher, registryAccess);
        CommandBroadcast.register(dispatcher, registryAccess);
        CommandBroadcastworld.register(dispatcher, registryAccess);
        CommandBigtree.register(dispatcher, registryAccess);
        CommandBurn.register(dispatcher, registryAccess);
        CommandCartographytable.register(dispatcher, registryAccess);
        CommandClearinventory.register(dispatcher, registryAccess);
        CommandClearinventoryconfirmtoggle.register(dispatcher, registryAccess);
        CommandCondense.register(dispatcher, registryAccess);
        CommandCompass.register(dispatcher, registryAccess);
        CommandCreatekit.register(dispatcher, registryAccess);
        CommandCustomtext.register(dispatcher, registryAccess);
        CommandDelhome.register(dispatcher, registryAccess);
        CommandDeljail.register(dispatcher, registryAccess);
        CommandDelkit.register(dispatcher, registryAccess);
        CommandDelwarp.register(dispatcher, registryAccess);
        CommandDepth.register(dispatcher, registryAccess);
        CommandDisposal.register(dispatcher, registryAccess);
        CommandEco.register(dispatcher, registryAccess);
        CommandEnchant.register(dispatcher, registryAccess);
        CommandEnderchest.register(dispatcher, registryAccess);
        CommandEssentials.register(dispatcher, registryAccess);
        CommandExp.register(dispatcher, registryAccess);
        CommandExt.register(dispatcher, registryAccess);
        CommandFeed.register(dispatcher, registryAccess);
        CommandFly.register(dispatcher, registryAccess);
        CommandFireball.register(dispatcher, registryAccess);
        CommandFirework.register(dispatcher, registryAccess);
        CommandGamemode.register(dispatcher, registryAccess);
        CommandGc.register(dispatcher, registryAccess);
        CommandGetpos.register(dispatcher, registryAccess);
        CommandGive.register(dispatcher, registryAccess);
        CommandGod.register(dispatcher, registryAccess);
        CommandGrindstone.register(dispatcher, registryAccess);
        CommandHat.register(dispatcher, registryAccess);
        CommandHeal.register(dispatcher, registryAccess);
        CommandHelp.register(dispatcher, registryAccess);
        CommandHelpop.register(dispatcher, registryAccess);
        CommandHome.register(dispatcher, registryAccess);
        CommandIce.register(dispatcher, registryAccess);
        CommandIgnore.register(dispatcher, registryAccess);
        CommandInfo.register(dispatcher, registryAccess);
        CommandInvsee.register(dispatcher, registryAccess);
        CommandItem.register(dispatcher, registryAccess);
        CommandItemdb.register(dispatcher, registryAccess);
        CommandItemlore.register(dispatcher, registryAccess);
        CommandItemname.register(dispatcher, registryAccess);
        CommandJailedplayers.register(dispatcher, registryAccess);
        CommandJails.register(dispatcher, registryAccess);
        CommandJump.register(dispatcher, registryAccess);
        CommandKick.register(dispatcher, registryAccess);
        CommandKickall.register(dispatcher, registryAccess);
        CommandKill.register(dispatcher, registryAccess);
        CommandKit.register(dispatcher, registryAccess);
        CommandKitreset.register(dispatcher, registryAccess);
        CommandKittycannon.register(dispatcher, registryAccess);
        CommandLightning.register(dispatcher, registryAccess);
        CommandList.register(dispatcher, registryAccess);
        CommandLoom.register(dispatcher, registryAccess);
        CommandMail.register(dispatcher, registryAccess);
        CommandMe.register(dispatcher, registryAccess);
        CommandMore.register(dispatcher, registryAccess);
        CommandMotd.register(dispatcher, registryAccess);
        CommandMsg.register(dispatcher, registryAccess);
        CommandMsgtoggle.register(dispatcher, registryAccess);
        CommandMute.register(dispatcher, registryAccess);
        CommandNear.register(dispatcher, registryAccess);
        CommandNick.register(dispatcher, registryAccess);
        CommandNuke.register(dispatcher, registryAccess);
        CommandPay.register(dispatcher, registryAccess);
        CommandPaytoggle.register(dispatcher, registryAccess);
        CommandPayconfirmtoggle.register(dispatcher, registryAccess);
        CommandPing.register(dispatcher, registryAccess);
        CommandPlaytime.register(dispatcher, registryAccess);
        CommandPotion.register(dispatcher, registryAccess);
        CommandPowertool.register(dispatcher, registryAccess);
        CommandPowertoollist.register(dispatcher, registryAccess);
        CommandPowertooltoggle.register(dispatcher, registryAccess);
        CommandPtime.register(dispatcher, registryAccess);
        CommandPweather.register(dispatcher, registryAccess);
        CommandR.register(dispatcher, registryAccess);
        CommandRtoggle.register(dispatcher, registryAccess);
        CommandRealname.register(dispatcher, registryAccess);
        CommandRecipe.register(dispatcher, registryAccess);
        CommandRemove.register(dispatcher, registryAccess);
        CommandRenamehome.register(dispatcher, registryAccess);
        CommandRepair.register(dispatcher, registryAccess);
        CommandRest.register(dispatcher, registryAccess);
        CommandRules.register(dispatcher, registryAccess);
        CommandSeen.register(dispatcher, registryAccess);
        CommandSell.register(dispatcher, registryAccess);
        CommandSethome.register(dispatcher, registryAccess);
        CommandSetjail.register(dispatcher, registryAccess);
        CommandSettpr.register(dispatcher, registryAccess);
        CommandSetwarp.register(dispatcher, registryAccess);
        CommandSetworth.register(dispatcher, registryAccess);
        CommandShowkit.register(dispatcher, registryAccess);
        CommandEditsign.register(dispatcher, registryAccess);
        CommandSkull.register(dispatcher, registryAccess);
        CommandSmithingtable.register(dispatcher, registryAccess);
        CommandSocialspy.register(dispatcher, registryAccess);
        CommandSpawner.register(dispatcher, registryAccess);
        CommandSpawnmob.register(dispatcher, registryAccess);
        CommandSpeed.register(dispatcher, registryAccess);
        CommandStonecutter.register(dispatcher, registryAccess);
        CommandSudo.register(dispatcher, registryAccess);
        CommandSuicide.register(dispatcher, registryAccess);
        CommandTempban.register(dispatcher, registryAccess);
        CommandTempbanip.register(dispatcher, registryAccess);
        CommandThunder.register(dispatcher, registryAccess);
        CommandTime.register(dispatcher, registryAccess);
        CommandTogglejail.register(dispatcher, registryAccess);
        CommandTop.register(dispatcher, registryAccess);
        CommandTp.register(dispatcher, registryAccess);
        CommandTpaall.register(dispatcher, registryAccess);
        CommandTpaccept.register(dispatcher, registryAccess);
        CommandTpauto.register(dispatcher, registryAccess);
        CommandTpacancel.register(dispatcher, registryAccess);
        CommandTpdeny.register(dispatcher, registryAccess);
        CommandTphere.register(dispatcher, registryAccess);
        CommandTppos.register(dispatcher, registryAccess);
        CommandTpr.register(dispatcher, registryAccess);
        CommandTptoggle.register(dispatcher, registryAccess);
        CommandTree.register(dispatcher, registryAccess);
        CommandUnban.register(dispatcher, registryAccess);
        CommandUnbanip.register(dispatcher, registryAccess);
        CommandUnlimited.register(dispatcher, registryAccess);
        CommandVanish.register(dispatcher, registryAccess);
        CommandWarp.register(dispatcher, registryAccess);
        CommandWarpinfo.register(dispatcher, registryAccess);
        CommandWeather.register(dispatcher, registryAccess);
        CommandWhois.register(dispatcher, registryAccess);
        CommandWorkbench.register(dispatcher, registryAccess);
        CommandWorld.register(dispatcher, registryAccess);
        CommandWorth.register(dispatcher, registryAccess);
    }
}