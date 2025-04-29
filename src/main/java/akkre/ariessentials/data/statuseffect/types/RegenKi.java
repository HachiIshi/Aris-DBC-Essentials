package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class RegenKi extends StatusEffect {

    public RegenKi() {
        name = "Ki Regeneration";
        id = Effects.REGEN_KI;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 16;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        int percentToRestore = ConfigDBCEffects.KiRegenPercent * playerEffect.level;
        dbcData.stats.restoreKiPercent(percentToRestore);
    }
}
