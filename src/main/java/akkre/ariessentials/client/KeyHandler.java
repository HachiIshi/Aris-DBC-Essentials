package kamkeel.npcdbc.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
    public static KeyBinding FormWheelKey = new KeyBinding("Form Wheel", Keyboard.KEY_Y, "key.categories.customnpc");

    public static void registerKeys() {
        ClientRegistry.registerKeyBinding(FormWheelKey);
    }
}
