import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r') as f:
    content = f.read()

# Add necessary imports if missing
if 'import net.minecraft.server.level.ServerPlayer;' not in content:
    content = content.replace('import net.minecraft.network.chat.Component;', 
                              'import net.minecraft.network.chat.Component;\nimport net.minecraft.server.level.ServerPlayer;\nimport com.mojang.brigadier.exceptions.CommandSyntaxException;')

# 1. /heal
heal_stub = """    private static int executeHeal(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command heal is not fully implemented yet!"));
        return 1;
    }"""
heal_impl = """    private static int executeHeal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        player.clearFire();
        player.removeAllEffects();
        context.getSource().sendSystemMessage(Component.literal("You have been healed."));
        return 1;
    }"""
content = content.replace(heal_stub, heal_impl)

# 2. /feed
feed_stub = """    private static int executeFeed(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command feed is not fully implemented yet!"));
        return 1;
    }"""
feed_impl = """    private static int executeFeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        context.getSource().sendSystemMessage(Component.literal("You have been fed."));
        return 1;
    }"""
content = content.replace(feed_stub, feed_impl)

# 3. /suicide
suicide_stub = """    private static int executeSuicide(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command suicide is not fully implemented yet!"));
        return 1;
    }"""
suicide_impl = """    private static int executeSuicide(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.kill();
        context.getSource().sendSystemMessage(Component.literal("You took your own life."));
        return 1;
    }"""
content = content.replace(suicide_stub, suicide_impl)

# 4. /fly
fly_stub = """    private static int executeFly(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command fly is not fully implemented yet!"));
        return 1;
    }"""
fly_impl = """    private static int executeFly(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean isFlying = player.getAbilities().mayfly;
        player.getAbilities().mayfly = !isFlying;
        if (isFlying) {
            player.getAbilities().flying = false;
        }
        player.onUpdateAbilities();
        context.getSource().sendSystemMessage(Component.literal("Set fly mode to " + (!isFlying ? "enabled" : "disabled") + " for " + player.getName().getString() + "."));
        return 1;
    }"""
content = content.replace(fly_stub, fly_impl)

# 5. /god
god_stub = """    private static int executeGod(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command god is not fully implemented yet!"));
        return 1;
    }"""
god_impl = """    private static int executeGod(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean isGod = player.isInvulnerable();
        player.setInvulnerable(!isGod);
        context.getSource().sendSystemMessage(Component.literal("God mode " + (!isGod ? "enabled" : "disabled") + "."));
        return 1;
    }"""
content = content.replace(god_stub, god_impl)

with open(filepath, 'w') as f:
    f.write(content)

print("Successfully injected Tier 1 commands!")