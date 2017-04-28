package com.builtbroken.mc.framework.access.gui.frame.group.nodes;

import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.gui.frame.group.GuiGroupFrame;
import com.builtbroken.mc.framework.access.gui.frame.main.GuiFrameCenter;
import net.minecraft.client.Minecraft;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroupNodes extends GuiGroupFrame<GuiFrameGroupNodes>
{
    public GuiFrameGroupNodes(GuiFrameCenter parent, String groupID, int id, int x, int y)
    {
        super(parent, groupID, id, x, y);
    }

    @Override
    protected void doRender(Minecraft mc, AccessGroup group, int mouseX, int mouseY)
    {
        super.doRender(mc, group, mouseX, mouseY);
        if (group != null)
        {
            int y = 0;
            for (String node : group.getNodes())
            {
                drawString(10, 20 + (y++ * 10), node);
            }
        }
    }
}
