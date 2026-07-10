import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Helper to replace method stubs easily
def inject_method(method_name, impl_body):
    global content
    pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\)[\s\S]*?return 1;\n    \}'
    # If the method was already overloaded or changed, we find the generic one.
    content = re.sub(pattern, impl_body, content)

# --- MOVEMENT ---
# /top
top_impl = '''    private static int executeTop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int topY = player.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
        player.teleportTo(player.level(), player.getX(), topY, player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to top."));
        return 1;
    }'''
inject_method('executeTop', top_impl)

# /bottom
bottom_impl = '''    private static int executeBottom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int bottomY = player.level().getMinY();
        player.teleportTo(player.level(), player.getX(), bottomY, player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to bottom."));
        return 1;
    }'''
inject_method('executeBottom', bottom_impl)

# /jump
jump_impl = '''    private static int executeJump(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
    }'''
inject_method('executeJump', jump_impl)


# --- INVENTORY & ITEMS ---
# /disposal
disposal_impl = '''    private static int executeDisposal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, inv, new net.minecraft.world.SimpleContainer(27)), Component.literal("Disposal")));
        return 1;
    }'''
inject_method('executeDisposal', disposal_impl)

# /ext (Extinguish)
ext_impl = '''    private static int executeExt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.clearFire();
        context.getSource().sendSystemMessage(Component.literal("You have been extinguished."));
        return 1;
    }'''
inject_method('executeExt', ext_impl)

# /more
more_impl = '''    private static int executeMore(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
            return 0;
        }
        hand.setCount(hand.getMaxStackSize());
        context.getSource().sendSystemMessage(Component.literal("Filled item stack to maximum."));
        return 1;
    }'''
inject_method('executeMore', more_impl)

# /repair
repair_impl = '''    private static int executeRepair(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty() || !hand.isDamageableItem()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding a repairable item."));
            return 0;
        }
        hand.setDamageValue(0);
        context.getSource().sendSystemMessage(Component.literal("Item repaired successfully."));
        return 1;
    }'''
inject_method('executeRepair', repair_impl)


# --- WORLD & HAZARDS ---
# /lightning
lightning_impl = '''    private static int executeLightning(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
            net.minecraft.world.entity.LightningBolt bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (bolt != null) {
                bolt.moveTo(net.minecraft.world.phys.Vec3.atBottomCenterOf(pos));
                player.level().addFreshEntity(bolt);
                context.getSource().sendSystemMessage(Component.literal("Smite!"));
            }
        }
        return 1;
    }'''
inject_method('executeLightning', lightning_impl)

# /nuke
nuke_impl = '''    private static int executeNuke(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        context.getSource().sendSystemMessage(Component.literal("May death rain upon them."));
        for (ServerPlayer target : context.getSource().getServer().getPlayerList().getPlayers()) {
            net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(target.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (tnt != null) {
                tnt.moveTo(target.getX(), target.getY() + 10, target.getZ());
                tnt.setFuse(40);
                target.level().addFreshEntity(tnt);
            }
        }
        return 1;
    }'''
inject_method('executeNuke', nuke_impl)

# /kill
kill_impl = '''    private static int executeKill(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().sendSystemMessage(Component.literal("Use /kill <player> (Missing arguments not fully mapped yet)"));
        return 0;
    }'''
inject_method('executeKill', kill_impl)

# /gc
gc_impl = '''    private static int executeGc(CommandContext<CommandSourceStack> context) {
        long maxMem = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long totalMem = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long freeMem = Runtime.getRuntime().freeMemory() / 1024 / 1024;
        long usedMem = totalMem - freeMem;
        context.getSource().sendSystemMessage(Component.literal("Max Memory: " + maxMem + " MB"));
        context.getSource().sendSystemMessage(Component.literal("Allocated Memory: " + totalMem + " MB"));
        context.getSource().sendSystemMessage(Component.literal("Free Memory: " + freeMem + " MB"));
        context.getSource().sendSystemMessage(Component.literal("Used Memory: " + usedMem + " MB"));
        return 1;
    }'''
inject_method('executeGc', gc_impl)

# /afk
afk_impl = '''
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
    }'''
inject_method('executeAfk', afk_impl)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Massive batch injected!")