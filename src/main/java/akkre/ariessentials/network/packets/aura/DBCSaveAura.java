package kamkeel.npcdbc.network.packets.aura;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCSaveAura extends AbstractPacket {
    public static final String packetName = "NPC|SaveAura";

    private String prevName;
    private NBTTagCompound aura;

    public DBCSaveAura(NBTTagCompound compound, String prev){
        this.aura = compound;
        this.prevName = prev;
    }

    public DBCSaveAura() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        Server.writeString(out, prevName);
        Server.writeNBT(out, aura);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        String prevName = Server.readString(in);
        if(!prevName.isEmpty()){
            AuraController.getInstance().deleteAuraFile(prevName);
        }
        Aura aura = new Aura();
        aura.readFromNBT(Server.readNBT(in));
        AuraController.getInstance().saveAura(aura);
        NetworkUtility.sendCustomAuraDataAll((EntityPlayerMP) player);
    }
}
