package com.builtbroken.mc.framework.access.global.gui.frame.main;

import com.builtbroken.mc.framework.access.global.gui.GuiAccessSystem;
import com.builtbroken.mc.prefab.gui.components.CallbackGuiArray;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class ProfileArrayCallback extends CallbackGuiArray
{
    public final GuiAccessSystem gui;

    public ProfileArrayCallback(GuiAccessSystem gui)
    {
        this.gui = gui;
    }

    @Override
    public String getEntryName(int index)
    {
        return gui.profileNames != null && index < gui.profileNames.length ? gui.profileNames[index] : "...";
    }

    @Override
    public boolean isEnabled(int index)
    {
        return gui.currentProfileIndex != index;
    }

    @Override
    public void onPressed(int index)
    {
        gui.loadProfile(index);
    }

    @Override
    protected int getEntryWidth()
    {
        return 100;
    }

    @Override
    public int getSize()
    {
        return gui.profileNames != null ? gui.profileNames.length : 0;
    }
}
