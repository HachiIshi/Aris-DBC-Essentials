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

import java.io.IOException;

import static akkre.ariessentials.network.DBCAddonPermissions.GLOBAL_DBCAURA;

public class DBCSaveOutline extends AbstractPacket {
    public static final String packetName = "NPC|SaveOutline";

    private String prevName;
    private NBTTagCompound outline;

    public DBCSaveOutline(NBTTagCompound compound, String prev){
        this.outline = compound;
        this.prevName = prev;
    }

    public DBCSaveOutline() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        Server.writeString(out, prevName);
        Server.writeNBT(out, outline);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA))
            return;

        String prevName = Server.readString(in);
        if(!prevName.isEmpty()){
            OutlineController.getInstance().deleteOutlineFile(prevName);
        }
        Outline outline = new Outline();
        outline.readFromNBT(Server.readNBT(in));
        OutlineController.getInstance().saveOutline(outline);
        NetworkUtility.sendCustomOutlineDataAll((EntityPlayerMP) player);
    }
}
