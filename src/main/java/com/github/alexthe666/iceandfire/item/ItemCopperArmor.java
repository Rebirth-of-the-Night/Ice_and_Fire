package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCopperArmor extends ItemArmor {
    
    public ItemCopperArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot, String gameName, String name) {
        super(material, renderIndex, slot);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setTranslationKey(name);
        this.setRegistryName(IceAndFire.MODID, gameName);
    }

    @SideOnly(Side.CLIENT)
    public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default) {
        return (ModelBiped) IceAndFire.PROXY.getArmorModel(renderIndex == 2 ? 21 : 20);
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = this.getArmorMaterial().getRepairItemStack();
        if (this.getArmorMaterial() == IafItemRegistry.copperArmor) {
            NonNullList<ItemStack> copperItems = OreDictionary.getOres("ingotCopper");
            for (ItemStack ingot : copperItems) {
                if (OreDictionary.itemMatches(repair, ingot, false)) {
                    return true;
                }
            }
        }
        if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
    }
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "iceandfire:textures/models/armor/" + (slot == EntityEquipmentSlot.LEGS ? "armor_copper_metal_layer_2" : "armor_copper_metal_layer_1") + ".png";
    }
}
