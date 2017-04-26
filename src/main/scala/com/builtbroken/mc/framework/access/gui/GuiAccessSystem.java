package com.builtbroken.mc.framework.access.gui;

import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.gui.dialogs.GuiDialogNewProfile;
import com.builtbroken.mc.framework.access.gui.frame.group.main.GuiFrameGroups;
import com.builtbroken.mc.framework.access.gui.packets.PacketAccessGui;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.GuiArray;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.screen.GuiScreenBase;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
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

    public String[] profileNames = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    public String[] profileIDs = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

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
            currentProfileIndex = -1;
            currentProfile = null;
            PacketAccessGui.doRequest(); //TODO keep track of last click to prevent abuse
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

    public void loadFrame(GuiFrame frame, boolean addReturn)
    {
        if (frame != null)
        {
            GuiFrame last = currentFrame;
            if (last != null)
            {
                last.hide();
            }
            currentFrame = frame;
            currentFrame.initGui();
            currentFrame.show();
            if (addReturn)
            {
                currentFrame.lastOpenedFrame = last;
            }
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
            this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal(errorMessage), this.width / 2, this.height / 2, Color.RED.getRGB());
        }
        else if (currentProfileIndex != -1)
        {
            if (currentProfile == null)
            {
                this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal("gui.access.waiting.on.server"), this.width / 2, this.height / 2, 16777215);
            }
        }
        else
        {
            this.drawCenteredString(this.fontRendererObj, LanguageUtility.getLocal("gui.access.select.profile"), this.width / 2, this.height / 2, 16777215);
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
}
