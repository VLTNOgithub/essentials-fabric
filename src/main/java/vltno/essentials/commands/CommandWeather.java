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

public class CommandWeather {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"weather", "eweather"}) {
            dispatcher.register(Commands.literal(alias)
                .requires(vltno.essentials.EssentialsCommands.require("essentials.weather", 2))
                .then(Commands.literal("clear").executes(context -> executeWeather(context, 0)))
                .then(Commands.literal("rain").executes(context -> executeWeather(context, 1)))
                .then(Commands.literal("thunder").executes(context -> executeWeather(context, 2))));
        }

        for (String alias : new String[]{"sky", "esky", "sun", "esun"}) {
            dispatcher.register(Commands.literal(alias)
                .requires(vltno.essentials.EssentialsCommands.require("essentials.weather.clear", 2))
                .executes(context -> executeWeather(context, 0)));
        }

        for (String alias : new String[]{"rain", "erain"}) {
            dispatcher.register(Commands.literal(alias)
                .requires(vltno.essentials.EssentialsCommands.require("essentials.weather.rain", 2))
                .executes(context -> executeWeather(context, 1)));
        }

        for (String alias : new String[]{"storm", "estorm"}) {
            dispatcher.register(Commands.literal(alias)
                .requires(vltno.essentials.EssentialsCommands.require("essentials.weather.storm", 2))
                .executes(context -> executeWeather(context, 2)));
        }

    }

    public static int executeWeather(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /weather <clear|rain|thunder>")); return 0; }

    public static int executeWeather(CommandContext<CommandSourceStack> context, int type) {
            net.minecraft.server.level.ServerLevel level = context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
            if (type == 0) level.setWeatherParameters(6000, 0, false, false);
            else if (type == 1) level.setWeatherParameters(0, 6000, true, false);
            else if (type == 2) level.setWeatherParameters(0, 6000, true, true);
            context.getSource().sendSystemMessage(Component.literal("Weather updated."));
            return 1;
        }

}
