package com.builtbroken.mc.framework.access.gui.frame.group.user;

import com.builtbroken.mc.framework.access.AccessUser;
import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.group.GuiGroupFrame;
import com.builtbroken.mc.prefab.gui.components.GuiArray;
import com.builtbroken.mc.prefab.gui.pos.GuiRelativePos;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroupUsers extends GuiGroupFrame<GuiFrameGroupUsers>
{
    public static int rowSpacingY = 10;
    public static int rows = 15;

    public GuiArray groupArray;
    public String[] users;

    public GuiFrameGroupUsers(GuiAccessSystem parent, String groupID, int id, int x, int y)
    {
        super(parent, groupID, id, x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if (getGroup() != null)
        {
            users = new String[getGroup().getMembers().size()];
            int i = 0;
            for (AccessUser user : getGroup().getMembers())
            {
                users[i++] = user.getName();
            }
            groupArray = add(new GuiArray(new UserArrayCallback(this), 5, 0, 0, rows, rowSpacingY));
            groupArray.setRelativePosition(new GuiRelativePos(this, 0, 20));
            groupArray.setWidth(200);
        }
        updatePositions();
    }
}
