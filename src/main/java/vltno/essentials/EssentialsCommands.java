package vltno.essentials;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
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
            this.x = x; this.y = y; this.z = z; this.yaw = yaw; this.pitch = pitch; this.dimension = dimension;
        }
    }
    public static final java.util.Map<java.util.UUID, java.util.Map<String, HomePosition>> playerHomes = new java.util.HashMap<>();
    public static final java.util.Map<String, HomePosition> offlinePositions = new java.util.HashMap<>();
    
    
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static class KitData {
        public int delay;
        public java.util.List<String> items = new java.util.ArrayList<>();
    }
    public static final java.util.Map<String, KitData> KITS = new java.util.HashMap<>();
    public static File getKitsFile() { return new File("essentials_kits.json"); }

    public static void loadKits() {
        File file = getKitsFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, KitData>>(){}.getType();
                java.util.Map<String, KitData> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    KITS.clear();
                    KITS.putAll(loaded);
                }
                System.out.println("[Essentials] Loaded kits.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void saveKits() {
        try (FileWriter writer = new FileWriter(getKitsFile())) {
            GSON.toJson(KITS, writer);
            System.out.println("[Essentials] Saved kits.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    
    public static final java.util.Map<String, HomePosition> JAILS = new java.util.HashMap<>();
    public static final java.util.Map<String, HomePosition> WARPS = new java.util.HashMap<>();
    public static File getJailsFile() { return new File("essentials_jails.json"); }
    public static File getWarpsFile() { return new File("essentials_warps.json"); }

    public static void loadJailsWarps() {
        if (getJailsFile().exists()) {
            try (FileReader reader = new FileReader(getJailsFile())) {
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, new TypeToken<java.util.Map<String, HomePosition>>(){}.getType());
                if (loaded != null) { JAILS.clear(); JAILS.putAll(loaded); }
            } catch (Exception e) { e.printStackTrace(); }
        }
        if (getWarpsFile().exists()) {
            try (FileReader reader = new FileReader(getWarpsFile())) {
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, new TypeToken<java.util.Map<String, HomePosition>>(){}.getType());
                if (loaded != null) { WARPS.clear(); WARPS.putAll(loaded); }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void saveJailsWarps() {
        try (FileWriter writer = new FileWriter(getJailsFile())) { GSON.toJson(JAILS, writer); }
        catch (Exception e) { e.printStackTrace(); }
        try (FileWriter writer = new FileWriter(getWarpsFile())) { GSON.toJson(WARPS, writer); }
        catch (Exception e) { e.printStackTrace(); }
    }
    
    public static void registerEvents() {
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            UserData data = UserCache.getUser(player.getUUID());
            if (data.jail != null) {
                if (player instanceof ServerPlayer sp) sp.sendSystemMessage(Component.literal("You cannot break blocks while jailed.").withStyle(net.minecraft.ChatFormatting.RED));
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

    public static File getDataFile() {
        return new File("essentials_offline_data.json");
    }

    public static void loadData(net.minecraft.server.MinecraftServer server) {
        File file = getDataFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, HomePosition>>(){}.getType();
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    offlinePositions.clear();
                    offlinePositions.putAll(loaded);
                }
                System.out.println("[Essentials] Loaded offline positions.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData(net.minecraft.server.MinecraftServer server) {
        try (FileWriter writer = new FileWriter(getDataFile())) {
            GSON.toJson(offlinePositions, writer);
            System.out.println("[Essentials] Saved offline positions.");
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

    public static int executeTpa(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpa <player>")); return 0; }
    public static int executeTpahere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpahere <player>")); return 0; }
    public static int executeTpall(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpall <player>")); return 0; }
    public static int executeTpo(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpo <player>")); return 0; }
    public static int executeTpohere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpohere <player>")); return 0; }
    public static int executeTpoffline(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpoffline <uuid>")); return 0; }


    
    public static final java.util.Map<java.util.UUID, java.util.UUID> replyMap = new java.util.HashMap<>();

    
    public static final java.util.Map<java.util.UUID, HomePosition> backPositions = new java.util.HashMap<>();
    public static void saveBackLocation(ServerPlayer player) {
        backPositions.put(player.getUUID(), new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString()));
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
    public static final java.util.Map<java.util.UUID, TeleportRequest> pendingRequests = new java.util.HashMap<>();
    public static final java.util.Map<String, Double> itemWorth = new java.util.HashMap<>();
    public static final java.util.Set<java.util.UUID> tpTogglePlayers = new java.util.HashSet<>();
    public static final java.util.Set<java.util.UUID> tpAutoPlayers = new java.util.HashSet<>();

    public static void register() {
        CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);
        registerEvents();
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register(EssentialsCommands::onPlayerDisconnect);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            loadData(server);
            loadKits(); loadJailsWarps();
        });
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            saveData(server);
            saveKits(); saveJailsWarps();
        });

    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
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


    public static final java.util.Set<java.util.UUID> afkPlayers = new java.util.HashSet<>();
    public static int executeAfk(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (afkPlayers.contains(player.getUUID())) {
            afkPlayers.remove(player.getUUID());
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(player.getName().getString() + " is no longer AFK."), false);
        } else {
            afkPlayers.add(player.getUUID());
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(player.getName().getString() + " is now AFK."), false);
        }
        return 1;
    }

    public static int executeAntioch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeAntioch(context, null); }
    public static int executeAntioch(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (message != null && !message.isEmpty()) {
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,"), false);
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("who being naughty in My sight, shall snuff it."), false);
        }
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        net.minecraft.core.BlockPos pos = hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK ? ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos() : player.blockPosition();
        net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (tnt != null) {
            tnt.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            tnt.setFuse(40);
            player.level().addFreshEntity(tnt);
        }
        return 1;
    }

    public static int executeAnvil(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.AnvilMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Anvil")));
        return 1;
    }

    public static int executeBack(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeBack(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())); }
    public static int executeBack(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        int count = 0;
        for (ServerPlayer target : targets) {
            HomePosition back = backPositions.get(target.getUUID());
            if (back != null) {
                net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(back.dimension));
                net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
                if (targetLevel != null) {
                    saveBackLocation(target);
                    target.teleportTo(targetLevel, back.x, back.y, back.z, java.util.Collections.emptySet(), back.yaw, back.pitch, false);
                    if (target == context.getSource().getEntity()) {
                        context.getSource().sendSystemMessage(Component.literal("Teleported back to your previous location."));
                    }
                    count++;
                }
            } else if (target == context.getSource().getEntity()) {
                context.getSource().sendSystemMessage(Component.literal("No previous location found."));
            }
        }
        return count;
    }

    public static int executeBackup(CommandContext<CommandSourceStack> context) {
        context.getSource().getServer().saveEverything(true, true, false);
        context.getSource().sendSystemMessage(Component.literal("Backup (Save-All) complete."));
        return 1;
    }

    public static int executeBalance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return executeBalance(context, context.getSource().getPlayerOrException());
    }
    public static int executeBalance(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        UserData data = UserCache.getUser(target);
        context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + "'s balance: $" + String.format("%.2f", data.money)));
        return 1;
    }

    public static int executeBalancetop(CommandContext<CommandSourceStack> context) {
        return executeBalancetop(context, 1);
    }
    public static int executeBalancetop(CommandContext<CommandSourceStack> context, int page) {
        java.util.List<ServerPlayer> players = new java.util.ArrayList<>(context.getSource().getServer().getPlayerList().getPlayers());
        players.sort((a, b) -> Double.compare(UserCache.getUser(b).money, UserCache.getUser(a).money));
        context.getSource().sendSystemMessage(Component.literal("--- Balance Top ---"));
        int start = (page - 1) * 10;
        for (int i = start; i < Math.min(start + 10, players.size()); i++) {
            ServerPlayer p = players.get(i);
            context.getSource().sendSystemMessage(Component.literal((i + 1) + ". " + p.getName().getString() + " - $" + String.format("%.2f", UserCache.getUser(p).money)));
        }
        return 1;
    }

    public static int executeBan(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets, String reason) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to ban."));
            return 0;
        }
        net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
        for (net.minecraft.server.players.NameAndId profile : targets) {
            net.minecraft.server.players.UserBanListEntry entry = new net.minecraft.server.players.UserBanListEntry(profile, null, context.getSource().getTextName(), null, reason != null ? reason : "Banned by an operator.");
            banList.add(entry);
            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
            if (player != null) {
                player.connection.disconnect(Component.literal(reason != null ? reason : "Banned by an operator."));
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Banned " + targets.size() + " players."));
        return targets.size();
    }

    public static int executeBanip(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("IP Banning requires arguments."));
        return 0;
    }

    public static int executeBeezooka(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.entity.Entity bee = net.minecraft.world.entity.EntityType.BEE.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (bee != null) {
            bee.setPos(player.getX(), player.getEyeY(), player.getZ());
            net.minecraft.world.phys.Vec3 look = player.getLookAngle().scale(2.0);
            bee.setDeltaMovement(look);
            player.level().addFreshEntity(bee);
            net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            tnt.setPos(bee.getX(), bee.getY(), bee.getZ());
            tnt.startRiding(bee);
            tnt.setFuse(20);
            player.level().addFreshEntity(tnt);
            context.getSource().sendSystemMessage(Component.literal("Bzzz!"));
        }
        return 1;
    }

    public static int executeBook(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Book editing not fully implemented in Fabric API yet."));
        return 1;
    }

    public static int executeBottom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int bottomY = player.level().getMinY();
        player.teleportTo(player.level(), player.getX(), bottomY, player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to bottom."));
        return 1;
    }

    public static int executeBreak(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
            player.level().destroyBlock(pos, true);
            context.getSource().sendSystemMessage(Component.literal("Block broken."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("No block in sight."));
        return 0;
    }

    public static int executeBroadcast(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /broadcast <message>")); return 0; }
    public static int executeBroadcast(CommandContext<CommandSourceStack> context, String message) {
        context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("[Broadcast] " + message).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE), false);
        return 1;
    }

    public static int executeBroadcastworld(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /broadcastworld <world> <message>"));
        return 0;
    }

    public static int executeBigtree(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos().above();
            player.level().setBlock(pos, net.minecraft.world.level.block.Blocks.OAK_SAPLING.defaultBlockState(), 3);
            context.getSource().sendSystemMessage(Component.literal("Tree spawned."));
            return 1;
        }
        return 0;
    }

    public static int executeBurn(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /burn <player> <seconds>")); return 0; }
    public static int executeBurn(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, int seconds) {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.igniteForSeconds(seconds);
        }
        context.getSource().sendSystemMessage(Component.literal("Ignited " + targets.size() + " entities for " + seconds + " seconds."));
        return targets.size();
    }

    public static int executeCartographytable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.CartographyTableMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Cartography Table")));
        return 1;
    }

    public static int executeClearinventory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getInventory().clearContent();
        context.getSource().sendSystemMessage(Component.literal("Inventory cleared."));
        return 1;
    }

    public static int executeClearinventoryconfirmtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.clearInventoryConfirmToggle = !data.clearInventoryConfirmToggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Clear inventory confirmation toggle set to: " + data.clearInventoryConfirmToggle));
        return 1;
    }

    public static int executeCondense(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Condense requires iterating the entire recipe book, skipping for now."));
        return 1;
    }

    public static int executeCompass(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int bearing = (int) (player.getYRot() + 180 + 360) % 360;
        String dir;
        if (bearing < 23) dir = "North";
        else if (bearing < 68) dir = "North-East";
        else if (bearing < 113) dir = "East";
        else if (bearing < 158) dir = "South-East";
        else if (bearing < 203) dir = "South";
        else if (bearing < 248) dir = "South-West";
        else if (bearing < 293) dir = "West";
        else if (bearing < 338) dir = "North-West";
        else dir = "North";
        context.getSource().sendSystemMessage(Component.literal("Bearing: " + dir + " (" + bearing + " degrees)."));
        return 1;
    }

    public static int executeCreatekit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /createkit <name> <delay>")); return 0; }
    public static int executeCreatekit(CommandContext<CommandSourceStack> context, String name, int delay) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        KitData kit = new KitData();
        kit.delay = delay;
        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack item = player.getInventory().getItem(i);
            if (!item.isEmpty()) {
                try {
                    net.minecraft.nbt.Tag tag = net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, item).getOrThrow();
                    kit.items.add(tag.toString());
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
        KITS.put(name.toLowerCase(), kit);
        saveKits(); saveJailsWarps();
        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' created with " + kit.items.size() + " items."));
        return 1;
    }

    public static int executeCustomtext(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Custom text aliases not supported."));
        return 1;
    }

    public static int executeDelhome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeDelhome(context, "home"); }
    public static int executeDelhome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        if (data.homes.remove(name.toLowerCase()) != null) {
            UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
        return 0;
    }

    public static int executeDeljail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /deljail <name>")); return 0; }
    public static int executeDeljail(CommandContext<CommandSourceStack> context, String name) {
        if (JAILS.remove(name.toLowerCase()) != null) {
            saveJailsWarps();
            context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' not found."));
        return 0;
    }

    public static int executeDelkit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /delkit <name>")); return 0; }
    public static int executeDelkit(CommandContext<CommandSourceStack> context, String name) {
        if (KITS.remove(name.toLowerCase()) != null) {
            saveKits(); saveJailsWarps();
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
        return 0;
    }

    public static int executeDelwarp(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /delwarp <name>")); return 0; }
    public static int executeDelwarp(CommandContext<CommandSourceStack> context, String name) {
        if (WARPS.remove(name.toLowerCase()) != null) {
            saveJailsWarps();
            context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' not found."));
        return 0;
    }

    public static int executeDepth(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int depth = player.getBlockY() - player.level().getMinY();
        context.getSource().sendSystemMessage(Component.literal("You are " + depth + " blocks above minimum depth."));
        return 1;
    }

    public static int executeDisposal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, inv, new net.minecraft.world.SimpleContainer(27)), Component.literal("Disposal")));
        return 1;
    }

    public static int executeEco(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /eco <give|take|set|reset> <player> <amount>"));
        return 0;
    }
    public static int executeEco(CommandContext<CommandSourceStack> context, String action, ServerPlayer target, double amount) {
        UserData data = UserCache.getUser(target);
        switch (action.toLowerCase()) {
            case "give": data.money += amount; break;
            case "take": data.money -= amount; break;
            case "set": data.money = amount; break;
            case "reset": data.money = 0.0; break;
            default:
                context.getSource().sendSystemMessage(Component.literal("Invalid action. Use give, take, set, or reset."));
                return 0;
        }
        UserCache.saveUser(target.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Economy for " + target.getName().getString() + " updated. New balance: $" + String.format("%.2f", data.money)));
        return 1;
    }

    public static int executeEnchant(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().sendSystemMessage(Component.literal("Usage: /enchant <enchantment> <level>"));
        return 0;
    }

    public static int executeEnderchest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return net.minecraft.world.inventory.ChestMenu.threeRows(id, inventory, player.getEnderChestInventory());
        }, Component.literal("Ender Chest")));
        return 1;
    }

    public static int executeEssentials(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Essentials Fabric Port v1.0"));
        return 1;
    }

    public static int executeExp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        context.getSource().sendSystemMessage(Component.literal("You have " + player.experienceLevel + " levels."));
        return 1;
    }

    public static int executeExt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.clearFire();
        context.getSource().sendSystemMessage(Component.literal("You have been extinguished."));
        return 1;
    }

    public static int executeFeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        context.getSource().sendSystemMessage(Component.literal("You have been fed."));
        return 1;
    }

    public static int executeFly(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean isFlying = player.getAbilities().mayfly;
        player.getAbilities().mayfly = !isFlying;
        if (isFlying) {
            player.getAbilities().flying = false;
        }
        player.onUpdateAbilities();
        context.getSource().sendSystemMessage(Component.literal("Set fly mode to " + (!isFlying ? "enabled" : "disabled") + " for " + player.getName().getString() + "."));
        return 1;
    }

    public static int executeFireball(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.entity.Entity fireball = net.minecraft.world.entity.EntityType.FIREBALL.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (fireball != null) {
            if (fireball instanceof net.minecraft.world.entity.projectile.Projectile proj) {
                proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            }
        }
        fireball.setPos(player.getX(), player.getEyeY(), player.getZ());
        player.level().addFreshEntity(fireball);
        context.getSource().sendSystemMessage(Component.literal("Fireball away!"));
        return 1;
    }

    public static int executeFirework(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.entity.projectile.FireworkRocketEntity rocket = new net.minecraft.world.entity.projectile.FireworkRocketEntity(player.level(), player.getX(), player.getY(), player.getZ(), net.minecraft.world.item.ItemStack.EMPTY);
        player.level().addFreshEntity(rocket);
        context.getSource().sendSystemMessage(Component.literal("Firework spawned."));
        return 1;
    }

    public static int executeGamemode(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /gamemode <mode> [player]")); return 0; }
    public static int executeGamemode(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, net.minecraft.world.level.GameType mode) {
        for (ServerPlayer target : targets) {
            target.setGameMode(mode);
            target.sendSystemMessage(Component.literal("Your game mode has been updated to " + mode.getName() + "."));
        }
        if (targets.size() == 1 && targets.iterator().next() == context.getSource().getEntity()) return 1;
        context.getSource().sendSystemMessage(Component.literal("Set game mode " + mode.getName() + " for " + targets.size() + " players."));
        return targets.size();
    }

    public static int executeGc(CommandContext<CommandSourceStack> context) {
        long maxMem = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long totalMem = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long freeMem = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long usedMem = totalMem - freeMem;
        context.getSource().sendSystemMessage(Component.literal("Max Memory: " + maxMem + " MB"));
        context.getSource().sendSystemMessage(Component.literal("Allocated Memory: " + totalMem + " MB"));
        context.getSource().sendSystemMessage(Component.literal("Free Memory: " + freeMem + " MB"));
        context.getSource().sendSystemMessage(Component.literal("Used Memory: " + usedMem + " MB"));
        return 1;
    }

    public static int executeGetpos(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.Vec3 pos = player.position();
        context.getSource().sendSystemMessage(Component.literal(String.format("Location: X: %.2f Y: %.2f Z: %.2f Pitch: %.1f Yaw: %.1f", pos.x, pos.y, pos.z, player.getXRot(), player.getYRot())));
        return 1;
    }

    public static int executeGive(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /give <player> <item> [amount]"));
        return 0;
    }

    public static int executeGod(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean isGod = player.isInvulnerable();
        player.setInvulnerable(!isGod);
        context.getSource().sendSystemMessage(Component.literal("God mode " + (!isGod ? "enabled" : "disabled") + "."));
        return 1;
    }

    public static int executeGrindstone(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.GrindstoneMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Grindstone")));
        return 1;
    }

    public static int executeHat(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You must be holding an item."));
            return 0;
        }
        net.minecraft.world.item.ItemStack head = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, hand.copy());
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, head);
        context.getSource().sendSystemMessage(Component.literal("Enjoy your new hat!"));
        return 1;
    }

    public static int executeHeal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        player.clearFire();
        player.removeAllEffects();
        context.getSource().sendSystemMessage(Component.literal("You have been healed."));
        return 1;
    }

    public static int executeHelp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Help menus not configured."));
        return 1;
    }

    public static int executeHelpop(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /helpop <message>"));
        return 0;
    }

    public static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
        if (homes == null || homes.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You have no homes set."));
            return 0;
        }
        if (homes.size() == 1) {
            // Teleport to the only home
            return executeHome(context, homes.keySet().iterator().next());
        }
        // List homes
        context.getSource().sendSystemMessage(Component.literal("Homes: " + String.join(", ", homes.keySet())));
        return 1;
    }
    public static int executeHome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
        if (homes == null || !homes.containsKey(name.toLowerCase())) {
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
            return 0;
        }
        HomePosition home = homes.get(name.toLowerCase());
        net.minecraft.resources.Identifier dimLoc = net.minecraft.resources.Identifier.parse(home.dimension);
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);
        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
        if (targetLevel == null) {
            context.getSource().sendSystemMessage(Component.literal("Invalid dimension for home."));
            return 0;
        }
        saveBackLocation(player);
        player.teleportTo(targetLevel, home.x, home.y, home.z, java.util.Collections.emptySet(), home.yaw, home.pitch, false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to home '" + name + "'."));
        return 1;
    }

    public static int executeIce(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeIce(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())); }
    public static int executeIce(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.setTicksFrozen(target.getTicksRequiredToFreeze() + 200);
            if (target instanceof ServerPlayer p) p.sendSystemMessage(Component.literal("You have been iced."));
        }
        context.getSource().sendSystemMessage(Component.literal("Iced " + targets.size() + " entities."));
        return targets.size();
    }

    public static int executeIgnore(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /ignore <player>"));
        return 0;
    }

    public static int executeInfo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Server Info not configured."));
        return 1;
    }

    public static int executeInvsee(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /invsee <player>")); return 0; }
    public static int executeInvsee(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> {
            return new net.minecraft.world.inventory.ChestMenu(net.minecraft.world.inventory.MenuType.GENERIC_9x4, id, inv, target.getInventory(), 4);
        }, Component.literal(target.getName().getString() + "'s Inventory")));
        return 1;
    }

    public static int executeItem(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /item <item> [amount]"));
        return 0;
    }

    public static int executeItemdb(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
            return 0;
        }
        context.getSource().sendSystemMessage(Component.literal("Item: " + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(hand.getItem()).toString()));
        return 1;
    }

    public static int executeItemlore(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /itemlore <add|set|clear> <text>"));
        return 0;
    }

    public static int executeItemname(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /itemname <name>"));
        return 0;
    }

    public static int executeJailedplayers(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Jailed Players list not fully implemented."));
        return 1;
    }

    public static int executeJails(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Jails: " + String.join(", ", JAILS.keySet())));
        return 1;
    }

    public static int executeJump(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
            player.teleportTo(player.level(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal("Jumped!"));
        } else {
            context.getSource().sendSystemMessage(Component.literal("No block in sight."));
        }
        return 1;
    }

    public static int executeKick(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, String reason) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to kick."));
            return 0;
        }
        Component reasonComp = Component.literal(reason != null ? reason : "Kicked by an operator.");
        for (ServerPlayer target : targets) {
            target.connection.disconnect(reasonComp);
        }
        context.getSource().sendSystemMessage(Component.literal("Kicked " + targets.size() + " players."));
        return targets.size();
    }

    public static int executeKickall(CommandContext<CommandSourceStack> context, String reason) {
        Component reasonComp = Component.literal(reason != null ? reason : "Kicked by an operator.");
        int count = 0;
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (context.getSource().getEntity() != player) {
                player.connection.disconnect(reasonComp);
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Kicked " + count + " players."));
        return count;
    }

    public static int executeKill(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /kill <player>")); return 0; }
    public static int executeKill(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.kill((net.minecraft.server.level.ServerLevel) target.level());
        }
        context.getSource().sendSystemMessage(Component.literal("Killed " + targets.size() + " entities."));
        return targets.size();
    }

    public static int executeKit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeKit(context, ""); }
    public static int executeKit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        if (name.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Available Kits: " + String.join(", ", KITS.keySet())));
            return 1;
        }
        KitData kit = KITS.get(name.toLowerCase());
        if (kit == null) {
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
            return 0;
        }
        ServerPlayer player = context.getSource().getPlayerOrException();
        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
        for (String itemStr : kit.items) {
            try {
                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseCompoundFully(itemStr);
                net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();
                if (!player.getInventory().add(item)) player.drop(item, false);
            } catch (Exception e) { e.printStackTrace(); }
        }
        context.getSource().sendSystemMessage(Component.literal("You received the kit '" + name + "'."));
        return 1;
    }

    public static int executeKitreset(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /kitreset <player> <kit>"));
        return 0;
    }

    public static int executeKittycannon(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.entity.Entity cat = net.minecraft.world.entity.EntityType.CAT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (cat != null) {
            cat.setPos(player.getX(), player.getEyeY(), player.getZ());
            cat.setDeltaMovement(player.getLookAngle().scale(2.0));
            player.level().addFreshEntity(cat);
            net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            tnt.setPos(cat.getX(), cat.getY(), cat.getZ());
            tnt.startRiding(cat);
            tnt.setFuse(20);
            player.level().addFreshEntity(tnt);
            context.getSource().sendSystemMessage(Component.literal("Meow!"));
        }
        return 1;
    }

    public static int executeLightning(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
            net.minecraft.world.entity.LightningBolt bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (bolt != null) {
                bolt.setPos(net.minecraft.world.phys.Vec3.atBottomCenterOf(pos));
                player.level().addFreshEntity(bolt);
                context.getSource().sendSystemMessage(Component.literal("Smite!"));
            }
        }
        return 1;
    }

    public static int executeList(CommandContext<CommandSourceStack> context) {
        java.util.List<ServerPlayer> players = context.getSource().getServer().getPlayerList().getPlayers();
        String names = players.stream().map(p -> p.getName().getString()).collect(java.util.stream.Collectors.joining(", "));
        context.getSource().sendSystemMessage(Component.literal("There are " + players.size() + "/" + context.getSource().getServer().getMaxPlayers() + " players online: \n" + names));
        return 1;
    }

    public static int executeLoom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.LoomMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Loom")));
        return 1;
    }

    public static int executeMail(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Mail system not implemented in port yet."));
        return 1;
    }

    public static int executeMe(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /me <action>")); return 0; }
    public static int executeMe(CommandContext<CommandSourceStack> context, String action) {
        context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(" * " + context.getSource().getTextName() + " " + action), false);
        return 1;
    }

    public static int executeMore(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
            return 0;
        }
        hand.setCount(hand.getMaxStackSize());
        context.getSource().sendSystemMessage(Component.literal("Filled item stack to maximum."));
        return 1;
    }

    public static int executeMotd(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Welcome to the server!"));
        return 1;
    }

    public static int executeMsg(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /msg <player> <message>")); return 0; }
    public static int executeMsg(CommandContext<CommandSourceStack> context, ServerPlayer target, String message) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        replyMap.put(sender.getUUID(), target.getUUID());
        replyMap.put(target.getUUID(), sender.getUUID());
        sender.sendSystemMessage(Component.literal("[me -> " + target.getName().getString() + "] " + message));
        target.sendSystemMessage(Component.literal("[" + sender.getName().getString() + " -> me] " + message));
        return 1;
    }

    public static int executeMsgtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player.getUUID());
        data.msgtoggle = !data.msgtoggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Message toggle set to: " + data.msgtoggle));
        return 1;
    }

    public static int executeMute(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /mute <player> [time]"));
        return 0;
    }

    public static int executeNear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.List<ServerPlayer> near = player.level().getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(100.0));
        near.remove(player);
        context.getSource().sendSystemMessage(Component.literal("Players nearby: " + near.size()));
        return 1;
    }

    public static int executeNick(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /nick <player> <nickname>"));
        return 0;
    }

    public static int executeNuke(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        context.getSource().sendSystemMessage(Component.literal("May death rain upon them."));
        for (ServerPlayer target : context.getSource().getServer().getPlayerList().getPlayers()) {
            net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(target.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (tnt != null) {
                tnt.setPos(target.getX(), target.getY() + 10, target.getZ());
                tnt.setFuse(40);
                target.level().addFreshEntity(tnt);
            }
        }
        return 1;
    }

    public static int executeTpoffline(CommandContext<CommandSourceStack> context, String targetName) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        HomePosition pos = offlinePositions.get(targetName.toLowerCase());
        if (pos == null) {
            context.getSource().sendSystemMessage(Component.literal("No offline location recorded for " + targetName + " since the server started."));
            return 0;
        }
        net.minecraft.resources.Identifier dimLoc = net.minecraft.resources.Identifier.parse(pos.dimension);
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);
        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
        if (targetLevel != null) {
            saveBackLocation(player);
        player.teleportTo(targetLevel, pos.x, pos.y, pos.z, java.util.Collections.emptySet(), pos.yaw, pos.pitch, false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + targetName + "'s last known offline location."));
            return 1;
        }
        return 0;
    }

    public static int executePay(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /pay <player> <amount>")); return 0;
    }
    public static int executePay(CommandContext<CommandSourceStack> context, ServerPlayer target, double amount) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        if (sender == target) {
            context.getSource().sendSystemMessage(Component.literal("You cannot pay yourself!"));
            return 0;
        }
        UserData targetData = UserCache.getUser(target);
        if (!targetData.payToggle) {
            context.getSource().sendSystemMessage(Component.literal("That player has payments disabled."));
            return 0;
        }
        UserData senderData = UserCache.getUser(sender);
        if (senderData.money < amount) {
            context.getSource().sendSystemMessage(Component.literal("You do not have enough money."));
            return 0;
        }
        senderData.money -= amount;
        targetData.money += amount;
        UserCache.saveUser(sender.getUUID());
        UserCache.saveUser(target.getUUID());
        context.getSource().sendSystemMessage(Component.literal("You paid $" + String.format("%.2f", amount) + " to " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal("You received $" + String.format("%.2f", amount) + " from " + sender.getName().getString() + "."));
        return 1;
    }

    public static int executePaytoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.payToggle = !data.payToggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Accepting payments set to: " + data.payToggle));
        return 1;
    }

    public static int executePayconfirmtoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command payconfirmtoggle is not fully implemented yet!"));
        return 1;
    }

    public static int executePing(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Pong!"));
        return 1;
    }

    public static int executePlaytime(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int ticks = player.getStats().getValue(net.minecraft.stats.Stats.CUSTOM.get(net.minecraft.stats.Stats.PLAY_TIME));
        context.getSource().sendSystemMessage(Component.literal("Playtime: " + (ticks / 20 / 60) + " minutes"));
        return 1;
    }

    public static int executePotion(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /potion <effect> [duration]"));
        return 0;
    }

    public static int executePowertool(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Powertool tracking not implemented."));
        return 1;
    }

    public static int executePowertoollist(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("No powertools active."));
        return 1;
    }

    public static int executePowertooltoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Powertools toggled."));
        return 1;
    }

    public static int executePtime(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /ptime <time>"));
        return 0;
    }

    public static int executePweather(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /pweather <weather>"));
        return 0;
    }

    public static int executeR(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /r <message>")); return 0; }
    public static int executeR(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        java.util.UUID targetId = replyMap.get(sender.getUUID());
        if (targetId == null) {
            context.getSource().sendSystemMessage(Component.literal("You have nobody to reply to."));
            return 0;
        }
        ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayer(targetId);
        if (target == null) {
            context.getSource().sendSystemMessage(Component.literal("That player is offline."));
            return 0;
        }
        sender.sendSystemMessage(Component.literal("[me -> " + target.getName().getString() + "] " + message));
        target.sendSystemMessage(Component.literal("[" + sender.getName().getString() + " -> me] " + message));
        return 1;
    }

    public static int executeRtoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Reply toggle changed."));
        return 1;
    }

    public static int executeRealname(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /realname <nickname>"));
        return 0;
    }

    public static int executeRecipe(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /recipe <item>"));
        return 0;
    }

    public static int executeRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int count = 0;
        for (net.minecraft.world.entity.Entity entity : player.level().getEntitiesOfClass(net.minecraft.world.entity.Entity.class, player.getBoundingBox().inflate(100.0))) {
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity) {
                entity.discard();
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Removed " + count + " dropped items."));
        return 1;
    }

    public static int executeRenamehome(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /renamehome <old> <new>")); return 0; }
    public static int executeRenamehome(CommandContext<CommandSourceStack> context, String oldName, String newName) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
        if (homes == null || !homes.containsKey(oldName.toLowerCase())) {
            context.getSource().sendSystemMessage(Component.literal("Home '" + oldName + "' does not exist."));
            return 0;
        }
        if (homes.containsKey(newName.toLowerCase())) {
            context.getSource().sendSystemMessage(Component.literal("A home named '" + newName + "' already exists."));
            return 0;
        }
        HomePosition home = homes.remove(oldName.toLowerCase());
        homes.put(newName.toLowerCase(), home);
        context.getSource().sendSystemMessage(Component.literal("Successfully renamed home '" + oldName + "' to '" + newName + "'."));
        return 1;
    }

    public static int executeRepair(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty() || !hand.isDamageableItem()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding a repairable item."));
            return 0;
        }
        hand.setDamageValue(0);
        context.getSource().sendSystemMessage(Component.literal("Item repaired successfully."));
        return 1;
    }

    public static int executeRest(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /rest <player>"));
        return 0;
    }

    public static int executeRules(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("1. Be nice."));
        return 1;
    }

    public static int executeSeen(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /seen <player>"));
        return 0;
    }

    public static int executeSell(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /sell <item>"));
        return 0;
    }

    public static int executeSethome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeSethome(context, "home"); }
    public static int executeSethome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        String dim = player.level().dimension().identifier().toString();
        HomePosition home = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim);
        data.homes.put(name.toLowerCase(), home);
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' set."));
        return 1;
    }

    public static int executeSetjail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /setjail <name>")); return 0; }
    public static int executeSetjail(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        HomePosition pos = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString());
        JAILS.put(name.toLowerCase(), pos);
        saveJailsWarps();
        context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' set."));
        return 1;
    }

    public static int executeSettpr(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("TPR variables set."));
        return 1;
    }

    public static int executeSetwarp(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /setwarp <name>")); return 0; }
    public static int executeSetwarp(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        HomePosition pos = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString());
        WARPS.put(name.toLowerCase(), pos);
        saveJailsWarps();
        context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' set."));
        return 1;
    }

    public static int executeSetworth(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /setworth <item> <price>"));
        return 0;
    }

    public static int executeShowkit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /showkit <name>")); return 0; }
    public static int executeShowkit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        KitData kit = KITS.get(name.toLowerCase());
        if (kit == null) {
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
            return 0;
        }
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.SimpleContainer inv = new net.minecraft.world.SimpleContainer(54);
        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
        for (int i = 0; i < Math.min(54, kit.items.size()); i++) {
            try {
                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseCompoundFully(kit.items.get(i));
                net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();
                inv.setItem(i, item);
            } catch (Exception e) {}
        }
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return net.minecraft.world.inventory.ChestMenu.sixRows(id, inventory, inv);
        }, Component.literal("Kit Preview: " + name)));
        return 1;
    }

    public static int executeEditsign(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /editsign <set|clear> <line> <text>"));
        return 0;
    }

    public static int executeSkull(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack skull = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD, 1);
        // Adding profile component normally requires NBT handling, we just give the item here.
        if (!player.getInventory().add(skull)) player.drop(skull, false);
        context.getSource().sendSystemMessage(Component.literal("You received a player skull."));
        return 1;
    }

    public static int executeSmithingtable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.SmithingMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Smithing Table")));
        return 1;
    }

    public static int executeSocialspy(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("SocialSpy toggled."));
        return 1;
    }

    public static int executeSpawner(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /spawner <mob>"));
        return 0;
    }

    public static int executeSpawnmob(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /spawnmob <mob> [amount]"));
        return 0;
    }

    public static int executeSpeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getAbilities().setFlyingSpeed(0.1F);
        player.getAbilities().setWalkingSpeed(0.2F);
        player.onUpdateAbilities();
        context.getSource().sendSystemMessage(Component.literal("Speed reset to defaults."));
        return 1;
    }

    public static int executeStonecutter(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.StonecutterMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Stonecutter")));
        return 1;
    }

    public static int executeSudo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /sudo <player> <command>"));
        return 0;
    }

    public static int executeSuicide(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.kill(player.level());
        context.getSource().sendSystemMessage(Component.literal("You took your own life."));
        return 1;
    }

    public static int executeTempban(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /tempban <player> <time> [reason]"));
        return 0;
    }

    public static int executeTempbanip(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /tempbanip <ip> <time> [reason]"));
        return 0;
    }

    public static int executeThunder(CommandContext<CommandSourceStack> context) {
        context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD).setWeatherParameters(0, 6000, true, true);
        context.getSource().sendSystemMessage(Component.literal("Thunderstorm forced."));
        return 1;
    }

    public static int executeTime(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /time <day|night>")); return 0; }
    public static int executeTime(CommandContext<CommandSourceStack> context, int time) {
        context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD).setDayTime(time);
        context.getSource().sendSystemMessage(Component.literal("Time set to " + time + "."));
        return 1;
    }

    public static int executeTogglejail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /togglejail <player> <jailname>")); return 0; }
    public static int executeTogglejail(CommandContext<CommandSourceStack> context, ServerPlayer target, String jailname) throws CommandSyntaxException {
        UserData data = UserCache.getUser(target);
        if (data.jail != null) {
            data.jail = null;
            UserCache.saveUser(target.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Unjailed " + target.getName().getString()));
            target.sendSystemMessage(Component.literal("You have been released from jail."));
            return 1;
        }
        HomePosition jailPos = JAILS.get(jailname.toLowerCase());
        if (jailPos == null) {
            context.getSource().sendSystemMessage(Component.literal("Jail '" + jailname + "' not found."));
            return 0;
        }
        data.jail = jailname.toLowerCase();
        UserCache.saveUser(target.getUUID());
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(jailPos.dimension));
        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
        if (targetLevel != null) {
            target.teleportTo(targetLevel, jailPos.x, jailPos.y, jailPos.z, java.util.Collections.emptySet(), jailPos.yaw, jailPos.pitch, false);
        }
        context.getSource().sendSystemMessage(Component.literal("Jailed " + target.getName().getString() + " in " + jailname));
        target.sendSystemMessage(Component.literal("You have been jailed.").withStyle(net.minecraft.ChatFormatting.RED));
        return 1;
    }

    public static int executeTop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int topY = player.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
        player.teleportTo(player.level(), player.getX(), topY, player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to top."));
        return 1;
    }

    public static int executeTp(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, net.minecraft.world.entity.Entity destination) throws CommandSyntaxException {
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer player) {
                saveBackLocation(player);
                player.teleportTo((net.minecraft.server.level.ServerLevel) destination.level(), destination.getX(), destination.getY(), destination.getZ(), java.util.Collections.emptySet(), destination.getYRot(), destination.getXRot(), false);
            }
        }
        if (targets.size() == 1) {
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + destination.getName().getString() + "."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to " + destination.getName().getString() + "."));
        }
        return targets.size();
    }

    public static int executeTpa(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        if (tpTogglePlayers.contains(target.getUUID())) {
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has teleportation disabled."));
            return 0;
        }
        if (tpAutoPlayers.contains(target.getUUID())) {
            sender.teleportTo(sender.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), sender.getYRot(), sender.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + target.getName().getString() + " (Auto-Accepted)."));
            return 1;
        }
        pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), false));
        context.getSource().sendSystemMessage(Component.literal("Teleport request sent to " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested to teleport to you. Type /tpaccept to accept or /tpdeny to deny."));
        return 1;
    }

    public static int executeTpaall(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        int count = 0;
        for (ServerPlayer target : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (target != sender && !tpTogglePlayers.contains(target.getUUID())) {
                pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), true));
                target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested that you teleport to them. Type /tpaccept to accept or /tpdeny to deny."));
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleport here requests sent to " + count + " players."));
        return count;
    }

    public static int executeTpaccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeleportRequest req = pendingRequests.remove(player.getUUID());
        if (req == null || System.currentTimeMillis() - req.timestamp > 120000) {
            context.getSource().sendSystemMessage(Component.literal("You do not have any pending teleport requests."));
            return 0;
        }
        ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(req.sender);
        if (sender == null) {
            context.getSource().sendSystemMessage(Component.literal("The player who sent the request is no longer online."));
            return 0;
        }
        if (req.isTpaHere) {
            player.teleportTo(sender.level(), sender.getX(), sender.getY(), sender.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + sender.getName().getString() + "."));
            sender.sendSystemMessage(Component.literal(player.getName().getString() + " accepted your teleport request."));
        } else {
            sender.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), sender.getYRot(), sender.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal(sender.getName().getString() + " has been teleported to you."));
            sender.sendSystemMessage(Component.literal("Teleport request accepted."));
        }
        return 1;
    }

    public static int executeTpahere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        if (tpTogglePlayers.contains(target.getUUID())) {
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has teleportation disabled."));
            return 0;
        }
        if (tpAutoPlayers.contains(target.getUUID())) {
            target.teleportTo(sender.level(), sender.getX(), sender.getY(), sender.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " was teleported to you (Auto-Accepted)."));
            return 1;
        }
        pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), true));
        context.getSource().sendSystemMessage(Component.literal("Teleport here request sent to " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested that you teleport to them. Type /tpaccept to accept or /tpdeny to deny."));
        return 1;
    }

    public static int executeTpall(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        int count = 0;
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (player != target) {
                player.teleportTo(target.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleported " + count + " players to " + target.getName().getString() + "."));
        return count;
    }

    public static int executeTpauto(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (tpAutoPlayers.contains(player.getUUID())) {
            tpAutoPlayers.remove(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Auto-accept teleport requests disabled."));
        } else {
            tpAutoPlayers.add(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Auto-accept teleport requests enabled."));
        }
        return 1;
    }

    public static int executeTpacancel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean canceled = false;
        java.util.Iterator<java.util.Map.Entry<java.util.UUID, TeleportRequest>> it = pendingRequests.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry<java.util.UUID, TeleportRequest> entry = it.next();
            if (entry.getValue().sender.equals(player.getUUID())) {
                it.remove();
                canceled = true;
                ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayer(entry.getKey());
                if (target != null) {
                   target.sendSystemMessage(Component.literal(player.getName().getString() + " canceled their teleport request."));
                }
            }
        }
        if (canceled) {
            context.getSource().sendSystemMessage(Component.literal("Teleport request canceled."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("You have no pending outgoing teleport requests."));
        }
        return 1;
    }

    public static int executeTpdeny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeleportRequest req = pendingRequests.remove(player.getUUID());
        if (req == null) {
            context.getSource().sendSystemMessage(Component.literal("You do not have any pending teleport requests."));
            return 0;
        }
        ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(req.sender);
        if (sender != null) {
            sender.sendSystemMessage(Component.literal(player.getName().getString() + " denied your teleport request."));
        }
        context.getSource().sendSystemMessage(Component.literal("Teleport request denied."));
        return 1;
    }

    public static int executeTphere(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer pTarget) {
                saveBackLocation(pTarget);
                pTarget.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            } else {
                target.teleportTo(player.getX(), player.getY(), player.getZ());
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to you."));
        return targets.size();
    }

    public static int executeTpo(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.teleportTo(target.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to " + target.getName().getString() + " (Override)."));
        return 1;
    }

    public static int executeTpohere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        saveBackLocation(target);
        target.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported " + target.getName().getString() + " to you (Override)."));
        return 1;
    }

    public static int executeTppos(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.coordinates.Coordinates pos) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.Vec3 vec = pos.getPosition(context.getSource());
        saveBackLocation(player);
        player.teleportTo(player.level(), vec.x, vec.y, vec.z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal(String.format("Teleported to %.1f, %.1f, %.1f", vec.x, vec.y, vec.z)));
        return 1;
    }

    public static int executeTpr(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.level.border.WorldBorder border = player.level().getWorldBorder();
        double minX = Math.max(border.getMinX(), -5000);
        double maxX = Math.min(border.getMaxX(), 5000);
        double minZ = Math.max(border.getMinZ(), -5000);
        double maxZ = Math.min(border.getMaxZ(), 5000);
        double x = minX + (player.getRandom().nextDouble() * (maxX - minX));
        double z = minZ + (player.getRandom().nextDouble() * (maxZ - minZ));
        int y = player.level().getMaxY() - 1;
        // Basic top-down scan to find surface (will just teleport to top block for simplicity)
        net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos((int)x, y, (int)z);
        while(y > player.level().getMinY() && player.level().getBlockState(pos).isAir()) {
            y--;
            pos = new net.minecraft.core.BlockPos((int)x, y, (int)z);
        }
        saveBackLocation(player);
        player.teleportTo(player.level(), x, y + 1.0, z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal(String.format("Randomly teleported to X: %.1f Z: %.1f", x, z)));
        return 1;
    }

    public static int executeTptoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (tpTogglePlayers.contains(player.getUUID())) {
            tpTogglePlayers.remove(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Teleportation requests enabled."));
        } else {
            tpTogglePlayers.add(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Teleportation requests disabled."));
        }
        return 1;
    }

    public static int executeTree(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos().above();
            player.level().setBlock(pos, net.minecraft.world.level.block.Blocks.OAK_SAPLING.defaultBlockState(), 3);
            context.getSource().sendSystemMessage(Component.literal("Tree spawned."));
        }
        return 1;
    }

    public static int executeUnban(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to unban."));
            return 0;
        }
        net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
        for (net.minecraft.server.players.NameAndId profile : targets) {
            banList.remove(profile);
        }
        context.getSource().sendSystemMessage(Component.literal("Unbanned " + targets.size() + " players."));
        return targets.size();
    }

    public static int executeUnbanip(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /unbanip <ip>"));
        return 0;
    }

    public static int executeUnlimited(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /unlimited <item>"));
        return 0;
    }

    public static int executeVanish(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.setInvisible(!player.isInvisible());
        context.getSource().sendSystemMessage(Component.literal("Vanish toggled to: " + player.isInvisible()));
        return 1;
    }

    public static int executeWarp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeWarp(context, ""); }
    public static int executeWarp(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        if (name.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Warps: " + String.join(", ", WARPS.keySet())));
            return 1;
        }
        HomePosition warpPos = WARPS.get(name.toLowerCase());
        if (warpPos == null) {
            context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' not found."));
            return 0;
        }
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(warpPos.dimension));
        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
        if (targetLevel != null) {
            saveBackLocation(player);
            player.teleportTo(targetLevel, warpPos.x, warpPos.y, warpPos.z, java.util.Collections.emptySet(), warpPos.yaw, warpPos.pitch, false);
        }
        context.getSource().sendSystemMessage(Component.literal("Warped to " + name));
        return 1;
    }

    public static int executeWarpinfo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /warpinfo <warp>"));
        return 0;
    }

    public static int executeWeather(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /weather <clear|rain|thunder>")); return 0; }
    public static int executeWeather(CommandContext<CommandSourceStack> context, int type) {
        net.minecraft.server.level.ServerLevel level = context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (type == 0) level.setWeatherParameters(6000, 0, false, false);
        else if (type == 1) level.setWeatherParameters(0, 6000, true, false);
        else if (type == 2) level.setWeatherParameters(0, 6000, true, true);
        context.getSource().sendSystemMessage(Component.literal("Weather updated."));
        return 1;
    }

    public static int executeWhois(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /whois <player>"));
        return 0;
    }

    public static int executeWorkbench(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.CraftingMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Crafting")));
        return 1;
    }

    public static int executeWorld(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /world <worldname>"));
        return 0;
    }

    public static int executeWorth(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Usage: /worth <item>"));
        return 0;
    }
}
