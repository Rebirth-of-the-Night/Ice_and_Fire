package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class MyrmexAIFindGaurdingEntity<T extends EntityMyrmexBase> extends EntityAITarget {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super EntityMyrmexBase> targetEntitySelector;
    public EntityMyrmexSoldier myrmex;
    protected EntityMyrmexBase targetEntity;

    public MyrmexAIFindGaurdingEntity(EntityMyrmexSoldier myrmex) {
        super(myrmex, false, false);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(myrmex);
        this.targetEntitySelector = new Predicate<EntityMyrmexBase>() {
            @Override
            public boolean apply(@Nullable EntityMyrmexBase myrmex) {
                return myrmex != null && !(myrmex instanceof EntityMyrmexSoldier) && myrmex.getGrowthStage() > 1 && EntityMyrmexBase.haveSameHive(MyrmexAIFindGaurdingEntity.this.myrmex, myrmex) && !myrmex.isBeingGuarded && myrmex.needsGaurding();
            }
        };
        this.myrmex = myrmex;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || this.myrmex.getAttackTarget() != null || this.myrmex.guardingEntity != null) {
            return false;
        }
        List<EntityMyrmexBase> list = this.taskOwner.world.getEntitiesWithinAABB(EntityMyrmexBase.class, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
        if (list.isEmpty()) {
            return false;
        } else {
            list.sort(this.theNearestAttackableTargetSorter);
            this.myrmex.guardingEntity = list.get(0);
            return true;
        }
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(EntityMyrmexBase theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.getDistanceSq(p_compare_1_);
            double d1 = this.theEntity.getDistanceSq(p_compare_2_);
            return Double.compare(d0, d1);
        }
    }
}