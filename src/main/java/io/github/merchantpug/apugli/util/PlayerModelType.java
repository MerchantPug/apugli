package io.github.merchantpug.apugli.util;

public enum PlayerModelType {
    DEFAULT ("default"),
    SLIM ("slim");

    private final String name;

    PlayerModelType(String string) {
        name = string;
    }

    public String toString() {
        return this.name;
    }
}
