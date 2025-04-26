package kamkeel.npcdbc.network.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.client.sound.ClientSound;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class PlaySound extends AbstractPacket {
    public static final String packetName = "NPC|PlaySound";

    public SoundSource sound;

    public PlaySound() {
    }

    public PlaySound(Entity entity, String soundDir) {
        sound = new SoundSource(soundDir, entity);
    }

    public PlaySound(SoundSource sound) {
        this.sound = sound;
    }


    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeNBT(out, sound.writeToNbt());
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        SoundSource sound = SoundSource.createFromNBT(ByteBufUtils.readNBT(in));

        if (Utility.isServer())
            play(sound);
        else
            playClient(sound);
    }

    public static void play(SoundSource sound) {
        if (sound == null || sound.entity == null)
            return;

        PacketHandler.Instance.sendToTrackingPlayers(sound.entity, new PlaySound(sound).generatePacket());
    }

    public static void play(Entity entity, String soundDir) {
        if (entity == null)
            return;
        SoundSource sound = new SoundSource(soundDir, entity);
        PacketHandler.Instance.sendToTrackingPlayers(sound.entity, new PlaySound(sound).generatePacket());
    }

    @SideOnly(Side.CLIENT)
    public static void playClient(SoundSource sound){
        new ClientSound(sound).play(false);
    }
}
