package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.BlockEggInIce;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;

import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class DragonType {

    public static final DragonType FIRE = new DragonType("fire");
    public static final DragonType ICE = new DragonType("ice").setPiscivore();
    public static final DragonType LIGHTNING = new DragonType("lightning");
    
    private String name;
    private boolean piscivore;

    public DragonType(String name) {
        this.name = name;
    }
    public static String getNameFromInt(int type){
        if(type == 2){
            return "lightning";
        }else if (type == 1){
            return "ice";
        }else{
            return "fire";
        }
    }
    public static int getIntFromType(DragonType type) {
        if(type == LIGHTNING) {
            return 2;
        } else if (type == ICE) {
            return 1;
        } else {
            return 0;
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPiscivore() {
        return piscivore;
    }

    public DragonType setPiscivore(){
        piscivore = true;
        return this;
    }

    public void updateEggCondition(EntityDragonEgg egg) {
        BlockPos pos = new BlockPos(egg);
        if(this == FIRE){
            if (egg.world.getBlockState(pos).getMaterial() == Material.FIRE) {
                egg.setDragonAge(egg.getDragonAge() + 1);
            }
            if (egg.getDragonAge() > IceAndFire.CONFIG.dragonEggTime) {
                if (egg.world.getBlockState(pos).getMaterial() == Material.FIRE) {
                    egg.world.setBlockToAir(pos);
                    EntityFireDragon dragon = new EntityFireDragon(egg.world);
                    if(egg.hasCustomName()){
                        dragon.setCustomNameTag(egg.getCustomNameTag());
                    }
                    dragon.setVariant(egg.getType().ordinal());
                    dragon.setGender(egg.getRNG().nextBoolean());
                    dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    dragon.setHunger(50);
                    if (!egg.world.isRemote) {
                        egg.world.spawnEntity(dragon);
                    }
                    dragon.setTamed(true);
                    dragon.setOwnerId(egg.getOwnerId());
                    egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                    egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, IafSoundRegistry.DRAGON_HATCH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                    egg.setDead();
                }

            }
        }
        if(this == ICE){
            if (egg.world.getBlockState(pos).getMaterial() == Material.WATER && egg.getRNG().nextInt(500) == 0) {
                egg.setDead();
                egg.world.setBlockState(pos, IafBlockRegistry.eggInIce.getDefaultState());
                egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, SoundEvents.BLOCK_GLASS_BREAK, egg.getSoundCategory(), 2.5F, 1.0F, false);
                if (egg.world.getBlockState(pos).getBlock() instanceof BlockEggInIce) {
                    ((TileEntityEggInIce) egg.world.getTileEntity(pos)).type = egg.getType();
                    ((TileEntityEggInIce) egg.world.getTileEntity(pos)).ownerUUID = egg.getOwnerId();
                }
            }
        }
        if(this == LIGHTNING) {
            if (egg.world.isRainingAt(pos) && egg.world.canSeeSky(egg.getPosition().up())) {
                egg.setDragonAge(egg.getDragonAge() + 1);
            }
            if (egg.getDragonAge() > IceAndFire.CONFIG.dragonEggTime) {
                if (egg.world.isRainingAt(pos)) {
                    egg.world.setBlockToAir(pos);
                    EntityLightningDragon dragon = new EntityLightningDragon(egg.world);
                    EntityLightningBolt lightningBolt = new EntityLightningBolt(egg.world, egg.posX, egg.posY, egg.posZ, true);
                    if(egg.hasCustomName()){
                        dragon.setCustomNameTag(egg.getCustomNameTag());
                    }
                    dragon.setVariant(egg.getType().ordinal() - 8);
                    dragon.setGender(egg.getRNG().nextBoolean());
                    dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    dragon.setHunger(50);
                    if (!egg.world.isRemote) {
                        egg.world.spawnEntity(dragon);
                        egg.world.addWeatherEffect(lightningBolt);
                    }
                    dragon.setTamed(true);
                    dragon.setOwnerId(egg.getOwnerId());
                    egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, egg.getSoundCategory(), 2.5F, 1.0F, false);
                    egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, IafSoundRegistry.DRAGON_HATCH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                    egg.setDead();
                }
            }
        }
    }
}