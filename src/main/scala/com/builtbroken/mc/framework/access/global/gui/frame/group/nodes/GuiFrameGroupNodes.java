package com.builtbroken.mc.framework.access.global.gui.frame.group.nodes;

import com.builtbroken.mc.framework.access.perm.Permissions;
import com.builtbroken.mc.framework.access.global.gui.frame.group.GuiGroupFrame;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.framework.access.global.packets.PacketAccessGui;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.components.GuiArray;
import com.builtbroken.mc.prefab.gui.components.GuiField;
import com.builtbroken.mc.prefab.gui.pos.GuiRelativePos;
import com.builtbroken.mc.prefab.gui.pos.HugBottom;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroupNodes extends GuiGroupFrame<GuiFrameGroupNodes>
{
    public String[] nodes;

    public static int rowSpacingY = 10;
    public static int rows = 15;

    public GuiArray groupArray;
    public GuiField nodeField;
    public GuiButton2 addButton;

    public GuiFrameGroupNodes(GuiFrameCenter parent, String groupID, int id, int x, int y)
    {
        super(parent, groupID, id, x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if (getGroup() != null)
        {
            groupArray = add(new GuiArray(new NodeArrayCallback(this), 1, 0, 0, rows, rowSpacingY));
            groupArray.setRelativePosition(new GuiRelativePos(this, 0, 20));
            groupArray.setWidth(200);

            nodeField = add(new GuiField(0, 0));
            nodeField.setRelativePosition(new HugBottom(this, 1, -41, true));
            nodeField.setRelativeSize(new GuiRelativeSize(this, -55, 20).setUseHostHeight(false));

            addButton = add(new GuiButton2(2, 0, 0, "Add"));
            addButton.setWidth(50);
            addButton.setRelativePosition(new HugBottom(this, -addButton.getWidth(), -40, false));
        }
        updatePositions();
    }

    @Override
    protected void update(Minecraft mc, int mouseX, int mouseY)
    {
        super.update(mc, mouseX, mouseY);
        if (getGroup() == null || nodes == null || nodes != null && getGroup().getNodes().size() != nodes.length) //TODO check if exact match
        {
            if (getGroup() != null)
            {
                nodes = new String[getGroup().getNodes().size()];
                int i = 0;
                for (String node : getGroup().getNodes())
                {
                    nodes[i++] = node;
                }
            }
            else
            {
                nodes = null;
            }
            groupArray.reloadEntries();
        }

        if (getGroup() != null)
        {
            addButton.setEnabled(getPlayer().hasNode(Permissions.groupPermissionAdd));
        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        if (id == 2)
        {
            if (nodeField.getText() != null && !nodeField.getText().isEmpty())
            {
                PacketAccessGui.addNode(getHost().currentProfile.getID(), groupID, nodeField.getText());
            }
        }
        else
        {
            super.actionPerformed(button);
        }
    }
}
