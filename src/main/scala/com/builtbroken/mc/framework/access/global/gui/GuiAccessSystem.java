package com.builtbroken.mc.framework.access.global.gui;

import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.AccessUser;
import com.builtbroken.mc.framework.access.global.gui.dialogs.GuiDialogNewProfile;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameEvents;
import com.builtbroken.mc.framework.access.global.gui.frame.main.ProfileArrayCallback;
import com.builtbroken.mc.framework.access.global.packets.PacketAccessGui;
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

    public GuiFrame leftFrame;
    public GuiFrame centerFrame;
    public GuiFrame rightFrame;

    public GuiFrameCenter defaultCenterFrame;

    @Override
    public void initGui()
    {
        super.initGui();
        //TODO add favorite option to profiles so they sort to top
        //TODO add search bar
        errorMessage = "";

        //Menu buttons
        refreshButton = add(GuiImageButton.newRefreshButton(0, 2, 16));
        newProfile = (GuiButton2) add(new GuiButton2(1, 20, 16, "New Profile").setWidth(60).setHeight(18));

        int sideWidths = 109;
        //Profile array
        profileArray = add(new GuiArray(new ProfileArrayCallback(this), -1, 2, 39, profileRows, 20));
        profileArray.setWidth(sideWidths);

        int remainingWidth = this.width - sideWidths * 2 - 4;
        defaultCenterFrame = new GuiFrameCenter(this, 109 + 4, 15);
        defaultCenterFrame.setWidth(remainingWidth);
        defaultCenterFrame.setHeight(this.height - 15);

        rightFrame = add(new GuiFrameEvents(this, defaultCenterFrame.x() + defaultCenterFrame.getWidth(), 15));
        rightFrame.setWidth(sideWidths);
        rightFrame.setHeight(this.height - 15);

        reloadProfileList();
        reloadGroupList();
    }

    public void reloadProfileList()
    {
        currentProfile = null;
        currentProfileIndex = -1;
        loadCenterFrame(defaultCenterFrame, false);
        profileArray.reloadEntries();
    }

    protected void reloadGroupList()
    {
        defaultCenterFrame.reloadGroupList();
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
            loadCenterFrame(guiDialogNewProfile, false);
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

        if (centerFrame == null)
        {
            loadCenterFrame(defaultCenterFrame, false);
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
            loadCenterFrame(defaultCenterFrame, false);
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
    public void loadCenterFrame(GuiFrame frame, boolean addReturn)
    {
        if (frame != null)
        {
            GuiFrame previousOpenedFrame = centerFrame;
            if (previousOpenedFrame != null)
            {
                previousOpenedFrame.hide();
                remove(previousOpenedFrame);
            }
            centerFrame = frame;
            if (!buttonList.contains(centerFrame))
            {
                add(centerFrame);
            }
            centerFrame.initGui();
            centerFrame.updatePositions();
            centerFrame.show();
            if (addReturn)
            {
                centerFrame.lastOpenedFrame = previousOpenedFrame;
            }
        }
        else if (centerFrame != null)
        {
            centerFrame.hide();
            centerFrame.lastOpenedFrame = null;
            remove(centerFrame);
            centerFrame = null;
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.drawDefaultBackground();

        ///====================================================
        Color a = new Color(122, 122, 122, 143);
        Color b = new Color(122, 122, 122, 143);
        this.drawGradientRect(0, 15, 109 + 4, this.height, a.getRGB(), b.getRGB());
        this.drawVerticalLine(109 + 3, 14, this.height, Color.BLACK.getRGB());

        //Header bar render
        this.drawGradientRect(0, 0, this.width, 15, a.getRGB(), b.getRGB());
        this.drawRect(0, 14, this.width, 15, Color.BLACK.getRGB());
        this.drawCenteredString(this.fontRendererObj, "Global Access Permission System", this.width / 2, 3, 16777215);



        String name = "";
        String id = "";
        if (profileNames != null && currentProfileIndex >= 0 && currentProfileIndex < profileNames.length)
        {
            name = currentProfile != null ? currentProfile.getName() : profileNames[currentProfileIndex];
            id = currentProfile != null ? currentProfile.getID() : profileIDs[currentProfileIndex];
        }
        this.drawString(this.fontRendererObj, "Profile: " + name, 122, 20, 16777215);
        this.drawString(this.fontRendererObj, "ID: " + id, 122, 30, 16777215);

        if (profileNames != null)
        {
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
        return new AccessUser(Minecraft.getMinecraft().thePlayer).setTemporary(true);
    }
}
