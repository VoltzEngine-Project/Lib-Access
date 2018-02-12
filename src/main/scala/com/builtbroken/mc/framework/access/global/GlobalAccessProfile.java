package com.builtbroken.mc.framework.access.global;

import com.builtbroken.mc.api.IVirtualObject;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.lib.helper.NBTUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.File;
import java.util.HashMap;

/**
 * Separate version from the {@link AccessProfile} to support saving profiles outside of a single object.
 * Design specifically to handle the global permission system but can be used for anything.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/30/2017.
 */
public class GlobalAccessProfile extends AccessProfile implements IVirtualObject
{
    public static final String SAVE_FOLDER = NBTUtility.BBM_FOLDER + "access/profiles/";

    /** Players who currently have a GUI open looking at this access profile */
    public final HashMap<EntityPlayer, Long> playersWithSettingsGUIOpen = new HashMap();

    /** Save file by which this was loaded. Not currently used but stored if ever needed. */
    protected File saveFile;

    /** Display name of the profile for the user to easily read */
    protected String profileName = "";

    /**
     * Only used by global profiles that have no defined container. Defaults to localHost defining
     * the profile as non-global
     */
    protected String profileID = "LocalHost";

    public GlobalAccessProfile()
    {
        //Need default for save manager
    }

    /**
     * Sets the names
     *
     * @param name
     * @param id
     * @return
     */
    public AccessProfile initName(String name, String id)
    {
        this.profileName = name;
        this.profileID = id;
        return this;
    }

    @Override
    public void onProfileUpdate()
    {
        super.onProfileUpdate();
        //TODO trigger update packet
    }

    @Override
    public File getSaveFile()
    {
        if (this.saveFile == null)
        {
            this.saveFile = new File(NBTUtility.getSaveDirectory(MinecraftServer.getServer().getFolderName()), getPathToProfile(this.getID()));
        }
        return this.saveFile;
    }

    public static String getPathToProfile(String name)
    {
        return SAVE_FOLDER + name + ".dat";
    }

    @Override
    public void setSaveFile(File file)
    {
        this.saveFile = file;

    }

    @Override
    public boolean shouldSaveForWorld(World world)
    {
        return world != null && world.provider.dimensionId == 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        super.load(nbt);
        this.profileName = nbt.getString("name");
        this.profileID = nbt.getString("profileID");
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setString("name", this.profileName);
        nbt.setString("profileID", this.profileID);
        return super.save(nbt);
    }

    /**
     * Display name of the profile
     */
    public String getName()
    {
        return this.profileName;
    }

    /**
     * Save/Global id of the profie
     */
    public String getID()
    {
        return this.profileID;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[" + profileID + ", " + profileName + "] - groups: " + groups.size();
    }
}
