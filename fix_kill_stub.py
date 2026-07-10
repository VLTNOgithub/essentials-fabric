import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

kill_stub = r'    private static int executeKill\(CommandContext<CommandSourceStack> context\) throws CommandSyntaxException \{[\s\S]*?return 0;\n    \}'
kill_impl = '''    private static int executeKill(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /kill <player>")); return 0; }
    private static int executeKill(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.kill((net.minecraft.server.level.ServerLevel) target.level());
        }
        context.getSource().sendSystemMessage(Component.literal("Killed " + targets.size() + " entities."));
        return targets.size();
    }'''

content = re.sub(kill_stub, kill_impl, content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Kill fixed.")