package com.builtbroken.mc.framework.access.gui.frame.group.main;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.group.edit.GuiFrameGroupSettings;
import com.builtbroken.mc.framework.access.gui.frame.group.nodes.GuiFrameGroupNodes;
import com.builtbroken.mc.framework.access.gui.frame.group.user.GuiFrameGroupUsers;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.GuiComponentContainer;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GroupEntry extends GuiComponentContainer<GroupEntry>
{
    public String groupID;
    private GuiImageButton[] groupButtons;

    public GroupEntry(int id, int x, int y)
    {
        super(id, x, y, 190, 10, "");
        resizeAsNeeded = false;
        reloadGroupList();
    }

    @Override
    protected Color getBackgroundColor()
    {
        return new Color(130, 130, 130);
    }

    @Override
    protected void doRender(Minecraft mc, int mouseX, int mouseY)
    {
        super.doRender(mc, mouseX, mouseY);
        this.drawString(mc.fontRenderer, "" + groupID, this.xPosition + 5, this.yPosition + 1, DEFAULT_STRING_COLOR);
    }

    @Override
    protected void updatePositions()
    {
        super.updatePositions();
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        //Edit group
        if (id == 0)
        {
            ((GuiAccessSystem) getHost()).loadFrame(new GuiFrameGroupSettings(((GuiAccessSystem) getHost()), groupID, 6, getParentComponent().xPosition, getParentComponent().yPosition), true);
        }
        //Edit nodes
        else if (id == 1)
        {
            ((GuiAccessSystem) getHost()).loadFrame(new GuiFrameGroupNodes(((GuiAccessSystem) getHost()), groupID, 6, getParentComponent().xPosition, getParentComponent().yPosition), true);
        }
        //Edit users
        else if (id == 2)
        {
            ((GuiAccessSystem) getHost()).loadFrame(new GuiFrameGroupUsers(((GuiAccessSystem) getHost()), groupID, 6, getParentComponent().xPosition, getParentComponent().yPosition), true);
        }
        else
        {
            super.actionPerformed(button);
        }
    }

    protected void reloadGroupList()
    {
        if (groupButtons == null)
        {
            groupButtons = new GuiImageButton[3];
        }

        if (groupButtons[0] == null)
        {
            groupButtons[0] = add(GuiButton9px.newGearButton(0, 0, 0));
            groupButtons[0].setRelativePosition(new HugXSide(this, -9 - 12 * 2, false));
        }
        if (groupButtons[1] == null)
        {
            groupButtons[1] = add(GuiButton9px.newNodeButton(1, 0, 0));
            groupButtons[1].setRelativePosition(new HugXSide(this, -9 - 12, false));
        }
        if (groupButtons[2] == null)
        {
            groupButtons[2] = add(GuiButton9px.newPlayerButton(2, 0, 0));
            groupButtons[2].setRelativePosition(new HugXSide(this, -9, false));
        }
        updatePositions();
    }
}
