package akkre.ariessentials.network.packets.form;

import io.netty.buffer.ByteBuf;
import akkre.ariessentials.network.AbstractPacket;
import akkre.ariessentials.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class DBCRequestFormWheel extends AbstractPacket {
    public static final String packetName = "NPC|RequestFormWheel";

    public DBCRequestFormWheel() {
    }


    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        NetworkUtility.sendPlayerFormWheel(player);
    }
}
