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
        dispatcher.register(Commands.literal("createkit")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("kitcreate")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("createk")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("kc")
            .executes(context -> executeCreatekit(context))
        );
        dispatcher.register(Commands.literal("ck")
            .executes(context -> executeCreatekit(context))
        );

    }

    public static int executeCreatekit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /createkit <name> <delay>")); return 0; }

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
