package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDragonsteelArmor extends ItemArmor implements IProtectAgainstDragonItem {

    private final ArmorMaterial material;

    public ItemDragonsteelArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot, String gameName, String name) {
        super(material, renderIndex, slot);
        this.material = material;
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setTranslationKey(name);
        this.setRegistryName(IceAndFire.MODID, gameName);
    }

    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (material == IafItemRegistry.dragonsteel_fire_armor) {
            return (ModelBiped) IceAndFire.PROXY.getArmorModel(renderIndex == 2 ? 11 : 10);
        } if (material == IafItemRegistry.dragonsteel_ice_armor) {
            return (ModelBiped) IceAndFire.PROXY.getArmorModel(renderIndex == 2 ? 13 : 12);
        } else {
            return (ModelBiped) IceAndFire.PROXY.getArmorModel(renderIndex == 2 ? 19 : 18);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("item.dragonscales_armor.desc"));
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (material == IafItemRegistry.dragonsteel_fire_armor) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_fire" + (renderIndex == 2 ? "_legs.png" : ".png");
        } if (material == IafItemRegistry.dragonsteel_ice_armor) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_ice" + (renderIndex == 2 ? "_legs.png" : ".png");
        } else {
            return "iceandfire:textures/models/armor/armor_dragonsteel_lightning" + (renderIndex == 2 ? "_legs.png" : ".png");
        }
    }
}
