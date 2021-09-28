package leszekJadacki.phonebook.security;

public enum AppUserPermission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    CONTACT_READ("contact:read"),
    CONTACT_WRITE("contact:write");

    private final String permission;

    AppUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
