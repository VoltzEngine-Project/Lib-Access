package com.builtbroken.mc.framework.access.gui.frame.group;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.prefab.gui.components.CallbackGuiArray;
import com.builtbroken.mc.prefab.gui.components.GuiComponent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GroupArrayCallback extends CallbackGuiArray
{
    public final GuiAccessSystem gui;

    public GroupArrayCallback(GuiAccessSystem gui)
    {
        this.gui = gui;
    }

    @Override
    protected GuiComponent newEntry(int index, int buttonID, int x, int y)
    {
        return new GroupEntry(buttonID, x, y);
    }

    @Override
    public String getEntryName(int index)
    {
        return "Group[" + index + "]";
    }

    @Override
    public void updateEntry(int index, GuiComponent buttonEntry)
    {
        buttonEntry.displayString = getEntryName(index);
        if (buttonEntry instanceof GroupEntry)
        {
            ((GroupEntry) buttonEntry).groupID = gui.currentProfile != null ? gui.currentProfile.getGroups().get(index).getName() : null;
        }
    }

    @Override
    public boolean isEnabled(int index)
    {
        //TODO check if user can edit group
        return true;
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
        return gui.currentProfile != null ? gui.currentProfile.getGroups().size() : 0;
    }
}
