package akkre.ariessentials.mixins.late;

import akkre.ariessentials.data.npc.DBCStats;

public interface INPCStats {
    DBCStats getDBCStats();

    boolean hasDBCData();
}
