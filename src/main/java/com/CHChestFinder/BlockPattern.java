package com.CHChestFinder;

import net.minecraft.block.Block;

public class BlockPattern {
    private String[][] pattern;
    public BlockPattern(String[] pat){
        String[] one = {pat[0], pat[1], pat[2]};
        String[] two = {pat[3], pat[4], pat[5]};
        String[] three = {pat[6], pat[7], pat[8]};
        pattern = new String[][]{one, two, three};
    }
    public String[][] getPattern(){
        return pattern;
    }
}
