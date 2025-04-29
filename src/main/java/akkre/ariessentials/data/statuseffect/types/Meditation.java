package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.controllers.BonusController;
import akkre.ariessentials.data.PlayerBonus;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Meditation extends StatusEffect {

    public Meditation() {
        name = "Meditation";
        id = Effects.MEDITATION;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 112;
        iconY = 0;
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        DBCData dbcData = DBCData.get(player);
        PlayerBonus medBonus = new PlayerBonus(name, (byte) 1);
        medBonus.spirit = dbcData.SPI * ((float) ConfigDBCEffects.MeditationSpiBoostPercent / 100);
        BonusController.getInstance().applyBonus(player, medBonus);
    }

    @Override
    public void kill(EntityPlayer player, PlayerEffect playerEffect) {
        BonusController.getInstance().removeBonus(player, name);
    }
}
