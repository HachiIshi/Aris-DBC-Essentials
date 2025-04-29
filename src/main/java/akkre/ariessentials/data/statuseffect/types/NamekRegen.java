package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.config.ConfigDBCGameplay;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class NamekRegen extends StatusEffect {

    public NamekRegen() {
        name = "NamekRegen";
        id = Effects.NAMEK_REGEN;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 48;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect){
        DBCData dbcData = DBCData.get(player);
        float currentBodyPercent = dbcData.stats.getCurrentBodyPercentage();
        float percentToRegen = ConfigDBCEffects.NamekRegenPercent * playerEffect.level;
        if(dbcData.Body != 0){
            if(currentBodyPercent < ConfigDBCGameplay.NamekianRegenMax){
                boolean kill = false;
                if (currentBodyPercent + percentToRegen > ConfigDBCGameplay.NamekianRegenMax) {
                    percentToRegen = ConfigDBCGameplay.NamekianRegenMax - currentBodyPercent;
                    kill = true;
                }
                dbcData.stats.restoreHealthPercent(percentToRegen);
                if (kill) {
                    playerEffect.kill();
                }
            } else {
                playerEffect.kill();
            }
        }
    }
}
