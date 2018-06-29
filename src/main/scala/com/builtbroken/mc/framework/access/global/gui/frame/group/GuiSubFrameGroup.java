package com.builtbroken.mc.framework.access.global.gui.frame.group;

import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.global.gui.frame.GuiSubFrameAccess;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.prefab.gui.components.GuiLabel;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.pos.GuiRelativePos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Prefab for any frame that is a sub frame for the group system
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public abstract class GuiSubFrameGroup<E extends GuiSubFrameGroup> extends GuiSubFrameAccess<E>
{
    public final String groupID;
    public final GuiFrameCenter frameCenter;

    protected GuiLabel groupNameLabel;

    public GuiSubFrameGroup(GuiFrameCenter frameCenter, String groupID, int id, int x, int y)
    {
        super(frameCenter.getHost(), id, x, y);
        this.frameCenter = frameCenter;
        this.groupID = groupID;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        groupNameLabel = add(new GuiLabel(0, 0, "Name: " + groupID));
        groupNameLabel.setRelativePosition(new GuiRelativePos(this, 20, 5));
        groupNameLabel.setWidth(100);
        groupNameLabel.setHeight(10);
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

        //Reset color
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    protected void doRender(Minecraft mc, AccessGroup group, int mouseX, int mouseY)
    {

    }

    public AccessGroup getGroup()
    {
        return getHost().currentProfile != null ? getHost().currentProfile.getGroup(groupID) : null;
    }

    public boolean canEditGroup()
    {
        return getGroup() != null && getHost().currentProfile.canEdit() && getGroup().canEdit();
    }
}
