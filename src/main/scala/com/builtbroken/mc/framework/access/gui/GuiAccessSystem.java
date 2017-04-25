package com.builtbroken.mc.framework.access.gui;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.gui.packets.PacketAccessGui;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.GuiScrollBar;
import com.builtbroken.mc.prefab.gui.screen.GuiScreenBase;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Global access system
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2017.
 */
public class GuiAccessSystem extends GuiScreenBase implements IPacketIDReceiver
{
    public static int groupRowSpacingY = 16;
    public static int groupRows = 14;
    public static int profileRows = 10;

    GuiButton2 refreshButton;
    GuiButton2[] profileButtons;
    GuiButton2[][] groupButtons;

    GuiScrollBar profileScrollBar;
    GuiScrollBar groupScrollBar;

    AccessProfile currentProfile;

    String[] profileNames = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    String[] profileIDs = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    String errorMessage = "";

    long lastKeepAlivePacket = 0L;

    int currentProfileIndex = -1;

    @Override
    public void initGui()
    {
        super.initGui();
        //TODO add favorite option to profiles so they sort to top
        //TODO add search bar
        errorMessage = "";

        refreshButton = GuiImageButton.newRefreshButton(0, width - 20, 2);
        buttonList.add(refreshButton);

        //Profile scroll bar
        profileScrollBar = new GuiScrollBar(4, 104, 40, 200, profileNames.length - profileRows);
        buttonList.add(profileScrollBar);
        groupScrollBar = new GuiScrollBar(5, 250, 40, 200, 0);
        buttonList.add(groupScrollBar);

        reloadProfileList();
        reloadGroupList();
    }

    protected void reloadProfileList()
    {
        //TODO ensure index matches

        //Reset visiblity
        if (profileButtons != null)
        {
            for (GuiButton2 button2 : profileButtons)
            {
                button2.visible = false;
                button2.enable();
            }
        }
        //Generate array if missing
        else
        {
            profileButtons = new GuiButton2[profileRows];
        }
        if (profileNames != null)
        {
            for (int i = 0; i < profileButtons.length; i++)
            {
                if (profileButtons[i] == null || !buttonList.contains(profileButtons[i]))
                {
                    profileButtons[i] = new GuiButton2(10 + i, 2, 40 + (i * 20), 100, 20, "");
                    profileButtons[i].visible = false;
                    buttonList.add(profileButtons[i]);
                }

                if (i < profileNames.length)
                {
                    profileButtons[i].displayString = profileNames[i + profileScrollBar.getCurrentScroll()];
                    profileButtons[i].visible = true;
                }
                else
                {
                    profileButtons[i].visible = false;
                }
            }
        }
        profileScrollBar.setMaxScroll(profileNames.length - profileRows);
    }

    protected void reloadGroupList()
    {
        if (groupButtons != null)
        {
            for (GuiButton2[] buttons : groupButtons)
            {
                for (GuiButton2 button2 : buttons)
                {
                    button2.visible = false;
                }
            }
        }
        if (currentProfile != null && currentProfile.getGroups().size() > 0)
        {
            if (groupButtons == null)
            {
                groupButtons = new GuiButton2[groupRows][3];
            }
            int y = 50;
            int x = 220;
            for (int i = 0; i < groupButtons.length; i++)
            {
                if (groupButtons[i][0] == null || !buttonList.contains(groupButtons[i][0]))
                {
                    groupButtons[i][0] = GuiButton9px.newBlankButton(20 + i, x, y + (i * groupRowSpacingY));
                    groupButtons[i][0].visible = false;
                    buttonList.add(groupButtons[i][0]);
                }
                if (groupButtons[i][1] == null || !buttonList.contains(groupButtons[i][1]))
                {
                    groupButtons[i][1] = GuiButton9px.newBlankButton(30 + i, x + 9, y + (i * groupRowSpacingY));
                    groupButtons[i][1].visible = false;
                    buttonList.add(groupButtons[i][1]);
                }
                if (groupButtons[i][2] == null || !buttonList.contains(groupButtons[i][2]))
                {
                    groupButtons[i][2] = GuiButton9px.newBlankButton(40 + i, x + 9 * 2, y + (i * groupRowSpacingY));
                    groupButtons[i][2].visible = false;
                    buttonList.add(groupButtons[i][2]);
                }
                if (i < currentProfile.getGroups().size())
                {
                    groupButtons[i][0].visible = true;
                    groupButtons[i][1].visible = true;
                    groupButtons[i][2].visible = true;
                }
            }
        }
        groupScrollBar.setMaxScroll(currentProfile != null ? (currentProfile.getGroups().size() - groupRows) : 0);
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
        //Refresh profile
        else if (id == 1)
        {
            PacketAccessGui.doRequest(profileIDs[currentProfileIndex]);
        }
        ///Del profile
        else if (id == 3)
        {
            //TODO if more than one person owns a profile then del acts as a remove self button
        }
        ///New profile
        else if (id == 3)
        {

        }
        //profile scroll bar
        else if (id == 4)
        {
            reloadProfileList();
        }
        //group scroll bar
        else if (id == 5)
        {
            reloadGroupList();
        }
        else if (id >= 10 && id < 20)
        {
            int index = id - 10;
            if (currentProfileIndex != -1)
            {
                profileButtons[currentProfileIndex].enable();
            }
            if (currentProfileIndex != index && profileButtons[index] != null)
            {
                profileButtons[index].disable();
                currentProfileIndex = index;
                loadProfile();
            }
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

    protected void loadProfile()
    {
        if (profileIDs != null && currentProfileIndex >= 0 && currentProfileIndex < profileIDs.length)
        {
            currentProfile = null;
            PacketAccessGui.doRequest(profileIDs[currentProfileIndex]);
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

        if (currentProfile != null)
        {
            int y = 0;
            for (AccessGroup group : currentProfile.getGroups())
            {
                this.drawString(this.fontRendererObj, "G[" + y + "]: " + group.getName(), 130, 50 + (y++ * groupRowSpacingY), 16777215);
            }
        }

        //Set texture and reset color
        this.mc.renderEngine.bindTexture(SharedAssets.GUI_COMPONENTS_BARS);
        float c = 192f / 255f;
        GL11.glColor4f(c, c, c, 1.0F);


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
