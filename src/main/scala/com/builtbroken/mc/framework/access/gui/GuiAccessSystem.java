package com.builtbroken.mc.framework.access.gui;

import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.AccessUser;
import com.builtbroken.mc.framework.access.gui.dialogs.GuiDialogNewProfile;
import com.builtbroken.mc.framework.access.gui.frame.group.main.GuiFrameGroups;
import com.builtbroken.mc.framework.access.gui.packets.PacketAccessGui;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.GuiArray;
import com.builtbroken.mc.prefab.gui.components.GuiComponent;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.screen.GuiScreenBase;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

/**
 * Global access system
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2017.
 */
public class GuiAccessSystem extends GuiScreenBase implements IPacketIDReceiver
{
    public static int profileRows = 12;

    public GuiButton2 refreshButton;
    public GuiButton2 newProfile;

    public GuiArray profileArray;

    public AccessProfile currentProfile;

    public String[] profileNames;
    public String[] profileIDs;

    public String errorMessage = "";

    public long lastKeepAlivePacket = 0L;

    public int currentProfileIndex = -1;

    public GuiFrame currentFrame;

    public GuiFrameGroups groupsFrame;

    @Override
    public void initGui()
    {
        super.initGui();
        //TODO add favorite option to profiles so they sort to top
        //TODO add search bar
        errorMessage = "";

        //Menu buttons
        refreshButton = add(GuiImageButton.newRefreshButton(0, width - 20, 2));
        newProfile = add(GuiImageButton.newButtonEmpty(1, 20, 2));

        //Profile array
        profileArray = add(new GuiArray(new ProfileArrayCallback(this), 4, 2, 40, profileRows, 20));
        profileArray.setWidth(100 + 9);

        //Group frame
        groupsFrame = add(new GuiFrameGroups(this, 5, 120, 40));
        groupsFrame.hide();
        groupsFrame.initGui();

        reloadProfileList();
        reloadGroupList();
    }

    public void reloadProfileList()
    {
        currentProfile = null;
        currentProfileIndex = -1;
        loadFrame(null, false);
        profileArray.reloadEntries();
    }

    protected void reloadGroupList()
    {
        groupsFrame.groupArray.reloadEntries();
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        //TODO add new button
        //TODO get list of profiles the user can edit

        //Refresh profile list
        if (id == 0)
        {
            reloadProfileList();
            PacketAccessGui.doRequest();
        }
        ///New profile
        else if (id == 1)
        {
            GuiDialogNewProfile guiDialogNewProfile = add(new GuiDialogNewProfile(2, 120, 40));
            loadFrame(guiDialogNewProfile, false);
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        if (currentProfile != null && System.currentTimeMillis() - lastKeepAlivePacket > 1000)
        {
            PacketAccessGui.keepAlive(currentProfile.getID());
            lastKeepAlivePacket = System.currentTimeMillis();
        }
    }

    /**
     * Called to load a profile
     *
     * @param index - index of the profile in the {@link #profileNames} array
     */
    public void loadProfile(int index)
    {
        currentProfileIndex = index;
        currentProfile = null;
        if (profileIDs != null && currentProfileIndex >= 0 && currentProfileIndex < profileIDs.length)
        {
            loadFrame(groupsFrame, false);
            PacketAccessGui.doRequest(profileIDs[currentProfileIndex]);
        }
        else
        {
            currentProfileIndex = -1;
        }
    }

    /**
     * Called to load a frame to display on the right side of the GUI
     *
     * @param frame     - frame to load
     * @param addReturn - should the last frame be stored for return
     */
    public void loadFrame(GuiFrame frame, boolean addReturn)
    {
        if (frame != null)
        {
            GuiFrame previousOpenedFrame = currentFrame;
            if (previousOpenedFrame != null)
            {
                remove(previousOpenedFrame);
                previousOpenedFrame.hide();
            }
            currentFrame = frame;
            if (!buttonList.contains(currentFrame))
            {
                add(currentFrame);
            }
            currentFrame.initGui();
            currentFrame.updatePositions();
            currentFrame.show();
            if (addReturn)
            {
                currentFrame.lastOpenedFrame = previousOpenedFrame;
            }
        }
        else if (currentFrame != null)
        {
            currentFrame.hide();
            currentFrame.lastOpenedFrame = null;
            remove(currentFrame);
            currentFrame = null;
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Global Access Permission System", this.width / 2, 3, 16777215);


        ///====================================================
        Color a = new Color(122, 122, 122, 143);
        Color b = new Color(122, 122, 122, 143);
        this.drawGradientRect(0, 0, 114, this.height, a.getRGB(), b.getRGB());

        if (profileNames != null)
        {
            String name = "";
            String id = "";
            if (currentProfileIndex >= 0 && currentProfileIndex < profileNames.length)
            {
                name = currentProfile != null ? currentProfile.getName() : profileNames[currentProfileIndex];
                id = currentProfile != null ? currentProfile.getID() : profileIDs[currentProfileIndex];
            }
            this.drawString(this.fontRendererObj, "Profile: " + name, 130, 20, 16777215);
            this.drawString(this.fontRendererObj, "ID: " + id, 130, 30, 16777215);


            //============================================================
            //Debug message
            if (errorMessage != null && !errorMessage.trim().isEmpty())
            {
                if (errorMessage.startsWith("error"))
                {
                    this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal(errorMessage), this.width / 2, this.height / 2, Color.RED.getRGB());
                }
                else
                {
                    this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal(errorMessage), this.width / 2, this.height / 2, GuiComponent.DEFAULT_STRING_COLOR);
                }
            }
            else if (currentProfileIndex != -1)
            {
                if (currentProfile == null)
                {
                    this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal("gui.access.waiting.on.server"), this.width / 2, this.height / 2, GuiComponent.DEFAULT_STRING_COLOR);
                }
            }
            else
            {
                this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal("gui.access.select.profile"), this.width / 2, this.height / 2, GuiComponent.DEFAULT_STRING_COLOR);
            }
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal("gui.access.click.refresh"), this.width / 2, this.height / 2, GuiComponent.DEFAULT_STRING_COLOR);
        }

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        //Read profile list
        if (id == 0)
        {
            int n = buf.readInt();
            profileIDs = new String[n];
            profileNames = new String[n];
            for (int i = 0; i < n; i++)
            {
                profileNames[i] = ByteBufUtils.readUTF8String(buf);
                profileIDs[i] = ByteBufUtils.readUTF8String(buf);
                buf.readBoolean();
            }
            reloadProfileList();
            return true;
        }
        //Read profile
        else if (id == 1)
        {
            if (currentProfile == null)
            {
                currentProfile = new AccessProfile(ByteBufUtils.readTag(buf));
            }
            else
            {
                currentProfile.load(ByteBufUtils.readTag(buf));
            }
            reloadGroupList();
            return true;
        }
        else if (id == 5)
        {
            errorMessage = ByteBufUtils.readUTF8String(buf);
            return true;
        }
        return false;
    }

    /**
     * Gets the local player's access entry from the current profile
     *
     * @return
     */
    public AccessUser getPlayer()
    {
        if (currentProfile != null)
        {
            AccessUser user = currentProfile.getUserAccess(Minecraft.getMinecraft().thePlayer);
            if (user != null)
            {
                return user;
            }
        }
        return new AccessUser(Minecraft.getMinecraft().thePlayer).setTempary(true);
    }
}
