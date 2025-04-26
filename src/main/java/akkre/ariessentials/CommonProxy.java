package kamkeel.npcdbc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import kamkeel.npcdbc.api.AbstractDBCAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.scripted.NpcAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonProxy {
    public static final Logger LOGGER = LogManager.getLogger(CustomNpcPlusDBC.ID);
    public static EntityPlayer CurrentJRMCTickPlayer = null;
    public static EntityPlayer CurrentAuraPlayer = null;

    public static void eventsInit() {
        FMLCommonHandler.instance().bus().register(new ServerEventHandler());
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        NpcAPI.EVENT_BUS.register(new NPCEventHandler());
    }

    public void preInit(FMLPreInitializationEvent ev) {
        eventsInit();
    }

    public void init(FMLInitializationEvent ev) {
        NpcAPI.Instance().addGlobalObject("DBCAPI", AbstractDBCAPI.Instance());
    }

    public void postInit(FMLPostInitializationEvent ev) {
    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public World getClientWorld() {
        return null;
    }

    public void registerItem(Item item) { }

    public int getNewRenderId() {
        return -1;
    }

    public boolean isRenderingGUI() {
        return false;
    }
}
