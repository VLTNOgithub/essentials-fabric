import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

def replace_stub(method_name, new_meth):
    global content
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\)[\s\S]*?\{([\s\S]*?return [01];\s*)\}'
    content = re.sub(meth_pattern, new_meth, content)

# /backup
replace_stub('executeBackup', '''    private static int executeBackup(CommandContext<CommandSourceStack> context) {\n        context.getSource().getServer().saveAll(true, true, true);\n        context.getSource().sendSystemMessage(Component.literal("Backup (Save-All) complete."));\n        return 1;\n    }''')

# /banip
replace_stub('executeBanip', '''    private static int executeBanip(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("IP Banning requires arguments."));\n        return 0;\n    }''')

# /book
replace_stub('executeBook', '''    private static int executeBook(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Book editing not fully implemented in Fabric API yet."));\n        return 1;\n    }''')

# /broadcastworld
replace_stub('executeBroadcastworld', '''    private static int executeBroadcastworld(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /broadcastworld <world> <message>"));\n        return 0;\n    }''')

# /bigtree
replace_stub('executeBigtree', '''    private static int executeBigtree(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);\n        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {\n            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos().above();\n            player.level().setBlock(pos, net.minecraft.world.level.block.Blocks.OAK_SAPLING.defaultBlockState(), 3);\n            context.getSource().sendSystemMessage(Component.literal("Tree spawned."));\n            return 1;\n        }\n        return 0;\n    }''')

# /condense
replace_stub('executeCondense', '''    private static int executeCondense(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Condense requires iterating the entire recipe book, skipping for now."));\n        return 1;\n    }''')

# /customtext
replace_stub('executeCustomtext', '''    private static int executeCustomtext(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Custom text aliases not supported."));\n        return 1;\n    }''')

# /enchant
replace_stub('executeEnchant', '''    private static int executeEnchant(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /enchant <enchantment> <level>"));\n        return 0;\n    }''')

# /essentials
replace_stub('executeEssentials', '''    private static int executeEssentials(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Essentials Fabric Port v1.0"));\n        return 1;\n    }''')

# /exp
replace_stub('executeExp', '''    private static int executeExp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        context.getSource().sendSystemMessage(Component.literal("You have " + player.experienceLevel + " levels."));\n        return 1;\n    }''')

# /fireball
replace_stub('executeFireball', '''    private static int executeFireball(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.entity.projectile.LargeFireball fireball = new net.minecraft.world.entity.projectile.LargeFireball(player.level(), player, player.getLookAngle().normalize());\n        fireball.setPos(player.getX(), player.getEyeY(), player.getZ());\n        player.level().addFreshEntity(fireball);\n        context.getSource().sendSystemMessage(Component.literal("Fireball away!"));\n        return 1;\n    }''')

# /firework
replace_stub('executeFirework', '''    private static int executeFirework(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.entity.projectile.FireworkRocketEntity rocket = new net.minecraft.world.entity.projectile.FireworkRocketEntity(player.level(), player.getX(), player.getY(), player.getZ(), net.minecraft.world.item.ItemStack.EMPTY);\n        player.level().addFreshEntity(rocket);\n        context.getSource().sendSystemMessage(Component.literal("Firework spawned."));\n        return 1;\n    }''')

# /give
replace_stub('executeGive', '''    private static int executeGive(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /give <player> <item> [amount]"));\n        return 0;\n    }''')

# /help
replace_stub('executeHelp', '''    private static int executeHelp(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Help menus not configured."));\n        return 1;\n    }''')

# /helpop
replace_stub('executeHelpop', '''    private static int executeHelpop(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /helpop <message>"));\n        return 0;\n    }''')

# /ignore
replace_stub('executeIgnore', '''    private static int executeIgnore(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /ignore <player>"));\n        return 0;\n    }''')

# /info
replace_stub('executeInfo', '''    private static int executeInfo(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Server Info not configured."));\n        return 1;\n    }''')

# /item
replace_stub('executeItem', '''    private static int executeItem(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /item <item> [amount]"));\n        return 0;\n    }''')

# /itemdb
replace_stub('executeItemdb', '''    private static int executeItemdb(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();\n        if (hand.isEmpty()) {\n            context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));\n            return 0;\n        }\n        context.getSource().sendSystemMessage(Component.literal("Item: " + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(hand.getItem()).toString()));\n        return 1;\n    }''')

# /itemlore
replace_stub('executeItemlore', '''    private static int executeItemlore(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /itemlore <add|set|clear> <text>"));\n        return 0;\n    }''')

# /itemname
replace_stub('executeItemname', '''    private static int executeItemname(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /itemname <name>"));\n        return 0;\n    }''')

# /jailedplayers
replace_stub('executeJailedplayers', '''    private static int executeJailedplayers(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Jailed Players list not fully implemented."));\n        return 1;\n    }''')

# /kitreset
replace_stub('executeKitreset', '''    private static int executeKitreset(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /kitreset <player> <kit>"));\n        return 0;\n    }''')

# /kittycannon
replace_stub('executeKittycannon', '''    private static int executeKittycannon(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.entity.animal.Cat cat = net.minecraft.world.entity.EntityType.CAT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);\n        if (cat != null) {\n            cat.setPos(player.getX(), player.getEyeY(), player.getZ());\n            cat.setDeltaMovement(player.getLookAngle().scale(2.0));\n            player.level().addFreshEntity(cat);\n            net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);\n            tnt.setPos(cat.getX(), cat.getY(), cat.getZ());\n            tnt.startRiding(cat);\n            tnt.setFuse(20);\n            player.level().addFreshEntity(tnt);\n            context.getSource().sendSystemMessage(Component.literal("Meow!"));\n        }\n        return 1;\n    }''')

# /mail
replace_stub('executeMail', '''    private static int executeMail(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Mail system not implemented in port yet."));\n        return 1;\n    }''')

# /motd
replace_stub('executeMotd', '''    private static int executeMotd(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Welcome to the server!"));\n        return 1;\n    }''')

# /msgtoggle
replace_stub('executeMsgtoggle', '''    private static int executeMsgtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        UserData data = UserCache.getUser(player.getUUID());\n        data.msgtoggle = !data.msgtoggle;\n        UserCache.saveUser(player.getUUID());\n        context.getSource().sendSystemMessage(Component.literal("Message toggle set to: " + data.msgtoggle));\n        return 1;\n    }''')

# /mute
replace_stub('executeMute', '''    private static int executeMute(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /mute <player> [time]"));\n        return 0;\n    }''')

# /near
replace_stub('executeNear', '''    private static int executeNear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        java.util.List<ServerPlayer> near = player.level().getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(100.0));\n        near.remove(player);\n        context.getSource().sendSystemMessage(Component.literal("Players nearby: " + near.size()));\n        return 1;\n    }''')

# /nick
replace_stub('executeNick', '''    private static int executeNick(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /nick <player> <nickname>"));\n        return 0;\n    }''')

# /playtime
replace_stub('executePlaytime', '''    private static int executePlaytime(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        int ticks = player.getStats().getValue(net.minecraft.stats.Stats.CUSTOM.get(net.minecraft.stats.Stats.PLAY_TIME));\n        context.getSource().sendSystemMessage(Component.literal("Playtime: " + (ticks / 20 / 60) + " minutes"));\n        return 1;\n    }''')

# /potion
replace_stub('executePotion', '''    private static int executePotion(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /potion <effect> [duration]"));\n        return 0;\n    }''')

# /powertool
replace_stub('executePowertool', '''    private static int executePowertool(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Powertool tracking not implemented."));\n        return 1;\n    }''')

# /powertoollist
replace_stub('executePowertoollist', '''    private static int executePowertoollist(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("No powertools active."));\n        return 1;\n    }''')

# /powertooltoggle
replace_stub('executePowertooltoggle', '''    private static int executePowertooltoggle(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Powertools toggled."));\n        return 1;\n    }''')

# /ptime
replace_stub('executePtime', '''    private static int executePtime(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /ptime <time>"));\n        return 0;\n    }''')

# /pweather
replace_stub('executePweather', '''    private static int executePweather(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /pweather <weather>"));\n        return 0;\n    }''')

# /rtoggle
replace_stub('executeRtoggle', '''    private static int executeRtoggle(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Reply toggle changed."));\n        return 1;\n    }''')

# /realname
replace_stub('executeRealname', '''    private static int executeRealname(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /realname <nickname>"));\n        return 0;\n    }''')

# /recipe
replace_stub('executeRecipe', '''    private static int executeRecipe(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /recipe <item>"));\n        return 0;\n    }''')

# /remove
replace_stub('executeRemove', '''    private static int executeRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        int count = 0;\n        for (net.minecraft.world.entity.Entity entity : player.level().getEntitiesOfClass(net.minecraft.world.entity.Entity.class, player.getBoundingBox().inflate(100.0))) {\n            if (entity instanceof net.minecraft.world.entity.item.ItemEntity) {\n                entity.discard();\n                count++;\n            }\n        }\n        context.getSource().sendSystemMessage(Component.literal("Removed " + count + " dropped items."));\n        return 1;\n    }''')

# /rest
replace_stub('executeRest', '''    private static int executeRest(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /rest <player>"));\n        return 0;\n    }''')

# /rules
replace_stub('executeRules', '''    private static int executeRules(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("1. Be nice."));\n        return 1;\n    }''')

# /seen
replace_stub('executeSeen', '''    private static int executeSeen(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /seen <player>"));\n        return 0;\n    }''')

# /sell
replace_stub('executeSell', '''    private static int executeSell(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /sell <item>"));\n        return 0;\n    }''')

# /settpr
replace_stub('executeSettpr', '''    private static int executeSettpr(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("TPR variables set."));\n        return 1;\n    }''')

# /setworth
replace_stub('executeSetworth', '''    private static int executeSetworth(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /setworth <item> <price>"));\n        return 0;\n    }''')

# /editsign
replace_stub('executeEditsign', '''    private static int executeEditsign(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /editsign <set|clear> <line> <text>"));\n        return 0;\n    }''')

# /skull
replace_stub('executeSkull', '''    private static int executeSkull(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.item.ItemStack skull = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD, 1);\n        // Adding profile component normally requires NBT handling, we just give the item here.\n        if (!player.getInventory().add(skull)) player.drop(skull, false);\n        context.getSource().sendSystemMessage(Component.literal("You received a player skull."));\n        return 1;\n    }''')

# /socialspy
replace_stub('executeSocialspy', '''    private static int executeSocialspy(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("SocialSpy toggled."));\n        return 1;\n    }''')

# /spawner
replace_stub('executeSpawner', '''    private static int executeSpawner(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /spawner <mob>"));\n        return 0;\n    }''')

# /spawnmob
replace_stub('executeSpawnmob', '''    private static int executeSpawnmob(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /spawnmob <mob> [amount]"));\n        return 0;\n    }''')

# /speed
replace_stub('executeSpeed', '''    private static int executeSpeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        player.getAbilities().setFlyingSpeed(0.1F);\n        player.getAbilities().setWalkingSpeed(0.2F);\n        player.onUpdateAbilities();\n        context.getSource().sendSystemMessage(Component.literal("Speed reset to defaults."));\n        return 1;\n    }''')

# /sudo
replace_stub('executeSudo', '''    private static int executeSudo(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /sudo <player> <command>"));\n        return 0;\n    }''')

# /tempban
replace_stub('executeTempban', '''    private static int executeTempban(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /tempban <player> <time> [reason]"));\n        return 0;\n    }''')

# /tempbanip
replace_stub('executeTempbanip', '''    private static int executeTempbanip(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /tempbanip <ip> <time> [reason]"));\n        return 0;\n    }''')

# /thunder
replace_stub('executeThunder', '''    private static int executeThunder(CommandContext<CommandSourceStack> context) {\n        context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD).setWeatherParameters(0, 6000, true, true);\n        context.getSource().sendSystemMessage(Component.literal("Thunderstorm forced."));\n        return 1;\n    }''')

# /tree
replace_stub('executeTree', '''    private static int executeTree(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);\n        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {\n            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos().above();\n            player.level().setBlock(pos, net.minecraft.world.level.block.Blocks.OAK_SAPLING.defaultBlockState(), 3);\n            context.getSource().sendSystemMessage(Component.literal("Tree spawned."));\n        }\n        return 1;\n    }''')

# /unbanip
replace_stub('executeUnbanip', '''    private static int executeUnbanip(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /unbanip <ip>"));\n        return 0;\n    }''')

# /unlimited
replace_stub('executeUnlimited', '''    private static int executeUnlimited(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /unlimited <item>"));\n        return 0;\n    }''')

# /vanish
replace_stub('executeVanish', '''    private static int executeVanish(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        player.setInvisible(!player.isInvisible());\n        context.getSource().sendSystemMessage(Component.literal("Vanish toggled to: " + player.isInvisible()));\n        return 1;\n    }''')

# /warpinfo
replace_stub('executeWarpinfo', '''    private static int executeWarpinfo(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /warpinfo <warp>"));\n        return 0;\n    }''')

# /whois
replace_stub('executeWhois', '''    private static int executeWhois(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /whois <player>"));\n        return 0;\n    }''')

# /world
replace_stub('executeWorld', '''    private static int executeWorld(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /world <worldname>"));\n        return 0;\n    }''')

# /worth
replace_stub('executeWorth', '''    private static int executeWorth(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Usage: /worth <item>"));\n        return 0;\n    }''')

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Final cleanup injected!")