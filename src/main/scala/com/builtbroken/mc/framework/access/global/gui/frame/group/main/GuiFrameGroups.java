package com.builtbroken.mc.framework.access.global.gui.frame.group.main;

import com.builtbroken.mc.framework.access.global.gui.frame.GuiFrameAccess;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.prefab.gui.components.GuiArray;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroups extends GuiFrameAccess<GuiFrameGroups>
{
    public static int groupRowSpacingY = 10;
    public static int groupRows = 17;

    public GuiFrameCenter frameCenter;
    public GuiArray groupArray;

    public GuiFrameGroups(GuiFrameCenter parent, int id, int x, int y)
    {
        super(parent.getHost(), id, x, y);
        this.frameCenter = parent;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        groupArray = add(new GuiArray(new GroupArrayCallback(frameCenter), 5, 0, 0, groupRows, groupRowSpacingY));
        groupArray.setRelativePosition(new HugXSide(this, 0, true));
        groupArray.setRelativeSize(new GuiRelativeSize(this, 0, 0));
    }

    @Override
    protected Color getBackgroundColor()
    {
        return enableDebug ? Color.CYAN : null;
    }
}
