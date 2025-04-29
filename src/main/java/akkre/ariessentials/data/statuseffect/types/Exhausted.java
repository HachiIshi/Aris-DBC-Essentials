package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.data.statuseffect.StatusEffect;

public class Exhausted extends StatusEffect {
    public Exhausted() {
        name = "Exhausted";
        id = Effects.EXHAUSTED;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 192;
        iconY = 0;
        length = ConfigDBCEffects.EXHAUST_TIME * 60;
        lossOnDeath = false;
    }
}
