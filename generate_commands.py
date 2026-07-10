import yaml
import sys
import re

def generate_java_commands():
    try:
        with open('Essentials/Essentials/src/main/resources/plugin.yml', 'r') as f:
            data = yaml.safe_load(f)
    except Exception as e:
        print(f"Error reading yaml: {e}")
        return

    commands = data.get('commands', {})
    
    java_code = """package vltno.essentials;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class EssentialsCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
"""

    for cmd_name, info in commands.items():
        aliases = info.get('aliases', [])
        if isinstance(aliases, str):
            aliases = [aliases]
            
        all_names = [cmd_name] + aliases
        
        for name in all_names:
            # Clean up command names (e.g., customtext uses /<alias> which is an edge case)
            if name.startswith('<'):
                continue
                
            java_code += f"""
        dispatcher.register(Commands.literal("{name}")
            .executes(context -> execute{cmd_name.capitalize().replace(':', '')}(context))
        );"""

    java_code += """
    }
"""

    for cmd_name in commands.keys():
        safe_name = cmd_name.capitalize().replace(':', '')
        java_code += f"""
    private static int execute{safe_name}(CommandContext<CommandSourceStack> context) {{
        context.getSource().sendSystemMessage(Component.literal("Command {cmd_name} is not fully implemented yet!"));
        return 1;
    }}
"""

    java_code += "}\n"

    with open('src/main/java/vltno/essentials/EssentialsCommands.java', 'w') as f:
        f.write(java_code)
        
    print("Successfully generated EssentialsCommands.java")

if __name__ == "__main__":
    generate_java_commands()