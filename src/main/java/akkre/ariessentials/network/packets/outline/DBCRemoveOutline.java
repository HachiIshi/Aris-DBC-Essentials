package akkre.ariessentials.network.packets.outline;

import io.netty.buffer.ByteBuf;
import akkre.ariessentials.controllers.OutlineController;
import akkre.ariessentials.data.outline.Outline;
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

public class DBCRemoveOutline extends AbstractPacket {
    public static final String packetName = "NPC|RemOutline";

    private int outlineID;

    public DBCRemoveOutline(int outlineID){
        this.outlineID = outlineID;
    }

    public DBCRemoveOutline() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.outlineID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        int outlineID = in.readInt();
        OutlineController.getInstance().delete(outlineID);
        NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Outline()).writeToNBT();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }
}
