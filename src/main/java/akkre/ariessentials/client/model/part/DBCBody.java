package akkre.ariessentials.client.model.part;

import akkre.ariessentials.client.ClientProxy;
import akkre.ariessentials.client.model.ModelDBCPartInterface;
import akkre.ariessentials.client.render.RenderEventHandler;
import akkre.ariessentials.constants.DBCRace;
import akkre.ariessentials.data.form.Form;
import akkre.ariessentials.data.form.FormDisplay;
import akkre.ariessentials.data.npc.DBCDisplay;
import akkre.ariessentials.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelRenderer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import org.lwjgl.opengl.GL11;

import static akkre.ariessentials.client.render.RenderEventHandler.disableStencilWriting;

public class DBCBody extends ModelDBCPartInterface {

    // Back Spikes
    public ModelRenderer BackSpikes;
    public ModelRenderer backSpike1;
    public ModelRenderer backSpike2;
    public ModelRenderer Oozaru, OozaruMouth;

    public DBCBody(ModelMPM base) {
        super(base);
        textureHeight = 32;
        textureWidth = 64;

        this.BackSpikes = new ModelRenderer(base, 0, 0);
        this.BackSpikes.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.BackSpikes.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.backSpike1 = new ModelRenderer(base, 8, 38);
        this.backSpike1.addBox(2.0F, -4.0F, 3.0F, 2, 6, 2);
        this.backSpike1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.backSpike1, -0.9773844F, 0.0F, 0.2094395F);
        this.backSpike2 = new ModelRenderer(base, 8, 38);
        this.backSpike2.addBox(-4.0F, -4.0F, 3.0F, 2, 6, 2);
        this.backSpike2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.backSpike2, -0.9773844F, 0.0F, -0.2094395F);
        this.BackSpikes.addChild(this.backSpike1);
        this.BackSpikes.addChild(this.backSpike2);
        this.addChild(BackSpikes);

        this.OozaruMouth = new ModelRenderer(base, 0, 8);
        this.OozaruMouth.addBox(-2.0F, -3.0F, -8.0F, 4, 3, 4);
        this.OozaruMouth.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.OozaruMouth, 0.0F, 0.0F, 0.0F);
        this.Oozaru = new ModelRenderer(base, 0, 0);
        this.Oozaru.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Oozaru.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Oozaru.addChild(this.OozaruMouth);
    }

    @Override
    public void render(float par1) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!display.enabled)
            return;

        if (!ClientProxy.renderingOutline && display.outlineID != -1)
            RenderEventHandler.enableStencilWriting((entity.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256);

        GL11.glPushMatrix();
        float spike1RotX = 0;
        if (ClientProxy.renderingOutline) {
            spike1RotX = backSpike1.rotationPointX;
            GL11.glTranslatef(0.015f, -0.02f, 0);
            GL11.glScaled(1.02, 1.02, 0.95);
            backSpike1.rotationPointX = -1.4f;
            disableStencilWriting((entity.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256, false);
        }

        if (display.useSkin) {
            this.useColor = 0;
            bodyCM = display.bodyCM;
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;
                if (d.hasColor("bodycm"))
                    bodyCM = d.bodyCM;
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////

            super.render(par1);
        } else {
            super.render(par1);
        }
        GL11.glPopMatrix();
        if (!ClientProxy.renderingOutline && display.outlineID != -1)
            RenderEventHandler.enableStencilWriting(entity.getEntityId() % 256);

        if (ClientProxy.renderingOutline) {
            disableStencilWriting((entity.getEntityId()) % 256, false);
            backSpike1.rotationPointX = spike1RotX;
        }
    }

    @Override
    public void initData(ModelData modelData, DBCDisplay display) {
        ModelPartData config = data.getPartData("dbcBody");
        if (config == null) {
            isHidden = true;
            return;
        }
        bodyCM = config.color;
        isHidden = false;

        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////
        //Forms
        Form form = display.getForm();
        if (form != null) {
            if (display.race == DBCRace.ARCOSIAN) {
                if (form.display.bodyType.equals("firstform")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("secondform")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("thirdform")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("finalform") || form.display.bodyType.equals("golden")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("ultimatecooler")) {
                    config.type = 1;
                }
            }
        }
        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////

        BackSpikes.isHidden = config.type != 1;

        if (!config.playerTexture) {
            location = config.getResource();
        } else
            location = null;
    }
}
