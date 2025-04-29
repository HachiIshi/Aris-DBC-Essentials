package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Darkness extends StatusEffect {

    public Darkness() {
        name = "Darkness";
        id = Effects.DARKNESS;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 144;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect){}
}
