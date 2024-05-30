package kamkeel.npcdbc.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.render.AuraRenderer;
import kamkeel.npcdbc.client.render.PotaraItemRenderer;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;


public class ClientProxy extends CommonProxy {
    public static boolean RenderingOutline;

    public static void eventsInit() {
        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());

    }

    @Override
    public void preInit(FMLPreInitializationEvent ev) {
        super.preInit(ev);
        eventsInit();

        forceStencilEnable();

        RenderingRegistry.registerEntityRenderingHandler(EntityAura.class, new AuraRenderer());
        MinecraftForgeClient.registerItemRenderer(ModItems.Potaras, new PotaraItemRenderer());
    }

    public void init(FMLInitializationEvent ev) {
        super.init(ev);
        KeyHandler.registerKeys();
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public int getNewRenderId() {
        return RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void registerItem(Item item) {
    }

    public static void forceStencilEnable() {

        System.setProperty("forge.forceDisplayStencil", "true");

        Field field;

        try {

            field = ForgeHooksClient.class.getDeclaredField("stencilBits");

            field.setAccessible(true);

            field.setInt(ForgeHooksClient.class, 8);
        }
        catch (NoSuchFieldException | SecurityException e) {

            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {

            e.printStackTrace();
        }
        catch (IllegalAccessException e) {

            e.printStackTrace();
        }
    }
}
