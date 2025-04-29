package akkre.ariessentials.mixins.late;

import akkre.ariessentials.data.PlayerDBCInfo;

public interface IPlayerDBCInfo {
    PlayerDBCInfo getPlayerDBCInfo();

    boolean getDBCInfoUpdate();

    void updateDBCInfo();

    void endDBCInfo();
}
