package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.util.ItemUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemModSword extends ItemSword implements IaFTool {
    private final Item.ToolMaterial toolMaterial;
    private final int toolID;
    
    public ItemModSword(ToolMaterial toolmaterial, String gameName, String name) {
    	this(toolmaterial, gameName, name, -1);
    }
    
    public ItemModSword(ToolMaterial toolmaterial, String gameName, String name, int toolID) {
        super(toolmaterial);
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName);
        this.toolMaterial = toolmaterial;
        this.toolID = toolID;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    	return ItemUtil.getIsRepairable(toolMaterial, toRepair, repair, super.getIsRepairable(toRepair, repair));
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
    		ItemUtil.getMyrmexComment(tooltip, true);
    		break;
    	case 3:
    		ItemUtil.getFireDragonsteelComment(tooltip);
    		break;
    	case 4: 
    		ItemUtil.getIceDragonsteelComment(tooltip);
    		break;
    	case 5:
    		ItemUtil.getLightningDragonsteelComment(tooltip);
    		break;
    	}
    }

	@Override
	public int getToolMode() {
		return this.toolID;
	}
}