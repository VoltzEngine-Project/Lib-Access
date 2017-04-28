package com.builtbroken.mc.framework.access.wrapper;

import com.builtbroken.mc.framework.access.AccessGroup;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.AccessUser;

import java.util.UUID;

/**
 * Used to ensure that permission calls use all groups and nodes possible.
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/28/2017.
 */
public class AccessUserMultiGroup extends AccessUser
{
    /** Primary user access, mainly used for ID and Username */
    private final AccessUser primary;
    private final AccessProfile profile;

    public AccessUserMultiGroup(AccessProfile profile, AccessUser primary)
    {
        this.profile = profile;
        this.primary = primary;
    }

    @Override
    public boolean groupHasNode(String node)
    {
        for (AccessGroup group : profile.getGroupsWithUser(getName()))
        {
            if (group.hasNode(node))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public UUID getUserID()
    {
        return primary.getUserID();
    }

    @Override
    public String getName()
    {
        return primary.getName();
    }
}
