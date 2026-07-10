with open('src/main/java/vltno/essentials/EssentialsCommands.java', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('bolt.moveTo', 'bolt.setPos')
content = content.replace('tnt.moveTo', 'tnt.setPos')

with open('src/main/java/vltno/essentials/EssentialsCommands.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("moveTo fixed to setPos")