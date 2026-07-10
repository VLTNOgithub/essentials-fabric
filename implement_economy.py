import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Helper to replace registration
def replace_reg(cmd_name, method_name, new_reg):
    global content
    import re
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)

# --- 1. /eco ---
old_eco = '''    private static int executeEco(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Command eco is not fully implemented yet!"));\n        return 1;\n    }'''
reg_eco = '''dispatcher.register(Commands.literal("eco")
        .then(Commands.argument("action", com.mojang.brigadier.arguments.StringArgumentType.word())
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("amount", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0))
                    .executes(context -> executeEco(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "action"), net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "amount")))
                )
            )
        )
    );'''
new_eco = '''    private static int executeEco(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /eco <give|take|set|reset> <player> <amount>")); return 0; }
    private static int executeEco(CommandContext<CommandSourceStack> context, String action, ServerPlayer target, double amount) {
        UserData data = UserCache.getUser(target);
        switch (action.toLowerCase()) {
            case "give": data.money += amount; break;
            case "take": data.money -= amount; break;
            case "set": data.money = amount; break;
            case "reset": data.money = 0.0; break;
            default:
                context.getSource().sendSystemMessage(Component.literal("Invalid action. Use give, take, set, or reset."));
                return 0;
        }
        UserCache.saveUser(target.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Economy for " + target.getName().getString() + " updated. New balance: $" + String.format("%.2f", data.money)));
        return 1;
    }'''
replace_reg('eco', 'executeEco', reg_eco)
content = content.replace(old_eco, new_eco)

# --- 2. /balance ---
old_bal = '''    private static int executeBalance(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Command balance is not fully implemented yet!"));\n        return 1;\n    }'''
reg_bal = '''dispatcher.register(Commands.literal("balance")
        .executes(context -> executeBalance(context, context.getSource().getPlayerOrException()))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeBalance(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
new_bal = '''    private static int executeBalance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeBalance(context, context.getSource().getPlayerOrException()); }
    private static int executeBalance(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        UserData data = UserCache.getUser(target);
        context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + "'s balance: $" + String.format("%.2f", data.money)));
        return 1;
    }'''
replace_reg('balance', 'executeBalance', reg_bal)
content = content.replace(old_bal, new_bal)

# --- 3. /balancetop ---
old_baltop = '''    private static int executeBalancetop(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Command balancetop is not fully implemented yet!"));\n        return 1;\n    }'''
reg_baltop = '''dispatcher.register(Commands.literal("balancetop")
        .executes(context -> executeBalancetop(context, 1))
        .then(Commands.argument("page", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
            .executes(context -> executeBalancetop(context, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "page")))
        )
    );'''
new_baltop = '''    private static int executeBalancetop(CommandContext<CommandSourceStack> context) { return executeBalancetop(context, 1); }
    private static int executeBalancetop(CommandContext<CommandSourceStack> context, int page) {
        // Realistically requires looping over all saved UUID json files, we will iterate current online players as a lightweight surrogate for now
        java.util.List<ServerPlayer> players = new java.util.ArrayList<>(context.getSource().getServer().getPlayerList().getPlayers());
        players.sort((a, b) -> Double.compare(UserCache.getUser(b).money, UserCache.getUser(a).money));
        context.getSource().sendSystemMessage(Component.literal("--- Balance Top ---"));
        int start = (page - 1) * 10;
        for (int i = start; i < Math.min(start + 10, players.size()); i++) {
            ServerPlayer p = players.get(i);
            context.getSource().sendSystemMessage(Component.literal((i + 1) + ". " + p.getName().getString() + " - $" + String.format("%.2f", UserCache.getUser(p).money)));
        }
        return 1;
    }'''
replace_reg('balancetop', 'executeBalancetop', reg_baltop)
content = content.replace(old_baltop, new_baltop)

# --- 4. /pay ---
old_pay = '''    private static int executePay(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Command pay is not fully implemented yet!"));\n        return 1;\n    }'''
reg_pay = '''dispatcher.register(Commands.literal("pay")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("amount", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0.01))
                .executes(context -> executePay(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "amount")))
            )
        )
    );'''
new_pay = '''    private static int executePay(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /pay <player> <amount>")); return 0; }
    private static int executePay(CommandContext<CommandSourceStack> context, ServerPlayer target, double amount) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        if (sender == target) {
            context.getSource().sendSystemMessage(Component.literal("You cannot pay yourself!"));
            return 0;
        }
        UserData targetData = UserCache.getUser(target);
        if (!targetData.payToggle) {
            context.getSource().sendSystemMessage(Component.literal("That player has payments disabled."));
            return 0;
        }
        UserData senderData = UserCache.getUser(sender);
        if (senderData.money < amount) {
            context.getSource().sendSystemMessage(Component.literal("You do not have enough money."));
            return 0;
        }
        // We skip confirm toggle implementation to avoid state machine complexity for now
        senderData.money -= amount;
        targetData.money += amount;
        UserCache.saveUser(sender.getUUID());
        UserCache.saveUser(target.getUUID());
        context.getSource().sendSystemMessage(Component.literal("You paid $" + String.format("%.2f", amount) + " to " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal("You received $" + String.format("%.2f", amount) + " from " + sender.getName().getString() + "."));
        return 1;
    }'''
replace_reg('pay', 'executePay', reg_pay)
content = content.replace(old_pay, new_pay)

# --- 5. /paytoggle ---
old_paytoggle = '''    private static int executePaytoggle(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Command paytoggle is not fully implemented yet!"));\n        return 1;\n    }'''
reg_paytoggle = '''dispatcher.register(Commands.literal("paytoggle")
        .executes(context -> executePaytoggle(context))
    );'''
new_paytoggle = '''    private static int executePaytoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.payToggle = !data.payToggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Accepting payments set to: " + data.payToggle));
        return 1;
    }'''
replace_reg('paytoggle', 'executePaytoggle', reg_paytoggle)
content = content.replace(old_paytoggle, new_paytoggle)

# --- 6. /payconfirmtoggle ---
old_payconfirmtoggle = '''    private static int executePayconfirmtoggle(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Command payconfirmtoggle is not fully implemented yet!"));\n        return 1;\n    }'''
reg_payconfirmtoggle = '''dispatcher.register(Commands.literal("payconfirmtoggle")
        .executes(context -> executePayconfirmtoggle(context))
    );'''
new_payconfirmtoggle = '''    private static int executePayconfirmtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.payConfirmToggle = !data.payConfirmToggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Payment confirmation toggle set to: " + data.payConfirmToggle));
        return 1;
    }'''
replace_reg('payconfirmtoggle', 'executePayconfirmtoggle', reg_payconfirmtoggle)
content = content.replace(old_payconfirmtoggle, new_payconfirmtoggle)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Economy injected safely.")