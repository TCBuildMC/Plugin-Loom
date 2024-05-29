package xyz.tcbuildmc.pluginloom.bukkit.extension

enum BasePermission {
    OP("op"),
    NOT_OP("not op"),
    TRUE("true"),
    FALSE("false");

    private final String id

    BasePermission(String id) {
        this.id = id
    }

    @Override
    String toString() {
        return id
    }
}