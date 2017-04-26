package com.builtbroken.mc.framework.access.gui.frame;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.prefab.gui.buttons.GuiLeftRightArrowButton;
import com.builtbroken.mc.prefab.gui.pos.GuiRelativePos;
import net.minecraft.client.gui.GuiButton;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiSubFrameAccess<E extends GuiSubFrameAccess> extends GuiFrameAccess<E>
{
    GuiLeftRightArrowButton backButton;

    public GuiSubFrameAccess(GuiAccessSystem parent, int id, int x, int y)
    {
        super(parent, id, x, y);
        parent.add(this);
        this.setWidth(200);
        this.setHeight(200);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        backButton = add(new GuiLeftRightArrowButton(0, 0, 0, true));
        backButton.setRelativePosition(new GuiRelativePos(this, 2, 2));

        updatePositions();
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        if (id == 0)
        {
            getHost().loadFrame(lastOpenedFrame, false);
            getHost().remove(this);
        }
    }
}
