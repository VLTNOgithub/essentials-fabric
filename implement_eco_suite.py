import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

def replace_reg(cmd_name, method_name, new_reg):
    global content
    import re
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)

def replace_stub(cmd_name, new_body):
    global content
    # Precisely target the string we know is inside the stub
    stub_string = f'context.getSource().sendSystemMessage(Component.literal("Command {cmd_name} is not fully implemented yet!"));\n        return 1;'
    if stub_string in content:
        content = content.replace(stub_string, new_body)
    else:
        print(f"Warning: Could not find stub for {cmd_name}")

# --- 1. /eco ---
reg_eco = '''dispatcher.register(Commands.literal("eco")
        .then(Commands.argument("action", com.mojang.brigadier.arguments.StringArgumentType.word())
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("amount", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0))
                    .executes(context -> executeEco(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "action"), net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "amount")))
                )
            )
        )
    );'''
meth_eco = '''context.getSource().sendSystemMessage(Component.literal("Usage: /eco <give|take|set|reset> <player> <amount>"));\n        return 0;\n    }\n    private static int executeEco(CommandContext<CommandSourceStack> context, String action, ServerPlayer target, double amount) {\n        UserData data = UserCache.getUser(target);\n        switch (action.toLowerCase()) {\n            case "give": data.money += amount; break;\n            case "take": data.money -= amount; break;\n            case "set": data.money = amount; break;\n            case "reset": data.money = 0.0; break;\n            default:\n                context.getSource().sendSystemMessage(Component.literal("Invalid action. Use give, take, set, or reset."));\n                return 0;\n        }\n        UserCache.saveUser(target.getUUID());\n        context.getSource().sendSystemMessage(Component.literal("Economy for " + target.getName().getString() + " updated. New balance: $" + String.format("%.2f", data.money)));\n        return 1;'''
replace_reg('eco', 'executeEco', reg_eco)
replace_stub('eco', meth_eco)

# --- 2. /balance ---
reg_bal = '''dispatcher.register(Commands.literal("balance")
        .executes(context -> executeBalance(context, context.getSource().getPlayerOrException()))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeBalance(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
meth_bal = '''return executeBalance(context, context.getSource().getPlayerOrException());\n    }\n    private static int executeBalance(CommandContext<CommandSourceStack> context, ServerPlayer target) {\n        UserData data = UserCache.getUser(target);\n        context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + "'s balance: $" + String.format("%.2f", data.money)));\n        return 1;'''
replace_reg('balance', 'executeBalance', reg_bal)
replace_stub('balance', meth_bal)

# --- 3. /balancetop ---
reg_baltop = '''dispatcher.register(Commands.literal("balancetop")
        .executes(context -> executeBalancetop(context, 1))
        .then(Commands.argument("page", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
            .executes(context -> executeBalancetop(context, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "page")))
        )
    );'''
meth_baltop = '''return executeBalancetop(context, 1);\n    }\n    private static int executeBalancetop(CommandContext<CommandSourceStack> context, int page) {\n        java.util.List<ServerPlayer> players = new java.util.ArrayList<>(context.getSource().getServer().getPlayerList().getPlayers());\n        players.sort((a, b) -> Double.compare(UserCache.getUser(b).money, UserCache.getUser(a).money));\n        context.getSource().sendSystemMessage(Component.literal("--- Balance Top ---"));\n        int start = (page - 1) * 10;\n        for (int i = start; i < Math.min(start + 10, players.size()); i++) {\n            ServerPlayer p = players.get(i);\n            context.getSource().sendSystemMessage(Component.literal((i + 1) + ". " + p.getName().getString() + " - $" + String.format("%.2f", UserCache.getUser(p).money)));\n        }\n        return 1;'''
replace_reg('balancetop', 'executeBalancetop', reg_baltop)
replace_stub('balancetop', meth_baltop)

# --- 4. /pay ---
reg_pay = '''dispatcher.register(Commands.literal("pay")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("amount", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0.01))
                .executes(context -> executePay(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "amount")))
            )
        )
    );'''
meth_pay = '''context.getSource().sendSystemMessage(Component.literal("Usage: /pay <player> <amount>")); return 0;\n    }\n    private static int executePay(CommandContext<CommandSourceStack> context, ServerPlayer target, double amount) throws CommandSyntaxException {\n        ServerPlayer sender = context.getSource().getPlayerOrException();\n        if (sender == target) {\n            context.getSource().sendSystemMessage(Component.literal("You cannot pay yourself!"));\n            return 0;\n        }\n        UserData targetData = UserCache.getUser(target);\n        if (!targetData.payToggle) {\n            context.getSource().sendSystemMessage(Component.literal("That player has payments disabled."));\n            return 0;\n        }\n        UserData senderData = UserCache.getUser(sender);\n        if (senderData.money < amount) {\n            context.getSource().sendSystemMessage(Component.literal("You do not have enough money."));\n            return 0;\n        }\n        senderData.money -= amount;\n        targetData.money += amount;\n        UserCache.saveUser(sender.getUUID());\n        UserCache.saveUser(target.getUUID());\n        context.getSource().sendSystemMessage(Component.literal("You paid $" + String.format("%.2f", amount) + " to " + target.getName().getString() + "."));\n        target.sendSystemMessage(Component.literal("You received $" + String.format("%.2f", amount) + " from " + sender.getName().getString() + "."));\n        return 1;'''
replace_reg('pay', 'executePay', reg_pay)
replace_stub('pay', meth_pay)

# --- 5. /paytoggle ---
meth_paytoggle = '''ServerPlayer player = context.getSource().getPlayerOrException();\n        UserData data = UserCache.getUser(player);\n        data.payToggle = !data.payToggle;\n        UserCache.saveUser(player.getUUID());\n        context.getSource().sendSystemMessage(Component.literal("Accepting payments set to: " + data.payToggle));\n        return 1;'''
replace_stub('paytoggle', meth_paytoggle)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Economy suite safely injected!")
