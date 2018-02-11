package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.framework.access.global.GlobalAccessProfile;
import com.builtbroken.mc.framework.access.global.GlobalAccessSystem;
import com.builtbroken.mc.framework.access.global.SingleOwnerAccessProfile;
import com.builtbroken.mc.framework.mod.loadable.AbstractLoadable;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/10/2018.
 */
public class AccessModule extends AbstractLoadable
{
    @Override
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(this);

        //Register virtual objects with save manager
        SaveManager.registerClass("SingleOwnerAccessProfile", SingleOwnerAccessProfile.class);
        SaveManager.registerClass("GlobalAccessProfile", GlobalAccessProfile.class);
    }

    //@SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        //TODO unload unused profiles
        //TODO check profiles for bad data
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) //TODO maybe use server starting event
    {
        if (event.world.provider.dimensionId == 0 && !event.world.isRemote && MinecraftServer.getServer() != null)
        {
            GlobalAccessSystem.loadProfilesFromDisk();
        }
    }
}
