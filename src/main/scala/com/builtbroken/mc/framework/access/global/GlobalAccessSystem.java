package com.builtbroken.mc.framework.access.global;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.framework.access.AccessUtility;
import com.builtbroken.mc.lib.helper.NBTUtility;
import com.builtbroken.mc.framework.mod.loadable.AbstractLoadable;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Singleton for dealing with {@link GlobalAccessProfile} that are shared globally over
 * the entire game and not a single world or machine instance.
 * <p>
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on  3/11/2016.
 */
public final class GlobalAccessSystem extends AbstractLoadable
{
    /** Map of profile IDs to profile instances, instances can be null if reserved or not loaded */
    private static final HashMap<String, GlobalAccessProfile> id_to_profiles = new HashMap();

    /** Used for event reg */
    public static final GlobalAccessSystem instance = new GlobalAccessSystem();

    /**
     * Called to get or create a profile
     *
     * @param name          - unique id of the profile, try to prefix with the mod and machine
     * @param defaultGroups - if true will create default groups
     * @return existing or new access profile
     */
    public static GlobalAccessProfile getOrCreateProfile(String name, boolean defaultGroups)
    {
        GlobalAccessProfile p = getProfile(name);
        if (p == null)
        {
            p = createProfile(name, defaultGroups);
        }
        return p;
    }

    /**
     * Gets a profile by ID from the map or loads it from disk
     *
     * @param id - unique profile ID
     * @return profile if found or null
     */
    public static GlobalAccessProfile getProfile(String id)
    {
        if (id_to_profiles.containsKey(id) && id_to_profiles.get(id) != null)
        {
            return id_to_profiles.get(id);
        }
        return loadProfile(id, false);
    }

    /**
     * Creates a new profile, adds it to the global system and save system
     *
     * @param name          - name of the profile, used as part of the ID to improve uniqueness
     * @param defaultGroups - should default groups be generated
     * @return new profile
     */
    public static GlobalAccessProfile createProfile(String name, boolean defaultGroups)
    {
        GlobalAccessProfile profile = new GlobalAccessProfile();
        if (defaultGroups)
        {
            AccessUtility.loadNewGroupSet(profile);
        }
        profile.initName(name.trim(), "P_" + name + "_" + System.nanoTime());
        if (!id_to_profiles.containsKey(name) || id_to_profiles.get(name) == null)
        {
            id_to_profiles.put(profile.getID(), profile);
        }
        SaveManager.register(profile);
        return profile;
    }

    /**
     * Called to load a profile from disk
     *
     * @param id     - unique global ID of the profile
     * @param create - if file is missing will create new group
     * @return existing profile from save or new profile
     */
    protected static GlobalAccessProfile loadProfile(String id, boolean create)
    {
        Engine.logger().info("GlobalAccessSystem: Loading a profile[" + id + "] from disk");
        NBTTagCompound tag = NBTUtility.loadData(GlobalAccessProfile.getPathToProfile(id));
        if (!tag.hasNoTags())
        {
            GlobalAccessProfile profile = new GlobalAccessProfile();
            profile.load(tag);
            if (profile.getID() != null && profile.getName() != null)
            {
                id_to_profiles.put(profile.getID(), profile);
                if (id_to_profiles.containsKey(id) && id_to_profiles.get(id) != null)
                {
                    Engine.logger().error("GlobalAccessSystem: Loading a profile over an existing profile[" + id + ", " + id_to_profiles.get(id) + "] with " + profile);
                }
                SaveManager.register(profile);
            }
            else
            {
                Engine.logger().error("GlobalAccessSystem: Profile was invalid due to not containing id and name, skipping loading.");
            }
            return profile;
        }
        else if (create)
        {
            return createProfile(id, true);
        }
        return null;
    }

    /**
     * Finds all profiles that a player is contained in
     *
     * @param player - player
     * @return list of profiles, or empty list
     */
    public static List<GlobalAccessProfile> getProfilesFor(EntityPlayer player)
    {
        List<GlobalAccessProfile> profiles = new ArrayList();
        for (String name : id_to_profiles.keySet())
        {
            if (name != null)
            {
                GlobalAccessProfile profile = getProfile(name); //Will load from disk if not loaded
                if (profile != null)
                {
                    if (profile.containsUser(player))
                    {
                        profiles.add(profile);
                    }
                }
            }
        }
        return profiles;
    }

    /**
     * Gets all profiles, do not edit as this is contained in a map.
     *
     * @return collection of profiles, contains null entries
     */
    public static Collection<GlobalAccessProfile> getProfiles()
    {
        return id_to_profiles.values();
    }


    //==================================================================================
    //======SERVER AND EVENT STUFF ===================================================
    //==================================================================================
    private int ticks = 0;

    private GlobalAccessSystem()
    {
    }

    @Override
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.world.provider.dimensionId == 0)
        {
            ticks++;
            if (ticks % 6000 == 0) //every 5 mins, TODO setup with config
            {
                cleanup();
            }
        }
    }

    public void cleanup()
    {
        //todo clear broken, empty, and unowned profiles
        // ^ might happen by neglect from users
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (event.world.provider.dimensionId == 0 && !event.world.isRemote && MinecraftServer.getServer() != null)
        {
            File folder = new File(NBTUtility.getSaveDirectory(MinecraftServer.getServer().getFolderName()), GlobalAccessProfile.SAVE_FOLDER);
            if (folder.exists())
            {
                File[] files = folder.listFiles();
                for (File file : files)
                {
                    if (file.getName().endsWith(".dat"))
                    {
                        NBTTagCompound tag = NBTUtility.loadData(file);
                        if (!tag.hasNoTags())
                        {
                            GlobalAccessProfile profile = new GlobalAccessProfile();
                            profile.load(tag);
                            if (profile.getID() != null && profile.getName() != null)
                            {
                                id_to_profiles.put(profile.getID(), profile);
                                SaveManager.register(profile);
                                //TODO check if has users, if not move to trash
                            }
                            else
                            {
                                //TODO note error and move to broken/trash folder
                            }
                        }
                    }
                }
            }
        }
    }

    //==================================================================================
}
