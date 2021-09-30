package io.github.merchantpug.apugli.util;

public enum PlayerModelType {
    DEFAULT ("default"),
    SLIM ("slim");

    private final String name;

    private PlayerModelType(String string) {
        name = string;
    }

    public String toString() {
        return this.name;
    }
}
