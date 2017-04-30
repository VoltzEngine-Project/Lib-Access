package com.builtbroken.mc.framework.access.global.gui.frame.group.edit;

import com.builtbroken.mc.framework.access.global.gui.frame.group.GuiSubFrameGroup;
import com.builtbroken.mc.framework.access.global.gui.frame.main.GuiFrameCenter;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.components.GuiField;
import com.builtbroken.mc.prefab.gui.components.GuiLabel;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;
import net.minecraft.client.Minecraft;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameGroupSettings extends GuiSubFrameGroup<GuiFrameGroupSettings>
{
    public GuiField nodeField;
    public GuiButton2 saveButton;

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
            parentFieldLabel.setWidth(40);
            parentFieldLabel.setHeight(10);
            parentFieldLabel.setRelativePosition(new HugXSide(this, 2, true).setYOffset(y + 5));

            nodeField = add(new GuiField(0, 0));
            nodeField.setText(getGroup() != null && getGroup().getExtendGroupName() != null ? getGroup().getExtendGroupName() : "");
            nodeField.setRelativePosition(new HugXSide(this, parentFieldLabel.getWidth() + 5, true).setYOffset(y + 1));
            nodeField.setRelativeSize(new GuiRelativeSize(this, -55 - parentFieldLabel.getWidth(), 18).setUseHostHeight(false));

            saveButton = add(new GuiButton2(2, 0, 0, "Set"));
            saveButton.setWidth(50);
            saveButton.setRelativePosition(new HugXSide(this, -saveButton.getWidth(), false).setYOffset(y));
        }
        updatePositions();
    }

    @Override
    protected void update(Minecraft mc, int mouseX, int mouseY)
    {
        super.update(mc, mouseX, mouseY);
        if(!nodeField.isFocused())
        {
            nodeField.setText(getGroup() != null && getGroup().getExtendGroupName() != null ? getGroup().getExtendGroupName() : "");
        }
    }
}
