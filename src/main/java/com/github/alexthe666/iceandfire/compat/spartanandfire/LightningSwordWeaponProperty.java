package com.github.alexthe666.iceandfire.compat.spartanandfire;

import com.github.alexthe666.iceandfire.entity.EntityDragonLightningBolt;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.util.ItemUtil;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponPropertyWithCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class LightningSwordWeaponProperty extends WeaponPropertyWithCallback {

    public LightningSwordWeaponProperty(String propType, String propModId) {
        super(propType, propModId);
    }

    public void onHitEntity(ToolMaterialEx material, ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, Entity projectile) {
        if (target instanceof EntityFireDragon || target instanceof EntityIceDragon) {
            target.attackEntityFrom(DamageSource.DROWN, 6.5F);
        }
        if(!attacker.world.isRemote && attacker.swingProgress < 0.2 && !target.isDead) target.world.spawnEntity(new EntityDragonLightningBolt(target.world, target.posX, target.posY, target.posZ, attacker, target));
        ItemUtil.knockbackWithDragonsteel(target, attacker);
    }
}