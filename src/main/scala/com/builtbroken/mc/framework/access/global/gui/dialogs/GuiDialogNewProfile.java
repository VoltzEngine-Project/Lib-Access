package com.builtbroken.mc.framework.access.global.gui.dialogs;

import com.builtbroken.mc.framework.access.global.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.global.packets.PacketAccessGui;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiButtonCheck;
import com.builtbroken.mc.prefab.gui.buttons.GuiLeftRightArrowButton;
import com.builtbroken.mc.prefab.gui.components.GuiField;
import com.builtbroken.mc.prefab.gui.components.dialog.GuiDialog;
import com.builtbroken.mc.prefab.gui.pos.GuiRelativePos;
import com.builtbroken.mc.prefab.gui.pos.HugBottom;
import net.minecraft.client.gui.GuiButton;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiDialogNewProfile extends GuiDialog<GuiDialogNewProfile>
{
    public GuiField profileNameField;
    public GuiButtonCheck defaultCheckBox;
    public GuiLeftRightArrowButton backButton;
    public GuiButton2 saveButton;
    public GuiButton2 cancelButton;

    public GuiDialogNewProfile(int id, int x, int y)
    {
        super(id, x, y);
        setComponentWidth(300);
        setComponentHeight(200);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        backButton = add(new GuiLeftRightArrowButton(0, 0, 0, true));
        backButton.setRelativePosition(new GuiRelativePos(this, 2, 2));

        profileNameField = add(new GuiField(0, 0)).setRelativePosition(new GuiRelativePos(this, 1, 30));
        profileNameField.setComponentWidth(140);

        defaultCheckBox = add(new GuiButtonCheck(1, 0, 0, 0, true)).setRelativePosition(new GuiRelativePos(this, 70, 70));

        saveButton = (GuiButton2) add(new GuiButton2(2, new HugBottom(this, 5, -25, true), "Save")).setComponentWidth(50);
        cancelButton = (GuiButton2) add(new GuiButton2(0, new HugBottom(this, -55, -25, false), "Cancel")).setComponentWidth(50);

        updatePositions();
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        if (id == 0 || id == 2)
        {
            if (id == 2)
            {
                PacketAccessGui.createProfile(profileNameField.getText(), defaultCheckBox.isChecked());
            }
            ((GuiAccessSystem) getHost()).loadCenterFrame(lastOpenedFrame, false);
            getHost().remove(this);
        }
    }
}
