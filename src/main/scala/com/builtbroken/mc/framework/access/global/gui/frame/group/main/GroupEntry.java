package com.builtbroken.mc.framework.access.global.gui.frame.group.main;

import com.builtbroken.mc.framework.access.perm.Permissions;
import com.builtbroken.mc.framework.access.global.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.global.gui.frame.group.edit.GuiFrameGroupSettings;
import com.builtbroken.mc.framework.access.global.gui.frame.group.nodes.GuiFrameGroupNodes;
import com.builtbroken.mc.framework.access.global.gui.frame.group.user.GuiFrameGroupUsers;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.GuiComponentContainer;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GroupEntry extends GuiComponentContainer<GroupEntry>
{
    public final GuiFrameCenter frameCenter;
    public String groupID;
    private GuiImageButton[] groupButtons;

    public GroupEntry(GuiFrameCenter frameCenter, int id, int x, int y)
    {
        super(id, x, y, 190, 10, "");
        this.frameCenter = frameCenter;
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
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        if (((GuiAccessSystem) getHost()).centerFrame instanceof GuiFrameCenter)
        {
            GuiFrameCenter guiFrameCenter = (GuiFrameCenter) ((GuiAccessSystem) getHost()).centerFrame;
            //Edit group
            if (id == 0)
            {
                guiFrameCenter.show(new GuiFrameGroupSettings(frameCenter, groupID, -1, 0, 0)
                        .setRelativePosition(guiFrameCenter.centerFramePos)
                        .setRelativeSize(new GuiRelativeSize(frameCenter.groupsFrame, 0, 0)));
                return;
            }
            //Edit nodes
            else if (id == 1)
            {
                guiFrameCenter.show(new GuiFrameGroupNodes(frameCenter, groupID, -1, 0, 0)
                        .setRelativePosition(guiFrameCenter.centerFramePos)
                        .setRelativeSize(new GuiRelativeSize(frameCenter.groupsFrame, 0, 0)));
                return;
            }
            //Edit users
            else if (id == 2)
            {
                guiFrameCenter.show(new GuiFrameGroupUsers(frameCenter, groupID, -1, 0, 0)
                        .setRelativePosition(guiFrameCenter.centerFramePos)
                        .setRelativeSize(new GuiRelativeSize(frameCenter.groupsFrame, 0, 0)));
                return;
            }
        }
        super.actionPerformed(button);
    }

    @Override
    protected void update(Minecraft mc, int mouseX, int mouseY)
    {
        super.update(mc, mouseX, mouseY);

        groupButtons[0].setEnabled(((GuiAccessSystem) getHost()).getPlayer().hasNode(Permissions.groupSetting));
        groupButtons[1].setEnabled(
                ((GuiAccessSystem) getHost()).getPlayer().hasNode(Permissions.groupPermissionAdd)
                        || ((GuiAccessSystem) getHost()).getPlayer().hasNode(Permissions.groupPermissionRemove));
        groupButtons[2].setEnabled(
                ((GuiAccessSystem) getHost()).getPlayer().hasNode(Permissions.groupUserPermissionAdd)
                        || ((GuiAccessSystem) getHost()).getPlayer().hasNode(Permissions.groupUserPermissionRemove));
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
