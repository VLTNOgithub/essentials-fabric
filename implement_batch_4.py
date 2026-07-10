import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Helper for replacing registration
def replace_reg(cmd_name, method_name, new_reg):
    global content
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)

# Helper for replacing method body
def replace_meth(method_name, new_meth):
    global content
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
    content = re.sub(meth_pattern, new_meth, content)

# --- 1. /kill ---
reg_kill = '''dispatcher.register(Commands.literal("kill")
        .executes(context -> executeKill(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeKill(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    );'''
meth_kill = '''    private static int executeKill(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.kill((net.minecraft.server.level.ServerLevel) target.level());
        }
        context.getSource().sendSystemMessage(Component.literal("Killed " + targets.size() + " entities."));
        return targets.size();
    }'''
content = re.sub(r'    private static int executeKill\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 0;\n    \}', meth_kill, content)
replace_reg('kill', 'executeKill', reg_kill)

# --- 2. /burn ---
reg_burn = '''dispatcher.register(Commands.literal("burn")
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .then(Commands.argument("seconds", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                .executes(context -> executeBurn(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "seconds")))
            )
        )
    );'''
meth_burn = '''    private static int executeBurn(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /burn <player> <seconds>")); return 0; }
    private static int executeBurn(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, int seconds) {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.igniteForSeconds(seconds);
        }
        context.getSource().sendSystemMessage(Component.literal("Ignited " + targets.size() + " entities for " + seconds + " seconds."));
        return targets.size();
    }'''
replace_reg('burn', 'executeBurn', reg_burn)
replace_meth('executeBurn', meth_burn)

# --- 3. /break ---
meth_break = '''    private static int executeBreak(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
    }'''
replace_meth('executeBreak', meth_break)

# --- 4. /list ---
meth_list = '''    private static int executeList(CommandContext<CommandSourceStack> context) {
        java.util.List<ServerPlayer> players = context.getSource().getServer().getPlayerList().getPlayers();
        String names = players.stream().map(p -> p.getName().getString()).collect(java.util.stream.Collectors.joining(", "));
        context.getSource().sendSystemMessage(Component.literal("There are " + players.size() + "/" + context.getSource().getServer().getMaxPlayers() + " players online:\n" + names));
        return 1;
    }'''
replace_meth('executeList', meth_list)

# --- 5. /invsee ---
reg_invsee = '''dispatcher.register(Commands.literal("invsee")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeInvsee(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
meth_invsee = '''    private static int executeInvsee(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /invsee <player>")); return 0; }
    private static int executeInvsee(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> {
            return new net.minecraft.world.inventory.ChestMenu(net.minecraft.world.inventory.MenuType.GENERIC_9x4, id, inv, target.getInventory(), 4);
        }, Component.literal(target.getName().getString() + "'s Inventory")));
        return 1;
    }'''
replace_reg('invsee', 'executeInvsee', reg_invsee)
replace_meth('executeInvsee', meth_invsee)

# --- 6. /gamemode ---
reg_gamemode = '''dispatcher.register(Commands.literal("gamemode")
        .then(Commands.argument("mode", net.minecraft.commands.arguments.GameModeArgument.gameMode())
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
            )
        )
    );'''
meth_gamemode = '''    private static int executeGamemode(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /gamemode <mode> [player]")); return 0; }
    private static int executeGamemode(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, net.minecraft.world.level.GameType mode) {
        for (ServerPlayer target : targets) {
            target.setGameMode(mode);
            target.sendSystemMessage(Component.literal("Your game mode has been updated to " + mode.getName() + "."));
        }
        if (targets.size() == 1 && targets.iterator().next() == context.getSource().getEntity()) return 1;
        context.getSource().sendSystemMessage(Component.literal("Set game mode " + mode.getName() + " for " + targets.size() + " players."));
        return targets.size();
    }'''
replace_reg('gamemode', 'executeGamemode', reg_gamemode)
replace_meth('executeGamemode', meth_gamemode)

# --- 7. /time ---
reg_time = '''dispatcher.register(Commands.literal("time")
        .then(Commands.literal("day").executes(context -> executeTime(context, 1000)))
        .then(Commands.literal("night").executes(context -> executeTime(context, 13000)))
    );'''
meth_time = '''    private static int executeTime(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /time <day|night>")); return 0; }
    private static int executeTime(CommandContext<CommandSourceStack> context, int time) {
        context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD).setDayTime(time);
        context.getSource().sendSystemMessage(Component.literal("Time set to " + time + "."));
        return 1;
    }'''
replace_reg('time', 'executeTime', reg_time)
replace_meth('executeTime', meth_time)

# --- 8. /weather ---
reg_weather = '''dispatcher.register(Commands.literal("weather")
        .then(Commands.literal("clear").executes(context -> executeWeather(context, 0)))
        .then(Commands.literal("rain").executes(context -> executeWeather(context, 1)))
        .then(Commands.literal("thunder").executes(context -> executeWeather(context, 2)))
    );'''
meth_weather = '''    private static int executeWeather(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /weather <clear|rain|thunder>")); return 0; }
    private static int executeWeather(CommandContext<CommandSourceStack> context, int type) {
        net.minecraft.server.level.ServerLevel level = context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (type == 0) level.setWeatherParameters(6000, 0, false, false);
        else if (type == 1) level.setWeatherParameters(0, 6000, true, false);
        else if (type == 2) level.setWeatherParameters(0, 6000, true, true);
        context.getSource().sendSystemMessage(Component.literal("Weather updated."));
        return 1;
    }'''
replace_reg('weather', 'executeWeather', reg_weather)
replace_meth('executeWeather', meth_weather)

# --- 9. /me ---
reg_me = '''dispatcher.register(Commands.literal("me")
        .then(Commands.argument("action", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeMe(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "action")))
        )
    );'''
meth_me = '''    private static int executeMe(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /me <action>")); return 0; }
    private static int executeMe(CommandContext<CommandSourceStack> context, String action) {
        context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(" * " + context.getSource().getTextName() + " " + action), false);
        return 1;
    }'''
replace_reg('me', 'executeMe', reg_me)
replace_meth('executeMe', meth_me)

# --- 10. /msg & /r (Private Messages) ---
reply_map = '''
    private static final java.util.Map<java.util.UUID, java.util.UUID> replyMap = new java.util.HashMap<>();
'''
if 'private static final java.util.Map<java.util.UUID, java.util.UUID> replyMap' not in content:
    content = content.replace('public static class TeleportRequest', reply_map + '\n    public static class TeleportRequest')

reg_msg = '''dispatcher.register(Commands.literal("msg")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeMsg(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
            )
        )
    );'''
meth_msg = '''    private static int executeMsg(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /msg <player> <message>")); return 0; }
    private static int executeMsg(CommandContext<CommandSourceStack> context, ServerPlayer target, String message) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        replyMap.put(sender.getUUID(), target.getUUID());
        replyMap.put(target.getUUID(), sender.getUUID());
        sender.sendSystemMessage(Component.literal("[me -> " + target.getName().getString() + "] " + message));
        target.sendSystemMessage(Component.literal("[" + sender.getName().getString() + " -> me] " + message));
        return 1;
    }'''
replace_reg('msg', 'executeMsg', reg_msg)
replace_meth('executeMsg', meth_msg)

reg_r = '''dispatcher.register(Commands.literal("r")
        .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeR(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
        )
    );'''
meth_r = '''    private static int executeR(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /r <message>")); return 0; }
    private static int executeR(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
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
    }'''
replace_reg('r', 'executeR', reg_r)
replace_meth('executeR', meth_r)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Batch 4 injected!")