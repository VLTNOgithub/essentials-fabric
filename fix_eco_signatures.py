import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('private static int executeBalance(CommandContext<CommandSourceStack> context) {\n        return executeBalance(context', 'private static int executeBalance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        return executeBalance(context')
content = content.replace('private static int executePaytoggle(CommandContext<CommandSourceStack> context) {\n        ServerPlayer player', 'private static int executePaytoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {\n        ServerPlayer player')

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Eco signatures fixed!")