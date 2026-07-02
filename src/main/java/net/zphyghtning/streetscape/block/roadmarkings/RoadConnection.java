package net.zphyghtning.streetscape.block.roadmarkings;

import net.minecraft.util.StringIdentifiable;

public enum RoadConnection implements StringIdentifiable {
    NONE("none"),
    FLAT("flat"),
    UP("up"),
    DOWN("down");

    private final String name;

    RoadConnection(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
