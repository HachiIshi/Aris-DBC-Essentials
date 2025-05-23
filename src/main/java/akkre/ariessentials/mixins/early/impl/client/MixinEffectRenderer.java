package akkre.ariessentials.mixins.early.impl.client;

import net.minecraft.client.particle.EffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EffectRenderer.class)
public abstract class MixinEffectRenderer {

    /**
     * @author jss2a98aj
     * @reason Makes most particles render with the expected depth.
     */
    @Redirect(method = "renderParticles", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V", ordinal = 0))
    private void skipGlDepthMask(boolean flag) {
    }

}
