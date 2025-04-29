package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.data.statuseffect.StatusEffect;

public class HumanSpirit extends StatusEffect {

    public HumanSpirit() {
        name = "HumanSpirit";
        id = Effects.HUMAN_SPIRIT;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 192;
        iconY = 0;
        length = ConfigDBCEffects.HumanSpiritLength;
        lossOnDeath = false;
    }
}
