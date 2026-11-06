package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityDragonArrow extends EntityArrow {

    public EntityDragonArrow(World worldIn) {
        super(worldIn);
        this.setDamage(10);
    }

    public EntityDragonArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setDamage(10);
    }

    public EntityDragonArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        this.setDamage(10);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setDouble("damage", 10);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setDamage(10);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(IafItemRegistry.dragonbone_arrow);
    }

}