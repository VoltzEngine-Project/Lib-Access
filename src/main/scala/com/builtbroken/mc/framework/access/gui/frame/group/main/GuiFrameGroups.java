package com.builtbroken.mc.framework.access.gui.frame.group.main;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.GuiFrameAccess;
import com.builtbroken.mc.prefab.gui.components.GuiArray;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroups extends GuiFrameAccess<GuiFrameGroups>
{
    public static int groupRowSpacingY = 10;
    public static int groupRows = 17;

    public GuiArray groupArray;

    public GuiFrameGroups(GuiAccessSystem parent, int id, int x, int y)
    {
        super(parent, id, x, y);
        this.setWidth(200);
        this.setHeight(200);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        groupArray = add(new GuiArray(new GroupArrayCallback(getHost()), 5, 0, 0, groupRows, groupRowSpacingY));
        groupArray.setRelativePosition(new HugXSide(this, 0, true));
        groupArray.setWidth(200);
    }
}
