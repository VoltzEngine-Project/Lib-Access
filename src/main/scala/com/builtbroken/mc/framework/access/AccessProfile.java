package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.api.IVirtualObject;
import com.builtbroken.mc.api.tile.ITile;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.framework.access.wrapper.AccessUserMultiGroup;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.helper.NBTUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.File;
import java.util.*;

/**
 * Designed to be used as a container for AccessGroups and AccessUser. If you plan to use this make
 * sure to use it correctly. This is designed to be saved separate from the world save if marked for
 * global access. Which means it can save/load at will from the world file.
 *
 * @author DarkGuardsman
 */
public class AccessProfile implements IVirtualObject
{
    /** List of all containers that use this profile to define some part of their functionality */
    private final Set<IProfileContainer> containers = Collections.newSetFromMap(new WeakHashMap<IProfileContainer, Boolean>());

    /** Players who currently have a GUI open looking at this access profile */
    public final HashMap<EntityPlayer, Long> playersWithSettingsGUIOpen = new HashMap();

    /** A list of all groups attached to this profile */
    protected List<AccessGroup> groups = new ArrayList();

    /** Display name of the profile for the user to easily read */
    protected String profileName = "";

    /**
     * Only used by global profiles that have no defined container. Defaults to localHost defining
     * the profile as non-global
     */
    protected String profileID = "LocalHost";

    /** Is this profile global */
    protected boolean global = false;

    /** Save file by which this was loaded. Not currently used but stored if ever needed. */
    protected File saveFile;

    static
    {
        //Registers this class to the save manager so loading is easier
        SaveManager.registerClass("AccessProfile", AccessProfile.class);
    }

    public AccessProfile()
    {

    }

    public AccessProfile(boolean global)
    {
        this.global = global;
        if (global)
        {
            SaveManager.register(this);
        }
    }

    public AccessProfile(NBTTagCompound nbt)
    {
        this(nbt, false);
    }

    public AccessProfile(NBTTagCompound nbt, boolean global)
    {
        this();
        this.load(nbt);
        if (this.profileName == null || this.profileID == null)
        {
            if (!global)
            {
                this.generateNew("Default", (ITile) null);
            }
            else
            {
                this.generateNew("New Group");
            }
        }
    }

    /**
     * Called to generate a new data for an
     * access profile. Will set {@link #profileID},
     * {@link #profileName}, and init {@link #groups}
     * with registered defaults
     *
     * @param name - profile name, used in ID
     * @return this
     */
    public AccessProfile generateNew(String name)
    {
        AccessUtility.loadNewGroupSet(this);
        initName(name.trim(), "P_" + name + "_" + System.currentTimeMillis());
        return this;
    }

    /**
     * Called to generate a new data for an
     * access profile. Will set {@link #profileID},
     * {@link #profileName}, and init {@link #groups}
     * with registered defaults
     *
     * @param name   - profile name, used in ID
     * @param entity - tile that created this group
     * @return this
     */
    public AccessProfile generateNew(String name, ITile entity)
    {
        AccessUtility.loadNewGroupSet(this);
        initName(name.trim(), "LocalHost:" + name);
        return this;
    }

    /**
     * Called to generate a new data for an
     * access profile. Will set {@link #profileID},
     * {@link #profileName}, and init {@link #groups}
     * with registered defaults
     *
     * @param name   - profile name, used in ID
     * @param player - user that created this group, used in profile id
     * @return this
     */
    public AccessProfile generateNew(String name, EntityPlayer player)
    {
        AccessUtility.loadNewGroupSet(this);
        initName(name, player.getCommandSenderName() + "_" + System.nanoTime());
        return this;
    }

    public AccessProfile initName(String name, String id)
    {
        this.profileName = name;
        this.profileID = id;
        return this;
    }

    /**
     * Display name of the profile
     */
    public String getName()
    {
        return this.profileName;
    }

    /**
     * Save/Global id of the profie
     */
    public String getID()
    {
        return this.profileID;
    }

    /**
     * Is this a global profile that is can be accessed by all objects
     */
    public boolean isGlobal()
    {
        return this.global;
    }

    /**
     * Checks to see if the profile contains the user
     *
     * @param player
     * @return
     */
    public boolean containsUser(EntityPlayer player)
    {
        return containsUser(player.getCommandSenderName());
    }

    /**
     * Checks to see if the profile contains the user
     *
     * @param username
     * @return
     */
    public boolean containsUser(String username)
    {
        for (AccessGroup group : this.groups)
        {
            AccessUser user = group.getMember(username);
            if (user != null)
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the players Access object
     *
     * @param player - entity player
     * @return AccessUser for the player, or an empty AccessUser instance if player was not found
     */
    public AccessUser getUserAccess(EntityPlayer player)
    {
        return getUserAccess(player.getCommandSenderName());
    }

    /**
     * EntityPlayer version should be used as usernames are not longer going to be supported.
     */
    public AccessUser getUserAccess(String username)
    {
        for (AccessGroup group : this.groups)
        {
            AccessUser user = group.getMember(username);
            if (user != null)
            {
                return new AccessUserMultiGroup(this, user); //temp fix for user being in several groups at once
            }
        }
        return new AccessUser(username).setTempary(true);
    }

    /**
     * Gets all groups that contain a user
     *
     * @param username - username
     * @return list of groups
     */
    public List<AccessGroup> getGroupsWithUser(String username)
    {
        List<AccessGroup> groups = new ArrayList();
        for (AccessGroup group : this.groups)
        {
            AccessUser user = group.getMember(username);
            if (user != null)
            {
                groups.add(group);
            }
        }
        return groups;
    }


    /**
     * List of all users, do not call often
     * as it creates a new list.
     *
     * @return
     */
    public List<AccessUser> getUsers()
    {
        //TODO create wrapper that will fake a list but will in fact iterate over all groups
        List<AccessUser> users = new ArrayList();
        for (AccessGroup group : this.groups)
        {
            users.addAll(group.getMembers());
        }
        return users;
    }

    /**
     * Adds a container to the profile so it can be
     * notified of changes.
     *
     * @param container - tile using this profile
     */
    public void addContainer(IProfileContainer container)
    {
        if (!this.containers.contains(container))
        {
            this.containers.add(container);
        }
    }

    /**
     * Removes a container from this profile.
     *
     * @param container - tile that was using this profile
     */
    public void removeContainer(IProfileContainer container)
    {
        if (this.containers.contains(container))
        {
            this.containers.remove(container);
        }
    }

    /**
     * Called to remove a user from the profile
     *
     * @param player
     * @return
     */
    public boolean removeUserAccess(String player)
    {
        boolean re = false;
        for (AccessGroup group : this.groups)
        {
            AccessUser user = group.getMember(player);
            if (user != null && group.removeMember(user))
            {
                re = true;
            }
        }
        if (re)
        {
            this.onProfileUpdate();
        }
        return re;
    }

    public void onProfileUpdate()
    {
        Iterator<IProfileContainer> it = containers.iterator();
        while (it.hasNext())
        {
            IProfileContainer container = it.next();
            if (container != null && this.equals(container.getAccessProfile()))
            {
                container.onProfileChange();
            }
            else
            {
                it.remove();
            }
        }
    }

    public AccessGroup getGroup(String name)
    {
        return AccessUtility.getGroup(this.getGroups(), name.toLowerCase());
    }

    public AccessGroup removeGroup(String name)
    {
        return removeGroup(getGroup(name));
    }

    public AccessGroup removeGroup(AccessGroup group)
    {
        if (group != null && getGroups().contains(group))
        {
            if (getGroups().remove(group))
            {
                this.onProfileUpdate();
            }
        }
        return group;
    }

    public boolean addGroup(AccessGroup group)
    {
        if (!this.groups.contains(group))
        {
            if (this.groups.add(group))
            {
                this.onProfileUpdate();
                return true;
            }
        }
        return false;
    }

    public AccessGroup getOwnerGroup()
    {
        return this.getGroup("owner");
    }

    public List<AccessGroup> getGroups()
    {
        if (this.groups == null)
        {
            return new ArrayList();
        }
        return this.groups;
    }


    public boolean hasNode(EntityPlayer player, String node)
    {
        return getUserAccess(player).hasNode(node);
    }


    public boolean hasNode(String username, String node)
    {
        return getUserAccess(username).hasNode(node);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        this.profileName = nbt.getString("name");
        this.global = nbt.getBoolean("global");
        this.profileID = nbt.getString("profileID");

        //Load groups
        NBTTagList group_list = nbt.getTagList("groups", 10);
        if (group_list != null && group_list.tagCount() > 0)
        {
            this.groups.clear();
            //Load group save data
            for (int i = 0; i < group_list.tagCount(); i++)
            {
                AccessGroup group = new AccessGroup("");
                group.load(group_list.getCompoundTagAt(i));
                this.groups.add(group);
            }
            //Set group extensions
            for (AccessGroup group : this.groups)
            {
                if (group.getExtendGroupName() != null)
                {
                    group.setToExtend(this.getGroup(group.getExtendGroupName()));
                }
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString("name", this.profileName);
        nbt.setBoolean("global", this.global);
        nbt.setString("profileID", this.profileID);
        NBTTagList groupTags = new NBTTagList();
        for (AccessGroup group : this.getGroups())
        {
            NBTTagCompound groupTag = new NBTTagCompound();
            group.save(groupTag);
            groupTags.appendTag(groupTag);
        }
        nbt.setTag("groups", groupTags);
        return nbt;
    }

    @Override
    public File getSaveFile()
    {
        if (this.saveFile == null)
        {
            this.saveFile = new File(NBTUtility.getSaveDirectory(MinecraftServer.getServer().getFolderName()), getPathToProfile(this.getID()));
        }
        return this.saveFile;
    }

    public static String getPathToProfile(String name)
    {
        return NBTUtility.BBM_FOLDER + "access/profiles/" + name + ".dat";
    }

    @Override
    public void setSaveFile(File file)
    {
        this.saveFile = file;

    }

    @Override
    public boolean shouldSaveForWorld(World world)
    {
        return world != null && world.provider.dimensionId == 0;
    }

    @Override
    public String toString()
    {
        return LanguageUtility.getLocal("info.accessprofile.tostring").replaceAll("%p", this.profileName.toString()).replaceAll("%g", groups.toString());
    }
}
