package com.builtbroken.mc.framework.access.global.packets;

import com.builtbroken.mc.api.data.IPacket;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.handler.SaveManager;
import com.builtbroken.mc.core.network.packet.PacketGui;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.global.GlobalAccessProfile;
import com.builtbroken.mc.framework.access.global.GlobalAccessSystem;
import com.builtbroken.mc.framework.access.perm.Permissions;
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
public class PacketAccessGui extends PacketType implements IPacket
{
    public static int REQUEST_ALL_PROFILES = 0;
    public static int REQUEST_PROFILE = 1;
    public static int KEEP_ALIVE = 2;
    public static int ADD_USER_TO_GROUP = 3;
    public static int REMOVE_USER_FROM_GROUP = 4;
    public static int CREATE_PROFILE = 5;
    public static int CREATE_GROUP = 6;
    public static int REMOVE_GROUP = 7;
    public static int UPDATE_GROUP_PARENT = 9;
    public static int ADD_NODE_TO_GROUP = 10;
    public static int REMOVE_NODE_FROM_GROUP = 11;
    public static int REMOVE_PROFILE = 12;

    int id = 0;

    public PacketAccessGui()
    {
        //Needed so forge can make the packet
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
    public void handleServerSide(EntityPlayer e)
    {
        //TODO check if world is loaded
        if (e instanceof EntityPlayerMP) //Could be a fake player
        {
            final EntityPlayerMP player = (EntityPlayerMP) e;
            //Returns a message to let the client know the packet was received
            if (id != KEEP_ALIVE)
            {
                sendMessageToClient(player, "packet.received");
            }

            if (id == REQUEST_ALL_PROFILES)
            {
                clearGui(player);
                sendProfilesToClient((EntityPlayerMP) player);
            }
            else if (id == REQUEST_PROFILE)
            {
                clearGui(player);
                final String profileID = ByteBufUtils.readUTF8String(data());
                sendProfileToClient((EntityPlayerMP) player, profileID);
            }
            else if (id == KEEP_ALIVE)
            {
                clearGui(player);
                String accessGroup = ByteBufUtils.readUTF8String(data());
                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(accessGroup);
                if (profile != null)
                {
                    profile.playersWithSettingsGUIOpen.put(player, System.currentTimeMillis());
                }
            }
            else if (id == ADD_USER_TO_GROUP)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                String groupID = ByteBufUtils.readUTF8String(data());
                String userID = ByteBufUtils.readUTF8String(data());

                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID);
                if (profile != null)
                {
                    if (profile.containsUser(player) && profile.hasNode(player, Permissions.profileEdit.toString()))
                    {
                        AccessGroup group = profile.getGroup(groupID);
                        if (group != null)
                        {
                            if (group.getMember(userID) != null)
                            {
                                sendMessageToClient(player, "error.group.user.add.exists");
                            }
                            else if (!group.addMember(userID)) //TODO get UUID
                            {
                                sendMessageToClient(player, "error.group.user.add");
                            }
                            else
                            {
                                sendProfileToClient((EntityPlayerMP) player, profileID);
                            }
                        }
                        else
                        {
                            sendMessageToClient(player, "error.group.not.found");
                        }
                    }
                    else
                    {
                        sendMessageToClient(player, "error.profile.access.invalid");
                    }
                }
                else
                {
                    sendMessageToClient(player, "error.profile.not.found");
                }
            }
            else if (id == REMOVE_USER_FROM_GROUP)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                String groupID = ByteBufUtils.readUTF8String(data());
                String userID = ByteBufUtils.readUTF8String(data());

                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID);
                if (profile != null)
                {
                    if (profile.containsUser(player) && profile.hasNode(player, Permissions.profileEdit.toString()))
                    {
                        AccessGroup group = profile.getGroup(groupID);
                        if (group != null)
                        {
                            if (group.getMember(userID) != null)
                            {
                                if (!group.removeMember(userID))
                                {
                                    sendMessageToClient(player, "error.group.user.remove");
                                }
                                else
                                {
                                    sendProfileToClient((EntityPlayerMP) player, profileID);
                                }
                            }
                            else
                            {
                                sendMessageToClient(player, "error.group.user.not.found");
                            }
                        }
                        else
                        {
                            sendMessageToClient(player, "error.group.not.found");
                        }
                    }
                    else
                    {
                        sendMessageToClient(player, "error.profile.access.invalid");
                    }
                }
                else
                {
                    sendMessageToClient(player, "error.profile.not.found");
                }
            }
            else if (id == CREATE_GROUP)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                String groupID = ByteBufUtils.readUTF8String(data());
                String parentID = ByteBufUtils.readUTF8String(data());

                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID);
                if (profile != null)
                {
                    if (profile.containsUser(player) && profile.hasNode(player, Permissions.profileAddGroup.toString()))
                    {
                        AccessGroup group = new AccessGroup(groupID);
                        if (profile.getGroup(groupID) == null)
                        {
                            //add group
                            profile.addGroup(group);

                            //Parent handled seperatly
                            AccessGroup parentGroup = profile.getGroup(parentID);
                            if (parentGroup != null)
                            {
                                if (group.setToExtend(parentGroup))
                                {
                                    sendMessageToClient(player, "group.parent.set");
                                }
                                else if (group.isParent(parentGroup))
                                {
                                    sendMessageToClient(player, "error.group.parent.set.recursive");
                                }
                                else
                                {
                                    sendMessageToClient(player, "error.group.parent.set");
                                }
                            }
                            else
                            {
                                sendMessageToClient(player, "error.group.parent.not.found");
                            }

                            sendProfileToClient(player, profileID);
                        }
                        else
                        {
                            sendMessageToClient(player, "error.group.add.exists");
                        }
                    }
                    else
                    {
                        sendMessageToClient(player, "error.profile.access.invalid");
                    }
                }
                else
                {
                    sendMessageToClient(player, "error.profile.not.found");
                }
            }
            else if (id == UPDATE_GROUP_PARENT)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                String groupID = ByteBufUtils.readUTF8String(data());
                String parentID = ByteBufUtils.readUTF8String(data());

                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID);
                if (profile != null)
                {
                    if (profile.containsUser(player) && profile.hasNode(player, Permissions.groupSetting.toString()))
                    {
                        AccessGroup group = profile.getGroup(groupID);
                        if (group != null)
                        {
                            if (parentID.trim() == "")
                            {
                                group.setToExtend(null);
                                sendMessageToClient(player, "group.parent.set");
                                sendProfileToClient(player, profileID);
                            }
                            else
                            {
                                AccessGroup parentGroup = profile.getGroup(parentID);
                                if (parentGroup != null)
                                {
                                    if (group.setToExtend(parentGroup))
                                    {
                                        sendMessageToClient(player, "group.parent.set");
                                        sendProfileToClient(player, profileID);
                                    }
                                    else if (group.isParent(parentGroup))
                                    {
                                        sendMessageToClient(player, "error.group.parent.set.recursive");
                                    }
                                    else
                                    {
                                        sendMessageToClient(player, "error.group.parent.set");
                                    }
                                }
                                else
                                {
                                    sendMessageToClient(player, "error.group.parent.not.found");
                                }
                            }
                        }
                        else
                        {
                            sendMessageToClient(player, "error.group.not.found");
                        }
                    }
                    else
                    {
                        sendMessageToClient(player, "error.profile.access.invalid");
                    }
                }
                else
                {
                    sendMessageToClient(player, "error.profile.not.found");
                }
            }
            else if (id == CREATE_PROFILE)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                boolean defaults = data().readBoolean();
                GlobalAccessProfile profile = GlobalAccessSystem.createProfile(profileID, defaults);
                profile.getOwnerGroup().addMember(player);
                sendProfilesToClient((EntityPlayerMP) player);
            }
            else if (id == REMOVE_PROFILE)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID);
                if (profile != null)
                {
                    if (profile.containsUser(player) && profile.canDelete(player))
                    {
                        GlobalAccessSystem.removeProfile(profile, player);
                        sendProfilesToClient((EntityPlayerMP) player);
                    }
                    else
                    {
                        sendMessageToClient(player, "error.profile.access.invalid");
                    }
                }
                else
                {
                    sendMessageToClient(player, "error.profile.not.found");
                }
            }
            else if (id == ADD_NODE_TO_GROUP)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                String groupID = ByteBufUtils.readUTF8String(data());
                String node = ByteBufUtils.readUTF8String(data());

                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID);
                if (profile != null)
                {
                    if (profile.containsUser(player) && profile.hasNode(player, Permissions.groupPermissionAdd.toString()))
                    {
                        AccessGroup group = profile.getGroup(groupID);
                        if (group != null)
                        {
                            if (group.hasExactNode(node))
                            {
                                group.addNode(node);
                                sendProfileToClient(player, profileID);
                            }
                            else
                            {
                                sendMessageToClient(player, "error.node.exists");
                            }
                        }
                        else
                        {
                            sendMessageToClient(player, "error.group.not.found");
                        }
                    }
                    else
                    {
                        sendMessageToClient(player, "error.profile.access.invalid");
                    }
                }
                else
                {
                    sendMessageToClient(player, "error.profile.not.found");
                }
            }
            else if (id == REMOVE_NODE_FROM_GROUP)
            {
                String profileID = ByteBufUtils.readUTF8String(data());
                String groupID = ByteBufUtils.readUTF8String(data());
                String node = ByteBufUtils.readUTF8String(data());

                GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID);
                if (profile != null)
                {
                    if (profile.containsUser(player) && profile.hasNode(player, Permissions.groupPermissionRemove.toString()))
                    {
                        AccessGroup group = profile.getGroup(groupID);
                        if (group != null)
                        {
                            if (group.hasExactNode(node))
                            {
                                group.removeNode(node);
                                sendProfileToClient(player, profileID);
                            }
                            else
                            {
                                sendMessageToClient(player, "error.node.not.found");
                            }
                        }
                        else
                        {
                            sendMessageToClient(player, "error.group.not.found");
                        }
                    }
                    else
                    {
                        sendMessageToClient(player, "error.profile.access.invalid");
                    }
                }
                else
                {
                    sendMessageToClient(player, "error.profile.not.found");
                }
            }
        }
    }

    public static void sendMessageToClient(EntityPlayer player, String message)
    {
        PacketGui packetGui = new PacketGui(5);
        ByteBufUtils.writeUTF8String(packetGui.data(), "" + message);
        Engine.packetHandler.sendToPlayer(packetGui, (EntityPlayerMP) player);
    }

    public static void sendProfilesToClient(EntityPlayerMP player)
    {
        PacketGui packetGui = new PacketGui(0);

        List<GlobalAccessProfile> profileList = GlobalAccessSystem.getProfilesFor(player);
        packetGui.data().writeInt(profileList.size());
        for (GlobalAccessProfile profile : profileList)
        {
            //We only want to send the bare minimal to function
            packetGui.write(profile.getName());
            packetGui.write(profile.getID());
            packetGui.write(profile.getUserAccess(player).hasNode(Permissions.profileView)); //Disables view option
        }

        Engine.packetHandler.sendToPlayer(packetGui, player);
    }

    public static void sendProfileToClient(EntityPlayerMP player, String profileID)
    {
        GlobalAccessProfile profile = GlobalAccessSystem.getProfile(profileID); //TODO send to all players
        if (profile != null) //TODO check if player can view profile
        {
            IPacket packetGui = new PacketGui(1).write(SaveManager.generateSaveData(profile));
            Engine.packetHandler.sendToPlayer(packetGui, player);
        }
        else
        {
            PacketGui packetGui = new PacketGui(5);
            ByteBufUtils.writeUTF8String(packetGui.data(), "error.profile.not.found");
            Engine.packetHandler.sendToPlayer(packetGui, player);
        }
    }

    /**
     * Called to clear the user off of all update
     * trackers for GUI packets.
     *
     * @param player - player to remove
     */
    public static void clearGui(EntityPlayer player)
    {
        for (GlobalAccessProfile profile : GlobalAccessSystem.getProfiles())
        {
            if (profile != null)
            {
                profile.playersWithSettingsGUIOpen.remove(player);
                //TODO send packet to client
            }
        }
    }

    /**
     * Called to download a list of profiles for the user
     */
    public static void doRequest()
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(REQUEST_ALL_PROFILES));
    }

    /**
     * Called to request data about the profile
     *
     * @param profileID - profile to download
     */
    public static void doRequest(String profileID)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(REQUEST_PROFILE).write(profileID));
    }

    /**
     * Called every so often to remind the server that a
     * player has a GUI open with the profile.
     * <p>
     * This needs to be sent so that the GUI will be updated
     * with current data.
     *
     * @param profileID - current profile opened by player
     */
    public static void keepAlive(String profileID)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(KEEP_ALIVE).write(profileID));
    }

    /**
     * Called to remove a user from a group
     *
     * @param profileID - profile to access
     * @param group     - group to access
     * @param userName  - user to remove
     */
    public static void removeUser(String profileID, String group, String userName)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(REMOVE_USER_FROM_GROUP).write(profileID).write(group).write(userName));
    }

    /**
     * Called to add a user to a group
     *
     * @param profileID - profile to access
     * @param group     - group to access
     * @param userName  - user to add
     */
    public static void addUser(String profileID, String group, String userName)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(ADD_USER_TO_GROUP).write(profileID).write(group).write(userName));
    }

    /**
     * Called to create a new profile
     *
     * @param name     - name of the group to use, a new ID will be made
     * @param defaults - should the group be generated with default values
     */
    public static void createProfile(String name, boolean defaults)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(CREATE_PROFILE).write(name).write(defaults));
    }

    /**
     * Called to remove a profile
     * <p>
     * Server does validate bad requests
     *
     * @param id - unique string to find the profile
     */
    public static void removeProfile(String id)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(REMOVE_PROFILE).write(id));
    }


    /**
     * Called to create a new group
     *
     * @param profile - profile to access
     * @param name    - group to access
     * @param parent  - group to extend
     */
    public static void createGroup(String profile, String name, String parent)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(CREATE_GROUP).write(profile).write(name).write(parent));
    }

    /**
     * Called to change the group's parent group
     *
     * @param profile - profile to access
     * @param name    - group to access
     * @param parent  - new value
     */
    public static void updateGroupParent(String profile, String name, String parent)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(UPDATE_GROUP_PARENT).write(profile).write(name).write(parent));
    }

    /**
     * Called to remove a group from a profile.
     *
     * @param profile         - id of the profile to remove the group from
     * @param name            - name to ID the group
     * @param pullUpSubGroups - should the sub groups be updated to use the group's parent in place of the removed group
     */
    public static void removeGroup(String profile, String name, boolean pullUpSubGroups)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(REMOVE_GROUP).write(profile).write(name).write(pullUpSubGroups));
    }

    public static void removeNodeFromGroup(String profileID, String group, String node)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(REMOVE_NODE_FROM_GROUP).write(profileID).write(group).write(node));
    }

    public static void addNodeToGroup(String profileID, String group, String node)
    {
        Engine.packetHandler.sendToServer(new PacketAccessGui(ADD_NODE_TO_GROUP).write(profileID).write(group).write(node));
    }
}
