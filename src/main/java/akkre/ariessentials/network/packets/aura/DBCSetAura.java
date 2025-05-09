package akkre.ariessentials.network.packets.aura;

import io.netty.buffer.ByteBuf;
import akkre.ariessentials.controllers.AuraController;
import akkre.ariessentials.data.PlayerDBCInfo;
import akkre.ariessentials.data.dbcdata.DBCData;
import akkre.ariessentials.network.AbstractPacket;
import akkre.ariessentials.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

public final class DBCSetAura extends AbstractPacket {
    public static final String packetName = "NPC|SetAura";
    private int auraID;

    public DBCSetAura(int auraID) {
        this.auraID = auraID;
    }

    public DBCSetAura() {}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.auraID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int auraID = in.readInt();
        PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
        PlayerDBCInfo dbcInfo = PlayerDataUtil.getDBCInfo(playerData);
        dbcInfo.currentAura = -1;
        if (auraID != -1 && AuraController.getInstance().has(auraID)){
            if(dbcInfo.hasAuraUnlocked(auraID)){
                dbcInfo.currentAura = auraID;
            }
        }
        dbcInfo.updateClient();
        DBCData.get(player).saveNBTData(true);
    }
}
