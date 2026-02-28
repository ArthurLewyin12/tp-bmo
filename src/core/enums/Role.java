package core.enums;

import java.util.Set;

public enum Role {
    ORGANIZER(Set.of(Permission.SPEAK,
            Permission.REQUEST_SPEAK,
            Permission.END_MEETING,
            Permission.SET_MODERATOR,
            Permission.ADD_PARTICIPANT,
            Permission.REMOVE_PARTICIPANT,
            Permission.MUTE_PARTICIPANT,
            Permission.VIEW_PARTICIPANTS)),

    MODERATOR(Set.of(Permission.SPEAK,
            Permission.REQUEST_SPEAK,
            Permission.REMOVE_PARTICIPANT,
            Permission.MUTE_PARTICIPANT,
            Permission.VIEW_PARTICIPANTS)),

    PARTICIPANT(Set.of(Permission.REQUEST_SPEAK,
            Permission.VIEW_PARTICIPANTS));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission p) {
        return permissions.contains(p);
    }
}