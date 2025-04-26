package kamkeel.npcdbc.network.packets.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;

import java.io.IOException;

public final class DBCGetAura extends AbstractPacket {
    public static final String packetName = "NPC|GetAura";
    private int auraID;

    public DBCGetAura(int auraID) {
        this.auraID = auraID;
    }

    public DBCGetAura() {}

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
        NBTTagCompound compound = new NBTTagCompound();
        if (auraID != -1 && AuraController.getInstance().has(auraID)){
            Aura aura = (Aura) AuraController.getInstance().get(auraID);
            if(aura != null){
                compound = aura.writeToNBT();
                compound.setString("Type", "ViewAura");
            }
        }
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }
}
