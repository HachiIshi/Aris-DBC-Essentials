package kamkeel.npcdbc.mixins.late.impl.npc;

import kamkeel.command.CommandKamkeel;
import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.command.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandKamkeel.class)
public abstract class MixinCommandKamkeel {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void init(CallbackInfo ci) {
        registerCommand(new FormCommand());
        registerCommand(new AuraCommand());
        registerCommand(new OutlineCommand());
        registerCommand(new FormMasteryCommand());
        registerCommand(new EffectCommand());
    }

    @Shadow
    public void registerCommand(CommandKamkeelBase command) {}
}
