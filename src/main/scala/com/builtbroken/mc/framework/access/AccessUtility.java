package com.builtbroken.mc.framework.access;

import com.builtbroken.mc.framework.access.api.IProfileContainer;
import com.builtbroken.mc.framework.access.perm.Permission;
import com.builtbroken.mc.framework.access.perm.Permissions;

import java.util.*;
import java.util.Map.Entry;

/**
 * Handler for the default group loaded by all machines that use AccessProfiles. Includes functions
 * that are helpful when dealing with access profiles. It is suggested to never modify the default
 * group unless there is not other way. However, any access node needs to be registered threw this
 * class to allow other things to access it. This also include applying those permissions to the
 * default
 * groups.
 *
 * @author DarkGuardsman
 */
public class AccessUtility
{
    /**
     * Global list of all permissions
     */
    public final static Set<Permission> permissions = new LinkedHashSet();
    /**
     * Map of default groups and those group permissions permissions. Used to build a new group set.
     */
    public static final HashMap<String, List<String>> groupDefaultNodes = new LinkedHashMap<>();
    /**
     * Map of default groups and the group it extends. Used to build a new group set.
     */
    public static final HashMap<String, String> groupDefaultExtends = new LinkedHashMap<>();

    // Pre-loads the default groups
    static
    {
        List<String> ownerPerms = new ArrayList();
        // Owner group defaults
        ownerPerms.add(Permissions.root.toString());
        ownerPerms.add(Permissions.PROFILE_OWNER.toString());

        ownerPerms.add(Permissions.inventoryDisable.toString());
        ownerPerms.add(Permissions.inventoryEnable.toString());

        ownerPerms.add(Permissions.profile.toString());

        // Admin group defaults
        List<String> adminPerms = new ArrayList();
        adminPerms.add(Permissions.PROFILE_ADMIN.toString());

        adminPerms.add(Permissions.inventoryModify.toString());
        adminPerms.add(Permissions.inventoryLock.toString());
        adminPerms.add(Permissions.inventoryUnlock.toString());
        adminPerms.add(Permissions.inventoryModify.toString());

        adminPerms.add(Permissions.machineLock.toString());
        adminPerms.add(Permissions.machineUnlock.toString());
        adminPerms.add(Permissions.machineUpgrade.toString());
        adminPerms.add(Permissions.machineDowngrade.toString());
        adminPerms.add(Permissions.machineEnable.toString());
        adminPerms.add(Permissions.machineDisable.toString());

        adminPerms.add(Permissions.group.toString());
        adminPerms.add(Permissions.profileAddGroup.toString());
        adminPerms.add(Permissions.profileModifyGroup.toString());
        adminPerms.add(Permissions.profileRemoveGroup.toString());
        adminPerms.add(Permissions.profileEditSettings.toString());

        // User group defaults
        List<String> userPerms = new ArrayList();
        userPerms.add(Permissions.PROFILE_USER.toString());

        userPerms.add(Permissions.machineTurnOn.toString());
        userPerms.add(Permissions.machineTurnOff.toString());

        userPerms.add(Permissions.inventoryOpen.toString());
        userPerms.add(Permissions.inventoryInput.toString());
        userPerms.add(Permissions.inventoryOutput.toString());

        userPerms.add(Permissions.profileView.toString());

        createDefaultGroup("user", null, userPerms);
        createDefaultGroup("admin", "user", adminPerms);
        createDefaultGroup("owner", "admin", ownerPerms);
        createDefaultGroup("owner", "admin", ownerPerms);

        //FoF system groups - used as a user friendly solution
        List<String> hostilePerms = new ArrayList();
        hostilePerms.add(Permissions.PROFILE_FOF.toString());
        hostilePerms.add(Permissions.targetHostile.toString());

        List<String> friendlyPerms = new ArrayList();
        friendlyPerms.add(Permissions.PROFILE_FOF.toString());
        friendlyPerms.add(Permissions.targetFriend.toString());

        createDefaultGroup("hostile", null, hostilePerms);
        createDefaultGroup("friendly", null, friendlyPerms);
    }

    /**
     * Creates a default group for all machines to use. Only add a group if there is no option to
     * really manage the group's settings
     *
     * @param name        - group name
     * @param prefabGroup - group this should extend. Make sure it exists.
     * @param nodes       - all commands or custom permissions
     */
    public static void createDefaultGroup(String name, String prefabGroup, List<String> nodes)
    {
        if (name != null)
        {
            groupDefaultNodes.put(name, nodes);
            groupDefaultExtends.put(name, prefabGroup);
        }
    }

    /**
     * Creates a default group for all machines to use. Only add a group if there is no option to
     * really manage the group's settings
     *
     * @param name        - group name
     * @param prefabGroup - group this should extend. Make sure it exists.
     * @param nodes       - all commands or custom permissions
     */
    public static void createDefaultGroup(String name, String prefabGroup, String... nodes)
    {
        createDefaultGroup(name, prefabGroup, nodes != null ? Arrays.asList(nodes) : null);
    }

    /**
     * Registers a node with the master list making it available
     */
    public static void registerPermission(String node, String group)
    {
        registerPermission(new Permission(node), group);
    }

    /**
     * Registers a node with the master list making it available
     */
    public static void registerPermission(Permission perm, String group)
    {
        if (!permissions.contains(perm))
        {
            permissions.add(perm);
        }
        if (group != null && !group.isEmpty() && groupDefaultNodes.containsKey(group))
        {
            List<String> perms = groupDefaultNodes.get(group);
            if (perms != null && !perms.contains(perm.id))
            {
                perms.add(perm.id);
            }
        }
    }

    /**
     * Builds a new default group list for a basic machine
     */
    public static List<AccessGroup> buildNewGroup()
    {
        List<AccessGroup> groups = new ArrayList<>();

        // Create groups and load permissions
        for (Entry<String, List<String>> entry : groupDefaultNodes.entrySet())
        {
            AccessGroup group = new AccessGroup(entry.getKey());
            if (entry.getValue() != null)
            {
                for (String string : entry.getValue())
                {
                    group.addNode(string);
                }
            }
            groups.add(group);
        }

        // Set group to extend each other
        for (Entry<String, String> entry : groupDefaultExtends.entrySet())
        {
            if (entry.getKey() != null && !entry.getKey().isEmpty())
            {
                AccessGroup group = getGroup(groups, entry.getKey());
                AccessGroup groupToExtend = getGroup(groups, entry.getValue());
                if (group != null && groupToExtend != null)
                {
                    group.setToExtend(groupToExtend);
                }
            }
        }

        return groups;
    }

    /**
     * Builds then loaded a new default group set into the terminal
     */
    public static void loadNewGroupSet(IProfileContainer container)
    {
        if (container != null)
        {
            loadNewGroupSet(container.getAccessProfile());
        }
    }

    public static void loadNewGroupSet(AccessProfile profile)
    {
        if (profile != null)
        {
            List<AccessGroup> groups = buildNewGroup();
            for (AccessGroup group : groups)
            {
                profile.addGroup(group);
            }
        }
    }

    /**
     * Picks a group out of a list using the groups name
     */
    public static AccessGroup getGroup(Collection<AccessGroup> groups, String name)
    {
        for (AccessGroup group : groups)
        {
            if (group.getName().equalsIgnoreCase(name))
            {
                return group;
            }
        }
        return null;
    }
}
