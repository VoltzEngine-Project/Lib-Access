package com.builtbroken.mc.framework.access.gui.frame.main;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.GuiFrameAccess;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2017.
 */
public class GuiFrameEvents extends GuiFrameAccess<GuiFrameEvents>
{
    public static Color backgroundColor = new Color(60, 60, 60);

    public GuiFrameEvents(GuiAccessSystem parent, int x, int y)
    {
        super(parent, -1, x, y);
    }

    @Override
    protected Color getBackgroundColor()
    {
        return enableDebug ? Color.BLUE : backgroundColor;
    }
}
