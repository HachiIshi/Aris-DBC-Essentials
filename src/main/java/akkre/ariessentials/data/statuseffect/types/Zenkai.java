package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.constants.DBCRace;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.controllers.BonusController;
import akkre.ariessentials.controllers.StatusEffectController;
import akkre.ariessentials.data.PlayerBonus;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Zenkai extends StatusEffect {
    public PlayerBonus saiyanZenkai;
    public PlayerBonus halfSaiyanZenkai;
    public Zenkai() {
        name = "Zenkai";
        id = Effects.ZENKAI;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 160;
        iconY = 0;
        length = ConfigDBCEffects.ZenkaiHALFLength;

        saiyanZenkai = new PlayerBonus("Saiyan" + name, (byte) 0, (float) ConfigDBCEffects.ZenkaiSaiyanStr, (float) ConfigDBCEffects.ZenkaiSaiyanDex, (float) ConfigDBCEffects.ZenkaiSaiyanWil);
        halfSaiyanZenkai = new PlayerBonus("HalfSaiyan" + name, (byte) 0, (float) ConfigDBCEffects.ZenkaiHALFStr, (float) ConfigDBCEffects.ZenkaiHALFDex, (float) ConfigDBCEffects.ZenkaiHALFWil);
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        DBCData dbcData = DBCData.get(player);
        if(dbcData.Race == DBCRace.SAIYAN)
            BonusController.getInstance().applyBonus(player, saiyanZenkai);
        else
            BonusController.getInstance().applyBonus(player, halfSaiyanZenkai);
    }

    @Override
    public void kill(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        if(dbcData.Race == DBCRace.SAIYAN)
            BonusController.getInstance().removeBonus(player, saiyanZenkai);
        else
            BonusController.getInstance().removeBonus(player, halfSaiyanZenkai);

        StatusEffectController.getInstance().applyEffect(player, Effects.EXHAUSTED);
    }
}
