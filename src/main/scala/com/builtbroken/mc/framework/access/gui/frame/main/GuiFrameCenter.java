package com.builtbroken.mc.framework.access.gui.frame.main;

import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.GuiFrameAccess;
import com.builtbroken.mc.framework.access.gui.frame.group.main.GuiFrameGroups;
import com.builtbroken.mc.framework.access.gui.packets.PacketAccessGui;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import net.minecraft.client.gui.GuiButton;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2017.
 */
public class GuiFrameCenter extends GuiFrameAccess<GuiFrameCenter>
{
    protected GuiFrame currentFrame;
    public GuiFrameGroups groupsFrame;

    public GuiFrameCenter(GuiAccessSystem parent, int x, int y)
    {
        super(parent, -1, x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int yLevel = 25;
        add(GuiImageButton.newRefreshButton(0, 300, 24));
        add(new GuiButton2(1, new HugXSide(this, 0, true).setYOffset(yLevel), "Groups").setWidth(50));
        add(new GuiButton2(2, new HugXSide(this, 50, true).setYOffset(yLevel), "Entity").setWidth(50).disable());
        add(new GuiButton2(3, new HugXSide(this, 100, true).setYOffset(yLevel), "Machines").setWidth(50).disable());
        add(new GuiButton2(4, new HugXSide(this, 150, true).setYOffset(yLevel), "Users").setWidth(50).disable());
        //Group frame
        groupsFrame = add(new GuiFrameGroups(getHost(), -1, 0, 0)).setRelativePosition(new HugXSide(this, 0, true).setYOffset(yLevel + 22));
        loadFrame(groupsFrame, false);
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        //TODO add new button
        //TODO get list of profiles the user can edit

        //Refresh profile list
        if (id == 0)
        {
            reloadGroupList();
            PacketAccessGui.doRequest(getHost().currentProfile.getID());
        }
        else if (id == 1)
        {
            loadFrame(groupsFrame, false);
        }
    }

    public void reloadGroupList()
    {
        groupsFrame.groupArray.reloadEntries();
    }

    /**
     * Called to load a frame to display on the right side of the GUI
     *
     * @param frame     - frame to load
     * @param addReturn - should the last frame be stored for return
     */
    public void loadFrame(GuiFrame frame, boolean addReturn)
    {
        if (frame != null)
        {
            GuiFrame previousOpenedFrame = currentFrame;
            if (previousOpenedFrame != null)
            {
                previousOpenedFrame.hide();
                remove(previousOpenedFrame);
            }
            currentFrame = frame;
            if (!getComponents().contains(currentFrame))
            {
                add(currentFrame);
            }
            currentFrame.initGui();
            currentFrame.updatePositions();
            currentFrame.show();
            if (addReturn)
            {
                currentFrame.lastOpenedFrame = previousOpenedFrame;
            }
        }
        else if (currentFrame != null)
        {
            currentFrame.hide();
            currentFrame.lastOpenedFrame = null;
            remove(currentFrame);
            currentFrame = null;
        }
    }
}
