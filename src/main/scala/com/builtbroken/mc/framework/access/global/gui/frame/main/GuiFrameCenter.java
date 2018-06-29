package com.builtbroken.mc.framework.access.global.gui.frame.main;

import com.builtbroken.jlib.data.vector.IPos2D;
import com.builtbroken.mc.framework.access.global.gui.GuiAccessSystem;
import com.builtbroken.mc.framework.access.global.gui.frame.GuiFrameAccess;
import com.builtbroken.mc.framework.access.global.gui.frame.group.main.GuiFrameGroups;
import com.builtbroken.mc.framework.access.global.packets.PacketAccessGui;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mc.prefab.gui.buttons.GuiLeftRightArrowButton;
import com.builtbroken.mc.prefab.gui.components.dialog.GuiYesNo;
import com.builtbroken.mc.prefab.gui.components.frame.GuiFrame;
import com.builtbroken.mc.prefab.gui.pos.HugXSide;
import com.builtbroken.mc.prefab.gui.pos.size.GuiRelativeSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2017.
 */
public class GuiFrameCenter extends GuiFrameAccess<GuiFrameCenter>
{
    public static final int BUTTON_REFRESH = 0;
    public static final int BUTTON_GROUPS = 1;
    public static final int BUTTON_ENTITY = 2;
    public static final int BUTTON_MACHINE = 3;
    public static final int BUTTON_USER = 4;
    public static final int BUTTON_COPY = 5;
    public static final int BUTTON_DELETE = 6;

    protected GuiFrame currentFrame;
    private GuiYesNo yesNoDialog;

    public GuiFrameGroups groupsFrame;
    public IPos2D centerFramePos;

    public static Color backgroundColor = new Color(85, 85, 85);

    private GuiImageButton refreshButton;
    private GuiImageButton deleteButton;

    public GuiFrameCenter(GuiAccessSystem parent, int x, int y)
    {
        super(parent, -1, x, y);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int yLevel = 25;
        add(refreshButton = GuiImageButton.newRefreshButton(BUTTON_REFRESH, 352, 17));
        add(deleteButton = GuiImageButton.newTrashCanButton(BUTTON_DELETE, 352, 37));
        add(new GuiButton2(BUTTON_GROUPS, new HugXSide(this, 0, true).setYOffset(yLevel), "Groups").setWidth(50));
        add(new GuiButton2(BUTTON_ENTITY, new HugXSide(this, 50, true).setYOffset(yLevel), "Entity").setWidth(50).disable());
        add(new GuiButton2(BUTTON_MACHINE, new HugXSide(this, 100, true).setYOffset(yLevel), "Machines").setWidth(50).disable());
        add(new GuiButton2(BUTTON_USER, new HugXSide(this, 150, true).setYOffset(yLevel), "Users").setWidth(50).disable());
        add(new GuiLeftRightArrowButton(BUTTON_COPY, 300, 24, false));

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

        if (getHost().currentProfile != null)
        {
            refreshButton.enable();
            refreshButton.show();
            if (getHost().currentProfile.canDelete(Minecraft.getMinecraft().thePlayer))
            {
                deleteButton.enable();
                deleteButton.show();
            }
            else
            {
                deleteButton.disable();
                deleteButton.hide();
            }
        }
        else
        {
            refreshButton.disable();
            refreshButton.hide();
            deleteButton.disable();
            deleteButton.hide();
        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        int id = button.id;
        //TODO add new button
        //TODO get list of profiles the user can edit

        //Refresh profile list
        if (id == BUTTON_REFRESH)
        {
            if (getHost().currentProfile != null)
            {
                reloadGroupList();
                PacketAccessGui.doRequest(getHost().currentProfile.getID());
            }
        }
        else if (id == BUTTON_GROUPS)
        {
            if (currentFrame != groupsFrame)
            {
                show(groupsFrame);
            }
        }
        else if (id == BUTTON_COPY)
        {
            try
            {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();
                StringSelection strSel = new StringSelection(getHost().currentProfile.getID());
                clipboard.setContents(strSel, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (id == BUTTON_DELETE)
        {
            if (getHost().currentProfile != null)
            {
                yesNoDialog = new GuiYesNo(-1, x(), y(), "Remove Profile Dialog",
                        "Delete profile '" + getHost().currentProfile.getName() + "' and all connected data?");
                yesNoDialog.setRelativePosition(centerFramePos);
                yesNoDialog.setRelativeSize(new GuiRelativeSize(groupsFrame, 0, 0));
                show(yesNoDialog);
                yesNoDialog.setParentComponent(this);
            }
        }
        //Callback from yes/no dialog
        else if (yesNoDialog == button)
        {
            if (getHost().currentProfile != null)
            {
                if (((GuiYesNo) button).state == 0)
                {
                    PacketAccessGui.removeProfile(getHost().currentProfile.getID());
                }

                //Refresh entire GUI
                getHost().initGui();
            }
        }
        else
        {
            super.actionPerformed(button);
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
