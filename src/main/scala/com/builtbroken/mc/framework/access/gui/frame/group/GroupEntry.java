package com.builtbroken.mc.framework.access.gui.frame.group;

import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.GuiComponentContainer;
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
        reloadGroupList();
        updatePositions();
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

        final int x = xPosition + width - 12 * 3;
        groupButtons[0].xPosition = x;
        groupButtons[1].xPosition = x + 12;
        groupButtons[2].xPosition = x + 12 * 2;

        groupButtons[0].yPosition = yPosition;
        groupButtons[1].yPosition = yPosition;
        groupButtons[2].yPosition = yPosition;
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        //Edit group
        if (id == 0)
        {

        }
        //Edit nodes
        else if (id == 1)
        {

        }
        //Edit users
        else if (id == 2)
        {

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
            groupButtons[0] = add(GuiButton9px.newBlankButton(0, xPosition, yPosition));
        }
        if (groupButtons[1] == null)
        {
            groupButtons[1] = add(GuiButton9px.newBlankButton(1, xPosition, yPosition));
        }
        if (groupButtons[2] == null)
        {
            groupButtons[2] = add(GuiButton9px.newBlankButton(2, xPosition, yPosition));
        }
        updatePositions();
    }
}
