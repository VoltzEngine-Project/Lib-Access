package com.builtbroken.mc.framework.access.gui.frame.group;

import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.AccessUser;
import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.GuiSubFrameAccess;
import net.minecraft.client.Minecraft;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiGroupFrame<E extends GuiGroupFrame> extends GuiSubFrameAccess<E>
{
    public final String groupID;

    public GuiGroupFrame(GuiAccessSystem parent, String groupID, int id, int x, int y)
    {
        super(parent, id, x, y);
        this.groupID = groupID;
    }

    @Override
    protected void doRender(Minecraft mc, int mouseX, int mouseY)
    {
        super.doRender(mc, mouseX, mouseY);
        AccessGroup group = getGroup();
        if (group != null)
        {
            doRender(mc, group, mouseX, mouseY);
        }
        else
        {
            drawCenteredString(mc.fontRenderer, "Error: could not load group", x() + (width / 2), y() + (height / 2), Color.red.getRGB());
        }
    }

    public AccessGroup getGroup()
    {
        return getHost().currentProfile != null ? getHost().currentProfile.getGroup(groupID) : null;
    }

    public AccessUser getPlayer()
    {
        return getHost().getPlayer();
    }

    protected void doRender(Minecraft mc, AccessGroup group, int mouseX, int mouseY)
    {
        drawString(mc.fontRenderer, "Name: " + group.getName(), x() + 20, y() + 5, DEFAULT_STRING_COLOR);
    }
}
