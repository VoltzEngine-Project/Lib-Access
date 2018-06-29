package com.builtbroken.mc.framework.access.prefab;

import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.framework.access.perm.Permission;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to define an object for a permission system. Can be extended to create subtypes
 * for example {@link com.builtbroken.mc.framework.access.AccessUser} or {@link com.builtbroken.mc.framework.access.AccessGroup}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/12/2018.
 */
public abstract class AccessObject implements ISave
{
    private static final String NBT_PERMISSIONS = "permissions";
    private static final String NBT_PERMISSION_NAME = "name";
    private static final String NBT_CAN_EDIT = "canEdit";

    /** Notes that this object is temporary and should not save */
    protected boolean isTemporary = false;
    /** Allows disabling edit ability on the object */
    protected boolean canEdit = true;

    /** List of permission nodes */
    public Set<String> nodes = new HashSet();

    /**
     * Checks if the user has the permission node
     *
     * @param permission - node
     * @return true if the user has the node or a super * node
     */
    public boolean hasNode(Permission permission)
    {
        return hasNode(permission.toString());
    }

    /**
     * Checks if the object has the permission node. This
     * will check the object and any parent objects.
     *
     * @param node - node
     * @return true if the user has the node or a parent of the node
     */
    public boolean hasNode(String node)
    {
        return hasExactNode(node) || containsNode(node);
    }

    /**
     * Checks if the object has the node contained.
     * <p>
     * Only checks the object and not any parent. Use
     * {@link #hasNode(String)} to check for actual
     * permissions.
     *
     * @param node - node
     * @return true if the object contains the node
     */
    public boolean containsNode(String node)
    {
        //Special handling for max perm users
        if (hasExactNode("*"))
        {
            return true;
        }

        final String tempNode = node.replace(".*", "");
        for (String groupNode : nodes)
        {
            final String headNode = groupNode.replace(".*", "");
            if (tempNode.startsWith(headNode))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the object has the exact node.
     * <p>
     * Only checks the object and not any parent. Use
     * {@link #hasNode(String)} to check for actual
     * permissions.
     *
     * @param node - node
     * @return true only if exact match is found
     */
    public boolean hasExactNode(String node)
    {
        return this.nodes.contains(node);
    }


    /**
     * Removes a permission node
     *
     * @param perm
     * @return true if the node was removed
     */
    public boolean removeNode(Permission perm)
    {
        return removeNode(perm.toString());
    }

    /**
     * Adds a permission node
     *
     * @param perm
     * @return
     */
    public boolean addNode(Permission perm)
    {
        return addNode(perm.toString());
    }

    /**
     * Removes a permission node from this user
     *
     * @param perm
     * @return true if the node was removed
     */
    public boolean removeNode(String perm)
    {
        if (canEdit)
        {
            return nodes.remove(perm);
        }
        return false;
    }

    /**
     * Adds a permission node to this user
     *
     * @param perm
     * @return
     */
    public boolean addNode(String perm)
    {
        //TODO remove sub nodes
        if (canEdit && !hasExactNode(perm))
        {
            nodes.add(perm);
        }
        return false;
    }

    public Set<String> getNodes()
    {
        return nodes;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setBoolean(NBT_CAN_EDIT, canEdit);
        NBTTagList usersTag = new NBTTagList();
        for (String str : this.nodes)
        {
            NBTTagCompound accessData = new NBTTagCompound();
            accessData.setString(NBT_PERMISSION_NAME, str);
            usersTag.appendTag(accessData);
        }
        nbt.setTag(NBT_PERMISSIONS, usersTag);
        return nbt;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        this.canEdit = nbt.getBoolean(NBT_CAN_EDIT);
        NBTTagList userList = nbt.getTagList(NBT_PERMISSIONS, 10);
        this.nodes.clear();
        for (int i = 0; i < userList.tagCount(); ++i)
        {
            this.nodes.add(userList.getCompoundTagAt(i).getString(NBT_PERMISSION_NAME));
        }
    }

    public <E extends AccessObject> E disableEdit()
    {
        canEdit = false;
        return (E) this;
    }

    public <E extends AccessObject> E enableEdit()
    {
        canEdit = true;
        return (E) this;
    }

    public <E extends AccessObject> E setEditState(boolean state)
    {
        canEdit = state;
        return (E) this;
    }

    public boolean canEdit()
    {
        return canEdit;
    }
}
