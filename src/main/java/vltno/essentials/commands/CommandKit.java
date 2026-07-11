package vltno.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
import vltno.essentials.UserCache;
import vltno.essentials.UserData;
import vltno.essentials.EssentialsCommands;
import static vltno.essentials.EssentialsCommands.*;

public class CommandKit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> kitCmd = Commands.literal("kit")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.kit", 0))
        .executes(context -> executeKit(context, ""))
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeKit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    ;
        dispatcher.register(kitCmd);
        dispatcher.register(Commands.literal("ekit").executes(kitCmd.getCommand()).redirect(kitCmd.build()));
        dispatcher.register(Commands.literal("kits").executes(kitCmd.getCommand()).redirect(kitCmd.build()));
        dispatcher.register(Commands.literal("ekits").executes(kitCmd.getCommand()).redirect(kitCmd.build()));


    }

    public static int executeKit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeKit(context, ""); }

    public static int executeKit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            if (name.isEmpty()) {
                if (KITS.isEmpty()) {
                    context.getSource().sendSystemMessage(Component.literal("There are no kits available.").withStyle(net.minecraft.ChatFormatting.RED));
                } else {
                    context.getSource().sendSystemMessage(Component.literal("Available Kits: " + String.join(", ", KITS.keySet())));
                }
                return 1;
            }
            KitData kit = KITS.get(name.toLowerCase());
            if (kit == null) {
                context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
                return 0;
            }
            ServerPlayer player = context.getSource().getPlayerOrException();
            UserData data = UserCache.getUser(player);
            if (data.kitCooldowns.containsKey(name.toLowerCase())) {
                long nextTime = data.kitCooldowns.get(name.toLowerCase());
                if (System.currentTimeMillis() < nextTime) {
                    long diff = (nextTime - System.currentTimeMillis()) / 1000;
                    context.getSource().sendSystemMessage(Component.literal("You must wait " + diff + " seconds before using this kit again.").withStyle(net.minecraft.ChatFormatting.RED));
                    return 0;
                }
            }

            com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
            for (String itemStr : kit.items) {
                try {
                    net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseCompoundFully(itemStr);
                    net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();
                    if (!player.getInventory().add(item)) player.drop(item, false);
                } catch (Exception e) { e.printStackTrace(); }
            }
            if (kit.delay > 0) {
                data.kitCooldowns.put(name.toLowerCase(), System.currentTimeMillis() + (kit.delay * 1000L));
                UserCache.saveUser(player.getUUID());
            }
            context.getSource().sendSystemMessage(Component.literal("You received the kit '" + name + "'."));
            return 1;
        }

}
