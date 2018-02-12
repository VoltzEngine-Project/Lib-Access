package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.framework.access.prefab.AccessObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * Used to define a users for a permission system
 *
 * @author DarkGuardsman
 */
public class AccessUser extends AccessObject
{
    /** Username of player, main way to check owner */
    protected String username;
    private UUID userID;

    /** Extra user data, only store permission related settings here */
    protected NBTTagCompound extraData;
    /** User's main group */
    protected AccessGroup group;

    protected AccessUser()
    {
        //Mainly for save/load
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

    public boolean groupHasNode(String node)
    {
        return this.getGroup() != null && this.getGroup().hasNode(node);
    }

    @Override
    public boolean hasNode(String node)
    {
        return super.hasNode(node) || groupHasNode(node);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString("username", this.username);
        nbt.setTag("extraData", this.userData());
        if (getUserID() != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("l", getUserID().getLeastSignificantBits());
            tag.setLong("m", getUserID().getMostSignificantBits());
            nbt.setTag("UUID", tag);
        }
        return super.save(nbt);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        this.username = nbt.getString("username");
        this.extraData = nbt.getCompoundTag("extraData");
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
        this.isTemporary = temp;
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
