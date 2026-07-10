import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('TagParser.parseTag(', 'TagParser.parseCompoundFully(')

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("TagParser fixed!")