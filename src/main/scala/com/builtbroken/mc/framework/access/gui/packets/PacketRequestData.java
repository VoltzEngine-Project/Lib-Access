package com.builtbroken.mc.framework.access.gui.packets;

import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketGui;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.GlobalAccessSystem;
import com.builtbroken.mc.framework.access.Permissions;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2017.
 */
public class PacketRequestData implements IPacket
{
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {

    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {

    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP) //Could be a fake player
        {
            PacketGui packetGui = new PacketGui(0);

            List<AccessProfile> profileList = GlobalAccessSystem.getProfilesFor(player);
            packetGui.data().writeInt(profileList.size());
            for(AccessProfile profile : profileList)
            {
                //We only want to send the bare minimal to function
                ByteBufUtils.writeUTF8String(packetGui.data(), profile.getName());
                ByteBufUtils.writeUTF8String(packetGui.data(), profile.getID());
                packetGui.data().writeBoolean(profile.getUserAccess(player).hasNode(Permissions.profileView)); //Disables view option
            }


            Engine.instance.packetHandler.sendToPlayer(packetGui, (EntityPlayerMP) player);
        }
    }

    public static void doRequest()
    {
        Engine.instance.packetHandler.sendToServer(new PacketRequestData());
    }
}
