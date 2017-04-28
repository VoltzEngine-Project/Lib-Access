package com.builtbroken.mc.framework.access.gui.frame.group.main;

import com.builtbroken.mc.framework.access.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.prefab.gui.components.CallbackGuiArray;
import com.builtbroken.mc.prefab.gui.components.GuiComponent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GroupArrayCallback extends CallbackGuiArray
{
    public final GuiFrameCenter frameCenter;

    public GroupArrayCallback(GuiFrameCenter center)
    {
        this.frameCenter = center;
    }

    @Override
    protected GuiComponent newEntry(int index, int buttonID, int x, int y)
    {
        return new GroupEntry(frameCenter, buttonID, x, y);
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
            ((GroupEntry) buttonEntry).groupID = frameCenter.getHost().currentProfile != null ? frameCenter.getHost().currentProfile.getGroups().get(index).getName() : null;
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
        return frameCenter.getHost().currentProfile != null ? frameCenter.getHost().currentProfile.getGroups().size() : 0;
    }
}
