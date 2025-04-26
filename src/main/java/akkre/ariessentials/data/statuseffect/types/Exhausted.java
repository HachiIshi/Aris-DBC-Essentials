package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;

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
