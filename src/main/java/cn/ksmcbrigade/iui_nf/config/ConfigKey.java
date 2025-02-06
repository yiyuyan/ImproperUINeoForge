package cn.ksmcbrigade.iui_nf.config;

public class ConfigKey {

    public final String modId, path, key;

    public ConfigKey(String entry) {
        entry = entry.trim()
                .replaceAll("\\s+|_", "-")
                .replaceAll("[^a-zA-Z0-9:.-]", "");

        String[] split = entry.split("\\s*:\\s*");


        switch (split.length) {
            case 2 -> {
                this.modId = "iui_nf";
                this.path = split[0];
                this.key = split[1];
            }
            case 3 -> {
                this.modId = split[0];
                this.path = split[1];
                this.key = split[2];
            }
            default -> throw new IllegalArgumentException("malformed config key: \"%s\"".formatted(entry));
        }
    }

    public ConfigKey(String modId, String path, String key) {
        this.modId = modId;
        this.path = path;
        this.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigKey configKey))
            return false;
        return configKey.path.equals(this.path) && configKey.key.equals(this.key);
    }
}
