import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('context.getSource().getServer().saveAll(true, true, true);',
                          'context.getSource().getServer().saveAll(true, true, false);')
content = content.replace('net.minecraft.world.entity.projectile.LargeFireball fireball = new net.minecraft.world.entity.projectile.LargeFireball(player.level(), player, player.getLookAngle().normalize());',
                          '''net.minecraft.world.entity.Entity fireball = net.minecraft.world.entity.EntityType.FIREBALL.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (fireball != null) {
            if (fireball instanceof net.minecraft.world.entity.projectile.Projectile proj) {
                proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            }
        }''')
content = content.replace('net.minecraft.world.entity.animal.Cat cat', 'net.minecraft.world.entity.Entity cat')

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Mappings fixed.")
