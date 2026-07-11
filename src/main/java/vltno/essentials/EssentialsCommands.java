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

    public static final java.util.Map<java.util.UUID, java.util.Map<String, HomePosition>> playerHomes = new java.util.HashMap<>();
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
    public static final java.util.Set<java.util.UUID> tpTogglePlayers = new java.util.HashSet<>();
    public static final java.util.Set<java.util.UUID> tpAutoPlayers = new java.util.HashSet<>();

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

    public static File getKitsFile() {
        return new File("essentials_kits.json");
    }

    public static File getJailsFile() {
        return new File("essentials_jails.json");
    }

    public static File getWarpsFile() {
        return new File("essentials_warps.json");
    }

        public static File getDataFile() { return new File("essentials_offline_data.json"); }
    public static File getWorthFile() { return new File("essentials_worth.json"); }

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
        return source -> {
            try {
                Class<?> permsClass = Class.forName("me.lucko.fabric.api.permissions.v0.Permissions");
                java.lang.reflect.Method checkMethod = permsClass.getMethod("check", CommandSourceStack.class, String.class, int.class);
                return (boolean) checkMethod.invoke(null, source, node, defaultLevel);
            } catch (Throwable t) {
                try {
                    if (defaultLevel <= 0) return true;
                    return source.getEntity() == null || source.getServer().getPlayerList().isOp(source.getPlayerOrException().nameAndId());
                } catch (Exception e) {
                    return true;
                }
            }
        };
    }

    public static void registerEvents() {
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            UserData data = UserCache.getUser(player.getUUID());
            if (data.jail != null) {
                if (player instanceof ServerPlayer sp)
                    sp.sendSystemMessage(Component.literal("You cannot break blocks while jailed.").withStyle(net.minecraft.ChatFormatting.RED));
                return false;
            }
            return true;
        });
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            UserData data = UserCache.getUser(player.getUUID());
            if (data.jail != null) {
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
        vltno.essentials.commands.CommandSethealth.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSethunger.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGethealth.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGethunger.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpa.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpahere.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpall.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpo.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpohere.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpoffline.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandAfk.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandAntioch.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandAnvil.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBack.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBackup.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBalance.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBalancetop.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBan.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBanip.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBeezooka.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBook.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBottom.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBreak.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBroadcast.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBroadcastworld.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBigtree.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandBurn.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandCartographytable.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandClearinventory.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandClearinventoryconfirmtoggle.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandCondense.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandCompass.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandCreatekit.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandCustomtext.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandDelhome.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandDeljail.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandDelkit.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandDelwarp.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandDepth.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandDisposal.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandEco.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandEnchant.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandEnderchest.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandEssentials.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandExp.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandExt.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandFeed.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandFly.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandFireball.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandFirework.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGamemode.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGc.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGetpos.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGive.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGod.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandGrindstone.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandHat.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandHeal.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandHelp.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandHelpop.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandHome.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandIce.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandIgnore.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandInfo.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandInvsee.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandItem.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandItemdb.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandItemlore.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandItemname.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandJailedplayers.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandJails.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandJump.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandKick.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandKickall.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandKill.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandKit.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandKitreset.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandKittycannon.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandLightning.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandList.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandLoom.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandMail.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandMe.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandMore.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandMotd.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandMsg.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandMsgtoggle.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandMute.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandNear.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandNick.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandNuke.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPay.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPaytoggle.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPayconfirmtoggle.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPing.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPlaytime.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPotion.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPowertool.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPowertoollist.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPowertooltoggle.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPtime.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandPweather.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandR.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRtoggle.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRealname.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRecipe.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRemove.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRenamehome.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRepair.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRest.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandRules.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSeen.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSell.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSethome.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSetjail.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSettpr.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSetwarp.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSetworth.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandShowkit.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandEditsign.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSkull.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSmithingtable.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSocialspy.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSpawner.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSpawnmob.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSpeed.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandStonecutter.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSudo.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandSuicide.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTempban.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTempbanip.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandThunder.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTime.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTogglejail.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTop.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTp.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpaall.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpaccept.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpauto.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpacancel.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpdeny.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTphere.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTppos.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTpr.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTptoggle.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandTree.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandUnban.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandUnbanip.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandUnlimited.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandVanish.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandWarp.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandWarpinfo.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandWeather.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandWhois.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandWorkbench.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandWorld.register(dispatcher, registryAccess);
        vltno.essentials.commands.CommandWorth.register(dispatcher, registryAccess);
    }
}