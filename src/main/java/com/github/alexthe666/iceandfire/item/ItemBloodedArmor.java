package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.enums.EnumBloodedDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/*
 *
 * Dragon Blood Armor From Ice and Fire - RLCraft Edition
 * Code by Shivaxi, FonnyMunkey, Kotlin-Programmer and ArtsyDy
 * Under LGPL-3.0 License
 * Port by keletu
 *
 * */
public class ItemBloodedArmor extends ItemArmor {

    public EnumBloodedDragonArmor armor_type;
    public EnumDragonEgg eggType;

    public ItemBloodedArmor(EnumDragonEgg eggType, EnumBloodedDragonArmor armorType, ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.armor_type = armorType;
        this.eggType = eggType;
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
    }

    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        DragonType type = armor_type.eggType.dragonType;
        if (type == DragonType.FIRE) {
            return (ModelBiped) IceAndFire.PROXY.getArmorModel(renderIndex == 2 ? 23 : 22);
        } else if (type == DragonType.ICE) {
            return (ModelBiped) IceAndFire.PROXY.getArmorModel(renderIndex == 2 ? 25 : 24);
        }
        return (ModelBiped) IceAndFire.PROXY.getArmorModel(renderIndex == 2 ? 27 : 26);
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "iceandfire:textures/models/armor/" + armor_type.name() + (renderIndex == 2 ? "_legs.png" : ".png");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(eggType.color + I18n.translateToLocal("dragon." + eggType.toString().toLowerCase()));
    }
}