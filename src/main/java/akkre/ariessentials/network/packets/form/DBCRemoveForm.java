package kamkeel.npcdbc.network.packets.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCFORM;

public class DBCRemoveForm extends AbstractPacket {
    public static final String packetName = "NPC|RemForm";

    private int formID;

    public DBCRemoveForm(int formID){
        this.formID = formID;
    }

    public DBCRemoveForm() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.formID);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCFORM))
            return;

        int formID = in.readInt();
        FormController.getInstance().delete(formID);
        NetworkUtility.sendCustomFormDataAll((EntityPlayerMP) player);
        NBTTagCompound compound = (new Form()).writeToNBT();
        Server.sendData((EntityPlayerMP) player, EnumPacketClient.GUI_DATA, compound);
    }
}
