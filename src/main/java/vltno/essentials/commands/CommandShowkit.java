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

public class CommandShowkit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> showkitCmd = Commands.literal("showkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeShowkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    ;
        dispatcher.register(showkitCmd);
        dispatcher.register(Commands.literal("kitpreview").redirect(showkitCmd.build()));
        dispatcher.register(Commands.literal("preview").redirect(showkitCmd.build()));
        dispatcher.register(Commands.literal("kitshow").redirect(showkitCmd.build()));


    }

    public static int executeShowkit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /showkit <name>")); return 0; }

    public static int executeShowkit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            KitData kit = KITS.get(name.toLowerCase());
            if (kit == null) {
                context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
                return 0;
            }
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.SimpleContainer inv = new net.minecraft.world.SimpleContainer(54);
            com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);
            for (int i = 0; i < Math.min(54, kit.items.size()); i++) {
                try {
                    net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseCompoundFully(kit.items.get(i));
                    net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();
                    inv.setItem(i, item);
                } catch (Exception e) {}
            }
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
                return net.minecraft.world.inventory.ChestMenu.sixRows(id, inventory, inv);
            }, Component.literal("Kit Preview: " + name)));
            return 1;
        }

}
