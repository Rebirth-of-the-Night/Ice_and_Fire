package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBallistaArrow extends EntityThrowable
{
    public Entity shooter;
    public ItemStack revolver;

    public EntityBallistaArrow(World w)
    {
        super(w);
        this.setEntityBoundingBox(new AxisAlignedBB(2, 2, 2, 2, 2, 2));
    }

    public EntityBallistaArrow(World w, EntityLivingBase shooter)
    {
        super(w, shooter);
        revolver = shooter.getHeldItemMainhand();
        this.shooter = shooter;
        float speedIndex = 3;

        this.motionX *=3;
        this.motionY *=3;
        this.motionZ *=3;

        this.motionX *= speedIndex;
        this.motionY *= speedIndex;
        this.motionZ *= speedIndex;
    }

    public void onUpdate()
    {
        if(this.world instanceof WorldServer)
            ((WorldServer) world).spawnParticle(EnumParticleTypes.FLAME, this.posX, this.posY, this.posZ, 1, 0, 0, 0, 0D);

        if(this.ticksExisted >= 1000)
            this.setDead();

        super.onUpdate();

        if(!isDead)
        {
            ++ticksExisted;
            onUpdate();
        }
    }

    protected float getGravityVelocity()
    {
        return 0;
    }

    @Override
    protected void onImpact(RayTraceResult object)
    {
        if(this.isDead)
            return;

        if(world.isRemote)
            return;

        if(object.typeOfHit == RayTraceResult.Type.BLOCK)
            this.setDead();

        if(object.typeOfHit == RayTraceResult.Type.ENTITY)
        {
            Entity e = object.entityHit;
            if(e == shooter)
                return;

            if(e instanceof EntityLivingBase && !(e instanceof IDreadMob))
            {
                EntityLivingBase elb = (EntityLivingBase) e;
                int attackDamage = IceAndFire.CONFIG.ballistaBaseDamage;

                if(!elb.onGround)
                    attackDamage *= 1.5;
                if(elb instanceof EntityDragonBase)
                    attackDamage *= 5;

                elb.attackEntityFrom(DamageSource.causeIndirectMagicDamage(elb, shooter), attackDamage);
            }
            this.setDead();
        }
    }
}