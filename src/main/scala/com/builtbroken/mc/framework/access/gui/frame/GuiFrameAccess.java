package com.builtbroken.mc.framework.access.gui.frame;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/25/2017.
 */
public class GuiFrameAccess<E extends GuiFrameAccess> extends GuiFrame<E>
{
    public GuiFrameAccess(GuiAccessSystem parent, int id, int x, int y)
    {
        super(id, x, y);
        this.setHost(parent);
    }

    @Override
    public GuiAccessSystem getHost()
    {
        return (GuiAccessSystem) super.getHost();
    }

    /**
     * Called to load a frame to display on the right side of the GUI
     *
     * @param frame     - frame to load
     * @param addReturn - should the last frame be stored for return
     */
    public void loadFrame(GuiFrame frame, boolean addReturn)
    {
        getHost().loadCenterFrame(frame, addReturn);
    }
}
