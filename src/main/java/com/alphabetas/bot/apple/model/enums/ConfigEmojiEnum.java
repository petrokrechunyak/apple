package com.alphabetas.bot.apple.model.enums;

public enum ConfigEmojiEnum {
    ALL("✅"),
    DOTTED("⚫"),
    OFF("❌");

    private final String comandName;

    ConfigEmojiEnum(String comandName) {
        this.comandName = comandName;
    }

    public String getComandName() {
        return comandName;
    }

    public static ConfigEmojiEnum convert(String name) {
        switch (name) {
            case "✅":
                return ALL;
            case "⚫":
                return DOTTED;
            case "❌":
                return OFF;
        }
        throw new IllegalArgumentException("Name cannot be found");
    }
}
