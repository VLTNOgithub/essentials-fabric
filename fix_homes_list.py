import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

old_home_impl = '''    private static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeHome(context, "home"); }'''

new_home_impl = '''    private static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
    }'''

content = content.replace(old_home_impl, new_home_impl)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Homes list fixed.")