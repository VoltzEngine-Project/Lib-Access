package com.builtbroken.mc.framework.access.gui.frame.group;

import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.AccessUser;
import com.builtbroken.mc.framework.access.gui.frame.GuiSubFrameAccess;
import com.builtbroken.mc.framework.access.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiGroupFrame<E extends GuiGroupFrame> extends GuiSubFrameAccess<E>
{
    public final String groupID;
    protected final GuiFrameCenter frameCenter;

    public GuiGroupFrame(GuiFrameCenter frameCenter, String groupID, int id, int x, int y)
    {
        super(frameCenter.getHost(), id, x, y);
        this.frameCenter = frameCenter;
        this.groupID = groupID;
    }

    @Override
    public void loadFrame(GuiFrame frame, boolean addReturn)
    {
        //Not used by group frame
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        if (id == 0)
        {
            frameCenter.show(frameCenter.groupsFrame);
            frameCenter.remove(this);
        }
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
            drawCenteredString(mc.fontRenderer, "Error: could not load group", x() + (getWidth() / 2), y() + (getHeight() / 2), Color.red.getRGB());
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
