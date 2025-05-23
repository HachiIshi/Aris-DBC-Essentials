package akkre.ariessentials;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.relauncher.Side;
import akkre.ariessentials.config.LoadConfiguration;
import akkre.ariessentials.controllers.*;
import akkre.ariessentials.items.ModItems;
import akkre.ariessentials.network.PacketHandler;

import java.io.File;

@Mod(modid = CustomNpcPlusDBC.ID, name = CustomNpcPlusDBC.name, version = CustomNpcPlusDBC.version, dependencies = "required-after:customnpcs;required-after:jinryuujrmcore;required-after:jinryuudragonblockc;")
public class CustomNpcPlusDBC {

    public static final String name = "CustomNPC+ DBC Addon";
    public static final String version = "1.0.2";
    public static final String ID = "npcdbc";
    @SidedProxy(clientSide = "akkre.ariessentials.client.ClientProxy", serverSide = "akkre.ariessentials.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static CustomNpcPlusDBC instance;
    public static String addonConfig;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ev) {
        proxy.preInit(ev);
        addonConfig = ev.getModConfigurationDirectory() + File.separator + "CustomNpcPlus" + File.separator + "dbc" + File.separator;
        LoadConfiguration.init(addonConfig);
        ModItems.init();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        CapsuleController.getInstance().load();
        StatusEffectController.getInstance().load();
        BonusController.getInstance().load();
    }

    @Mod.EventHandler
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
        FormController.getInstance().load();
        AuraController.getInstance().load();
        CapsuleController.getInstance().load();
        StatusEffectController.getInstance().load();
        BonusController.getInstance().load();
        OutlineController.getInstance().load();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        PacketHandler.Instance = new PacketHandler();
        PacketHandler.Instance.register();
    }

    public static Side side() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }


}
