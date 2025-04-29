package akkre.ariessentials.network.packets.aura;

import io.netty.buffer.ByteBuf;
import akkre.ariessentials.controllers.AuraController;
import akkre.ariessentials.controllers.FormController;
import akkre.ariessentials.data.aura.Aura;
import akkre.ariessentials.data.form.Form;
import akkre.ariessentials.network.AbstractPacket;
import akkre.ariessentials.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;

import java.io.IOException;

import static akkre.ariessentials.network.DBCAddonPermissions.GLOBAL_DBCAURA;
import static akkre.ariessentials.network.DBCAddonPermissions.GLOBAL_DBCFORM;

public class DBCRemoveAura extends AbstractPacket {
    public static final String packetName = "NPC|RemAura";

    private int auraID;

    public DBCRemoveAura(int auraID){
        this.auraID = auraID;
    }

    public DBCRemoveAura() {

    }

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
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        int auraID = in.readInt();
        AuraController.getInstance().delete(auraID);
        NetworkUtility.sendCustomAuraDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Aura()).writeToNBT();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }
}
