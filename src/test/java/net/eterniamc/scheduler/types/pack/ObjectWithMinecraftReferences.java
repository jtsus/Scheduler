package net.eterniamc.scheduler.types.pack;

import net.eterniamc.scheduler.annotation.Delayed;
import net.minecraft.entity.player.EntityPlayerMP;

public class ObjectWithMinecraftReferences {

    @Delayed(milliseconds = 1, async = true)
    public void doSomethingWithPlayer(EntityPlayerMP player) {
        System.out.println(player.getName());
    }
}
