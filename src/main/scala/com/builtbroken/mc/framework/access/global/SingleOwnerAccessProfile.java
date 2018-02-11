package com.builtbroken.mc.framework.access.global;

import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.AccessUser;
import com.builtbroken.mc.framework.access.perm.Permissions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * Seperate version from the base {@link AccessProfile} that contains additional settings and features.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2017.
 */
public class SingleOwnerAccessProfile extends GlobalAccessProfile
{
    public String ownerUsername;
    public UUID ownerID;

    public SingleOwnerAccessProfile()
    {
        //Needed for save manager
    }

    public SingleOwnerAccessProfile(EntityPlayer player)
    {
        this(player.getGameProfile().getName(), player.getGameProfile().getId());
    }

    public SingleOwnerAccessProfile(String username, UUID ownerID)
    {
        this.ownerUsername = username;
        this.ownerID = ownerID;
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
            user.addNode(Permissions.root);
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
            user.addNode(Permissions.root + ".*");
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
