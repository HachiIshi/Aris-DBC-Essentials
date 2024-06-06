package kamkeel.npcdbc.entity;

import JinRyuu.DragonBC.common.Npcs.EntityAuraRing;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.particle.ParticleFormHandler;
import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.controllers.EntityLightController;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixins.early.IEntityMC;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;

import static kamkeel.npcdbc.constants.enums.EnumAuraTypes3D.Default;

public class EntityAura extends Entity {

    public final Entity entity;
    public final Aura aura;
    public IAuraData auraData;

    @SideOnly(Side.CLIENT)
    public AuraSound auraSound;


    public EntityLightController light;
    public boolean isKaioken, isInKaioken;
    public boolean isTransforming;
    public boolean isCharging;
    public boolean isVanillaDefault; // when custom aura is hidden and revamp is enabled

    public EnumAuraTypes3D type3D;
    public EnumAuraTypes2D type2D;
    public int color1 = -1, color2 = -1, color3 = -1, speed = 10, renderPass;
    public float alpha, maxAlpha = 0.05f, size = 1f, effectiveSize, skinColorAlpha = 0.25f;

    public boolean hasLightning;
    public int lightningColor = 0x25c9cf, lightningAlpha = 255;

    public boolean isInner, fadeOut = false, fadeIn = true;

    public String name;
    public EntityAura parent;
    public HashMap<String, EntityAura> children = new HashMap<>();

    public float fadeFactor = 0.005f;

    public ResourceLocation text1, text2, text3;

    public EntityAura(Entity entity, Aura aura) {
        super(entity.worldObj);
        this.entity = entity;
        this.aura = aura;

        if (entity instanceof EntityPlayer) {
            auraData = DBCData.get((EntityPlayer) entity);
        } else if (entity instanceof EntityNPCInterface) {
            auraData = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();
        }
        auraData.setAuraEntity(this);
        setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
        text1 = new ResourceLocation( "jinryuudragonbc:aura.png");

        if (aura.hasSecondaryAura() && children.containsKey("Secondary"))
            new EntityAura(entity, aura.getSecondaryAur()).load(true).setParent(this, "Secondary").spawn();
    }


    public EntityAura load(boolean all) {
        color1 = auraData.getAuraColor();
        AuraDisplay display = aura.display;

        type3D = display.type;
        if (aura.display.type == Default)
            type3D = EnumAuraTypes3D.getType(auraData);

        type2D = display.type2D;
        if (aura.display.type2D == EnumAuraTypes2D.Default)
            type2D = EnumAuraTypes2D.getType(auraData);


        // Vanilla DBC form colors
        if (auraData instanceof DBCData && ((DBCData) auraData).State > 0)
            color1 = auraData.getDBCColor();

        int mimicColor = EnumAuraTypes3D.getManualAuraColor(type3D, true);
        if (mimicColor != -1)
            color1 = mimicColor;


        if (display.hasColor("color1")) //IAura color
            color1 = display.color1;

        Form form = PlayerDataUtil.getForm(entity);
        if (form != null && form.display.hasColor("aura")) //IForm color
            color1 = form.display.auraColor;

        if (isVanillaDefault) {
            if (DBCRace.isSaiyan(auraData.getRace()) && (auraData.getState() == 5 || auraData.getState() == 6))
                hasLightning = true;
            else
                hasLightning = false;
        }


        if (isInKaioken) {
            boolean kaiokenOverride = display.kaiokenOverrides;
            if (isVanillaDefault && DBCForm.isSaiyanGod(auraData.getState()))
                kaiokenOverride = false;


            if (!kaiokenOverride && !children.containsKey("Kaioken"))
                new EntityAura(entity, aura).loadKaioken().setParent(this, "Kaioken").spawn();

            if (kaiokenOverride) {
                color1 = lightningColor = 16646144;
            }

        } else {
            if (lightningColor == 16646144)
                lightningColor = 0x25c9cf;
        }

        if (all) {
            if (display.hasColor("color2"))
                color2 = display.color2;
            if (display.hasColor("color3"))
                color3 = display.color3;

            //loadType();

            if (display.hasAlpha("aura")){
                // New Aura has many layers and gets extremely dark without
                // this modification. Even alphas set to 30 will become fully
                // solid without this change, due to the mass amount of layers
                // rendered and overlapping
                maxAlpha = (float) display.alpha / (255 * 5);
            }
            //  maxAlpha = 0.51f;


            if (display.hasSpeed())
                speed = (int) display.speed;
            if (display.hasSize())
                size = display.size;

            light = new EntityLightController(entity);

            hasLightning = display.hasLightning;
            if (display.hasColor("lightning"))
                lightningColor = display.lightningColor;
            if (display.hasAlpha("lightning"))
                lightningAlpha = display.lightningAlpha;
        }

        return this;
    }

    public EntityAura loadKaioken() {
        AuraDisplay display = aura.display;
        color1 = 16646144;

        text1 = new ResourceLocation("jinryuudragonbc:aurak.png");
        renderPass = 0;
        maxAlpha = 0.2f;

        if (aura.display.hasAlpha("kaioken"))
            maxAlpha = (float) display.kaiokenAlpha / 255;


        if (aura.display.hasColor("kaioken"))
            color1 = display.kaiokenColor;

        if (aura.display.hasSpeed())
            speed = (int) display.speed;

        size = display.size * display.kaiokenSize;

        hasLightning = display.hasLightning;
        if (display.hasColor("lightning"))
            lightningColor = display.lightningColor;
        if (display.hasAlpha("lightning"))
            lightningAlpha = display.lightningAlpha;

        isKaioken = true;
        return this;
    }

    public void loadType() {
        String auraDir = "jinryuudragonbc:";
        if (type3D == EnumAuraTypes3D.SaiyanGod) {
            maxAlpha = 0.2f;
            text1 = new ResourceLocation(auraDir + "aurai.png");
            text2 = new ResourceLocation(auraDir + "auraj.png");
            color2 = 16747301;
        } else if (type3D == EnumAuraTypes3D.SaiyanBlue) {
            maxAlpha = 0.05F;
            text1 = new ResourceLocation(auraDir + "aurag.png");
            text3 = new ResourceLocation(auraDir + "auragb.png");
            color3 = 15727354;
        } else if (type3D == EnumAuraTypes3D.SaiyanBlueEvo) {
            maxAlpha = 0.05F;
            text1 = new ResourceLocation(auraDir + "aurag.png");
            text3 = new ResourceLocation(auraDir + "auragb.png");
            color3 = 12310271;
        } else if (type3D == EnumAuraTypes3D.SaiyanRose) {
            maxAlpha = 0.05F;
            text1 = new ResourceLocation(auraDir + "aurai.png");
            text2 = new ResourceLocation(auraDir + "auraj.png");
            color2 = 7872713;
        } else if (type3D == EnumAuraTypes3D.SaiyanRoseEvo) {
            maxAlpha = 0.05F;
            text1 = new ResourceLocation(auraDir + "aurai.png");
            text2 = new ResourceLocation(auraDir + "auraj.png");
            color2 = 8592109;
        } else if (type3D == EnumAuraTypes3D.UI) {
            maxAlpha = 0.15F;
            color1 = 15790320;
            text1 = new ResourceLocation(auraDir + "auras.png");
            color3 = 4746495;
            text3 = new ResourceLocation(auraDir + "auragb.png");
        } else if (type3D == EnumAuraTypes3D.GoD) {
            maxAlpha = 0.05F;
            text1 = new ResourceLocation(auraDir + "aurag.png");
            text3 = new ResourceLocation(auraDir + "auragb.png");
            color2 = 12464847;
        } else if (type3D == EnumAuraTypes3D.UltimateArco) {
            maxAlpha = 0.05F;
            text1 = new ResourceLocation(auraDir + "aurau.png");
            text2 = new ResourceLocation(auraDir + "aurau2.png");
            color2 = 16776724;
        }

    }

    public EntityAura setParent(EntityAura aura, String thisName) {
        parent = aura;
        auraData.setAuraEntity(aura);
        parent.children.put(name = thisName, this);
        return this;
    }
    protected void entityInit() {
         ignoreFrustumCheck = true;
        renderPass =1;
        height = 20 * 0.11f;

    }

    public void updateDisplay() {
        if (entity.isInWater())
            ((IEntityMC) entity).setRenderPass(renderPass = 0);
        else if (renderPass == 0)
           ((IEntityMC) entity).setRenderPass(renderPass = ClientProxy.MiddleRenderPass);
     
        if (isKaioken) {
            if (parent.isVanillaDefault && !DBCForm.isSaiyanGod(auraData.getState()))
                fadeOut = true;

            return;
        }


        if (entity.ticksExisted % 10 == 0)
            load(false);


        if (!fadeOut && aura.display.type2D != EnumAuraTypes2D.None && JGConfigClientSettings.CLIENT_DA13) {
            float height = effectiveSize <= 0 ? entity.height : (this.height * 0.53f) * effectiveSize;
            ParticleFormHandler.spawnAura2D(entity, auraData, height);
        }


        String auraDir = "jinryuudragonbc:";
        //  setTexture(1, auraDir + "auragbpng");
        // setTexture(3, auraDir + "auragb.png");
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void despawn() {
        if (fadeOut)
            return;
        fadeOut = true;
        fadeOutSound();

        for (EntityAura child : children.values()) //children of root cannot have children
            child.despawn();
    }

    public void onUpdate() {

        if (isRoot() && !fadeOut) { //check aura death conditions
            Aura currentAura = PlayerDataUtil.getToggledAura(entity);
            if (!isVanillaDefault && (entity == null || entity.isDead || currentAura == null || aura != currentAura || auraData.getAuraEntity() != this))
                despawn();
            else if (isVanillaDefault && (!auraData.isAuraOn() || entity == null || entity.isDead || currentAura != null || !ConfigDBCClient.RevampAura))
                despawn();


        }
        isInKaioken = auraData.isForm(DBCForm.Kaioken);
        if (!isInKaioken && isKaioken && !fadeOut)
            despawn();


        if (fadeIn && !fadeOut)
            if (alpha < maxAlpha) {
                float fadeFactor = this.fadeFactor + (maxAlpha / 1) * 0.04f;
                alpha = Math.min(alpha + fadeFactor, maxAlpha);
                if (alpha >= maxAlpha)
                    fadeIn = false;
            }


        if (fadeOut) {
            float fadeFactor = this.fadeFactor + (maxAlpha / 1) * 0.04f;
            alpha -= fadeFactor;//(float) Math.pow(fadeFactor, 1 + (alpha / 1) * 7);
            if (alpha <= 0)
                setDead();
        }


        setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
        isTransforming = auraData.isTransforming();
        isCharging = auraData.isChargingKi() || isTransforming;

        updateDisplay();


    }


    @SideOnly(Side.CLIENT)
    public void playSound() {
        String sound = !isKaioken ? aura.display.getFinalSound() : aura.display.getFinalKKSound();
        if (sound != null) {
            auraSound = new AuraSound(aura, new SoundSource(sound, entity));
            if (isTransforming)
                auraSound.setVolume(0.2f);
            auraSound.setRepeat(true).play(false);
        }
    }

    @SideOnly(Side.CLIENT)
    public void fadeOutSound(){
        if (auraSound != null)
            auraSound.soundSource.fadeOut = true;
    }


    public EntityAuraRing spawnAuraRing(Entity entity, int color) {
        if (entity.ticksExisted % 20 != 0)
            return null;

        boolean isPlayer = entity instanceof EntityPlayer;
        EntityAuraRing ring = new EntityAuraRing(entity.worldObj, isPlayer ? entity.getCommandSenderName() : Utility.getEntityID(entity), color, 0, 0, 0);

        entity.worldObj.spawnEntityInWorld(ring);
        return ring;

    }

    public boolean shouldRenderInPass(int pass) {
        return pass == renderPass;
    }

    public double getYOffset(float size) { //for correctly offsetting aura size
        float scaledAuraHeight = height * size;
        float yOffset = -0.05f + scaledAuraHeight + scaledAuraHeight * (scaledAuraHeight / 50) * 2.25f;

        boolean client = Minecraft.getMinecraft().thePlayer == entity;
        float clientOffset = !client ? 1.62f : 0;

        return yOffset + clientOffset;
    }

    public EntityAura spawn() {
        ((IEntityMC) entity).setRenderPass(renderPass = ClientProxy.MiddleRenderPass);
        entity.ignoreFrustumCheck = true;

        entity.worldObj.spawnEntityInWorld(this);
        playSound();
        return this;
    }

    public boolean shouldRender() {
        return type3D != EnumAuraTypes3D.None && aura.display.kettleModeType != 1 && JGConfigClientSettings.CLIENT_DA14;
    }
    public void setDead() {
        super.setDead();
        ((IEntityMC) entity).setRenderPass(0); //rest player renderpass on aura despawn
        entity.ignoreFrustumCheck = false;


        if (auraData.getAuraEntity() == this)
            auraData.setAuraEntity(null);

        if (parent != null && parent.children != null && parent.children.containsKey(name))
            parent.children.remove(name);

    }

    public void setTexture(int type, String path){
        ResourceLocation loc = path == null ? null : new ResourceLocation(path);
        switch (type){
            case 2:
                text2 = loc;
                break;
            case 3:
                text3 = loc;
                break;
            default:
                text1 = loc;
                break;
        }
    }

    public EntityAura setIsVanilla(boolean bo) {
        this.isVanillaDefault = bo;
        return this;
    }

    protected void readEntityFromNBT(NBTTagCompound tagCompund) {

    }

    protected void writeEntityToNBT(NBTTagCompound tagCompound) {

    }
}
