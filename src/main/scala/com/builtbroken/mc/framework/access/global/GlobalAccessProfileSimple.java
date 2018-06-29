package com.builtbroken.mc.framework.access.global;

import com.builtbroken.mc.framework.access.AccessUser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * Simplified version of the {@link GlobalAccessProfile} to setup a single owner of the profile. This
 * prevents the profile from switching hands. The intended use for this version is for default profiles.
 * An example use case is friends lists that will only have one group.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2017.
 */
public class GlobalAccessProfileSimple extends GlobalAccessProfile
{
    /** Username of the owner */
    protected String ownerUsername;
    /** Universal unique id of the owner */
    protected UUID ownerID;

    public GlobalAccessProfileSimple()
    {
        //Needed for save manager
    }

    public GlobalAccessProfileSimple(EntityPlayer player)
    {
        this(player.getGameProfile().getName(), player.getGameProfile().getId());
    }

    public GlobalAccessProfileSimple(String username, UUID ownerID)
    {
        this.ownerUsername = username;
        this.ownerID = ownerID;
    }

    @Override
    public boolean canSeeProfile(EntityPlayer player)
    {
        return isOwner(player);
    }

    @Override
    public boolean canDelete(EntityPlayer player)
    {
        return false; //TODO add flag to toggle
    }

    @Override
    public boolean containsUser(EntityPlayer player)
    {
        return isOwner(player) || super.containsUser(player);
    }

    @Override
    public boolean containsUser(String username)
    {
        return username.equalsIgnoreCase(ownerUsername) || super.containsUser(username);
    }

    @Override
    public AccessUser getUserAccess(EntityPlayer player)
    {
        if (isOwner(player))
        {
            AccessUser user = new AccessUser(player);
            user.addNode("*");
            user.disableEdit();
            return user;
        }
        return super.getUserAccess(player);
    }

    @Override
    public AccessUser getUserAccess(String username)
    {
        if (username.equalsIgnoreCase(ownerUsername))
        {
            AccessUser user = new AccessUser(ownerUsername, ownerID);
            user.addNode("*");
            user.disableEdit();
            return user;
        }
        return super.getUserAccess(username);
    }

    public boolean isOwner(EntityPlayer player)
    {
        return player.getGameProfile().getId().equals(ownerID) || player.getGameProfile().getName().equalsIgnoreCase(ownerUsername);
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        ownerUsername = nbt.getString("s_owner");
        ownerID = new UUID(nbt.getLong("s_id_max"), nbt.getLong("s_id_min"));
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString("s_owner", ownerUsername);
        nbt.setLong("s_id_min", ownerID.getLeastSignificantBits());
        nbt.setLong("s_id_max", ownerID.getMostSignificantBits());
        return super.save(nbt);
    }
}
