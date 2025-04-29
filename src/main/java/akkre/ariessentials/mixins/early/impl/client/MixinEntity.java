package akkre.ariessentials.mixins.early.impl.client;


import akkre.ariessentials.client.ClientProxy;
import akkre.ariessentials.mixins.early.IEntityMC;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Entity.class, remap = true)
public class MixinEntity implements IEntityMC {
    @Unique
    private int renderPass = 0;
    @Unique
    public boolean renderPassTampered;

    @Unique
    public void setRenderPass(int renderPass) {
        this.renderPass = renderPass;
        renderPassTampered = renderPass == ClientProxy.MiddleRenderPass;

    }

    @Unique
    public int getRenderPass() {
        return renderPass;
    }

    @Unique
    @Override
    public boolean getRenderPassTampered() {
        return renderPassTampered;
    }


    public boolean shouldRenderInPass(int pass) {
        return pass == renderPass;
    }

}
