package net.zphyghtning.streetscape.block.roadmarkings;

import net.minecraft.util.StringIdentifiable;

public enum DoubleLineShape implements StringIdentifiable {
    NONE("none"),
    NORTH_SOUTH("north_south"),
    EAST_WEST("east_west"),
    NORTH_EAST("north_east"),
    EAST_SOUTH("east_south"),
    SOUTH_WEST("south_west"),
    WEST_NORTH("west_north"),
    NORTH_EAST_SOUTH("north_east_south"),
    EAST_SOUTH_WEST("east_south_west"),
    SOUTH_WEST_NORTH("south_west_north"),
    WEST_NORTH_EAST("west_north_east"),
    ALL("all");

    private final String name;

    DoubleLineShape(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
