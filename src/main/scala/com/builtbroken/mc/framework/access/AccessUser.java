package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.framework.access.perm.Permission;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Used to define a users access to a terminal based object.
 *
 * @author DarkGuardsman
 */
public class AccessUser implements ISave
{
    /** Username of player, main way to check owner */
    protected String username;
    private UUID userID;

    /** Toggle to note this user will not save, and is only used for a short time */
    protected boolean isTempary = false;

    /** Extra user data, only store permission related settings here */
    protected NBTTagCompound extraData;
    /** User's main group */
    protected AccessGroup group;

    /** List of permission nodes */
    public List<String> nodes = new ArrayList();

    /** Only use for save/load */
    protected AccessUser()
    {
    }

    /**
     * @param username
     * @deprecated use {@link #AccessUser(EntityPlayer)} as
     * the main way to create new users. This way the UUID
     * is stored correctly
     */
    @Deprecated
    public AccessUser(String username)
    {
        this.username = username;
    }

    public AccessUser(String username, UUID id)
    {
        this.username = username;
        this.setUserID(id);
    }

    @Deprecated
    public AccessUser(EntityPlayer player)
    {
        this(player.getCommandSenderName(), player.getGameProfile().getId());
    }

    public static AccessUser loadFromNBT(NBTTagCompound nbt)
    {
        AccessUser user = new AccessUser();
        user.load(nbt);
        return user;
    }

    public AccessGroup getGroup()
    {
        return this.group;
    }

    public AccessUser setGroup(AccessGroup group)
    {
        this.group = group;
        return this;
    }

    /**
     * Checks if the user has the permission node
     *
     * @param permission - node
     * @return true if the user has the node or a super * node
     */
    public boolean hasNode(Permission permission)
    {
        return hasNode(permission.toString());
    }

    /**
     * Checks if the user has the permission node
     *
     * @param node - node
     * @return true if the user has the node or a super * node
     */
    public boolean hasNode(String node)
    {
        return hasExactNode(node) || hasNodeInUser(node) || groupHasNode(node);
    }

    public boolean groupHasNode(String node)
    {
        return this.getGroup() != null && this.getGroup().hasNode(node);
    }

    /**
     * Checks if the user has the permission node
     * for this user only. Doesn't check group nodes
     * user {@link #hasNode(String)} to check
     * group as well
     *
     * @param node - node
     * @return true if the user has the node or a super * node
     */
    public boolean hasNodeInUser(String node)
    {
        String tempNode = node.replace(".*", "");
        for (String headNode : nodes)
        {
            if (tempNode.contains(headNode))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the user has the exact node
     *
     * @param node - node
     * @return true only if exact match is found
     */
    public boolean hasExactNode(String node)
    {
        return this.nodes.contains(node);
    }

    /**
     * Removes a permission node from this user
     *
     * @param perm
     * @return
     */
    public boolean removeNode(Permission perm)
    {

        return removeNode(perm.toString());
    }

    /**
     * Adds a permission node to this user
     *
     * @param perm
     * @return
     */
    public boolean addNode(Permission perm)
    {
        return addNode(perm.toString());
    }

    /**
     * Removes a permission node from this user
     *
     * @param perm
     * @return
     */
    public boolean removeNode(String perm)
    {
        return nodes.remove(perm);
    }

    /**
     * Adds a permission node to this user
     *
     * @param perm
     * @return
     */
    public boolean addNode(String perm)
    {
        //TODO if contains * remove all sub nodes
        if (!hasExactNode(perm))
        {
            nodes.add(perm);
        }
        return false;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString("username", this.username);
        nbt.setTag("extraData", this.userData());
        NBTTagList usersTag = new NBTTagList();
        for (String str : this.nodes)
        {
            NBTTagCompound accessData = new NBTTagCompound();
            accessData.setString("name", str);
            usersTag.appendTag(accessData);
        }
        nbt.setTag("permissions", usersTag);
        if (getUserID() != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("l", getUserID().getLeastSignificantBits());
            tag.setLong("m", getUserID().getMostSignificantBits());
            nbt.setTag("UUID", tag);
        }
        return nbt;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        this.username = nbt.getString("username");
        this.extraData = nbt.getCompoundTag("extraData");
        NBTTagList userList = nbt.getTagList("permissions", 10);
        this.nodes.clear();
        for (int i = 0; i < userList.tagCount(); ++i)
        {
            this.nodes.add(userList.getCompoundTagAt(i).getString("name"));
        }
        if (nbt.hasKey("UUID"))
        {
            NBTTagCompound tag = nbt.getCompoundTag("UUID");
            long l = tag.getLong("l");
            long m = tag.getLong("m");
            setUserID(new UUID(m, l));
        }
    }

    /**
     * Saves the data to a new NBTTagCompound
     *
     * @return
     */
    public NBTTagCompound toNBT()
    {
        return save(new NBTTagCompound());
    }

    /**
     * Set user as temporary and notes it should not save
     *
     * @param temp
     * @return
     */
    public AccessUser setTemporary(boolean temp)
    {
        this.isTempary = temp;
        return this;
    }

    /**
     * Used to add other data to the user
     */
    public NBTTagCompound userData()
    {
        if (this.extraData == null)
        {
            this.extraData = new NBTTagCompound();
        }
        return this.extraData;
    }

    public String getName()
    {
        return this.username;
    }


    /** User's UUID, secondary way to check owner or main way if permission node is major */
    public UUID getUserID()
    {
        return userID;
    }

    public void setUserID(UUID userID)
    {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof String)
        {
            return ((String) obj).equalsIgnoreCase(this.getName());
        }
        return obj instanceof AccessUser && ((AccessUser) obj).getName().equalsIgnoreCase(this.getName());
    }

    @Override
    public String toString()
    {
        return "[User:" + this.getName() + "]";
    }

    public AccessUser copyToNewUser(String username)
    {
        return copyData(new AccessUser(username));
    }

    public AccessUser copyToNewUser(EntityPlayer player)
    {
        return copyData(new AccessUser(player));
    }

    public AccessUser copyData(AccessUser user)
    {
        user.extraData = extraData;
        user.group = group;
        for (String node : nodes)
        {
            user.nodes.add(node);
        }
        return user;
    }
}
