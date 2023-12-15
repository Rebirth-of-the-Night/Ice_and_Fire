package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumToolEffect;
import com.github.alexthe666.iceandfire.util.ItemUtil;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModPickaxe extends ItemPickaxe implements IaFTool {
    private final EnumToolEffect toolEffect;
	
	public ItemModPickaxe(ToolMaterial toolmaterial, String gameName, String name) {
		this(toolmaterial, gameName, name, EnumToolEffect.NONE);
	}
	
	public ItemModPickaxe(ToolMaterial toolmaterial, String gameName, String name, EnumToolEffect toolEffect) {
		super(toolmaterial);
		this.setTranslationKey(name);
		this.setCreativeTab(IceAndFire.TAB_ITEMS);
		this.setRegistryName(IceAndFire.MODID, gameName);
		this.toolEffect = toolEffect;
	}

	@Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    	return ItemUtil.getIsRepairable(toolMaterial, toRepair, repair, super.getIsRepairable(toRepair, repair));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	switch(this.toolEffect) {
    	case SILVER:
    		ItemUtil.getSilverComment(tooltip);
    		break;
    	case MYRMEX:
    		ItemUtil.getMyrmexComment(tooltip);
    		break;
    	case MYRMEX_POISON:
    		ItemUtil.getMyrmexComment(tooltip, true);
    		break;
    	case FIRE: 
    		ItemUtil.getFireDragonsteelComment(tooltip);
    		break;
    	case ICE: 
    		ItemUtil.getIceDragonsteelComment(tooltip);
    		break;
    	case LIGHTNING:
    		ItemUtil.getLightningDragonsteelComment(tooltip);
    		break;
		default:
			break;
    	}
    }

	@Override
	public EnumToolEffect getToolEffect() {
		return this.toolEffect;
	}
}
