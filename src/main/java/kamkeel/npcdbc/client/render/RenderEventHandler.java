package kamkeel.npcdbc.client.render;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.entity.EntityCusPar;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.early.IEntityMC;
import kamkeel.npcdbc.mixins.late.IEntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.IRenderCusPar;
import kamkeel.npcdbc.mixins.late.IRenderEntityAura2;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static kamkeel.npcdbc.client.shader.PostProcessing.*;
import static org.lwjgl.opengl.GL11.*;

public class RenderEventHandler {
    public static final int TAIL_STENCIL_ID = 2;
    public static List<RenderLivingEvent> renderObjectQueue = new ArrayList<>();
    public static boolean hi = true;

    @SubscribeEvent
    public void enableHandStencil(DBCPlayerEvent.RenderArmEvent.Pre e) {
        if (mc.theWorld != null && PlayerDataUtil.useStencilBuffer(e.entity)) {
            glEnable(GL_STENCIL_TEST);
            glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
            enableStencilWriting(e.entity.getEntityId() % 256);
            Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
        }
    }

    @SubscribeEvent
    public void enableEntityStencil(RenderLivingEvent.Pre e) {
        if (mc.theWorld != null && (e.entity instanceof EntityPlayer || e.entity instanceof EntityNPCInterface)) {
            if (PlayerDataUtil.useStencilBuffer(e.entity)) {
                glEnable(GL_STENCIL_TEST);
                //     glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
                enableStencilWriting(e.entity.getEntityId() % 256);
                Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
                glDepthMask(true); //fixes a native MC RP1 entity bug in which the depth test is disabled

                renderObjectQueue.add(e);
//                if (e.entity.isInWater())
//                    ((IEntityMC) e.entity).setRenderPass(0);
//                else
                ((IEntityMC) e.entity).setRenderPass(0);

            } else {
                if (((IEntityMC) e.entity).getRenderPassTampered())
                    ((IEntityMC) e.entity).setRenderPass(0);

                IAuraData data = PlayerDataUtil.getAuraData(e.entity);
                if (data != null) {
                    if (data.isAuraOn()) {
                        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
                    }

                }
            }

        }
    }

    public static void test() {
        for (ListIterator<RenderLivingEvent> iter = renderObjectQueue.listIterator(renderObjectQueue.size()); iter.hasPrevious(); ) {
            RenderLivingEvent e = iter.previous();
            if ((e.entity instanceof EntityNPCInterface)) {
                EntityNPCInterface entity = (EntityNPCInterface) e.entity;
                DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
                if (!display.enabled || !display.useStencilBuffer)
                    return;

                EntityAura aura = display.auraEntity;
                RenderCustomNpc r = (RenderCustomNpc) e.renderer;
                float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;

                glEnable(GL_STENCIL_TEST);
                mc.entityRenderer.disableLightmap(0);

                boolean renderAura = aura != null && aura.shouldRender(), renderParticles = !display.particleRenderQueue.isEmpty();
                ////////////////////////////////////////
                ////////////////////////////////////////
                //Aura
                if (renderAura && aura.shouldRender()) {
                    glPushMatrix();
                    if (!ClientProxy.renderingGUI)
                        glLoadMatrix(DEFAULT_MODELVIEW); //RESETS TRANSFORMATIONS DONE TO CURRENT MATRIX TO PRE-ENTITY RENDERING STATE
                    glStencilFunc(GL_GREATER, entity.getEntityId() % 256, 0xFF);
                    glStencilMask(0x0);
                    for (EntityAura child : aura.children.values())
                        AuraRenderer.Instance.renderAura(child, partialTicks);
                    AuraRenderer.Instance.renderAura(aura, partialTicks);

                    //  NewAura.renderAura(aura, partialTicks);
                    glPopMatrix();
                }

                ////////////////////////////////////////
                ////////////////////////////////////////
                //Custom Particles
                if (renderParticles) {
                    mc.entityRenderer.disableLightmap(0);
                    glPushMatrix();
                    if (!ClientProxy.renderingGUI)
                        glLoadMatrix(DEFAULT_MODELVIEW); //IMPORTANT, PARTICLES WONT ROTATE PROPERLY WITHOUT THIS
                    IRenderCusPar particleRender = null;
                    for (Iterator<EntityCusPar> it = display.particleRenderQueue.iterator(); it.hasNext(); ) {
                        EntityCusPar particle = it.next();

                        if (particleRender == null)
                            particleRender = (IRenderCusPar) RenderManager.instance.getEntityRenderObject(particle);

                        particleRender.renderParticle(particle, partialTicks);
                        if (particle.isDead)
                            it.remove();
                    }
                    glPopMatrix();
                }

                ////////////////////////////////////////
                ////////////////////////////////////////
                // enableStencilWriting(e.entity.getEntityId() % 256);
                postStencilRendering();
                if (ClientProxy.renderingGUI)
                    PostProcessing.bloom(1.5f, true);
                Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
                //  postStencilRendering();//LETS YOU DRAW TO THE COLOR BUFFER AGAIN
                iter.remove();
            }
        }

        renderObjectQueue.clear();
        //glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
        glDisable(GL_STENCIL_TEST);

    }

    public void renderPlayer(EntityPlayer player, Render renderer, float partialTicks, boolean isArm, boolean isItem) {
        RenderPlayerJBRA render = (RenderPlayerJBRA) renderer;
        DBCData data = DBCData.get(player);
        if (!data.useStencilBuffer)
            return;

        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
        EntityAura aura = data.auraEntity;

        ////////////////////////////////////////
        ////////////////////////////////////////
        //Outline
        Outline outline = data.getOutline();
        if (outline != null && ConfigDBCClient.EnableOutlines && !isItem) {
            startBlooming(ClientProxy.renderingGUI);
            glStencilFunc(GL_GREATER, player.getEntityId() % 256, 0xFF);  // Test stencil value
            glStencilMask(0xff);
            OutlineRenderer.renderOutline(render, outline, player, partialTicks, isArm);
            endBlooming();
        }

        boolean renderAura = aura != null && aura.shouldRender(), renderParticles = !data.particleRenderQueue.isEmpty();
        ////////////////////////////////////////
        ////////////////////////////////////////
        //Aura
        if (renderAura && !isArm) {
            FloatBuffer currentMV = ShaderHelper.getModelView(), currentProj = ShaderHelper.getProjection();
            if (isItem)
                loadMatrices(DEFAULT_MODELVIEW, DEFAULT_PROJECTION);
            else
                glLoadMatrix(DEFAULT_MODELVIEW);

            glPushMatrix();
            glStencilFunc(GL_GREATER, player.getEntityId() % 256, 0xFF);
            glStencilMask(0x00);
            for (EntityAura child : aura.children.values())
                AuraRenderer.Instance.renderAura(child, partialTicks);

            AuraRenderer.Instance.renderAura(aura, partialTicks);

            // NewAura.renderAura(aura, partialTicks);
            glPopMatrix();
            loadMatrices(currentMV, currentProj);
        }


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Custom Particles
        if (renderParticles && !isArm) {
            FloatBuffer currentMV = ShaderHelper.getModelView(), currentProj = ShaderHelper.getProjection();
            if (isItem)
                loadMatrices(DEFAULT_MODELVIEW, DEFAULT_PROJECTION);
            else
                glLoadMatrix(DEFAULT_MODELVIEW);

            glPushMatrix();
            glStencilFunc(GL_GREATER, player.getEntityId() % 256, 0xFF);
            glStencilMask(0x0);
            IRenderCusPar particleRender = null;


            for (Iterator<EntityCusPar> iter = data.particleRenderQueue.iterator(); iter.hasNext(); ) {
                EntityCusPar particle = iter.next();
                if (particleRender == null)
                    particleRender = (IRenderCusPar) RenderManager.instance.getEntityRenderObject(particle);

                particleRender.renderParticle(particle, partialTicks);
                if (particle.isDead)
                    iter.remove();
            }


            glPopMatrix();
            loadMatrices(currentMV, currentProj);
        }

        ////////////////////////////////////////
        ////////////////////////////////////////
        postStencilRendering();
        if (ClientProxy.renderingGUI)
            PostProcessing.bloom(1.5f, true);
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
    }

    @SubscribeEvent
    public void renderNPC(RenderLivingEvent.Post e) {
        glDisable(GL_STENCIL_TEST);
        postStencilRendering();//LETS YOU DRAW TO THE COLOR BUFFER AGAIN
        if (!(e.entity instanceof EntityNPCInterface) || !ClientProxy.renderingGUI)
            return;

        glEnable(GL_STENCIL_TEST);
        EntityNPCInterface entity = (EntityNPCInterface) e.entity;
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!display.enabled || !display.useStencilBuffer)
            return;

        EntityAura aura = display.auraEntity;
        RenderCustomNpc r = (RenderCustomNpc) e.renderer;
        float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;

        disableStencilWriting(entity.getEntityId() % 256, false);
        mc.entityRenderer.disableLightmap(0);

        boolean renderAura = aura != null && aura.shouldRender(), renderParticles = !display.particleRenderQueue.isEmpty();
        ////////////////////////////////////////
        ////////////////////////////////////////
        //Aura
        if (renderAura && aura.shouldRender()) {
            glPushMatrix();
            if (!ClientProxy.renderingGUI)
                glLoadMatrix(DEFAULT_MODELVIEW); //RESETS TRANSFORMATIONS DONE TO CURRENT MATRIX TO PRE-ENTITY RENDERING STATE
            glStencilFunc(GL_GREATER, entity.getEntityId() % 256, 0xFF);
            glStencilMask(0x0);
            for (EntityAura child : aura.children.values())
                AuraRenderer.Instance.renderAura(child, partialTicks);
            AuraRenderer.Instance.renderAura(aura, partialTicks);

            //  NewAura.renderAura(aura, partialTicks);
            glPopMatrix();
        }

        ////////////////////////////////////////
        ////////////////////////////////////////
        //DBC Aura
        if (!display.dbcSecondaryAuraQueue.isEmpty()) {
            glStencilFunc(GL_GREATER, entity.getEntityId() % 256, 0xFF);
            glStencilMask(0x0);
            glPushMatrix();
            IRenderEntityAura2 auraRenderer = null;

            for (Iterator<EntityAura2> iter = display.dbcSecondaryAuraQueue.values().iterator(); iter.hasNext(); ) {
                EntityAura2 aur = iter.next();
                IEntityAura au = (IEntityAura) aur;

                if (aur.isDead)
                    iter.remove();

                if (auraRenderer == null)
                    auraRenderer = (IRenderEntityAura2) RenderManager.instance.getEntityRenderObject(aur);

                auraRenderer.renderParticle(aur, partialTicks);
            }
            glPopMatrix();
        }

        if (!display.dbcAuraQueue.isEmpty()) {
            glStencilFunc(GL_GREATER, entity.getEntityId() % 256, 0xFF);
            glStencilMask(0x0);
            glPushMatrix();
            IRenderEntityAura2 auraRenderer = null;

            for (Iterator<EntityAura2> iter = display.dbcAuraQueue.values().iterator(); iter.hasNext(); ) {
                EntityAura2 aur = iter.next();
                IEntityAura au = (IEntityAura) aur;

                if (aur.isDead)
                    iter.remove();

                if (auraRenderer == null)
                    auraRenderer = (IRenderEntityAura2) RenderManager.instance.getEntityRenderObject(aur);

                auraRenderer.renderParticle(aur, partialTicks);
            }
            glPopMatrix();
        }

        ////////////////////////////////////////
        ////////////////////////////////////////
        //Custom Particles
        if (renderParticles) {
            mc.entityRenderer.disableLightmap(0);
            glPushMatrix();
            if (!ClientProxy.renderingGUI)
                glLoadMatrix(DEFAULT_MODELVIEW); //IMPORTANT, PARTICLES WONT ROTATE PROPERLY WITHOUT THIS
            IRenderCusPar particleRender = null;
            for (Iterator<EntityCusPar> iter = display.particleRenderQueue.iterator(); iter.hasNext(); ) {
                EntityCusPar particle = iter.next();

                if (particleRender == null)
                    particleRender = (IRenderCusPar) RenderManager.instance.getEntityRenderObject(particle);

                particleRender.renderParticle(particle, partialTicks);
                if (particle.isDead)
                    iter.remove();
            }
            glPopMatrix();

        }

        ////////////////////////////////////////
        ////////////////////////////////////////
        enableStencilWriting(e.entity.getEntityId() % 256);
        if (ClientProxy.renderingGUI)
            PostProcessing.bloom(1.5f, true);
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
        // postStencilRendering();//LETS YOU DRAW TO THE COLOR BUFFER AGAIN
        //  glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
        glDisable(GL_STENCIL_TEST);

    }

    @SubscribeEvent
    public void renderPlayer(DBCPlayerEvent.RenderEvent.Post e) {
        renderPlayer(e.entityPlayer, e.renderer, e.partialRenderTick, false, false);
    }

    @SubscribeEvent
    public void renderHandPost(DBCPlayerEvent.RenderArmEvent.Post e) {
        renderPlayer(e.entityPlayer, e.renderer, e.partialRenderTick, true, false);
    }

    @SubscribeEvent
    public void renderHandItem(DBCPlayerEvent.RenderArmEvent.Item e) {
        renderPlayer(e.entityPlayer, e.renderer, e.partialRenderTick, false, true);
    }

    public static void enableStencilWriting(int id) {
        glStencilFunc(GL_ALWAYS, id, 0xFF);  // Always draw to the color buffer & pass the stencil test
        glStencilMask(0xFF);  // Write to stencil buffer
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);  // Keep stencil value
    }

    public static void disableStencilWriting(int id, boolean invert) {
        glStencilFunc(invert ? GL_EQUAL : GL_NOTEQUAL, id, 0xFF);  // Test stencil value
        glStencilMask(0x00);  // Do not write to stencil buffer
    }

    public static void postStencilRendering() {
        glStencilFunc(GL_ALWAYS, 0, 0xFF);
        glStencilMask(0xff);
    }

    public static void loadMatrices(FloatBuffer modelView, FloatBuffer projection) {
        glMatrixMode(GL_PROJECTION);
        glLoadMatrix(projection);
        glMatrixMode(GL_MODELVIEW);
        glLoadMatrix(modelView);
    }
}
