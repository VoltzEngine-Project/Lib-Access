package com.builtbroken.mc.framework.access.global;

import com.builtbroken.mc.framework.access.AccessProfile;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * Seperate version from the base {@link AccessProfile} that contains additional settings and features.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2017.
 */
public class GlobalAccessProfile extends AccessProfile
{
    /** Players who currently have a GUI open looking at this access profile */
    public final HashMap<EntityPlayer, Long> playersWithSettingsGUIOpen = new HashMap();

    public GlobalAccessProfile()
    {

    }

    @Override
    public void onProfileUpdate()
    {
        super.onProfileUpdate();
        //TODO trigger update packet
    }

    @Override
    public boolean isGlobal()
    {
        return true;
    }
}
