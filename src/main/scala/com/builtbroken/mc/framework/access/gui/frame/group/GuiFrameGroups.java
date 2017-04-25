package com.builtbroken.mc.framework.access.gui.frame.group;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.prefab.gui.components.GuiArray;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroups extends GuiFrame<GuiFrameGroups>
{
    public static int groupRowSpacingY = 10;
    public static int groupRows = 14;

    GuiAccessSystem parent;

    public GuiArray groupArray;

    public GuiFrameGroups(GuiAccessSystem parent, int id, int x, int y)
    {
        super(id, x, y);
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        groupArray = add(new GuiArray(new GroupArrayCallback(parent), 5, 130, 50, groupRows, groupRowSpacingY));
        groupArray.setWidth(200);
    }
}
