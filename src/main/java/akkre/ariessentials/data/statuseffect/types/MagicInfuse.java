package akkre.ariessentials.data.statuseffect.types;

import akkre.ariessentials.CustomNpcPlusDBC;
import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.constants.Effects;
import akkre.ariessentials.controllers.BonusController;
import akkre.ariessentials.data.PlayerBonus;
import akkre.ariessentials.data.PlayerDBCInfo;
import akkre.ariessentials.data.aura.Aura;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.data.statuseffect.PlayerEffect;
import akkre.ariessentials.data.statuseffect.StatusEffect;
import akkre.ariessentials.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FruitOfMight extends StatusEffect {
    public static Aura fruitOfMightAura = null;
    public float kiToDrain;
    public PlayerBonus fruitOfMightBonus;

    public FruitOfMight() {
        name = "FruitOfMight";
        id = Effects.FRUIT_OF_MIGHT;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        iconX = 224;
        iconY = 0;
        length = ConfigDBCEffects.FOM_EffectLength;
        fruitOfMightBonus = new PlayerBonus(name, (byte) 0, (float) ConfigDBCEffects.FOM_Strength, (float) ConfigDBCEffects.FOM_Dex, (float) ConfigDBCEffects.FOM_Will);
        kiToDrain = (float) ConfigDBCEffects.FOM_KiDrain;

        if (fruitOfMightAura == null) {
            fruitOfMightAura = new Aura();
            fruitOfMightAura.id = -10;
            fruitOfMightAura.display.setColor("color1", 0x0); //black
            fruitOfMightAura.display.setColor("color3", 0xb329ba); //purple
            fruitOfMightAura.display.hasLightning = true;
            fruitOfMightAura.display.lightningColor = 0xb329ba; //purple
        }
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        BonusController.getInstance().applyBonus(player, fruitOfMightBonus);
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        c.currentAura = fruitOfMightAura.id;
        c.updateClient();
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        dbcData.stats.restoreKiPercent(kiToDrain);
        if (dbcData.Ki <= 0)
            playerEffect.kill();
    }

    @Override
    public void kill(EntityPlayer player, PlayerEffect playerEffect) {
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        if (c.currentAura == fruitOfMightAura.id) {
            c.currentAura = -1;
            c.updateClient();
        }
        BonusController.getInstance().removeBonus(player, fruitOfMightBonus);
    }
}
