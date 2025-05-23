package akkre.ariessentials;

import JinRyuu.JRMCore.entity.EntityCusPar;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import akkre.ariessentials.combat.Dodge;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.config.ConfigDBCGameplay;
import akkre.ariessentials.constants.DBCForm;
import akkre.ariessentials.constants.DBCRace;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.controllers.*;
import akkre.ariessentials.data.IAuraData;
import akkre.ariessentials.data.PlayerDBCInfo;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.form.Form;
import akkre.ariessentials.data.npc.DBCDisplay;
import akkre.ariessentials.entity.EntityAura;
import akkre.ariessentials.mixins.late.INPCDisplay;
import akkre.ariessentials.mixins.late.IPlayerDBCInfo;
import akkre.ariessentials.network.PacketHandler;
import akkre.ariessentials.network.packets.CapsuleInfo;
import akkre.ariessentials.network.packets.LoginInfo;
import akkre.ariessentials.util.DBCUtils;
import akkre.ariessentials.util.PlayerDataUtil;
import akkre.ariessentials.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.Iterator;
import java.util.Random;

public class ServerEventHandler {

    @SubscribeEvent
    public void loginEvent(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;
        DBCData dbcData = DBCData.get(event.player);
        dbcData.loadNBTData(true);
        PacketHandler.Instance.sendToPlayer(new CapsuleInfo(CapsuleInfo.InfoType.COOLDOWN).generatePacket(), (EntityPlayerMP) event.player);
        PacketHandler.Instance.sendToPlayer(new CapsuleInfo(CapsuleInfo.InfoType.STRENGTH).generatePacket(), (EntityPlayerMP) event.player);
        PacketHandler.Instance.sendToPlayer(new CapsuleInfo(CapsuleInfo.InfoType.EFFECT_TIME).generatePacket(), (EntityPlayerMP) event.player);
        PacketHandler.Instance.sendToPlayer(new LoginInfo().generatePacket(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void joinWorld(EntityJoinWorldEvent event) {
        Entity e = event.entity;

        if (e instanceof EntityPlayer || e instanceof EntityCustomNpc) {
            IAuraData auraData = PlayerDataUtil.getAuraData(e);
            EntityAura aura = auraData.getAuraEntity();
            if (aura != null) {
                aura.setDead();
                for (EntityAura child : aura.children.values()) //children of root cannot have children
                    child.setDead();
            }
            for (Iterator<EntityCusPar> iter = auraData.getParticles().iterator(); iter.hasNext(); ) {
                iter.next().setDead();
                iter.remove();
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;

        EntityPlayer player = event.player;
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            // Send Form Information
            if (PlayerDataController.Instance != null) {
                PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
                if (((IPlayerDBCInfo) playerData).getDBCInfoUpdate()) {
                    NBTTagCompound formCompound = new NBTTagCompound();
                    playerData.getDBCSync(formCompound);
                    NoppesUtilServer.sendDBCCompound((EntityPlayerMP) player, formCompound);
                    ((IPlayerDBCInfo) playerData).endDBCInfo();
                }
            }

            if (player.ticksExisted % ConfigDBCGameplay.CheckEffectsTick == 0)
                StatusEffectController.Instance.runEffects(player);

            if (ConfigDBCGameplay.WearableEarrings)
                if (player.ticksExisted % 60 == 0)
                    FusionHandler.checkNearbyPlayers(player);

            if (player.ticksExisted % 10 == 0) {
                // Keep the Player informed on their own data
                DBCData dbcData = DBCData.get(player);
                if (ConfigDBCGameplay.EnableNamekianRegen && dbcData.Race == DBCRace.NAMEKIAN)
                    dbcData.stats.applyNamekianRegen();

                if (player.ticksExisted % 20 == 0)
                    StatusEffectController.Instance.decrementEffects(dbcData.player);

                dbcData.syncTracking();
                // ChargeKi
            }
            handleFormProcesses(player);

            if (ConfigDBCGameplay.RevampKiCharging) {
                chargeKi(player);
            }
        }
    }

    public void chargeKi(EntityPlayer player) {
        DBCData dbcData = DBCData.getData(player);
        dbcData.loadCharging();
        if (!dbcData.isChargingKi())
            return;

        int chargeTick = 16 - (ConfigDBCGameplay.KiPotentialUnlock ? dbcData.stats.getPotentialUnlockLevel() : 6);
        if (player.ticksExisted % chargeTick != 0)
            return;

        int releaseFactor = ConfigDBCGameplay.KiChargeRate;
        boolean powerDown = dbcData.isFnPressed;
        byte release = dbcData.Release;

        byte maxRelease = (byte) ((byte) (50 + dbcData.stats.getPotentialUnlockLevel() * 5) + (byte) (StatusEffectController.Instance.hasEffect(player, Effects.OVERPOWER) ? ConfigDBCEffects.OVERPOWER_AMOUNT : 0));

        int newRelease = ValueUtil.clamp(!powerDown ? release + releaseFactor : release - releaseFactor, (byte) releaseFactor, maxRelease);
        dbcData.getRawCompound().setByte("jrmcRelease", (byte) newRelease);
    }

    @SubscribeEvent
    public void playerDeathEvent(LivingDeathEvent event) {
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote)
            return;

        if (event.entityLiving.worldObj instanceof WorldServer && event.entityLiving instanceof EntityPlayer) {
            StatusEffectController.getInstance().killEffects((EntityPlayer) event.entityLiving);

            PlayerDBCInfo dbcInfo = PlayerDataUtil.getDBCInfo((EntityPlayer) event.entityLiving);
            DBCData dbcData = DBCData.get((EntityPlayer) event.entityLiving);
            dbcData.addonFormID = -1;
            dbcInfo.currentForm = -1;
            dbcInfo.updateClient();
        }
    }

    public void handleFormProcesses(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        Form form = dbcData.getForm();
        if (form != null) {
            boolean isInSurvival = !player.capabilities.isCreativeMode;
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
            // Reverts player from form when ki or release are 0
            if (dbcData.Release <= 0 || dbcData.Ki <= 0) {
                formData.currentForm = -1;
                formData.updateClient();
                dbcData.loadNBTData(true);
            }

            if (!form.stackable.godStackable && dbcData.isForm(DBCForm.GodOfDestruction))
                dbcData.setForm(DBCForm.GodOfDestruction, false);

            if (!form.stackable.mysticStackable && dbcData.isForm(DBCForm.Mystic))
                dbcData.setForm(DBCForm.Mystic, false);

            if (!form.stackable.uiStackable && dbcData.isForm(DBCForm.UltraInstinct))
                dbcData.setForm(DBCForm.UltraInstinct, false);

            if (!form.stackable.kaiokenStackable && dbcData.isForm(DBCForm.Kaioken))
                dbcData.setForm(DBCForm.Kaioken, false);


            // Updates form Timer
            if (formData.hasTimer(form.id)) {
                formData.decrementTimer(form.id);
                if (player.ticksExisted % 20 == 0)
                    formData.updateClient();
            }
            if (form.mastery.hasKiDrain() && isInSurvival) {
                if (player.ticksExisted % 10 == 0) {

                    int might = DBCUtils.calculateKiDrainMight(dbcData, player);

                    double cost = might * form.mastery.getKiDrain();
                    cost *= ((double) dbcData.Release / 100);

                    if (JGConfigDBCFormMastery.FM_Enabled)
                        cost *= form.mastery.calculateMulti("kiDrain", formData.getCurrentLevel());

                    dbcData.stats.restoreKiFlat((int) (-cost / form.mastery.kiDrainTimer * 10));
                }
            }

            if (form.mastery.hasHeat() && player.ticksExisted % 20 == 0) {
                float heatToAdd = form.mastery.calculateMulti("heat", formData.getCurrentLevel());
                float newHeat = ValueUtil.clamp(dbcData.addonCurrentHeat + heatToAdd, 0, form.mastery.maxHeat);

                if (newHeat == form.mastery.maxHeat) {
                    int painTime = (int) (form.mastery.painTime * 60 / 5 * form.mastery.calculateMulti("pain", formData.getCurrentLevel()));
                    dbcData.getRawCompound().setInteger("jrmcGyJ7dp", painTime);
                    formData.currentForm = -1;
                    formData.updateClient();

                    newHeat = 0;
                }

                dbcData.getRawCompound().setFloat("addonCurrentHeat", newHeat);
            }

            if ((form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")) && DBCRace.isSaiyan(dbcData.Race) && !dbcData.hasTail()) {
                TransformController.handleFormDescend(player, -10);
            }
        }
    }

    @SubscribeEvent
    public void NPCAscend(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityCustomNpc && Utility.isServer(event.entity)) {
            EntityCustomNpc npc = (EntityCustomNpc) event.entity;
            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            if (!display.enabled)
                return;
            if (display.isTransforming && display.selectedForm != -1 && display.selectedForm != display.formID) {
                TransformController.npcAscend(npc, (Form) FormController.Instance.get(display.selectedForm));
            } else
                TransformController.npcDecrementRage(npc, display);
        }
    }

    @SubscribeEvent
    public void attackEvent(LivingAttackEvent event) {
        if (event.entity.worldObj.isRemote)
            return;

        if (event.entity instanceof EntityPlayer || event.entity instanceof EntityNPCInterface) {
            Entity attacker = event.source.getEntity();
            Form form = PlayerDataUtil.getForm(event.entity);

            if (form != null) {
                float formLevel = PlayerDataUtil.getFormLevel(event.entity);

                if (form.mastery.hasDodge()) {
                    Random rand = new Random();
                    float dodgeChance = form.mastery.dodgeChance * form.mastery.calculateMulti("dodge", formLevel);
                    if (dodgeChance >= rand.nextInt(100)) {
                        if (Dodge.dodge(event.entity, attacker)) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }
}
