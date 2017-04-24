package com.builtbroken.mc.framework.access.gui;

import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.gui.packets.PacketAccessGui;
import com.builtbroken.mc.imp.transform.region.Rectangle;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Global access system
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2017.
 */
public class GuiAccessSystem extends GuiScreen implements IPacketIDReceiver
{
    protected HashMap<Rectangle, String> tooltips = new HashMap();
    protected ArrayList<GuiTextField> fields = new ArrayList();

    GuiButton2 refreshButton;
    GuiButton2[] profileButtons;

    int currentProfileIndex = -1;
    AccessProfile currentProfile;

    String[] profileNames = new String[]{"One", "Two", "There"};
    String[] profileIDs = new String[]{"0", "1", "3"};

    long lastUpdate = 0L;

    String errorMessage = "";


    @Override
    public void initGui()
    {
        errorMessage = "";
        this.buttonList.clear();
        this.fields.clear();
        tooltips.clear();

        refreshButton = GuiImageButton.newRefreshButton(0, width - 20, 2);
        buttonList.add(refreshButton);
        createProfileList();
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    protected void createProfileList()
    {
        profileButtons = new GuiButton2[10];
        for (int i = 0; i < 10 && i < profileNames.length; i++)
        {
            profileButtons[i] = new GuiButton2(10 + i, 5, 40 + (i * 20), 100, 20, profileNames[i]);
            buttonList.add(profileButtons[i].setEnabled(i != currentProfileIndex));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
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
        //Save profile
        else if (id == 2)
        {

        }
        ///New profile
        else if (id == 3)
        {

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
        if (currentProfile != null && System.currentTimeMillis() - lastUpdate > 1000)
        {
            PacketAccessGui.keepAlive(currentProfile.getID());
            lastUpdate = System.currentTimeMillis();
        }
    }

    protected void loadProfile()
    {
        initGui();
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
                this.drawString(this.fontRendererObj, "G[" + y + "]: " + group.getName(), 130, 50 + (y++ * 20), 16777215);
            }
        }

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
            int prev_n = profileIDs.length;
            int n = buf.readInt();
            profileIDs = new String[n];
            profileNames = new String[n];
            for (int i = 0; i < n; i++)
            {
                profileNames[i] = ByteBufUtils.readUTF8String(buf);
                profileIDs[i] = ByteBufUtils.readUTF8String(buf);
                buf.readBoolean();
            }
            if (prev_n != n)
            {
                //TODO ensure index matches
                for (GuiButton2 button2 : profileButtons)
                {
                    buttonList.remove(button2);
                }
                createProfileList();
            }
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
