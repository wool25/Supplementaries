package net.mehvahdjukaar.supplementaries.common.entities;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public interface ISlimeable {

    int supp$getSlimedTicks();

    void supp$setSlimedTicks(int slimed);

    static float getAlpha(LivingEntity le, float partialTicks) {
        float slimeTicks = ((ISlimeable) le).supp$getSlimedTicks() + 1 - partialTicks;
        float maxFade = 60;
        return slimeTicks > maxFade ? 1 : Mth.clamp(slimeTicks / maxFade, 0, 1);
    }
}
