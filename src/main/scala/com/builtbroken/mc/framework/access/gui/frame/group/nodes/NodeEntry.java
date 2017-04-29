package com.builtbroken.mc.framework.access.gui.frame.group.nodes;

import com.builtbroken.mc.framework.access.Permissions;
import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.packets.PacketAccessGui;
import com.builtbroken.mc.prefab.gui.buttons.GuiButton9px;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.GuiComponentContainer;
import com.builtbroken.mc.prefab.gui.components.dialog.GuiYesNo;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class NodeEntry extends GuiComponentContainer<NodeEntry>
{
    public String node;
    public String group;
    private GuiImageButton removeNodeButton;

    private GuiYesNo yesNoDialog;

    public NodeEntry(int id, int x, int y)
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
        this.drawString(mc.fontRenderer, "" + node, this.xPosition + 5, this.yPosition + 1, DEFAULT_STRING_COLOR);
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        //Edit group
        if (id == 0)
        {
            yesNoDialog = new GuiYesNo(-1, getParentComponent().x(), getParentComponent().y(), "Remove Node Dialog", "Do you want to remove node '" + node + "' from group '" + group + "'?");
            yesNoDialog.setParentComponent(this);
            ((GuiAccessSystem) getHost()).loadCenterFrame(yesNoDialog, true);
        }
        //Callback from yes/no dialog
        else if (yesNoDialog == button)
        {
            if (((GuiYesNo) button).state == 0)
            {
                PacketAccessGui.removeNode(((GuiAccessSystem) getHost()).currentProfile.getID(), group, node);
            }
            ((GuiAccessSystem) getHost()).loadCenterFrame(((GuiYesNo) button).lastOpenedFrame, false);
        }
        else
        {
            super.actionPerformed(button);
        }
    }

    @Override
    protected void update(Minecraft mc, int mouseX, int mouseY)
    {
        super.update(mc, mouseX, mouseY);
        removeNodeButton.setEnabled(((GuiAccessSystem) getHost()).getPlayer().hasNode(Permissions.groupPermissionRemove));
    }

    protected void reloadGroupList()
    {
        if (removeNodeButton == null)
        {
            removeNodeButton = add(GuiButton9px.newOffButton(0, 0, 0));
            removeNodeButton.setRelativePosition(new HugXSide(this, -9, false));
        }
        updatePositions();
    }
}
