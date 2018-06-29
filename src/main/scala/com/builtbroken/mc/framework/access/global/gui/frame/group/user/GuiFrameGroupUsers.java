package com.builtbroken.mc.framework.access.global.gui.frame.group.user;

import com.builtbroken.mc.framework.access.AccessUser;
import com.builtbroken.mc.framework.access.global.gui.frame.group.GuiSubFrameGroup;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.framework.access.global.packets.PacketAccessGui;
import com.builtbroken.mc.framework.access.perm.Permission;
import com.builtbroken.mc.framework.access.perm.Permissions;
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
public class GuiFrameGroupUsers extends GuiSubFrameGroup<GuiFrameGroupUsers>
{
    public static int rowSpacingY = 10;
    public static int rows = 15;

    public GuiArray groupArray;
    public GuiField userNameField;
    public GuiButton2 addButton;
    public String[] users;

    public GuiFrameGroupUsers(GuiFrameCenter parent, String groupID, int id, int x, int y)
    {
        super(parent, groupID, id, x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if (getGroup() != null)
        {
            groupArray = add(new GuiArray(new UserArrayCallback(this), 1, 0, 0, rows, rowSpacingY));
            groupArray.setRelativePosition(new GuiRelativePos(this, 0, 20));
            groupArray.setWidth(200);

            userNameField = add(new GuiField(0, 0));
            userNameField.setRelativePosition(new HugBottom(this, 1, -41, true));
            userNameField.setRelativeSize(new GuiRelativeSize(this, -55, 20).setUseHostHeight(false));

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
        if (getGroup() == null || users == null || users != null && getGroup().getMembers().size() != users.length) //TODO check if exact match
        {
            if (getGroup() != null)
            {
                users = new String[getGroup().getMembers().size()];
                int i = 0;
                for (AccessUser user : getGroup().getMembers())
                {
                    users[i++] = user.getName();
                }
            }
            else
            {
                users = null;
            }
            groupArray.reloadEntries();
        }

        if (getGroup() != null)
        {
            addButton.setEnabled(hasNodes(Permissions.groupUserAdd));
        }
    }

    protected boolean hasNodes(Permission... nodes)
    {
        return getHost().doesPlayerHavePerms(nodes);
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        if (id == 2)
        {
            if (userNameField.getText() != null && !userNameField.getText().isEmpty())
            {
                PacketAccessGui.addUser(getHost().currentProfile.getID(), groupID, userNameField.getText());
            }
        }
        else
        {
            super.actionPerformed(button);
        }
    }
}
