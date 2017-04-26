package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.NBTUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Reference class for storing and access {@link AccessProfile} that are shared
 * across several objects.
 * Created by Dark(DarkGuardsman, Robert) on 3/11/2016.
 */
public final class GlobalAccessSystem
{
    private static final HashMap<String, AccessProfile> id_to_profiles = new HashMap();
    //TODO implement an automated unload system


    /**
     * Called to get or create a profile
     *
     * @param name          - unique id of the profile, try to prefix with the mod and machine
     * @param defaultGroups - if true will create default groups
     * @return existing or new access profile
     */
    public static AccessProfile getOrCreateProfile(String name, boolean defaultGroups)
    {
        if (id_to_profiles.containsKey(name) && id_to_profiles.get(name) != null)
        {
            return id_to_profiles.get(name);
        }
        AccessProfile p = loadProfile(name, false);
        if (p == null)
        {
            p = createProfile(name, defaultGroups);
        }
        if (!id_to_profiles.containsKey(name) || id_to_profiles.get(name) == null)
        {
            id_to_profiles.put(name, p);
        }
        return p;
    }

    public static AccessProfile getProfile(String name)
    {
        if (id_to_profiles.containsKey(name) && id_to_profiles.get(name) != null)
        {
            return id_to_profiles.get(name);
        }
        return loadProfile(name, false);
    }

    public static AccessProfile createProfile(String name, boolean defaultGroups)
    {
        AccessProfile profile = new AccessProfile();
        if (defaultGroups)
        {
            AccessUtility.loadNewGroupSet(profile);
        }
        profile.initName(name.trim(), "P_" + name + "_" + System.currentTimeMillis());
        if (!id_to_profiles.containsKey(name) || id_to_profiles.get(name) == null)
        {
            id_to_profiles.put(name, profile);
        }
        return profile;
    }

    public static void cleanup()
    {
        //todo clear broken, empty, and unowned profiles
        // ^ might happen by neglect from users
    }

    /**
     * Called to load a profile from disk
     *
     * @param name   - name of the profile
     * @param create - if file is missing will create new group
     * @return existing profile from save or new profile
     */
    protected static AccessProfile loadProfile(String name, boolean create)
    {
        NBTTagCompound tag = NBTUtility.loadData("bbm/accessProfiles/" + name + ".dat");
        if (!tag.hasNoTags())
        {
            return new AccessProfile(tag, true);
        }
        else if (create)
        {
            return createProfile(name, true);
        }
        return null;
    }

    public static List<AccessProfile> getProfilesFor(EntityPlayer player)
    {
        List<AccessProfile> profiles = new ArrayList();
        for (String name : id_to_profiles.keySet())
        {
            if (name != null)
            {
                AccessProfile profile = getProfile(name); //Will load from disk if not loaded
                if (profile != null)
                {
                    if (profile.containsUser(player) && !profile.getUserAccess(player).hasExactNode(Permissions.targetHostile.toString()))
                    {
                        profiles.add(profile);
                    }
                }
            }
        }
        if (Engine.runningAsDev)
        {
            if (profiles.isEmpty())
            {
                AccessProfile profile = new AccessProfile(true).generateNew("Profile1", player);
                id_to_profiles.put(profile.getID(), profile);
                profile.getOwnerGroup().addMember(player);
                profiles.add(profile);

                profile = new AccessProfile(true).generateNew("Profile2", player);
                id_to_profiles.put(profile.getID(), profile);
                profile.getOwnerGroup().addMember(player);
                profiles.add(profile);

                for (int i = 0; i < 30; i++)
                {
                    profile.getGroup(Permissions.GROUP_USER.id).addMember(new AccessUser("player_" + i));
                }

                profile = new AccessProfile(true).generateNew("Profile3", player);
                id_to_profiles.put(profile.getID(), profile);
                profile.getOwnerGroup().addMember(player);
                profiles.add(profile);

                profile = new AccessProfile(true).generateNew("Profile4", player);
                id_to_profiles.put(profile.getID(), profile);
                profile.getOwnerGroup().addMember(player);
                profiles.add(profile);

                profile = new AccessProfile(true).generateNew("Profile5", player);
                id_to_profiles.put(profile.getID(), profile);
                profile.getOwnerGroup().addMember(player);
                profiles.add(profile);
            }
        }
        return profiles;
    }

    public static Collection<AccessProfile> getProfiles()
    {
        return id_to_profiles.values();
    }
}
