package akkre.ariessentials.mixins.late.impl.npc;

import io.netty.buffer.ByteBuf;
import kamkeel.addon.DBCAddon;
import akkre.ariessentials.constants.DBCDamageSource;
import akkre.ariessentials.controllers.DBCSyncController;
import akkre.ariessentials.controllers.FormController;
import akkre.ariessentials.data.form.Form;
import akkre.ariessentials.data.npc.DBCStats;
import akkre.ariessentials.mixins.late.INPCDisplay;
import akkre.ariessentials.mixins.late.INPCStats;
import akkre.ariessentials.mixins.late.IPlayerDBCInfo;
import akkre.ariessentials.network.NetworkUtility;
import akkre.ariessentials.scripted.DBCEventHooks;
import akkre.ariessentials.scripted.DBCPlayerEvent;
import akkre.ariessentials.util.DBCUtils;
import akkre.ariessentials.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

@Mixin(DBCAddon.class)
public class MixinDBCAddon {

    @Shadow(remap = false)
    public boolean supportEnabled;

    /**
     * @author Kamkeel
     * @reason Allow for Copying of Data for DBC Model information
     */
    @Overwrite(remap = false)
    public void dbcCopyData(EntityLivingBase copied, EntityLivingBase entity) {
        if (!supportEnabled)
            return;

        if (entity instanceof EntityNPCInterface && copied instanceof EntityNPCInterface) {
            EntityNPCInterface receiverNPC = (EntityNPCInterface) entity;
            EntityNPCInterface npc = (EntityNPCInterface) copied;
            INPCStats stats = (INPCStats) npc.stats;
            INPCStats receiverStats = (INPCStats) receiverNPC.stats;

            INPCDisplay display = (INPCDisplay) npc.display;
            INPCDisplay receiverDisplay = (INPCDisplay) receiverNPC.display;

            NBTTagCompound dbcStats = new NBTTagCompound();
            stats.getDBCStats().writeToNBT(dbcStats);
            receiverStats.getDBCStats().readFromNBT(dbcStats);

            receiverDisplay.getDBCDisplay().setEnabled(display.getDBCDisplay().isEnabled());
            receiverDisplay.getDBCDisplay().setFormAuraTypes(display.getDBCDisplay().getFormAuraTypes());
        }
    }

    /**
     * @author Kamkeel
     * @reason Checks if the DBC Attack event will be run
     */
    @Overwrite(remap = false)
    public boolean canDBCAttack(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if (!supportEnabled)
            return false;
        DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
        return dbcStats.enabled && receiver instanceof EntityPlayer && attackStrength > 0;
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public void doDBCDamage(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if (npc.isRemote())
            return;

        if (attackStrength <= 0)
            return;

        if (!(receiver instanceof EntityPlayer))
            return;

        if (npc.stats instanceof INPCStats) {
            EntityPlayer player = (EntityPlayer) receiver;
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();

            // Calculate DBC Damage
            int damageToHP = DBCUtils.calculateDBCStatDamage(player, (int) attackStrength, dbcStats);
            DamageSource damageSource = new NpcDamageSource("mob", npc);
            DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(player, damageToHP, damageSource, DBCDamageSource.NPC);
            if (DBCEventHooks.onDBCDamageEvent(damagedEvent))
                return;

            DBCUtils.lastSetDamage = (int) damagedEvent.damage;
            DBCUtils.doDBCDamage(player, damageToHP, dbcStats, damageSource);
        }
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public boolean isKO(EntityNPCInterface npc, EntityPlayer player) {
        if (npc.isRemote())
            return false;

        if (npc.stats instanceof INPCStats) {
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
            if (dbcStats.enabled && dbcStats.isFriendlyFist()) {
                int currentKO = getInt(player, "jrmcHar4va");
                return currentKO > 0;
            }
        }
        return false;
    }


    /**
     * @author Kamkeel
     * @reason Writes Custom Form Player Data
     */
    @Overwrite(remap = false)
    public void writeToNBT(PlayerData playerData, NBTTagCompound nbtTagCompound) {
        ((IPlayerDBCInfo) playerData).getPlayerDBCInfo().saveNBTData(nbtTagCompound);
    }

    /**
     * @author Kamkeel
     * @reason Reads Custom Form Player Data
     */
    @Overwrite(remap = false)
    public void readFromNBT(PlayerData playerData, NBTTagCompound nbtTagCompound) {
        ((IPlayerDBCInfo) playerData).getPlayerDBCInfo().loadNBTData(nbtTagCompound);
    }

    /**
     * @author Kamkeel
     * @reason Performs Syncing | SyncController. Sent by Server to Client
     */
    @Overwrite(remap = false)
    public void syncPlayer(EntityPlayerMP playerMP) {
        DBCSyncController.syncPlayer(playerMP);
    }
}
