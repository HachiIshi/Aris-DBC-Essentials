package akkre.ariessentials;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.form.Form;
import akkre.ariessentials.scripted.DBCPlayerEvent;
import akkre.ariessentials.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

public class NPCEventHandler {

    @SubscribeEvent
    public void DBCAttackEvent(DBCPlayerEvent.DamagedEvent event) {
        if (event.player == null || event.player == null || event.player.getMCEntity() == null || event.player.getMCEntity().worldObj.isRemote)
            return;

        Form form = DBCData.getForm((EntityPlayer) event.player.getMCEntity());
        if (form != null) {
            float formLevel = PlayerDataUtil.getFormLevel(event.player.getMCEntity());

            if (form.mastery.hasDamageNegation()) {
                float damageNegation = form.mastery.damageNegation * form.mastery.calculateMulti("damageNegation", formLevel);
                float newDamage = event.getDamage() * (100 - damageNegation) / 100;
                event.setDamage(newDamage);
            }

        }
    }
}
