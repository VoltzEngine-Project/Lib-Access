package com.builtbroken.mc.framework.access.global;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.AccessUtility;
import com.builtbroken.mc.framework.access.perm.Permissions;
import com.builtbroken.mc.framework.mod.loadable.AbstractLoadable;
import com.builtbroken.mc.lib.helper.NBTUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.*;

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

    /**
     * String ID to use to note you want to use the user's friend list.
     * Still requires providing the actual ID of the user's UUID
     */
    public static final String FRIENDS_LIST_ID = "friends_list";

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
        if (id != null && !id.trim().isEmpty())
        {
            if (id_to_profiles.containsKey(id) && id_to_profiles.get(id) != null)
            {
                return id_to_profiles.get(id);
            }
            return loadProfile(id, false);
        }
        return null;
    }

    public static GlobalAccessProfile getFriendList(EntityPlayer player)
    {
        return getFriendList(player.getGameProfile().getName(), player.getGameProfile().getId());
    }

    public static GlobalAccessProfile getFriendList(final String username, final UUID uuid)
    {
        //Get id from UUID
        final String id = uuid.toString().replace("-", "");

        //Try to locate profile
        GlobalAccessProfile profile = getProfile(id);
        if (profile == null)
        {
            //Init profile
            profile = new GlobalAccessProfileSimple(username, uuid);
            profile.initName("Friends", id);

            //Add default group
            final AccessGroup friendsGroup = new AccessGroup("friends");
            friendsGroup.setDisplayName("Friends");
            friendsGroup.setDescription("People you trust and want to have access to your stuff");
            friendsGroup.addNode(Permissions.PROFILE_FOF);
            friendsGroup.addNode(Permissions.targetFriend);
            friendsGroup.addNode(Permissions.inventory);
            friendsGroup.addNode(Permissions.machine);
            profile.addGroup(friendsGroup.disableEdit());

            //Register
            registerProfile(id, profile.disableEdit());
        }
        return profile;
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
        registerProfile(profile.getID(), profile);
        return profile;
    }

    /**
     * Registers the profile with the system
     *
     * @param id      - id to access the profile
     * @param profile - profile instance
     */
    public static void registerProfile(String id, GlobalAccessProfile profile)
    {
        if (id_to_profiles.containsKey(id) && id_to_profiles.get(id) != null)
        {
            Engine.logger().error("GlobalAccessSystem: Loading a profile over an existing profile[" + id + ", " + id_to_profiles.get(id) + "] with " + profile);
        }
        id_to_profiles.put(profile.getID(), profile);
        SaveManager.register(profile);
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
        String path = GlobalAccessProfile.getPathToProfile(id);
        File file = NBTUtility.getSaveFile(path);
        if (file.exists())
        {
            NBTTagCompound tag = NBTUtility.loadData(file);
            if (!tag.hasNoTags())
            {
                //Load profile
                GlobalAccessProfile profile = createFromSave(id, tag);

                //Only use profile if we have an ID and name
                if (profile.getID() != null && profile.getName() != null)
                {
                    registerProfile(id, profile);
                }
                else
                {
                    Engine.logger().error("GlobalAccessSystem: Profile was invalid due to not containing id and name, skipping loading.");
                }
                return profile;
            }
        }
        return create ? createProfile(id, true) : null;
    }

    public static GlobalAccessProfile createFromSave(String id, NBTTagCompound tag)
    {
        //Load profile
        GlobalAccessProfile profile;

        //Try save manager to allow custom profile objects
        Object profileObject = SaveManager.createAndLoad(tag);
        if (profileObject instanceof GlobalAccessProfile)
        {
            profile = (GlobalAccessProfile) profileObject;
        }
        //Fail safe to default to prevent data loss
        else
        {
            Engine.logger().error("GlobalAccessSystem#loadProfile(" + id + ") failed to create profile from save. Using default object to prevent full data loss.", new RuntimeException("Error loading access profile"));
            profile = new GlobalAccessProfile();
            profile.load(tag);
        }

        return profile;
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

    /** Cleanup data linked to the current save instance */
    public static void cleanup()
    {
        id_to_profiles.clear();
    }

    /**
     * Called to load all profiles from disk.
     * Should only be called on server start
     */
    public static void loadProfilesFromDisk()
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
                        GlobalAccessProfile profile = createFromSave(file.getName(), tag);
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
