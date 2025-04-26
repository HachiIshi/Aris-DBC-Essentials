package kamkeel.npcdbc.network.packets.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

public final class DBCSelectAura extends AbstractPacket {
    public static final String packetName = "NPC|SelectAura";
    private int auraID;

    public DBCSelectAura(int auraID) {
        this.auraID = auraID;
    }

    public DBCSelectAura() {}

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
        if(auraID == -1)
            dbcInfo.currentAura = -1;
        dbcInfo.selectedAura = -1;
        NBTTagCompound compound = new NBTTagCompound();
        if (auraID != -1 && AuraController.getInstance().has(auraID)){
            if(dbcInfo.hasAuraUnlocked(auraID)){
                Aura aura = (Aura) AuraController.getInstance().get(auraID);
                dbcInfo.selectedAura = auraID;
                NetworkUtility.sendServerMessage(player, "§b", "npcdbc.auraSelect", " ", aura.getMenuName());
                compound = aura.writeToNBT();
            }
        } else {
            NetworkUtility.sendServerMessage(player, "§9", "npcdbc.clearedSelection");
        }

        dbcInfo.updateClient();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
        DBCData.get(player).saveNBTData(true);
    }
}
