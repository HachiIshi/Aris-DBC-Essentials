package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Bloated extends StatusEffect {

    public Bloated() {
        name = "Bloated";
        id = Effects.BLOATED;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 128;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect){}
}
