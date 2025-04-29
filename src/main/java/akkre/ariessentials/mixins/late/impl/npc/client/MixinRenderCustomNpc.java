package akkre.ariessentials.mixins.late.impl.npc.client;

import akkre.ariessentials.client.model.ModelDBC;
import akkre.ariessentials.mixins.late.IModelMPM;
import net.minecraft.client.model.ModelBase;
import noppes.npcs.client.model.ModelNPCMale;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.client.renderer.RenderNPCHumanMale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = RenderCustomNpc.class, remap = false)
public class MixinRenderCustomNpc extends RenderNPCHumanMale implements IModelMPM {


    public MixinRenderCustomNpc(ModelNPCMale mainmodel, ModelNPCMale armorChest, ModelNPCMale armor) {
        super(mainmodel, armorChest, armor);
    }

    @Unique
    @Override
    public ModelBase getMainModel() {
        return mainModel;
    }

    @Unique
    @Override
    public ModelDBC getDBCModel() {
        return null;
    }
}
