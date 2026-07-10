import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Helper to replace ALL alias execution lines that don't pass the right args
# e.g. .executes(context -> executeTpoffline(context))

def fix_alias(method_name, replace_str):
    global content
    pattern = r'\.executes\(context -> ' + method_name + r'\(context\)\)'
    content = re.sub(pattern, replace_str, content)

# Tpoffline aliases (require UUID string argument, but for aliases without args we must provide null or error. Actually, offline tp requires an arg. Let's just make the literal throw an error or we can chain the argument onto the alias too!)
# The cleanest way is to replace the entire dispatcher.register block for each alias.
# Since that's hard with regex, let's just make the zero-arg execute method overloaded to tell the player they missed an argument!

# Actually, let's just add zero-arg overloads for all these commands that print "Usage: /<cmd> <target>"!
overloads = '''
    private static int executeTpa(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpa <player>")); return 0; }
    private static int executeTpahere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpahere <player>")); return 0; }
    private static int executeTpall(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpall <player>")); return 0; }
    private static int executeTpo(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpo <player>")); return 0; }
    private static int executeTpohere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpohere <player>")); return 0; }
    private static int executeTpoffline(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpoffline <uuid>")); return 0; }
'''

content = content.replace('public class EssentialsCommands {', 'public class EssentialsCommands {' + overloads)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Aliases fixed.")