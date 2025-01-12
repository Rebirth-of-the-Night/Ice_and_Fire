package com.github.alexthe666.iceandfire.entity;

import net.minecraft.util.DamageSource;

public class EntityDragonPart extends EntityMutlipartPart {

    private final EntityDragonBase dragon;

    public EntityDragonPart(EntityDragonBase dragon, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(dragon, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
        this.dragon = dragon;
        this.isImmuneToFire = dragon instanceof EntityFireDragon;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if(source.getTrueSource() != null && source.getTrueSource() == dragon) return false;
        return super.attackEntityFrom(source, damage);
    }

    @Override
    public void collideWithNearbyEntities() {
    }

    @Override
    public boolean shouldNotExist(){
        return this.dragon != null && !this.dragon.isEntityAlive() && !this.dragon.isModelDead();
    }
}