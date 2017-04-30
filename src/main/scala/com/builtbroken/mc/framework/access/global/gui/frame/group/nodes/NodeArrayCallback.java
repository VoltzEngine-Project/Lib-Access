package com.builtbroken.mc.framework.access.global.gui.frame.group.nodes;

import com.builtbroken.mc.prefab.gui.components.CallbackGuiArray;
import com.builtbroken.mc.prefab.gui.components.GuiComponent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class NodeArrayCallback extends CallbackGuiArray
{
    public final GuiFrameGroupNodes gui;

    public NodeArrayCallback(GuiFrameGroupNodes gui)
    {
        this.gui = gui;
    }

    @Override
    protected GuiComponent newEntry(int index, int buttonID, int x, int y)
    {
        return new NodeEntry(buttonID, x, y);
    }

    @Override
    public String getEntryName(int index)
    {
        String username;
        if (gui.nodes != null && index < gui.nodes.length)
        {
            username = gui.nodes[index];
        }
        else
        {
            username = "Node[" + index + "]";
        }
        return username;
    }

    @Override
    public void updateEntry(int index, GuiComponent buttonEntry)
    {
        buttonEntry.displayString = getEntryName(index);
        if (buttonEntry instanceof NodeEntry)
        {
            ((NodeEntry) buttonEntry).group = gui.groupID;
            ((NodeEntry) buttonEntry).node = getEntryName(index);
        }
    }

    @Override
    public boolean isEnabled(int index)
    {
        return true;
    }

    @Override
    public int getSize()
    {
        return gui.nodes != null ? gui.nodes.length : 0;
    }
}
