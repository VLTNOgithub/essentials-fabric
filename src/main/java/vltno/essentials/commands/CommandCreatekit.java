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

public class CommandCreatekit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> createkitCmd = Commands.literal("createkit")
            .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
                .then(Commands.argument("delay", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                    .executes(context -> executeCreatekit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "delay")))
                )
            );
        dispatcher.register(createkitCmd);
        dispatcher.register(Commands.literal("kitcreate").redirect(createkitCmd.build()));
        dispatcher.register(Commands.literal("createk").redirect(createkitCmd.build()));
        dispatcher.register(Commands.literal("kc").redirect(createkitCmd.build()));
        dispatcher.register(Commands.literal("ck").redirect(createkitCmd.build()));

    }

    public static int executeCreatekit(CommandContext<CommandSourceStack> context, String name, int delay) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            KitData kit = new KitData();
            kit.delay = delay;
            com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                net.minecraft.world.item.ItemStack item = player.getInventory().getItem(i);
                if (!item.isEmpty()) {
                    try {
                        net.minecraft.nbt.Tag tag = net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, item).getOrThrow();
                        kit.items.add(tag.toString());
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
            KITS.put(name.toLowerCase(), kit);
            saveKits(); saveJailsWarps();
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' created with " + kit.items.size() + " items."));
            return 1;
        }

}
