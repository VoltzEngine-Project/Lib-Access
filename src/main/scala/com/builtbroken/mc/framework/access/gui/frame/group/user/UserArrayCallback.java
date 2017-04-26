package com.builtbroken.mc.framework.access.gui.frame.group.user;

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
        return new UserEntry(buttonID, x, y);
    }

    @Override
    public String getEntryName(int index)
    {
        return "User[" + index + "]";
    }

    @Override
    public void updateEntry(int index, GuiComponent buttonEntry)
    {
        buttonEntry.displayString = getEntryName(index);
        if (buttonEntry instanceof UserEntry)
        {
            ((UserEntry) buttonEntry).group = gui.groupID;
            ((UserEntry) buttonEntry).userName = gui.users[index];

        }
    }

    @Override
    public boolean isEnabled(int index)
    {
        //TODO check if user can edit user
        return !gui.users[index].equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
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
        return gui.users.length;
    }
}
