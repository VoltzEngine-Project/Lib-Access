package com.builtbroken.mc.framework.access.global.gui.frame.group.edit;

import com.builtbroken.mc.framework.access.global.gui.frame.group.GuiSubFrameGroup;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.framework.access.global.packets.PacketAccessGui;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.components.GuiField;
import com.builtbroken.mc.prefab.gui.components.GuiLabel;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroupSettings extends GuiSubFrameGroup<GuiFrameGroupSettings>
{
    public GuiField groupParentField;
    public GuiButton2 groupParentFieldSave;

    public GuiFrameGroupSettings(GuiFrameCenter parent, String groupID, int id, int x, int y)
    {
        super(parent, groupID, id, x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if (getGroup() != null)
        {
            int y = 30;

            GuiLabel parentFieldLabel = add(new GuiLabel(0, 0, "Parent:"));
            parentFieldLabel.setComponentWidth(40);
            parentFieldLabel.setComponentHeight(10);
            parentFieldLabel.setRelativePosition(new HugXSide(this, 2, true).setYOffset(y + 5));

            groupParentField = add(new GuiField(0, 0));
            groupParentField.setText(getGroup() != null && getGroup().getExtendGroupName() != null ? getGroup().getExtendGroupName() : "");
            groupParentField.setRelativePosition(new HugXSide(this, parentFieldLabel.getWidth() + 5, true).setYOffset(y + 1));
            groupParentField.setRelativeSize(new GuiRelativeSize(this, -55 - parentFieldLabel.getWidth(), 18).setUseHostHeight(false));

            groupParentFieldSave = add(new GuiButton2(1, 0, 0, "Set"));
            groupParentFieldSave.setComponentWidth(50);
            groupParentFieldSave.setRelativePosition(new HugXSide(this, -groupParentFieldSave.getWidth(), false).setYOffset(y));
        }
        updatePositions();
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        int id = button.id;
        if (id == 1)
        {
            if (getHost().currentProfile != null)
            {
                PacketAccessGui.updateGroupParent(getHost().currentProfile.getID(), groupID, "" + groupParentField.getText());
            }
            else
            {
                getHost().errorMessage = "error.profile.active.null";
            }
        }
    }

    @Override
    protected void update(Minecraft mc, int mouseX, int mouseY)
    {
        super.update(mc, mouseX, mouseY);
        if (!groupParentField.isFocused() && groupParentField.timeSinceFocused() > 3e+9)
        {
            groupParentField.setText(getGroup() != null && getGroup().getExtendGroupName() != null ? getGroup().getExtendGroupName() : "");
        }
    }
}
