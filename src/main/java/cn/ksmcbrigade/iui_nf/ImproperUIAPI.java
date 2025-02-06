package cn.ksmcbrigade.iui_nf;

import cn.ksmcbrigade.iui_nf.config.ConfigReader;
import cn.ksmcbrigade.iui_nf.config.Paths;
import cn.ksmcbrigade.iui_nf.render.Element;
import cn.ksmcbrigade.iui_nf.render.ImproperUIPanel;
import cn.ksmcbrigade.iui_nf.script.CallbackListener;
import cn.ksmcbrigade.iui_nf.script.ScriptParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImproperUIAPI {

    public static final Logger LOGGER = LoggerFactory.getLogger("ImproperUIAPI");
    private static final Map<String, InitContext> CONTEXTS = new HashMap<>();

    /**
     * Example: ImproperUI.init("iui_nf", ImproperUI.class, "scripts/example.ui");
     * @param modId YOUR mod's mod ID
     * @param initializer YOUR mod's main initializer, NOT CLIENT INITIALIZER
     * @param scriptPaths Target script files
     */
    public static void init(String modId, Class<?> initializer, String... scriptPaths) {
        InitContext context = CONTEXTS.get(modId);

        if (context == null) {
            context = new InitContext(modId, initializer, scriptPaths);
            CONTEXTS.put(modId, context);
        }
        context.init();
    }

    public static void reload() {
        CONTEXTS.values().forEach(InitContext::reload);
    }

    public static List<InitContext> collectContext() {
        return new ArrayList<>(CONTEXTS.values());
    }

    public static InitContext getContext(String modId) {
        return CONTEXTS.get(modId);
    }

    public static ConfigReader getConfigReader(String modId, String configFile) {
        return new ConfigReader(modId, configFile);
    }



    // parse helper methods

    public static List<Element> parse(String script) {
        return ScriptParser.parse(script);
    }

    public static List<Element> parse(File file) {
        return ScriptParser.parseFile(file);
    }

    public static void parseAndRunScript(String script, CallbackListener... callbackListeners) {
        new ImproperUIPanel(script, callbackListeners).open();
    }

    /**
     * Parses and runs the script from the path provided
     * @param modId Multiple mods may use ImproperUI at the same time with their own respective scripts, specify the mod ID!
     * @param fileName The file NAME, NOT THE FILE PATH
     * @param callbackListeners A list of callbacks that you want to add to the panel screen
     */
    public static void parseAndRunFile(String modId, String fileName, CallbackListener... callbackListeners) {
        File script = new File(Paths.getScripts(modId) + fileName);
        new ImproperUIPanel(script, callbackListeners).open();
    }
}
