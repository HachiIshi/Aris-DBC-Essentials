package akkre.ariessentials.constants.enums;

import akkre.ariessentials.config.ConfigDBCEffects;
import akkre.ariessentials.config.ConfigDBCGameplay;

public enum EnumPotaraTypes {

    One("One", ConfigDBCGameplay.PotaraOneTime, (float) ConfigDBCEffects.TierOneMulti),
    Two("Two", ConfigDBCGameplay.PotaraTwoTime, (float) ConfigDBCEffects.TierTwoMulti),
    Three("Three", ConfigDBCGameplay.PotaraThreeTime, (float) ConfigDBCEffects.TierThreeMulti);

    private final String name;
    private final int length;
    private final float multi;

    EnumPotaraTypes(String name, int length, float multi){
        this.name = name;
        this.length = length;
        this.multi = multi;
    }

    public int getMeta(){
        return this.ordinal();
    }

    public String getName(){
        return this.name;
    }

    public int getLength(){
        return this.length;
    }

    public float getMulti(){
        return this.multi;
    }

    public static int count(){
        return values().length;
    }

    public static EnumPotaraTypes getPotaraFromMeta(int meta){
        for(EnumPotaraTypes potaraTypes : EnumPotaraTypes.values()){
            if(potaraTypes.getMeta() == meta){
                return potaraTypes;
            }
        }
        return One;
    }
}
