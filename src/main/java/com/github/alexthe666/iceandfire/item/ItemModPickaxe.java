package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.util.ItemUtil;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModPickaxe extends ItemPickaxe {
    private final int toolID;
	
	public ItemModPickaxe(ToolMaterial toolmaterial, String gameName, String name) {
		this(toolmaterial, gameName, name, -1);
	}
	
	public ItemModPickaxe(ToolMaterial toolmaterial, String gameName, String name, int toolID) {
		super(toolmaterial);
		this.setTranslationKey(name);
		this.setCreativeTab(IceAndFire.TAB_ITEMS);
		this.setRegistryName(IceAndFire.MODID, gameName);
		this.toolID = toolID;
	}

	@Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    	return ItemUtil.getIsRepairable(toolMaterial, toRepair, repair, super.getIsRepairable(toRepair, repair));
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    	switch(this.toolID) {
    	case 0: 
    		ItemUtil.hitWithSilver(target, this.toolMaterial, 4.0F);
    		break;
    	case 1: 
    		ItemUtil.hitWithMyrmex(target, this.toolMaterial, 6.0F);
    		break;
    	case 2: 
    		ItemUtil.hitWithFireDragonsteel(target, attacker);
    		break;
    	case 3: 
    		ItemUtil.hitWithIceDragonsteel(target, attacker);
    		break;
    	case 4: 
    		ItemUtil.hitWithLightningDragonsteel(target, attacker);
    		break;
    	}
    	return super.hitEntity(stack, target, attacker);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	switch(this.toolID) {
    	case 0:
    		ItemUtil.getSilverComment(tooltip);
    		break;
    	case 1:
    		ItemUtil.getMyrmexComment(tooltip);
    		break;
    	case 2: 
    		ItemUtil.getFireDragonsteelComment(tooltip);
    		break;
    	case 3: 
    		ItemUtil.getIceDragonsteelComment(tooltip);
    		break;
    	case 4:
    		ItemUtil.getLightningDragonsteelComment(tooltip);
    		break;
    	}
    }
}
