package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.IDreadMob;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

import javax.annotation.Nullable;

public class DreadAITargetNonDread extends EntityAINearestAttackableTarget<EntityLivingBase> {

    public DreadAITargetNonDread(EntityCreature entityIn, Class<EntityLivingBase> classTarget, boolean checkSight, Predicate<? super EntityLivingBase> targetSelector) {
        super(entityIn, classTarget, 0, checkSight, false, targetSelector);
    }

    @Override
    protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean includeInvincibles) {
        return target != null
                && target.attackable()
                && !(target instanceof IDreadMob)
                && super.isSuitableTarget(target, includeInvincibles)
                && DragonUtils.isAlive(target);
    }
}
