package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.util.IsImmune;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityDragonLightningBolt extends EntityLightningBolt {
    /** Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc. */
    private int lightningState;
    /** Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time. */
    private int boltLivingTime;
    
    @Nullable
    public final EntityLivingBase summoner;
    @Nullable
    public final EntityLivingBase mainTarget;

    public EntityDragonLightningBolt(World worldIn) {
        this(worldIn, 0.0f, 0.0f, 0.0f, null, null);
    }
	
	public EntityDragonLightningBolt(World worldIn, double x, double y, double z, @Nullable EntityLivingBase summoner, @Nullable EntityLivingBase target) {
		super(worldIn, x, y, z, true);
        this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = 2;
        this.summoner = summoner;
        this.mainTarget = target;
	}
	
	@Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
	
    public void onUpdate() {
		if (!this.world.isRemote) {
			this.setFlag(6, this.isGlowing());
		}

		this.onEntityUpdate();

        if (this.lightningState == 2) {
            this.world.playSound(null, new BlockPos(this), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSound(null, new BlockPos(this), SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        --this.lightningState;

        if (this.lightningState < 0) {
            if (this.boltLivingTime == 0)  {
                this.setDead();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                --this.boltLivingTime;
                this.lightningState = 1;
            }
        }

        if (this.lightningState >= 0) {
            if (this.world.isRemote) {
                this.world.setLastLightningBolt(2);
            } else {
                List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.posX - 3.0D, this.posY - 3.0D, this.posZ - 3.0D, this.posX + 3.0D, this.posY + 6.0D + 3.0D, this.posZ + 3.0D));
                for (EntityLivingBase entity : list) {
                    if (!IsImmune.toDragonLightning(entity) && this.summoner != null && this.mainTarget != null && !entity.equals(this.summoner) && !entity.equals(this.mainTarget) && !net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                        entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, (float) IceAndFire.CONFIG.dragonAttackDamageLightning);
                    }
                }
            }
        }
    }
}
