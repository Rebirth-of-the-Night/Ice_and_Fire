package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.util.ItemUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAlchemySword extends ItemSword {
	public final int toolID;

    public ItemAlchemySword(ToolMaterial toolmaterial, String gameName, String name, int toolID) {
        super(toolmaterial);
        this.setTranslationKey(name);
        this.setCreativeTab(IceAndFire.TAB_ITEMS);
        this.setRegistryName(IceAndFire.MODID, gameName); 
        this.toolID = toolID;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	switch(this.toolID) {
    	case 0:
    		ItemUtil.getFireDragonsteelComment(tooltip, true);
    		break;
    	case 1:
    		ItemUtil.getIceDragonsteelComment(tooltip, true);
    		break;
    	case 2:
    		ItemUtil.getLightningDragonsteelComment(tooltip, true);
    		break;
    	}
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
