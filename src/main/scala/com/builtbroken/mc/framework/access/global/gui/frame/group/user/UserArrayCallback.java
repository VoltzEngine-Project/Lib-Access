package com.builtbroken.mc.framework.access.global.gui.frame.group.user;

import com.builtbroken.mc.prefab.gui.components.CallbackGuiArray;
import com.builtbroken.mc.prefab.gui.components.GuiComponent;
import net.minecraft.client.Minecraft;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class UserArrayCallback extends CallbackGuiArray
{
    public final GuiFrameGroupUsers gui;

    public UserArrayCallback(GuiFrameGroupUsers gui)
    {
        this.gui = gui;
    }

    @Override
    protected GuiComponent newEntry(int index, int buttonID, int x, int y)
    {
        return new UserEntry(gui, buttonID, x, y);
    }

    @Override
    public String getEntryName(int index)
    {
        String username;
        if (gui.users != null && index < gui.users.length)
        {
            username = gui.users[index];
            if (username.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()))
            {
                username += "  [You]";
            }
        }
        else
        {
            username = "User[" + index + "]";
        }
        return username;
    }

    @Override
    public void updateEntry(int index, GuiComponent buttonEntry)
    {
        buttonEntry.displayString = getEntryName(index);
        if (buttonEntry instanceof UserEntry)
        {
            ((UserEntry) buttonEntry).group = gui.groupID;
            ((UserEntry) buttonEntry).userName = getEntryName(index);
        }
    }

    @Override
    public boolean isEnabled(int index)
    {
        if (gui.users != null && index < gui.users.length)
        {
            return !gui.users[index].equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()); //TODO prevent removing higher permission users than current user
        }
        return false;
    }

    @Override
    public void onPressed(int index)
    {

    }

    @Override
    protected int getEntryWidth()
    {
        return 100;
    }

    @Override
    public int getSize()
    {
        return gui.users != null ? gui.users.length : 0;
    }
}
