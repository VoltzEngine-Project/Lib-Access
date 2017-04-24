package com.builtbroken.mc.framework.access.gui.packets;

import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketGui;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.GlobalAccessSystem;
import com.builtbroken.mc.framework.access.Permissions;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2017.
 */
public class PacketAccessGui extends PacketType implements IPacket
{
    int id = 0;

    public PacketAccessGui()
    {

    }

    public PacketAccessGui(int id)
    {
        this.id = id;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(id);
        buffer.writeBytes(data());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        id = buffer.readInt();
        data_$eq(buffer.slice());
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP) //Could be a fake player
        {
            if (id == 0)
            {
                clearGui(player);
                PacketGui packetGui = new PacketGui(0);

                List<AccessProfile> profileList = GlobalAccessSystem.getProfilesFor(player);
                packetGui.data().writeInt(profileList.size());
                for (AccessProfile profile : profileList)
                {
                    //We only want to send the bare minimal to function
                    packetGui.write(profile.getName());
                    packetGui.write(profile.getID());
                    packetGui.write(profile.getUserAccess(player).hasNode(Permissions.profileView)); //Disables view option
                }

                Engine.instance.packetHandler.sendToPlayer(packetGui, (EntityPlayerMP) player);
            }
            else if (id == 1)
            {
                clearGui(player);
                String accessGroup = ByteBufUtils.readUTF8String(data());
                AccessProfile profile = GlobalAccessSystem.getProfile(accessGroup);
                if (profile != null)
                {
                    IPacket packetGui = new PacketGui(1).write(profile.save(new NBTTagCompound()));
                    Engine.instance.packetHandler.sendToPlayer(packetGui, (EntityPlayerMP) player);
                }
                else
                {
                    PacketGui packetGui = new PacketGui(5);
                    ByteBufUtils.writeUTF8String(packetGui.data(), "error.profile.not.found");
                    Engine.instance.packetHandler.sendToPlayer(packetGui, (EntityPlayerMP) player);
                }
            }
            else if (id == 2)
            {
                clearGui(player);
                String accessGroup = ByteBufUtils.readUTF8String(data());
                AccessProfile profile = GlobalAccessSystem.getProfile(accessGroup);
                if (profile != null)
                {
                    profile.playersWithSettingsGUIOpen.put(player, System.currentTimeMillis());
                }
            }
        }
    }

    protected void clearGui(EntityPlayer player)
    {
        for (AccessProfile profile : GlobalAccessSystem.getProfiles())
        {
            if (profile != null)
            {
                profile.playersWithSettingsGUIOpen.remove(player);
            }
        }
    }

    public static void doRequest()
    {
        Engine.instance.packetHandler.sendToServer(new PacketAccessGui(0));
    }

    public static void doRequest(String profileID)
    {
        Engine.instance.packetHandler.sendToServer(new PacketAccessGui(1).write(profileID));
    }

    public static void keepAlive(String profileID)
    {
        Engine.instance.packetHandler.sendToServer(new PacketAccessGui(2).write(profileID));
    }
}
