package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.framework.access.prefab.AccessObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

/**
 * Permission system group used to track players with the access they have. Used
 * with several different systems include sentry gun AI targeting, and inventory
 * locking.
 *
 * @author DarkGuardsman
 */
public class AccessGroup extends AccessObject implements Cloneable
{
    private static final String NBT_GROUP_NAME = "groupName";
    private static final String NBT_GROUP_EXTEND = "extendGroup";
    private static final String NBT_USERS = "users";
    private static final String NBT_CREATION_DATE = "creationDate";

    /** Name of the group */
    private String name;
    /** Overrides the default name for display in GUIs */
    protected String displayName;
    /** Show description for the GUI */
    protected String description;

    /** System time when the group was created */
    protected long creation_time;
    /** Group this group inebriates permissions from */
    protected AccessGroup extendGroup;
    /** Name of the extend group, used mainly for save/load */
    protected String extendGroup_name;

    //Cache of user profiles by name, names should be lowercase to reduce issues with checks
    private final HashMap<String, AccessUser> username_to_profile = new HashMap();
    /** Map of user's UUID to profiles */
    protected final HashMap<UUID, AccessUser> uuid_to_profile = new HashMap();

    public AccessGroup(String group_name, AccessUser... users)
    {
        this.name = group_name;
        this.creation_time = System.currentTimeMillis();

        for (AccessUser user : users)
        {
            addMember(user);
        }
    }

    /**
     * Gets the AccessUser object that goes with the user name
     *
     * @param username - user name of the EntityPlayer
     * @return the exact user
     */
    public AccessUser getMember(final String username)
    {
        final String key = username.toLowerCase();
        if (username_to_profile.containsKey(key))
        {
            return username_to_profile.get(key);
        }
        return null;
    }

    /**
     * Gets the AccessUser object that goes to the player
     *
     * @param player - instance of player
     * @return the exact user
     */
    public AccessUser getMember(EntityPlayer player)
    {
        if (player != null)
        {
            //try UUID first
            UUID id = player.getGameProfile().getId();
            if (uuid_to_profile.containsKey(id))
            {
                return uuid_to_profile.get(id);
            }

            //try username last
            return getMember(player.getCommandSenderName());
        }
        return null;
    }

    /**
     * Adds a user profile directly to the group
     *
     * @param obj - access profile with valid username
     * @return true if the profile was valid and added
     */
    public boolean addMember(AccessUser obj)
    {
        if (isValid(obj))
        {
            if (obj.getUserID() != null)
            {
                uuid_to_profile.put(obj.getUserID(), obj);
            }
            username_to_profile.put(obj.username.toLowerCase(), obj);
            obj.setGroup(this);
            return true;
        }
        return false;
    }

    @Deprecated
    public boolean addMember(String name)
    {
        //TODO trigger super profile that a new member was added
        return getMember(name) == null && addMember(new AccessUser(name));
    }

    /**
     * Adds a user to the group
     *
     * @param player - user with a valid UUID
     * @return true if the user was added
     */
    public boolean addMember(EntityPlayer player)
    {
        //TODO trigger super profile that a new member was added
        return player != null && addMember(new AccessUser(player));
    }

    /**
     * Removes a player from the group
     *
     * @param player - player with a valid UUID
     * @return true if the player was removed using it's UUID
     */
    public boolean removeMember(EntityPlayer player)
    {
        return player != null && removeMember(player.getGameProfile().getId());
    }

    /**
     * Removes a user with a username
     *
     * @param name - user's name
     * @return true if it was contained in {@link #username_to_profile}
     */
    public boolean removeMember(String name)
    {
        return removeMember(getMember(name));
    }

    /**
     * Removes a user's access from this group
     *
     * @param user - user's profile
     * @return true if removed
     */
    public boolean removeMember(AccessUser user)
    {
        //TODO trigger super profile that a member removed
        if (user != null)
        {
            final String key = user.getName().toLowerCase();
            if (username_to_profile.containsKey(key))
            {
                username_to_profile.remove(key);
                if (user.getUserID() != null)
                {
                    uuid_to_profile.remove(user.getUserID());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a user with a {@link UUID}
     *
     * @param id - user's id
     * @return true if it was contained in {@link #uuid_to_profile}
     */
    public boolean removeMember(UUID id)
    {
        if (uuid_to_profile.containsKey(id))
        {
            return removeMember(uuid_to_profile.get(id));
        }
        return false;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString(NBT_GROUP_NAME, this.getName());
        if (this.extendGroup_name != null)
        {
            nbt.setString(NBT_GROUP_EXTEND, this.extendGroup_name);
        }
        NBTTagList usersTag = new NBTTagList();
        for (AccessUser user : this.username_to_profile.values())
        {
            NBTTagCompound accessData = new NBTTagCompound();
            user.save(accessData);
            usersTag.appendTag(accessData);
        }

        nbt.setTag(NBT_USERS, usersTag);

        nbt.setLong(NBT_CREATION_DATE, this.creation_time);
        return super.save(nbt);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);

        // load group name
        this.setName(nbt.getString(NBT_GROUP_NAME));

        // Load extend group
        if (nbt.hasKey(NBT_GROUP_EXTEND))
        {
            this.extendGroup_name = nbt.getString(NBT_GROUP_EXTEND);
        }

        // Load users
        NBTTagList userList = nbt.getTagList(NBT_USERS, 10);
        getMembers().clear();

        for (int i = 0; i < userList.tagCount(); ++i)
        {
            AccessUser user = AccessUser.loadFromNBT(userList.getCompoundTagAt(i));
            this.addMember(user);
        }

        // Load creation date
        if (nbt.hasKey(NBT_CREATION_DATE))
        {
            this.creation_time = nbt.getLong(NBT_CREATION_DATE);
        }
        else
        {
            this.creation_time = System.currentTimeMillis();
        }
    }

    @Override
    public boolean hasNode(String node)
    {
        return super.hasNode(node) || this.getExtendGroup() != null && this.getExtendGroup().hasNode(node);
    }

    /**
     * Sets this group it extends another group
     */
    public boolean setToExtend(AccessGroup group)
    {
        if (!isParent(group))
        {
            this.extendGroup = group;
            if (this.extendGroup != null)
            {
                this.extendGroup_name = this.extendGroup.getName();
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if this group is the passed in groups parent
     *
     * @param group
     * @return
     */
    public boolean isParent(AccessGroup group)
    {
        if (group == this)
        {
            return true;
        }
        if (group != null)
        {
            if (group.getExtendGroup() != null)
            {
                return group.getExtendGroup() == this || isParent(group.getExtendGroup());
            }
        }
        return false;
    }

    /**
     * Gets the group this group extends
     */
    public AccessGroup getExtendGroup()
    {
        return this.extendGroup;
    }

    /**
     * Gets the name of the group this group extends. Only used to init the
     * extend group after loading the group from a save.
     */
    public String getExtendGroupName()
    {
        return this.extendGroup_name;
    }

    /**
     * Gets a list of all users in the group
     *
     * @return collection of users
     */
    public Collection<AccessUser> getMembers()
    {
        return username_to_profile.values();
    }

    /**
     * Checks if an access profile for a user is valid. Normal
     * checks involve NPE, username, and contains
     *
     * @param obj - valid profile
     * @return true if the profile is valid
     */
    protected boolean isValid(AccessUser obj)
    {
        return obj != null && obj.username != null && !getMembers().contains(obj);
    }

    /**
     * Name of the group
     *
     * @return string
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of the group, warning this
     * may break group connections. As the
     * name is also used to link groups in a
     * profile. Avoid using this directly.
     *
     * @param name - valid name
     */
    public void setName(String name)
    {
        this.name = name;
    }


    @Override
    public AccessGroup clone()
    {
        AccessGroup group = new AccessGroup(this.getName());
        for (String node : getNodes())
        {
            group.getNodes().add(node);
        }
        return group;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof AccessGroup && ((AccessGroup) obj).getName().equalsIgnoreCase(this.getName());
    }

    @Override
    public String toString()
    {
        return "[Group:" + this.getName() + "]";
    }

    /** Overrides the default name for display in GUIs */
    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /** Show description for the GUI */
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
