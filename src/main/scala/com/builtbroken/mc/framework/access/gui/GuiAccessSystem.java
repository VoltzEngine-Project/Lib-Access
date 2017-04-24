package com.builtbroken.mc.framework.access.gui;

import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.access.gui.packets.PacketRequestData;
import com.builtbroken.mc.imp.transform.region.Rectangle;
import com.builtbroken.mc.prefab.gui.GuiButton2;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Global access system
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/24/2017.
 */
public class GuiAccessSystem extends GuiScreen implements IPacketIDReceiver
{
    protected HashMap<Rectangle, String> tooltips = new HashMap();
    protected ArrayList<GuiTextField> fields = new ArrayList();

    GuiButton2 refreshButton;

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.fields.clear();
        tooltips.clear();

        refreshButton = GuiImageButton.newRefreshButton(0, 10, 10);
        buttonList.add(refreshButton);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        int id = button.id;
        //TODO add new button
        //TODO get list of profiles the user can edit
        if (id == 0)
        {
            PacketRequestData.doRequest(); //TODO keep track of last click to prevent abuse
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("menu.game", new Object[0]), this.width / 2, 40, 16777215);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        return false;
    }
}
