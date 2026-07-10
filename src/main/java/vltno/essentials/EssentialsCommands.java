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
    private static final java.util.Map<java.util.UUID, java.util.Map<String, HomePosition>> playerHomes = new java.util.HashMap<>();
    private static final java.util.Map<String, HomePosition> offlinePositions = new java.util.HashMap<>();
    
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File getDataFile() {
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

    private static int executeTpa(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpa <player>")); return 0; }
    private static int executeTpahere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpahere <player>")); return 0; }
    private static int executeTpall(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpall <player>")); return 0; }
    private static int executeTpo(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpo <player>")); return 0; }
    private static int executeTpohere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpohere <player>")); return 0; }
    private static int executeTpoffline(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpoffline <uuid>")); return 0; }


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
    private static final java.util.Map<java.util.UUID, TeleportRequest> pendingRequests = new java.util.HashMap<>();
    private static final java.util.Set<java.util.UUID> tpTogglePlayers = new java.util.HashSet<>();
    private static final java.util.Set<java.util.UUID> tpAutoPlayers = new java.util.HashSet<>();

    public static void register() {
        CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register(EssentialsCommands::onPlayerDisconnect);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(EssentialsCommands::loadData);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(EssentialsCommands::saveData);

    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {

        dispatcher.register(Commands.literal("afk")
            .executes(context -> executeAfk(context))
        );
        dispatcher.register(Commands.literal("eafk")
            .executes(context -> executeAfk(context))
        );
        dispatcher.register(Commands.literal("away")
            .executes(context -> executeAfk(context))
        );
        dispatcher.register(Commands.literal("eaway")
            .executes(context -> executeAfk(context))
        );
        dispatcher.register(Commands.literal("antioch")
            .executes(context -> executeAntioch(context))
        );
        dispatcher.register(Commands.literal("eantioch")
            .executes(context -> executeAntioch(context))
        );
        dispatcher.register(Commands.literal("grenade")
            .executes(context -> executeAntioch(context))
        );
        dispatcher.register(Commands.literal("egrenade")
            .executes(context -> executeAntioch(context))
        );
        dispatcher.register(Commands.literal("tnt")
            .executes(context -> executeAntioch(context))
        );
        dispatcher.register(Commands.literal("etnt")
            .executes(context -> executeAntioch(context))
        );
        dispatcher.register(Commands.literal("anvil")
            .executes(context -> executeAnvil(context))
        );
        dispatcher.register(Commands.literal("eanvil")
            .executes(context -> executeAnvil(context))
        );
        dispatcher.register(Commands.literal("back")
            .executes(context -> executeBack(context))
        );
        dispatcher.register(Commands.literal("eback")
            .executes(context -> executeBack(context))
        );
        dispatcher.register(Commands.literal("return")
            .executes(context -> executeBack(context))
        );
        dispatcher.register(Commands.literal("ereturn")
            .executes(context -> executeBack(context))
        );
        dispatcher.register(Commands.literal("backup")
            .executes(context -> executeBackup(context))
        );
        dispatcher.register(Commands.literal("ebackup")
            .executes(context -> executeBackup(context))
        );
        dispatcher.register(Commands.literal("balance")
            .executes(context -> executeBalance(context))
        );
        dispatcher.register(Commands.literal("bal")
            .executes(context -> executeBalance(context))
        );
        dispatcher.register(Commands.literal("ebal")
            .executes(context -> executeBalance(context))
        );
        dispatcher.register(Commands.literal("ebalance")
            .executes(context -> executeBalance(context))
        );
        dispatcher.register(Commands.literal("money")
            .executes(context -> executeBalance(context))
        );
        dispatcher.register(Commands.literal("emoney")
            .executes(context -> executeBalance(context))
        );
        dispatcher.register(Commands.literal("balancetop")
            .executes(context -> executeBalancetop(context))
        );
        dispatcher.register(Commands.literal("ebalancetop")
            .executes(context -> executeBalancetop(context))
        );
        dispatcher.register(Commands.literal("baltop")
            .executes(context -> executeBalancetop(context))
        );
        dispatcher.register(Commands.literal("ebaltop")
            .executes(context -> executeBalancetop(context))
        );
        dispatcher.register(Commands.literal("ban")
        .executes(context -> executeBan(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    );
        dispatcher.register(Commands.literal("eban")
        .executes(context -> executeBan(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    );
        dispatcher.register(Commands.literal("banip")
            .executes(context -> executeBanip(context))
        );
        dispatcher.register(Commands.literal("ebanip")
            .executes(context -> executeBanip(context))
        );
        dispatcher.register(Commands.literal("beezooka")
            .executes(context -> executeBeezooka(context))
        );
        dispatcher.register(Commands.literal("ebeezooka")
            .executes(context -> executeBeezooka(context))
        );
        dispatcher.register(Commands.literal("beecannon")
            .executes(context -> executeBeezooka(context))
        );
        dispatcher.register(Commands.literal("ebeecannon")
            .executes(context -> executeBeezooka(context))
        );
        dispatcher.register(Commands.literal("book")
            .executes(context -> executeBook(context))
        );
        dispatcher.register(Commands.literal("ebook")
            .executes(context -> executeBook(context))
        );
        dispatcher.register(Commands.literal("bottom")
            .executes(context -> executeBottom(context))
        );
        dispatcher.register(Commands.literal("ebottom")
            .executes(context -> executeBottom(context))
        );
        dispatcher.register(Commands.literal("break")
            .executes(context -> executeBreak(context))
        );
        dispatcher.register(Commands.literal("ebreak")
            .executes(context -> executeBreak(context))
        );
        dispatcher.register(Commands.literal("broadcast")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("bc")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("ebc")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("bcast")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("ebcast")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("ebroadcast")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("shout")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("eshout")
            .executes(context -> executeBroadcast(context))
        );
        dispatcher.register(Commands.literal("broadcastworld")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("bcw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("ebcw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("bcastw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("ebcastw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("ebroadcastworld")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("shoutworld")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("eshoutworld")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("bigtree")
            .executes(context -> executeBigtree(context))
        );
        dispatcher.register(Commands.literal("ebigtree")
            .executes(context -> executeBigtree(context))
        );
        dispatcher.register(Commands.literal("largetree")
            .executes(context -> executeBigtree(context))
        );
        dispatcher.register(Commands.literal("elargetree")
            .executes(context -> executeBigtree(context))
        );
        dispatcher.register(Commands.literal("burn")
            .executes(context -> executeBurn(context))
        );
        dispatcher.register(Commands.literal("eburn")
            .executes(context -> executeBurn(context))
        );
        dispatcher.register(Commands.literal("cartographytable")
            .executes(context -> executeCartographytable(context))
        );
        dispatcher.register(Commands.literal("ecartographytable")
            .executes(context -> executeCartographytable(context))
        );
        dispatcher.register(Commands.literal("carttable")
            .executes(context -> executeCartographytable(context))
        );
        dispatcher.register(Commands.literal("ecarttable")
            .executes(context -> executeCartographytable(context))
        );
        dispatcher.register(Commands.literal("clearinventory")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("ci")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("eci")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("clean")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("eclean")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("clear")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("eclear")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("clearinvent")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("eclearinvent")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("eclearinventory")
            .executes(context -> executeClearinventory(context))
        );
        dispatcher.register(Commands.literal("clearinventoryconfirmtoggle")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearinventoryconfirmtoggle")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearinventoryconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearinventoryconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearconfirmon")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearconfirmon")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearconfirm")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearconfirm")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("condense")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("econdense")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("compact")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("ecompact")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("blocks")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("eblocks")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("toblocks")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("etoblocks")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("compass")
            .executes(context -> executeCompass(context))
        );
        dispatcher.register(Commands.literal("ecompass")
            .executes(context -> executeCompass(context))
        );
        dispatcher.register(Commands.literal("direction")
            .executes(context -> executeCompass(context))
        );
        dispatcher.register(Commands.literal("edirection")
            .executes(context -> executeCompass(context))
        );
        dispatcher.register(Commands.literal("createkit")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("kitcreate")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("createk")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("kc")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("ck")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("customtext")
            .executes(context -> executeCustomtext(context))
        );
        dispatcher.register(Commands.literal("delhome")
        .executes(context -> executeDelhome(context))
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelhome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );
        dispatcher.register(Commands.literal("edelhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("remhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("eremhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("rmhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("ermhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("deljail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("edeljail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("remjail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("eremjail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("rmjail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("ermjail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("delkit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("edelkit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("remkit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("eremkit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("rmkit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("ermkit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("deletekit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("edeletekit")
            .executes(context -> executeDelkit(context))
        );
        dispatcher.register(Commands.literal("delwarp")
            .executes(context -> executeDelwarp(context))
        );
        dispatcher.register(Commands.literal("edelwarp")
            .executes(context -> executeDelwarp(context))
        );
        dispatcher.register(Commands.literal("remwarp")
            .executes(context -> executeDelwarp(context))
        );
        dispatcher.register(Commands.literal("eremwarp")
            .executes(context -> executeDelwarp(context))
        );
        dispatcher.register(Commands.literal("rmwarp")
            .executes(context -> executeDelwarp(context))
        );
        dispatcher.register(Commands.literal("ermwarp")
            .executes(context -> executeDelwarp(context))
        );
        dispatcher.register(Commands.literal("depth")
            .executes(context -> executeDepth(context))
        );
        dispatcher.register(Commands.literal("edepth")
            .executes(context -> executeDepth(context))
        );
        dispatcher.register(Commands.literal("height")
            .executes(context -> executeDepth(context))
        );
        dispatcher.register(Commands.literal("eheight")
            .executes(context -> executeDepth(context))
        );
        dispatcher.register(Commands.literal("disposal")
            .executes(context -> executeDisposal(context))
        );
        dispatcher.register(Commands.literal("edisposal")
            .executes(context -> executeDisposal(context))
        );
        dispatcher.register(Commands.literal("trash")
            .executes(context -> executeDisposal(context))
        );
        dispatcher.register(Commands.literal("etrash")
            .executes(context -> executeDisposal(context))
        );
        dispatcher.register(Commands.literal("eco")
            .executes(context -> executeEco(context))
        );
        dispatcher.register(Commands.literal("eeco")
            .executes(context -> executeEco(context))
        );
        dispatcher.register(Commands.literal("economy")
            .executes(context -> executeEco(context))
        );
        dispatcher.register(Commands.literal("eeconomy")
            .executes(context -> executeEco(context))
        );
        dispatcher.register(Commands.literal("enchant")
            .executes(context -> executeEnchant(context))
        );
        dispatcher.register(Commands.literal("eenchant")
            .executes(context -> executeEnchant(context))
        );
        dispatcher.register(Commands.literal("enchantment")
            .executes(context -> executeEnchant(context))
        );
        dispatcher.register(Commands.literal("eenchantment")
            .executes(context -> executeEnchant(context))
        );
        dispatcher.register(Commands.literal("enderchest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("echest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eechest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eenderchest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("endersee")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eendersee")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("ec")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eec")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("essentials")
            .executes(context -> executeEssentials(context))
        );
        dispatcher.register(Commands.literal("eessentials")
            .executes(context -> executeEssentials(context))
        );
        dispatcher.register(Commands.literal("ess")
            .executes(context -> executeEssentials(context))
        );
        dispatcher.register(Commands.literal("eess")
            .executes(context -> executeEssentials(context))
        );
        dispatcher.register(Commands.literal("essversion")
            .executes(context -> executeEssentials(context))
        );
        dispatcher.register(Commands.literal("exp")
            .executes(context -> executeExp(context))
        );
        dispatcher.register(Commands.literal("eexp")
            .executes(context -> executeExp(context))
        );
        dispatcher.register(Commands.literal("xp")
            .executes(context -> executeExp(context))
        );
        dispatcher.register(Commands.literal("ext")
            .executes(context -> executeExt(context))
        );
        dispatcher.register(Commands.literal("eext")
            .executes(context -> executeExt(context))
        );
        dispatcher.register(Commands.literal("extinguish")
            .executes(context -> executeExt(context))
        );
        dispatcher.register(Commands.literal("eextinguish")
            .executes(context -> executeExt(context))
        );
        dispatcher.register(Commands.literal("feed")
            .executes(context -> executeFeed(context))
        );
        dispatcher.register(Commands.literal("eat")
            .executes(context -> executeFeed(context))
        );
        dispatcher.register(Commands.literal("eeat")
            .executes(context -> executeFeed(context))
        );
        dispatcher.register(Commands.literal("efeed")
            .executes(context -> executeFeed(context))
        );
        dispatcher.register(Commands.literal("fly")
            .executes(context -> executeFly(context))
        );
        dispatcher.register(Commands.literal("efly")
            .executes(context -> executeFly(context))
        );
        dispatcher.register(Commands.literal("fireball")
            .executes(context -> executeFireball(context))
        );
        dispatcher.register(Commands.literal("efireball")
            .executes(context -> executeFireball(context))
        );
        dispatcher.register(Commands.literal("fireentity")
            .executes(context -> executeFireball(context))
        );
        dispatcher.register(Commands.literal("efireentity")
            .executes(context -> executeFireball(context))
        );
        dispatcher.register(Commands.literal("fireskull")
            .executes(context -> executeFireball(context))
        );
        dispatcher.register(Commands.literal("efireskull")
            .executes(context -> executeFireball(context))
        );
        dispatcher.register(Commands.literal("firework")
            .executes(context -> executeFirework(context))
        );
        dispatcher.register(Commands.literal("efirework")
            .executes(context -> executeFirework(context))
        );
        dispatcher.register(Commands.literal("gamemode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("adventure")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("eadventure")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("adventuremode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("eadventuremode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("creative")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("ecreative")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("eecreative")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("creativemode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("ecreativemode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("egamemode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("gm")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("egm")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("gma")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("egma")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("gmc")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("egmc")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("gms")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("egms")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("gmt")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("egmt")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("survival")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("esurvival")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("survivalmode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("esurvivalmode")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("gmsp")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("sp")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("egmsp")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("spec")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("spectator")
            .executes(context -> executeGamemode(context))
        );
        dispatcher.register(Commands.literal("gc")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("lag")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("elag")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("egc")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("mem")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("emem")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("memory")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("ememory")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("uptime")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("euptime")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("tps")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("etps")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("entities")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("eentities")
            .executes(context -> executeGc(context))
        );
        dispatcher.register(Commands.literal("getpos")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("coords")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("egetpos")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("position")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("eposition")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("whereami")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("ewhereami")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("getlocation")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("egetlocation")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("getloc")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("egetloc")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("give")
            .executes(context -> executeGive(context))
        );
        dispatcher.register(Commands.literal("egive")
            .executes(context -> executeGive(context))
        );
        dispatcher.register(Commands.literal("god")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("egod")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("godmode")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("egodmode")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("tgm")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("etgm")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("grindstone")
            .executes(context -> executeGrindstone(context))
        );
        dispatcher.register(Commands.literal("egrindstone")
            .executes(context -> executeGrindstone(context))
        );
        dispatcher.register(Commands.literal("hat")
            .executes(context -> executeHat(context))
        );
        dispatcher.register(Commands.literal("ehat")
            .executes(context -> executeHat(context))
        );
        dispatcher.register(Commands.literal("head")
            .executes(context -> executeHat(context))
        );
        dispatcher.register(Commands.literal("ehead")
            .executes(context -> executeHat(context))
        );
        dispatcher.register(Commands.literal("heal")
            .executes(context -> executeHeal(context))
        );
        dispatcher.register(Commands.literal("eheal")
            .executes(context -> executeHeal(context))
        );
        dispatcher.register(Commands.literal("help")
            .executes(context -> executeHelp(context))
        );
        dispatcher.register(Commands.literal("ehelp")
            .executes(context -> executeHelp(context))
        );
        dispatcher.register(Commands.literal("helpop")
            .executes(context -> executeHelpop(context))
        );
        dispatcher.register(Commands.literal("ac")
            .executes(context -> executeHelpop(context))
        );
        dispatcher.register(Commands.literal("eac")
            .executes(context -> executeHelpop(context))
        );
        dispatcher.register(Commands.literal("amsg")
            .executes(context -> executeHelpop(context))
        );
        dispatcher.register(Commands.literal("eamsg")
            .executes(context -> executeHelpop(context))
        );
        dispatcher.register(Commands.literal("ehelpop")
            .executes(context -> executeHelpop(context))
        );
        dispatcher.register(Commands.literal("home")
        .executes(context -> executeHome(context))
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeHome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );
        dispatcher.register(Commands.literal("ehome")
            .executes(context -> executeHome(context))
        );
        dispatcher.register(Commands.literal("homes")
            .executes(context -> executeHome(context))
        );
        dispatcher.register(Commands.literal("ehomes")
            .executes(context -> executeHome(context))
        );
        dispatcher.register(Commands.literal("ice")
            .executes(context -> executeIce(context))
        );
        dispatcher.register(Commands.literal("eice")
            .executes(context -> executeIce(context))
        );
        dispatcher.register(Commands.literal("efreeze")
            .executes(context -> executeIce(context))
        );
        dispatcher.register(Commands.literal("ignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("eignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("unignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("eunignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("delignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("edelignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("remignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("eremignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("rmignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("ermignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("info")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("about")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("eabout")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("ifo")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("eifo")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("einfo")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("inform")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("einform")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("news")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("enews")
            .executes(context -> executeInfo(context))
        );
        dispatcher.register(Commands.literal("invsee")
            .executes(context -> executeInvsee(context))
        );
        dispatcher.register(Commands.literal("einvsee")
            .executes(context -> executeInvsee(context))
        );
        dispatcher.register(Commands.literal("item")
            .executes(context -> executeItem(context))
        );
        dispatcher.register(Commands.literal("i")
            .executes(context -> executeItem(context))
        );
        dispatcher.register(Commands.literal("eitem")
            .executes(context -> executeItem(context))
        );
        dispatcher.register(Commands.literal("ei")
            .executes(context -> executeItem(context))
        );
        dispatcher.register(Commands.literal("itemdb")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("dura")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("edura")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("durability")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("edurability")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("eitemdb")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("itemno")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("eitemno")
            .executes(context -> executeItemdb(context))
        );
        dispatcher.register(Commands.literal("itemlore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("lore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("elore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("ilore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("eilore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("eitemlore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("itemname")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("iname")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("einame")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("eitemname")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("itemrename")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("irename")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("eitemrename")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("eirename")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("jailedplayers")
            .executes(context -> executeJailedplayers(context))
        );
        dispatcher.register(Commands.literal("ejailedplayers")
            .executes(context -> executeJailedplayers(context))
        );
        dispatcher.register(Commands.literal("ejailed")
            .executes(context -> executeJailedplayers(context))
        );
        dispatcher.register(Commands.literal("ejp")
            .executes(context -> executeJailedplayers(context))
        );
        dispatcher.register(Commands.literal("jails")
            .executes(context -> executeJails(context))
        );
        dispatcher.register(Commands.literal("ejails")
            .executes(context -> executeJails(context))
        );
        dispatcher.register(Commands.literal("jump")
            .executes(context -> executeJump(context))
        );
        dispatcher.register(Commands.literal("j")
            .executes(context -> executeJump(context))
        );
        dispatcher.register(Commands.literal("ej")
            .executes(context -> executeJump(context))
        );
        dispatcher.register(Commands.literal("ejump")
            .executes(context -> executeJump(context))
        );
        dispatcher.register(Commands.literal("jumpto")
            .executes(context -> executeJump(context))
        );
        dispatcher.register(Commands.literal("ejumpto")
            .executes(context -> executeJump(context))
        );
        dispatcher.register(Commands.literal("kick")
        .executes(context -> executeKick(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
            .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    );
        dispatcher.register(Commands.literal("ekick")
        .executes(context -> executeKick(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
            .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    );
        dispatcher.register(Commands.literal("kickall")
        .executes(context -> executeKickall(context, null))
        .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeKickall(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
        )
    );
        dispatcher.register(Commands.literal("ekickall")
        .executes(context -> executeKickall(context, null))
        .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeKickall(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
        )
    );
        dispatcher.register(Commands.literal("kill")
            .executes(context -> executeKill(context))
        );
        dispatcher.register(Commands.literal("ekill")
            .executes(context -> executeKill(context))
        );
        dispatcher.register(Commands.literal("kit")
            .executes(context -> executeKit(context))
        );
        dispatcher.register(Commands.literal("ekit")
            .executes(context -> executeKit(context))
        );
        dispatcher.register(Commands.literal("kits")
            .executes(context -> executeKit(context))
        );
        dispatcher.register(Commands.literal("ekits")
            .executes(context -> executeKit(context))
        );
        dispatcher.register(Commands.literal("kitreset")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("ekitreset")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("kitr")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("ekitr")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("resetkit")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("eresetkit")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("kittycannon")
            .executes(context -> executeKittycannon(context))
        );
        dispatcher.register(Commands.literal("ekittycannon")
            .executes(context -> executeKittycannon(context))
        );
        dispatcher.register(Commands.literal("lightning")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("elightning")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("shock")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("eshock")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("smite")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("esmite")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("strike")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("estrike")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("thor")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("ethor")
            .executes(context -> executeLightning(context))
        );
        dispatcher.register(Commands.literal("list")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("elist")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("online")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("eonline")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("playerlist")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("eplayerlist")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("plist")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("eplist")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("who")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("ewho")
            .executes(context -> executeList(context))
        );
        dispatcher.register(Commands.literal("loom")
            .executes(context -> executeLoom(context))
        );
        dispatcher.register(Commands.literal("eloom")
            .executes(context -> executeLoom(context))
        );
        dispatcher.register(Commands.literal("mail")
            .executes(context -> executeMail(context))
        );
        dispatcher.register(Commands.literal("email")
            .executes(context -> executeMail(context))
        );
        dispatcher.register(Commands.literal("eemail")
            .executes(context -> executeMail(context))
        );
        dispatcher.register(Commands.literal("memo")
            .executes(context -> executeMail(context))
        );
        dispatcher.register(Commands.literal("ememo")
            .executes(context -> executeMail(context))
        );
        dispatcher.register(Commands.literal("me")
            .executes(context -> executeMe(context))
        );
        dispatcher.register(Commands.literal("action")
            .executes(context -> executeMe(context))
        );
        dispatcher.register(Commands.literal("eaction")
            .executes(context -> executeMe(context))
        );
        dispatcher.register(Commands.literal("describe")
            .executes(context -> executeMe(context))
        );
        dispatcher.register(Commands.literal("edescribe")
            .executes(context -> executeMe(context))
        );
        dispatcher.register(Commands.literal("eme")
            .executes(context -> executeMe(context))
        );
        dispatcher.register(Commands.literal("more")
            .executes(context -> executeMore(context))
        );
        dispatcher.register(Commands.literal("emore")
            .executes(context -> executeMore(context))
        );
        dispatcher.register(Commands.literal("motd")
            .executes(context -> executeMotd(context))
        );
        dispatcher.register(Commands.literal("emotd")
            .executes(context -> executeMotd(context))
        );
        dispatcher.register(Commands.literal("msg")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("w")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("m")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("t")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("pm")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("emsg")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("epm")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("tell")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("etell")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("whisper")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("ewhisper")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("msgtoggle")
            .executes(context -> executeMsgtoggle(context))
        );
        dispatcher.register(Commands.literal("emsgtoggle")
            .executes(context -> executeMsgtoggle(context))
        );
        dispatcher.register(Commands.literal("mute")
            .executes(context -> executeMute(context))
        );
        dispatcher.register(Commands.literal("emute")
            .executes(context -> executeMute(context))
        );
        dispatcher.register(Commands.literal("silence")
            .executes(context -> executeMute(context))
        );
        dispatcher.register(Commands.literal("esilence")
            .executes(context -> executeMute(context))
        );
        dispatcher.register(Commands.literal("unmute")
            .executes(context -> executeMute(context))
        );
        dispatcher.register(Commands.literal("eunmute")
            .executes(context -> executeMute(context))
        );
        dispatcher.register(Commands.literal("near")
            .executes(context -> executeNear(context))
        );
        dispatcher.register(Commands.literal("enear")
            .executes(context -> executeNear(context))
        );
        dispatcher.register(Commands.literal("nearby")
            .executes(context -> executeNear(context))
        );
        dispatcher.register(Commands.literal("enearby")
            .executes(context -> executeNear(context))
        );
        dispatcher.register(Commands.literal("nick")
            .executes(context -> executeNick(context))
        );
        dispatcher.register(Commands.literal("enick")
            .executes(context -> executeNick(context))
        );
        dispatcher.register(Commands.literal("nickname")
            .executes(context -> executeNick(context))
        );
        dispatcher.register(Commands.literal("enickname")
            .executes(context -> executeNick(context))
        );
        dispatcher.register(Commands.literal("nuke")
            .executes(context -> executeNuke(context))
        );
        dispatcher.register(Commands.literal("enuke")
            .executes(context -> executeNuke(context))
        );
        dispatcher.register(Commands.literal("tpoffline")
        .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeTpoffline(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("otp")
            .executes(context -> executeTpoffline(context))
        );
        dispatcher.register(Commands.literal("offlinetp")
            .executes(context -> executeTpoffline(context))
        );
        dispatcher.register(Commands.literal("tpoff")
            .executes(context -> executeTpoffline(context))
        );
        dispatcher.register(Commands.literal("tpoffline")
        .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeTpoffline(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("etpoffline")
            .executes(context -> executeTpoffline(context))
        );
        dispatcher.register(Commands.literal("pay")
            .executes(context -> executePay(context))
        );
        dispatcher.register(Commands.literal("epay")
            .executes(context -> executePay(context))
        );
        dispatcher.register(Commands.literal("paytoggle")
            .executes(context -> executePaytoggle(context))
        );
        dispatcher.register(Commands.literal("epaytoggle")
            .executes(context -> executePaytoggle(context))
        );
        dispatcher.register(Commands.literal("payoff")
            .executes(context -> executePaytoggle(context))
        );
        dispatcher.register(Commands.literal("epayoff")
            .executes(context -> executePaytoggle(context))
        );
        dispatcher.register(Commands.literal("payon")
            .executes(context -> executePaytoggle(context))
        );
        dispatcher.register(Commands.literal("epayon")
            .executes(context -> executePaytoggle(context))
        );
        dispatcher.register(Commands.literal("payconfirmtoggle")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirmtoggle")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("payconfirmoff")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirmoff")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("payconfirmon")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirmon")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("payconfirm")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirm")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("ping")
            .executes(context -> executePing(context))
        );
        dispatcher.register(Commands.literal("echo")
            .executes(context -> executePing(context))
        );
        dispatcher.register(Commands.literal("eecho")
            .executes(context -> executePing(context))
        );
        dispatcher.register(Commands.literal("eping")
            .executes(context -> executePing(context))
        );
        dispatcher.register(Commands.literal("pong")
            .executes(context -> executePing(context))
        );
        dispatcher.register(Commands.literal("epong")
            .executes(context -> executePing(context))
        );
        dispatcher.register(Commands.literal("playtime")
            .executes(context -> executePlaytime(context))
        );
        dispatcher.register(Commands.literal("eplaytime")
            .executes(context -> executePlaytime(context))
        );
        dispatcher.register(Commands.literal("potion")
            .executes(context -> executePotion(context))
        );
        dispatcher.register(Commands.literal("epotion")
            .executes(context -> executePotion(context))
        );
        dispatcher.register(Commands.literal("elixer")
            .executes(context -> executePotion(context))
        );
        dispatcher.register(Commands.literal("eelixer")
            .executes(context -> executePotion(context))
        );
        dispatcher.register(Commands.literal("powertool")
            .executes(context -> executePowertool(context))
        );
        dispatcher.register(Commands.literal("epowertool")
            .executes(context -> executePowertool(context))
        );
        dispatcher.register(Commands.literal("pt")
            .executes(context -> executePowertool(context))
        );
        dispatcher.register(Commands.literal("ept")
            .executes(context -> executePowertool(context))
        );
        dispatcher.register(Commands.literal("powertoollist")
            .executes(context -> executePowertoollist(context))
        );
        dispatcher.register(Commands.literal("epowertoollist")
            .executes(context -> executePowertoollist(context))
        );
        dispatcher.register(Commands.literal("ptlist")
            .executes(context -> executePowertoollist(context))
        );
        dispatcher.register(Commands.literal("eptlist")
            .executes(context -> executePowertoollist(context))
        );
        dispatcher.register(Commands.literal("powertooltoggle")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("epowertooltoggle")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("ptt")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("eptt")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("pttoggle")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("epttoggle")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("ptime")
            .executes(context -> executePtime(context))
        );
        dispatcher.register(Commands.literal("playertime")
            .executes(context -> executePtime(context))
        );
        dispatcher.register(Commands.literal("eplayertime")
            .executes(context -> executePtime(context))
        );
        dispatcher.register(Commands.literal("eptime")
            .executes(context -> executePtime(context))
        );
        dispatcher.register(Commands.literal("pweather")
            .executes(context -> executePweather(context))
        );
        dispatcher.register(Commands.literal("playerweather")
            .executes(context -> executePweather(context))
        );
        dispatcher.register(Commands.literal("eplayerweather")
            .executes(context -> executePweather(context))
        );
        dispatcher.register(Commands.literal("epweather")
            .executes(context -> executePweather(context))
        );
        dispatcher.register(Commands.literal("r")
            .executes(context -> executeR(context))
        );
        dispatcher.register(Commands.literal("er")
            .executes(context -> executeR(context))
        );
        dispatcher.register(Commands.literal("reply")
            .executes(context -> executeR(context))
        );
        dispatcher.register(Commands.literal("ereply")
            .executes(context -> executeR(context))
        );
        dispatcher.register(Commands.literal("rtoggle")
            .executes(context -> executeRtoggle(context))
        );
        dispatcher.register(Commands.literal("ertoggle")
            .executes(context -> executeRtoggle(context))
        );
        dispatcher.register(Commands.literal("replytoggle")
            .executes(context -> executeRtoggle(context))
        );
        dispatcher.register(Commands.literal("ereplytoggle")
            .executes(context -> executeRtoggle(context))
        );
        dispatcher.register(Commands.literal("realname")
            .executes(context -> executeRealname(context))
        );
        dispatcher.register(Commands.literal("erealname")
            .executes(context -> executeRealname(context))
        );
        dispatcher.register(Commands.literal("recipe")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("formula")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("eformula")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("method")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("emethod")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("erecipe")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("recipes")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("erecipes")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("remove")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("eremove")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("butcher")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("ebutcher")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("killall")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("ekillall")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("mobkill")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("emobkill")
            .executes(context -> executeRemove(context))
        );
        dispatcher.register(Commands.literal("renamehome")
        .then(Commands.argument("oldName", com.mojang.brigadier.arguments.StringArgumentType.word())
            .then(Commands.argument("newName", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeRenamehome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "oldName"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "newName")))
            )
        )
    );
        dispatcher.register(Commands.literal("erenamehome")
            .executes(context -> executeRenamehome(context))
        );
        dispatcher.register(Commands.literal("repair")
            .executes(context -> executeRepair(context))
        );
        dispatcher.register(Commands.literal("fix")
            .executes(context -> executeRepair(context))
        );
        dispatcher.register(Commands.literal("efix")
            .executes(context -> executeRepair(context))
        );
        dispatcher.register(Commands.literal("erepair")
            .executes(context -> executeRepair(context))
        );
        dispatcher.register(Commands.literal("rest")
            .executes(context -> executeRest(context))
        );
        dispatcher.register(Commands.literal("erest")
            .executes(context -> executeRest(context))
        );
        dispatcher.register(Commands.literal("rules")
            .executes(context -> executeRules(context))
        );
        dispatcher.register(Commands.literal("erules")
            .executes(context -> executeRules(context))
        );
        dispatcher.register(Commands.literal("seen")
            .executes(context -> executeSeen(context))
        );
        dispatcher.register(Commands.literal("eseen")
            .executes(context -> executeSeen(context))
        );
        dispatcher.register(Commands.literal("ealts")
            .executes(context -> executeSeen(context))
        );
        dispatcher.register(Commands.literal("alts")
            .executes(context -> executeSeen(context))
        );
        dispatcher.register(Commands.literal("sell")
            .executes(context -> executeSell(context))
        );
        dispatcher.register(Commands.literal("esell")
            .executes(context -> executeSell(context))
        );
        dispatcher.register(Commands.literal("sethome")
        .executes(context -> executeSethome(context))
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeSethome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );
        dispatcher.register(Commands.literal("esethome")
            .executes(context -> executeSethome(context))
        );
        dispatcher.register(Commands.literal("createhome")
            .executes(context -> executeSethome(context))
        );
        dispatcher.register(Commands.literal("ecreatehome")
            .executes(context -> executeSethome(context))
        );
        dispatcher.register(Commands.literal("setjail")
            .executes(context -> executeSetjail(context))
        );
        dispatcher.register(Commands.literal("esetjail")
            .executes(context -> executeSetjail(context))
        );
        dispatcher.register(Commands.literal("createjail")
            .executes(context -> executeSetjail(context))
        );
        dispatcher.register(Commands.literal("ecreatejail")
            .executes(context -> executeSetjail(context))
        );
        dispatcher.register(Commands.literal("settpr")
            .executes(context -> executeSettpr(context))
        );
        dispatcher.register(Commands.literal("esettpr")
            .executes(context -> executeSettpr(context))
        );
        dispatcher.register(Commands.literal("settprandom")
            .executes(context -> executeSettpr(context))
        );
        dispatcher.register(Commands.literal("esettprandom")
            .executes(context -> executeSettpr(context))
        );
        dispatcher.register(Commands.literal("setwarp")
            .executes(context -> executeSetwarp(context))
        );
        dispatcher.register(Commands.literal("createwarp")
            .executes(context -> executeSetwarp(context))
        );
        dispatcher.register(Commands.literal("ecreatewarp")
            .executes(context -> executeSetwarp(context))
        );
        dispatcher.register(Commands.literal("esetwarp")
            .executes(context -> executeSetwarp(context))
        );
        dispatcher.register(Commands.literal("setworth")
            .executes(context -> executeSetworth(context))
        );
        dispatcher.register(Commands.literal("esetworth")
            .executes(context -> executeSetworth(context))
        );
        dispatcher.register(Commands.literal("showkit")
            .executes(context -> executeShowkit(context))
        );
        dispatcher.register(Commands.literal("kitpreview")
            .executes(context -> executeShowkit(context))
        );
        dispatcher.register(Commands.literal("preview")
            .executes(context -> executeShowkit(context))
        );
        dispatcher.register(Commands.literal("kitshow")
            .executes(context -> executeShowkit(context))
        );
        dispatcher.register(Commands.literal("editsign")
            .executes(context -> executeEditsign(context))
        );
        dispatcher.register(Commands.literal("sign")
            .executes(context -> executeEditsign(context))
        );
        dispatcher.register(Commands.literal("esign")
            .executes(context -> executeEditsign(context))
        );
        dispatcher.register(Commands.literal("eeditsign")
            .executes(context -> executeEditsign(context))
        );
        dispatcher.register(Commands.literal("skull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("eskull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("playerskull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("eplayerskull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("head")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("ehead")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("smithingtable")
            .executes(context -> executeSmithingtable(context))
        );
        dispatcher.register(Commands.literal("esmithingtable")
            .executes(context -> executeSmithingtable(context))
        );
        dispatcher.register(Commands.literal("smithtable")
            .executes(context -> executeSmithingtable(context))
        );
        dispatcher.register(Commands.literal("esmithtable")
            .executes(context -> executeSmithingtable(context))
        );
        dispatcher.register(Commands.literal("socialspy")
            .executes(context -> executeSocialspy(context))
        );
        dispatcher.register(Commands.literal("esocialspy")
            .executes(context -> executeSocialspy(context))
        );
        dispatcher.register(Commands.literal("spawner")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("changems")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("echangems")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("espawner")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("mobspawner")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("emobspawner")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("spawnmob")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("mob")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("emob")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("spawnentity")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("espawnentity")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("espawnmob")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("speed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("flyspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("eflyspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("fspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("efspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("espeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("walkspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("ewalkspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("wspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("ewspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("stonecutter")
            .executes(context -> executeStonecutter(context))
        );
        dispatcher.register(Commands.literal("estonecutter")
            .executes(context -> executeStonecutter(context))
        );
        dispatcher.register(Commands.literal("sudo")
            .executes(context -> executeSudo(context))
        );
        dispatcher.register(Commands.literal("esudo")
            .executes(context -> executeSudo(context))
        );
        dispatcher.register(Commands.literal("suicide")
            .executes(context -> executeSuicide(context))
        );
        dispatcher.register(Commands.literal("esuicide")
            .executes(context -> executeSuicide(context))
        );
        dispatcher.register(Commands.literal("tempban")
            .executes(context -> executeTempban(context))
        );
        dispatcher.register(Commands.literal("etempban")
            .executes(context -> executeTempban(context))
        );
        dispatcher.register(Commands.literal("tempbanip")
            .executes(context -> executeTempbanip(context))
        );
        dispatcher.register(Commands.literal("etempbanip")
            .executes(context -> executeTempbanip(context))
        );
        dispatcher.register(Commands.literal("thunder")
            .executes(context -> executeThunder(context))
        );
        dispatcher.register(Commands.literal("ethunder")
            .executes(context -> executeThunder(context))
        );
        dispatcher.register(Commands.literal("time")
            .executes(context -> executeTime(context))
        );
        dispatcher.register(Commands.literal("day")
            .executes(context -> executeTime(context))
        );
        dispatcher.register(Commands.literal("eday")
            .executes(context -> executeTime(context))
        );
        dispatcher.register(Commands.literal("night")
            .executes(context -> executeTime(context))
        );
        dispatcher.register(Commands.literal("enight")
            .executes(context -> executeTime(context))
        );
        dispatcher.register(Commands.literal("etime")
            .executes(context -> executeTime(context))
        );
        dispatcher.register(Commands.literal("togglejail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("jail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("ejail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("tjail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("etjail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("etogglejail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("unjail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("eunjail")
            .executes(context -> executeTogglejail(context))
        );
        dispatcher.register(Commands.literal("top")
            .executes(context -> executeTop(context))
        );
        dispatcher.register(Commands.literal("etop")
            .executes(context -> executeTop(context))
        );
        dispatcher.register(Commands.literal("tp")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("tele")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("etele")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("teleport")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("eteleport")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("etp")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("tp2p")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("etp2p")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        dispatcher.register(Commands.literal("tpa")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpa(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("call")
            .executes(context -> executeTpa(context))
        );
        dispatcher.register(Commands.literal("ecall")
            .executes(context -> executeTpa(context))
        );
        dispatcher.register(Commands.literal("etpa")
            .executes(context -> executeTpa(context))
        );
        dispatcher.register(Commands.literal("tpask")
            .executes(context -> executeTpa(context))
        );
        dispatcher.register(Commands.literal("etpask")
            .executes(context -> executeTpa(context))
        );
        dispatcher.register(Commands.literal("tpaall")
        .executes(context -> executeTpaall(context))
    );
        dispatcher.register(Commands.literal("etpaall")
            .executes(context -> executeTpaall(context))
        );
        dispatcher.register(Commands.literal("tpaccept")
        .executes(context -> executeTpaccept(context))
    );
        dispatcher.register(Commands.literal("etpaccept")
            .executes(context -> executeTpaccept(context))
        );
        dispatcher.register(Commands.literal("tpyes")
            .executes(context -> executeTpaccept(context))
        );
        dispatcher.register(Commands.literal("etpyes")
            .executes(context -> executeTpaccept(context))
        );
        dispatcher.register(Commands.literal("tpahere")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpahere(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("etpahere")
            .executes(context -> executeTpahere(context))
        );
        dispatcher.register(Commands.literal("tpall")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpall(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("etpall")
            .executes(context -> executeTpall(context))
        );
        dispatcher.register(Commands.literal("tpauto")
        .executes(context -> executeTpauto(context))
    );
        dispatcher.register(Commands.literal("etpauto")
            .executes(context -> executeTpauto(context))
        );
        dispatcher.register(Commands.literal("tpacancel")
        .executes(context -> executeTpacancel(context))
    );
        dispatcher.register(Commands.literal("etpacancel")
            .executes(context -> executeTpacancel(context))
        );
        dispatcher.register(Commands.literal("tpdeny")
        .executes(context -> executeTpdeny(context))
    );
        dispatcher.register(Commands.literal("etpdeny")
            .executes(context -> executeTpdeny(context))
        );
        dispatcher.register(Commands.literal("tpno")
            .executes(context -> executeTpdeny(context))
        );
        dispatcher.register(Commands.literal("etpno")
            .executes(context -> executeTpdeny(context))
        );
        dispatcher.register(Commands.literal("tphere")
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeTphere(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("s")
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeTphere(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("etphere")
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeTphere(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("tpo")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpo(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("etpo")
            .executes(context -> executeTpo(context))
        );
        dispatcher.register(Commands.literal("tpohere")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpohere(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("etpohere")
            .executes(context -> executeTpohere(context))
        );
        dispatcher.register(Commands.literal("tppos")
        .then(Commands.argument("pos", net.minecraft.commands.arguments.coordinates.Vec3Argument.vec3())
            .executes(context -> executeTppos(context, net.minecraft.commands.arguments.coordinates.Vec3Argument.getCoordinates(context, "pos")))
        )
    );
        dispatcher.register(Commands.literal("etppos")
        .then(Commands.argument("pos", net.minecraft.commands.arguments.coordinates.Vec3Argument.vec3())
            .executes(context -> executeTppos(context, net.minecraft.commands.arguments.coordinates.Vec3Argument.getCoordinates(context, "pos")))
        )
    );
        dispatcher.register(Commands.literal("tpr")
        .executes(context -> executeTpr(context))
    );
        dispatcher.register(Commands.literal("etpr")
            .executes(context -> executeTpr(context))
        );
        dispatcher.register(Commands.literal("tprandom")
            .executes(context -> executeTpr(context))
        );
        dispatcher.register(Commands.literal("etprandom")
            .executes(context -> executeTpr(context))
        );
        dispatcher.register(Commands.literal("tptoggle")
        .executes(context -> executeTptoggle(context))
    );
        dispatcher.register(Commands.literal("etptoggle")
            .executes(context -> executeTptoggle(context))
        );
        dispatcher.register(Commands.literal("tree")
            .executes(context -> executeTree(context))
        );
        dispatcher.register(Commands.literal("etree")
            .executes(context -> executeTree(context))
        );
        dispatcher.register(Commands.literal("unban")
        .executes(context -> executeUnban(context, Collections.emptyList()))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeUnban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("pardon")
        .executes(context -> executeUnban(context, Collections.emptyList()))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeUnban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("eunban")
        .executes(context -> executeUnban(context, Collections.emptyList()))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeUnban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("epardon")
        .executes(context -> executeUnban(context, Collections.emptyList()))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeUnban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("unbanip")
            .executes(context -> executeUnbanip(context))
        );
        dispatcher.register(Commands.literal("eunbanip")
            .executes(context -> executeUnbanip(context))
        );
        dispatcher.register(Commands.literal("pardonip")
            .executes(context -> executeUnbanip(context))
        );
        dispatcher.register(Commands.literal("epardonip")
            .executes(context -> executeUnbanip(context))
        );
        dispatcher.register(Commands.literal("unlimited")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("eunlimited")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("ul")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("unl")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("eul")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("eunl")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("vanish")
            .executes(context -> executeVanish(context))
        );
        dispatcher.register(Commands.literal("v")
            .executes(context -> executeVanish(context))
        );
        dispatcher.register(Commands.literal("ev")
            .executes(context -> executeVanish(context))
        );
        dispatcher.register(Commands.literal("evanish")
            .executes(context -> executeVanish(context))
        );
        dispatcher.register(Commands.literal("warp")
            .executes(context -> executeWarp(context))
        );
        dispatcher.register(Commands.literal("ewarp")
            .executes(context -> executeWarp(context))
        );
        dispatcher.register(Commands.literal("warps")
            .executes(context -> executeWarp(context))
        );
        dispatcher.register(Commands.literal("ewarps")
            .executes(context -> executeWarp(context))
        );
        dispatcher.register(Commands.literal("warpinfo")
            .executes(context -> executeWarpinfo(context))
        );
        dispatcher.register(Commands.literal("ewarpinfo")
            .executes(context -> executeWarpinfo(context))
        );
        dispatcher.register(Commands.literal("weather")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("rain")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("erain")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("sky")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("esky")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("storm")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("estorm")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("sun")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("esun")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("eweather")
            .executes(context -> executeWeather(context))
        );
        dispatcher.register(Commands.literal("whois")
            .executes(context -> executeWhois(context))
        );
        dispatcher.register(Commands.literal("ewhois")
            .executes(context -> executeWhois(context))
        );
        dispatcher.register(Commands.literal("workbench")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("craft")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("ecraft")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("wb")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("ewb")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("wbench")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("ewbench")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("eworkbench")
            .executes(context -> executeWorkbench(context))
        );
        dispatcher.register(Commands.literal("world")
            .executes(context -> executeWorld(context))
        );
        dispatcher.register(Commands.literal("eworld")
            .executes(context -> executeWorld(context))
        );
        dispatcher.register(Commands.literal("worth")
            .executes(context -> executeWorth(context))
        );
        dispatcher.register(Commands.literal("eprice")
            .executes(context -> executeWorth(context))
        );
        dispatcher.register(Commands.literal("price")
            .executes(context -> executeWorth(context))
        );
        dispatcher.register(Commands.literal("eworth")
            .executes(context -> executeWorth(context))
        );
    }


    private static final java.util.Set<java.util.UUID> afkPlayers = new java.util.HashSet<>();
    private static int executeAfk(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeAntioch(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command antioch is not fully implemented yet!"));
        return 1;
    }

    private static int executeAnvil(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.AnvilMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Anvil")));
        return 1;
    }

    private static int executeBack(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command back is not fully implemented yet!"));
        return 1;
    }

    private static int executeBackup(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command backup is not fully implemented yet!"));
        return 1;
    }

    private static int executeBalance(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command balance is not fully implemented yet!"));
        return 1;
    }

    private static int executeBalancetop(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command balancetop is not fully implemented yet!"));
        return 1;
    }

    private static int executeBan(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets, String reason) {
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

    private static int executeBanip(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command banip is not fully implemented yet!"));
        return 1;
    }

    private static int executeBeezooka(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command beezooka is not fully implemented yet!"));
        return 1;
    }

    private static int executeBook(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command book is not fully implemented yet!"));
        return 1;
    }

    private static int executeBottom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int bottomY = player.level().getMinY();
        player.teleportTo(player.level(), player.getX(), bottomY, player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to bottom."));
        return 1;
    }

    private static int executeBreak(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command break is not fully implemented yet!"));
        return 1;
    }

    private static int executeBroadcast(CommandContext<CommandSourceStack> context) {
        context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("[Broadcast] This is a test broadcast."), false);
        return 1;
    }

    private static int executeBroadcastworld(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command broadcastworld is not fully implemented yet!"));
        return 1;
    }

    private static int executeBigtree(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command bigtree is not fully implemented yet!"));
        return 1;
    }

    private static int executeBurn(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command burn is not fully implemented yet!"));
        return 1;
    }

    private static int executeCartographytable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.CartographyTableMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Cartography Table")));
        return 1;
    }

    private static int executeClearinventory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getInventory().clearContent();
        context.getSource().sendSystemMessage(Component.literal("Inventory cleared."));
        return 1;
    }

    private static int executeClearinventoryconfirmtoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command clearinventoryconfirmtoggle is not fully implemented yet!"));
        return 1;
    }

    private static int executeCondense(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command condense is not fully implemented yet!"));
        return 1;
    }

    private static int executeCompass(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command compass is not fully implemented yet!"));
        return 1;
    }

    private static int executeCreatekit(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command createkit is not fully implemented yet!"));
        return 1;
    }

    private static int executeCustomtext(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command customtext is not fully implemented yet!"));
        return 1;
    }

    private static int executeDelhome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeDelhome(context, "home"); }
    private static int executeDelhome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
        if (homes != null && homes.remove(name.toLowerCase()) != null) {
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
        return 0;
    }

    private static int executeDeljail(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command deljail is not fully implemented yet!"));
        return 1;
    }

    private static int executeDelkit(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command delkit is not fully implemented yet!"));
        return 1;
    }

    private static int executeDelwarp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command delwarp is not fully implemented yet!"));
        return 1;
    }

    private static int executeDepth(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int depth = player.getBlockY() - player.level().getMinY();
        context.getSource().sendSystemMessage(Component.literal("You are " + depth + " blocks above minimum depth."));
        return 1;
    }

    private static int executeDisposal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, inv, new net.minecraft.world.SimpleContainer(27)), Component.literal("Disposal")));
        return 1;
    }

    private static int executeEco(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command eco is not fully implemented yet!"));
        return 1;
    }

    private static int executeEnchant(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command enchant is not fully implemented yet!"));
        return 1;
    }

    private static int executeEnderchest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return net.minecraft.world.inventory.ChestMenu.threeRows(id, inventory, player.getEnderChestInventory());
        }, Component.literal("Ender Chest")));
        return 1;
    }

    private static int executeEssentials(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command essentials is not fully implemented yet!"));
        return 1;
    }

    private static int executeExp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command exp is not fully implemented yet!"));
        return 1;
    }

    private static int executeExt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.clearFire();
        context.getSource().sendSystemMessage(Component.literal("You have been extinguished."));
        return 1;
    }

    private static int executeFeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        context.getSource().sendSystemMessage(Component.literal("You have been fed."));
        return 1;
    }

    private static int executeFly(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeFireball(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command fireball is not fully implemented yet!"));
        return 1;
    }

    private static int executeFirework(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command firework is not fully implemented yet!"));
        return 1;
    }

    private static int executeGamemode(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command gamemode is not fully implemented yet!"));
        return 1;
    }

    private static int executeGc(CommandContext<CommandSourceStack> context) {
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

    private static int executeGetpos(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.Vec3 pos = player.position();
        context.getSource().sendSystemMessage(Component.literal(String.format("Location: X: %.2f Y: %.2f Z: %.2f Pitch: %.1f Yaw: %.1f", pos.x, pos.y, pos.z, player.getXRot(), player.getYRot())));
        return 1;
    }

    private static int executeGive(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command give is not fully implemented yet!"));
        return 1;
    }

    private static int executeGod(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean isGod = player.isInvulnerable();
        player.setInvulnerable(!isGod);
        context.getSource().sendSystemMessage(Component.literal("God mode " + (!isGod ? "enabled" : "disabled") + "."));
        return 1;
    }

    private static int executeGrindstone(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.GrindstoneMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Grindstone")));
        return 1;
    }

    private static int executeHat(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeHeal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        player.clearFire();
        player.removeAllEffects();
        context.getSource().sendSystemMessage(Component.literal("You have been healed."));
        return 1;
    }

    private static int executeHelp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command help is not fully implemented yet!"));
        return 1;
    }

    private static int executeHelpop(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command helpop is not fully implemented yet!"));
        return 1;
    }

    private static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
    private static int executeHome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
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
        player.teleportTo(targetLevel, home.x, home.y, home.z, java.util.Collections.emptySet(), home.yaw, home.pitch, false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to home '" + name + "'."));
        return 1;
    }

    private static int executeIce(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command ice is not fully implemented yet!"));
        return 1;
    }

    private static int executeIgnore(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command ignore is not fully implemented yet!"));
        return 1;
    }

    private static int executeInfo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command info is not fully implemented yet!"));
        return 1;
    }

    private static int executeInvsee(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command invsee is not fully implemented yet!"));
        return 1;
    }

    private static int executeItem(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command item is not fully implemented yet!"));
        return 1;
    }

    private static int executeItemdb(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command itemdb is not fully implemented yet!"));
        return 1;
    }

    private static int executeItemlore(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command itemlore is not fully implemented yet!"));
        return 1;
    }

    private static int executeItemname(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command itemname is not fully implemented yet!"));
        return 1;
    }

    private static int executeJailedplayers(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command jailedplayers is not fully implemented yet!"));
        return 1;
    }

    private static int executeJails(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command jails is not fully implemented yet!"));
        return 1;
    }

    private static int executeJump(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeKick(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, String reason) {
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

    private static int executeKickall(CommandContext<CommandSourceStack> context, String reason) {
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

    private static int executeKill(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().sendSystemMessage(Component.literal("Use /kill <player> (Missing arguments not fully mapped yet)"));
        return 0;
    }

    private static int executeKit(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command kit is not fully implemented yet!"));
        return 1;
    }

    private static int executeKitreset(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command kitreset is not fully implemented yet!"));
        return 1;
    }

    private static int executeKittycannon(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command kittycannon is not fully implemented yet!"));
        return 1;
    }

    private static int executeLightning(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeList(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command list is not fully implemented yet!"));
        return 1;
    }

    private static int executeLoom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.LoomMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Loom")));
        return 1;
    }

    private static int executeMail(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command mail is not fully implemented yet!"));
        return 1;
    }

    private static int executeMe(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command me is not fully implemented yet!"));
        return 1;
    }

    private static int executeMore(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeMotd(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command motd is not fully implemented yet!"));
        return 1;
    }

    private static int executeMsg(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command msg is not fully implemented yet!"));
        return 1;
    }

    private static int executeMsgtoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command msgtoggle is not fully implemented yet!"));
        return 1;
    }

    private static int executeMute(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command mute is not fully implemented yet!"));
        return 1;
    }

    private static int executeNear(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command near is not fully implemented yet!"));
        return 1;
    }

    private static int executeNick(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command nick is not fully implemented yet!"));
        return 1;
    }

    private static int executeNuke(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeTpoffline(CommandContext<CommandSourceStack> context, String targetName) throws CommandSyntaxException {
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
            player.teleportTo(targetLevel, pos.x, pos.y, pos.z, java.util.Collections.emptySet(), pos.yaw, pos.pitch, false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + targetName + "'s last known offline location."));
            return 1;
        }
        return 0;
    }

    private static int executePay(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command pay is not fully implemented yet!"));
        return 1;
    }

    private static int executePaytoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command paytoggle is not fully implemented yet!"));
        return 1;
    }

    private static int executePayconfirmtoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command payconfirmtoggle is not fully implemented yet!"));
        return 1;
    }

    private static int executePing(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Pong!"));
        return 1;
    }

    private static int executePlaytime(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command playtime is not fully implemented yet!"));
        return 1;
    }

    private static int executePotion(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command potion is not fully implemented yet!"));
        return 1;
    }

    private static int executePowertool(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command powertool is not fully implemented yet!"));
        return 1;
    }

    private static int executePowertoollist(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command powertoollist is not fully implemented yet!"));
        return 1;
    }

    private static int executePowertooltoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command powertooltoggle is not fully implemented yet!"));
        return 1;
    }

    private static int executePtime(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command ptime is not fully implemented yet!"));
        return 1;
    }

    private static int executePweather(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command pweather is not fully implemented yet!"));
        return 1;
    }

    private static int executeR(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command r is not fully implemented yet!"));
        return 1;
    }

    private static int executeRtoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command rtoggle is not fully implemented yet!"));
        return 1;
    }

    private static int executeRealname(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command realname is not fully implemented yet!"));
        return 1;
    }

    private static int executeRecipe(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command recipe is not fully implemented yet!"));
        return 1;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command remove is not fully implemented yet!"));
        return 1;
    }

    private static int executeRenamehome(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /renamehome <old> <new>")); return 0; }
    private static int executeRenamehome(CommandContext<CommandSourceStack> context, String oldName, String newName) throws CommandSyntaxException {
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

    private static int executeRepair(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeRest(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command rest is not fully implemented yet!"));
        return 1;
    }

    private static int executeRules(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command rules is not fully implemented yet!"));
        return 1;
    }

    private static int executeSeen(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command seen is not fully implemented yet!"));
        return 1;
    }

    private static int executeSell(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command sell is not fully implemented yet!"));
        return 1;
    }

    private static int executeSethome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeSethome(context, "home"); }
    private static int executeSethome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String dim = player.level().dimension().identifier().toString();
        HomePosition home = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim);
        playerHomes.computeIfAbsent(player.getUUID(), k -> new java.util.HashMap<>()).put(name.toLowerCase(), home);
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' set."));
        return 1;
    }

    private static int executeSetjail(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command setjail is not fully implemented yet!"));
        return 1;
    }

    private static int executeSettpr(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command settpr is not fully implemented yet!"));
        return 1;
    }

    private static int executeSetwarp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command setwarp is not fully implemented yet!"));
        return 1;
    }

    private static int executeSetworth(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command setworth is not fully implemented yet!"));
        return 1;
    }

    private static int executeShowkit(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command showkit is not fully implemented yet!"));
        return 1;
    }

    private static int executeEditsign(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command editsign is not fully implemented yet!"));
        return 1;
    }

    private static int executeSkull(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command skull is not fully implemented yet!"));
        return 1;
    }

    private static int executeSmithingtable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.SmithingMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Smithing Table")));
        return 1;
    }

    private static int executeSocialspy(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command socialspy is not fully implemented yet!"));
        return 1;
    }

    private static int executeSpawner(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command spawner is not fully implemented yet!"));
        return 1;
    }

    private static int executeSpawnmob(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command spawnmob is not fully implemented yet!"));
        return 1;
    }

    private static int executeSpeed(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command speed is not fully implemented yet!"));
        return 1;
    }

    private static int executeStonecutter(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.StonecutterMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Stonecutter")));
        return 1;
    }

    private static int executeSudo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command sudo is not fully implemented yet!"));
        return 1;
    }

    private static int executeSuicide(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.kill(player.level());
        context.getSource().sendSystemMessage(Component.literal("You took your own life."));
        return 1;
    }

    private static int executeTempban(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command tempban is not fully implemented yet!"));
        return 1;
    }

    private static int executeTempbanip(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command tempbanip is not fully implemented yet!"));
        return 1;
    }

    private static int executeThunder(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command thunder is not fully implemented yet!"));
        return 1;
    }

    private static int executeTime(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command time is not fully implemented yet!"));
        return 1;
    }

    private static int executeTogglejail(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command togglejail is not fully implemented yet!"));
        return 1;
    }

    private static int executeTop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int topY = player.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
        player.teleportTo(player.level(), player.getX(), topY, player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to top."));
        return 1;
    }

    private static int executeTp(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, net.minecraft.world.entity.Entity destination) throws CommandSyntaxException {
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer player) {
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

    private static int executeTpa(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
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

    private static int executeTpaall(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeTpaccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeTpahere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
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

    private static int executeTpall(CommandContext<CommandSourceStack> context, ServerPlayer target) {
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

    private static int executeTpauto(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeTpacancel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeTpdeny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeTphere(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer pTarget) {
                pTarget.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            } else {
                target.teleportTo(player.getX(), player.getY(), player.getZ());
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to you."));
        return targets.size();
    }

    private static int executeTpo(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.teleportTo(target.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to " + target.getName().getString() + " (Override)."));
        return 1;
    }

    private static int executeTpohere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        target.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported " + target.getName().getString() + " to you (Override)."));
        return 1;
    }

    private static int executeTppos(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.coordinates.Coordinates pos) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.Vec3 vec = pos.getPosition(context.getSource());
        player.teleportTo(player.level(), vec.x, vec.y, vec.z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal(String.format("Teleported to %.1f, %.1f, %.1f", vec.x, vec.y, vec.z)));
        return 1;
    }

    private static int executeTpr(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
        player.teleportTo(player.level(), x, y + 1.0, z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal(String.format("Randomly teleported to X: %.1f Z: %.1f", x, z)));
        return 1;
    }

    private static int executeTptoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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

    private static int executeTree(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command tree is not fully implemented yet!"));
        return 1;
    }

    private static int executeUnban(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets) {
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

    private static int executeUnbanip(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command unbanip is not fully implemented yet!"));
        return 1;
    }

    private static int executeUnlimited(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command unlimited is not fully implemented yet!"));
        return 1;
    }

    private static int executeVanish(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command vanish is not fully implemented yet!"));
        return 1;
    }

    private static int executeWarp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command warp is not fully implemented yet!"));
        return 1;
    }

    private static int executeWarpinfo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command warpinfo is not fully implemented yet!"));
        return 1;
    }

    private static int executeWeather(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command weather is not fully implemented yet!"));
        return 1;
    }

    private static int executeWhois(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command whois is not fully implemented yet!"));
        return 1;
    }

    private static int executeWorkbench(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.CraftingMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Crafting")));
        return 1;
    }

    private static int executeWorld(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command world is not fully implemented yet!"));
        return 1;
    }

    private static int executeWorth(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command worth is not fully implemented yet!"));
        return 1;
    }
}
