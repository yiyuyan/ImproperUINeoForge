package cn.ksmcbrigade.iui_nf.config;

import cn.ksmcbrigade.iui_nf.render.Element;

import java.util.function.Function;

public interface ConfigKeyHolder {

    Function<Element, ConfigKey> ELEMENT_KEY_HOLDER = element -> {
        String regex = "([a-zA-Z0-9_.-]+:)?[a-zA-Z0-9_.-]+\\.[a-zA-Z0-9_.-]+:[a-zA-Z0-9_.-]+";
        for (var s : element.classList)
            if (s.matches(regex))
                return new ConfigKey(s);
        return null;
    };

    ConfigKey getConfigKey();
}
