package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Chocolated extends StatusEffect {

    public Chocolated() {
        name = "Chocolated";
        id = Effects.CHOCOLATED;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 96;
        iconY = 0;
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect){}
}
