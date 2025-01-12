package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.iceandfire.item.ItemBestiary;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public enum EnumBestiaryPages {

    INTRODUCTION(2),
    FIREDRAGON(4),
    FIREDRAGONEGG(1),
    ICEDRAGON(4),
    ICEDRAGONEGG(1),
    LIGHTNINGDRAGON(5),
    LIGHTNINGDRAGONEGG(1),
    TAMEDDRAGONS(3),
    MATERIALS(2),
    ALCHEMY(1),
    DRAGONFORGE(3),
    VILLAGERS(0),
    HIPPOGRYPH(1),
    GORGON(1),
    PIXIE(1),
    CYCLOPS(2),
    SIREN(2),
    HIPPOCAMPUS(2),
    DEATHWORM(3),
    COCKATRICE(2),
    STYMPHALIANBIRD(1),
    TROLL(2),
    MYRMEX(4),
    AMPHITHERE(2),
    SEASERPENT(2),
    HYDRA(2),
    GHOST(1),
    DREAD_MOBS(1),
    DREADLAND(1),
    DREADLAND_FORTRESS(1);

    public int pages;

    EnumBestiaryPages(int pages) {
        this.pages = pages;
    }

    public static List<Integer> toList(int[] containedpages) {
        List<Integer> intList = new ArrayList<>();
        for (int containedpage : containedpages) {
            if (containedpage >= 0 && containedpage < EnumBestiaryPages.values().length) {
                intList.add(containedpage);
            }
        }
        return intList;
    }

    public static int[] fromList(List<Integer> containedpages) {
        int[] pages = new int[containedpages.size()];
        for (int i = 0; i < pages.length; i++)
            pages[i] = containedpages.get(i);
        return pages;
    }

    public static List<EnumBestiaryPages> containedPages(List<Integer> pages) {
        List<EnumBestiaryPages> list = new ArrayList<>();
        for (Integer page : pages) {
            if (page >= 0 && page < EnumBestiaryPages.values().length) {
                list.add(EnumBestiaryPages.values()[page]);
            }
        }
        return list;
    }

    public static boolean hasAllPages(ItemStack book) {
        List<EnumBestiaryPages> allPages = new ArrayList<>(Arrays.asList(EnumBestiaryPages.values()));
        List<EnumBestiaryPages> pages = containedPages(EnumBestiaryPages.toList(book.getTagCompound().getIntArray("Pages")));
        for (EnumBestiaryPages page : allPages) {
            return !pages.contains(page);
        }
        return false;
    }

    public static List<Integer> enumToInt(List<EnumBestiaryPages> pages) {
        Iterator<com.github.alexthe666.iceandfire.enums.EnumBestiaryPages> itr = pages.iterator();
        List<Integer> list = new ArrayList<>();
        while (itr.hasNext()) {
            list.add(EnumBestiaryPages.values()[(itr.next()).ordinal()].ordinal());
        }
        return list;
    }

    public static EnumBestiaryPages getRand() {
        return EnumBestiaryPages.values()[new Random().nextInt(EnumBestiaryPages.values().length)];

    }

    public static void addRandomPage(ItemStack book) {
        if (book.getItem() instanceof ItemBestiary) {
            List<EnumBestiaryPages> list = EnumBestiaryPages.possiblePages(book);
            if (list != null && !list.isEmpty()) {
                addPage(list.get(new Random().nextInt(list.size())), book);
            }
        }
    }

    public static List<EnumBestiaryPages> possiblePages(ItemStack book) {
        if (book.getItem() instanceof ItemBestiary) {
            NBTTagCompound tag = book.getTagCompound();
            List<EnumBestiaryPages> allPages = new ArrayList<>(Arrays.asList(EnumBestiaryPages.values()));
            List<EnumBestiaryPages> containedPages = containedPages(toList(tag.getIntArray("Pages")));
            List<EnumBestiaryPages> possiblePages = new ArrayList<>();
	        for (EnumBestiaryPages page : allPages) {
		        if (!containedPages.contains(page)) {
			        possiblePages.add(page);
		        }
	        }
            return possiblePages;
        }
        return null;
    }


    public static boolean addPage(EnumBestiaryPages page, ItemStack book) {
        boolean flag = false;
        if (book.getItem() instanceof ItemBestiary) {
            NBTTagCompound tag = book.getTagCompound();
            List<EnumBestiaryPages> enumlist = containedPages(toList(tag.getIntArray("Pages")));
            if (!enumlist.contains(page)) {
                enumlist.add(page);
                flag = true;
            }
            tag.setIntArray("Pages", fromList(enumToInt(enumlist)));
        }
        return flag;
    }
}
