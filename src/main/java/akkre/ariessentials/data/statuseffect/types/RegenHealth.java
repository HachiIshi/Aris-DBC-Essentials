package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class RegenHealth extends StatusEffect {

    public RegenHealth() {
        name = "Health Regeneration";
        id = Effects.REGEN_HEALTH;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 0;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        int percentToRegen = ConfigDBCEffects.HealthRegenPercent * playerEffect.level;
        if(dbcData.Body > 0)
            dbcData.stats.restoreHealthPercent(percentToRegen);
    }
}
