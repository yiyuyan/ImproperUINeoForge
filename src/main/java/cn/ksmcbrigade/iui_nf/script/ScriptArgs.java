package cn.ksmcbrigade.iui_nf.script;

import cn.ksmcbrigade.iui_nf.render.math.Color;

public class ScriptArgs {

    private String[] args;

    public ScriptArgs(String... args) {
        this.args = args;
    }

    public Arg getAll() {
        return getAll(0);
    }

    public Arg getAll(int beginIndex) {
        String str = "";
        for (int i = beginIndex; i < args.length; i++) {
            str = str.concat(args[i] + " ");
        }
        return new Arg(str.trim());
    }

    public Arg get(int index) {
        if (args.length == 0) {
            return new Arg("");
        }
        return new Arg(args[Math.min(Math.max(index, 0), args.length - 1)]);
    }

    public String getQuoteAndRemove(int beginIndex) {
        String all = getAll(beginIndex).toString();
        var section = ScriptReader.firstSectionWithIndex(all, '"', '"');

        if (section.left.isEmpty()) {
            args = all.split("\\s+");
            return all;
        }

        args = all.substring(section.right).trim().split("\\s+");
        return section.left;
    }

    public String getQuoteAndRemove() {
        return getQuoteAndRemove(0);
    }

    public String getQuote(int beginIndex) {
        String all = getAll(beginIndex).toString();
        String quote = ScriptReader.firstSection(all, '"');
        return quote.isEmpty() ? all : quote;
    }

    public String getQuote() {
        return getQuote(0);
    }

    public Arg first() {
        return get(0);
    }

    public Arg last() {
        return get(args.length - 1);
    }

    public boolean match(int index, String arg) {
        if (index < 0 || index >= args.length) {
            return false;
        }
        return get(index).toString().equalsIgnoreCase(arg);
    }

    public int getSize() {
        return args.length;
    }

    public boolean isEmpty() {
        return args.length == 0;
    }

    public String[] args() {
        return args;
    }



    public static class Arg {

        private final String arg;

        public Arg(String arg) {
            this.arg = arg;
        }

        public int toInt() {
            return (int) toDouble();
        }

        public long toLong() {
            return (long) toDouble();
        }

        public byte toByte() {
            return (byte) toDouble();
        }

        public short toShort() {
            return (short) toDouble();
        }

        public float toFloat() {
            return (float) toDouble();
        }

        public double toDouble() {
            return Double.parseDouble(arg.replaceAll("[^0-9-+e.]", ""));
        }

        public boolean toBool() {
            return Boolean.parseBoolean(arg);
        }

        public char toChar() {
            return arg.isEmpty() ? ' ' : arg.charAt(0);
        }

        public Color toColor() {
            return Color.parse(arg);
        }

        @Override
        public String toString() {
            return arg;
        }

        public <T extends Enum<?>> T toEnum(Class<T> enumType) {
            return toEnum(enumType, null);
        }

        public <T extends Enum<?>> T toEnum(Class<T> enumType, T fallback) {
            String arg = this.arg.replace('-', '_');
            for (T constant : enumType.getEnumConstants()) {
                if (arg.equalsIgnoreCase(constant.name())) {
                    return constant;
                }
            }

            if (fallback == null) {
                throw new IllegalArgumentException("'%s' is not a value of %s".formatted(arg, enumType.getSimpleName()));
            }
            return fallback;
        }
    }
}
