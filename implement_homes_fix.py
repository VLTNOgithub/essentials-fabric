import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('net.minecraft.resources.ResourceLocation dimLoc = net.minecraft.resources.ResourceLocation.parse(home.dimension);',
                        'net.minecraft.resources.Identifier dimLoc = net.minecraft.resources.Identifier.parse(home.dimension);')
content = content.replace('player.level().dimension().location().toString();', 'player.level().dimension().identifier().toString();')

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Homes fixed.")