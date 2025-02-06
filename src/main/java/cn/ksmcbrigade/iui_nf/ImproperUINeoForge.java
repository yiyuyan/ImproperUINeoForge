package cn.ksmcbrigade.iui_nf;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ImproperUINeoForge.MODID)
public class ImproperUINeoForge {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "iui_nf";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ImproperUI UI = new ImproperUI();

    public ImproperUINeoForge(IEventBus modEventBus, ModContainer modContainer) {
        UI.onInitialize();
        NeoForge.EVENT_BUS.register(UI);
        LOGGER.info("Improper UI mod in NeoForge loaded.");
    }
}
