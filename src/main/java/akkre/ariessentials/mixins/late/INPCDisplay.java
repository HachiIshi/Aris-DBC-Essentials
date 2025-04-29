package akkre.ariessentials.mixins.late;

import akkre.ariessentials.data.npc.DBCDisplay;

public interface INPCDisplay {
    DBCDisplay getDBCDisplay();

    boolean hasDBCData();
}
