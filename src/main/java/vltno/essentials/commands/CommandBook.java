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

public class CommandBook {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("book")
            .executes(context -> executeBook(context))
        );
        dispatcher.register(Commands.literal("ebook")
            .executes(context -> executeBook(context))
        );

    }

    public static int executeBook(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.is(net.minecraft.world.item.Items.WRITTEN_BOOK)) {
            // Unseal the book by transferring its pages to a WRITABLE_BOOK
            net.minecraft.world.item.ItemStack newBook = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WRITABLE_BOOK);
            net.minecraft.world.item.component.WrittenBookContent written = hand.get(net.minecraft.core.component.DataComponents.WRITTEN_BOOK_CONTENT);
            if (written != null) {
                java.util.List<net.minecraft.server.network.Filterable<String>> rawPages = new java.util.ArrayList<>();
                for (net.minecraft.network.chat.Component comp : written.getPages(false)) {
                    rawPages.add(net.minecraft.server.network.Filterable.passThrough(comp.getString()));
                }
                newBook.set(net.minecraft.core.component.DataComponents.WRITABLE_BOOK_CONTENT, new net.minecraft.world.item.component.WritableBookContent(rawPages));
            }
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, newBook);
            context.getSource().sendSystemMessage(Component.literal("Book unsealed."));
            return 1;
        } else {
            context.getSource().sendSystemMessage(Component.literal("You must hold a written (sealed) book."));
            return 0;
        }
    }

}
