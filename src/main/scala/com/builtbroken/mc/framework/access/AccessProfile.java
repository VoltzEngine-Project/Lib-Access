package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.framework.access.wrapper.AccessUserMultiGroup;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

/**
 * Designed to be used as a container for {@link AccessGroup}s containing  {@link AccessUser}s. Great for
 * simple node based permission systems.
 *
 * @author DarkGuardsman
 */
public class AccessProfile implements ISave
{
    protected boolean canEdit = true; //TODO implement, allow disabling modifications to object

    /** A list of all groups attached to this profile */
    protected List<AccessGroup> groups = new ArrayList();

    /**
     * Checks to see if the profile contains the user
     *
     * @param player - user to check
     * @return true if profile contains user in at least one group
     */
    public boolean containsUser(EntityPlayer player)
    {
        for (AccessGroup group : this.groups)
        {
            AccessUser user = group.getMember(player);
            if (user != null)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the profile contains the user
     * <p>
     * Try to use {@link EntityPlayer} instance if possible
     *
     * @param username - name of the user
     * @return true if profile contains user in at least one group
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
        for (AccessGroup group : this.groups)
        {
            AccessUser user = group.getMember(player);
            if (user != null)
            {
                return new AccessUserMultiGroup(this, user); //temp fix for user being in several groups at once
            }
        }
        return new AccessUser(player).setTemporary(true).disableEdit();
    }

    /**
     * Gets the players Access object
     * <p>
     * Use {@link EntityPlayer} object if possible with {@link #getUserAccess(EntityPlayer)}
     * this is meant as a legacy and edge case method. It should not be used as the main
     * access point for permission checks.
     *
     * @param username - name of the user
     * @return AccessUser for the player, or an empty AccessUser instance if player was not found
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
        return new AccessUser(username).setTemporary(true).disableEdit();
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
        for (AccessGroup group : this.getGroups())
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
     * Called to remove a user from the profile
     *
     * @param player
     * @return
     */
    public boolean removeUserAccess(String player)
    {
        boolean re = false;
        if (canEdit)
        {
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
        }
        return re;
    }

    /**
     * Called any time the profile changes
     */
    public void onProfileUpdate()
    {

    }

    public AccessGroup getGroup(String name)
    {
        if (name != null && !name.isEmpty())
        {
            return AccessUtility.getGroup(this.getGroups(), name.toLowerCase());
        }
        return null;
    }

    public AccessGroup removeGroup(String name)
    {
        return removeGroup(getGroup(name));
    }

    public AccessGroup removeGroup(AccessGroup group)
    {
        if (canEdit && group != null && getGroups().contains(group))
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
        if (canEdit && !this.groups.contains(group))
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

    public <E extends AccessProfile> E disableEdit()
    {
        canEdit = false;
        return (E) this;
    }

    public <E extends AccessProfile> E enableEdit()
    {
        canEdit = true;
        return (E) this;
    }

    public <E extends AccessProfile> E setEditState(boolean state)
    {
        canEdit = state;
        return (E) this;
    }

    public boolean canEdit()
    {
        return canEdit;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + " groups: " + groups.size();
    }
}
