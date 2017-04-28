package com.builtbroken.mc.framework.access.gui.frame.group.edit;

import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.group.GuiGroupFrame;
import net.minecraft.client.Minecraft;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroupSettings extends GuiGroupFrame<GuiFrameGroupSettings>
{
    public GuiFrameGroupSettings(GuiAccessSystem parent, String groupID, int id, int x, int y)
    {
        super(parent, groupID, id, x, y);
    }

    @Override
    protected void doRender(Minecraft mc, AccessGroup group, int mouseX, int mouseY)
    {
        super.doRender(mc, group, mouseX, mouseY);
        if (group != null)
        {
            drawString(mc.fontRenderer, "Parent: " + group.getExtendGroupName(), x() + 20, y() + 15, DEFAULT_STRING_COLOR);
            drawCenteredString(mc.fontRenderer, "No current settings to edit", x() + (width / 2), y() + (height / 2), DEFAULT_STRING_COLOR);
        }
    }
}
