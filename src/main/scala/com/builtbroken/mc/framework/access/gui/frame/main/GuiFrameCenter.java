package com.builtbroken.mc.framework.access.gui.frame.main;

import com.builtbroken.jlib.data.vector.IPos2D;
import com.builtbroken.mc.framework.access.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.gui.frame.GuiFrameAccess;
import com.builtbroken.mc.framework.access.gui.frame.group.main.GuiFrameGroups;
import com.builtbroken.mc.framework.access.gui.packets.PacketAccessGui;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2017.
 */
public class GuiFrameCenter extends GuiFrameAccess<GuiFrameCenter>
{
    protected GuiFrame currentFrame;
    public GuiFrameGroups groupsFrame;
    public IPos2D centerFramePos;

    public static Color backgroundColor = new Color(85, 85, 85);

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

        centerFramePos = new HugXSide(this, 0, true).setYOffset(yLevel + 22);
        //Group frame
        groupsFrame = add(new GuiFrameGroups(this, -1, 0, 0));
        groupsFrame.setRelativePosition(centerFramePos);
        groupsFrame.setRelativeSize(new GuiRelativeSize(this, 0, -centerFramePos.yi())); //TODO get y dynamically in case it changes
        show(groupsFrame);
    }

    @Override
    protected void update(Minecraft mc, int mouseX, int mouseY)
    {
        super.update(mc, mouseX, mouseY);
        int newHeight = getHost().height - y();
        if (getHeight() != newHeight)
        {
            setHeight(newHeight);
        }
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
            show(groupsFrame);
        }
    }

    public void show(GuiFrame frame)
    {
        if (frame != null)
        {
            if (!getComponents().contains(frame))
            {
                add(frame);
            }
            frame.show();
            frame.initGui();
            frame.updatePositions();
            if (currentFrame != null)
            {
                currentFrame.hide();
            }
            currentFrame = frame;
        }
    }

    @Override
    public void loadFrame(GuiFrame frame, boolean addReturn)
    {
        getHost().loadCenterFrame(frame, addReturn);
    }

    public void reloadGroupList()
    {
        groupsFrame.groupArray.reloadEntries();
    }

    @Override
    protected Color getBackgroundColor()
    {
        return enableDebug ? Color.YELLOW : backgroundColor;
    }
}
