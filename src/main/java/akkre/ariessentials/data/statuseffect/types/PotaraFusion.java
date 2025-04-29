package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.constants.enums.EnumPotaraTypes;
import akkre.ariessentials.controllers.BonusController;
import akkre.ariessentials.data.PlayerBonus;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class PotaraFusion extends StatusEffect {

    public PotaraFusion() {
        name = "Potara";
        id = Effects.POTARA;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 176;
        iconY = 0;
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        EnumPotaraTypes potaraTypes = EnumPotaraTypes.getPotaraFromMeta(playerEffect.level);
        float bonusMulti = potaraTypes.getMulti();
        if(bonusMulti > 0){
            PlayerBonus bonus = new PlayerBonus(name, (byte) 0, bonusMulti, bonusMulti, bonusMulti);
            BonusController.getInstance().applyBonus(player, bonus);
        }
    }

    @Override
    public void kill(EntityPlayer player, PlayerEffect playerEffect) {
        BonusController.getInstance().removeBonus(player, name);
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        boolean isFused = dbcData.containsSE(10) || dbcData.containsSE(11);
        if(!isFused){
            playerEffect.kill();
            BonusController.getInstance().removeBonus(player, name);
        }
    }
}
