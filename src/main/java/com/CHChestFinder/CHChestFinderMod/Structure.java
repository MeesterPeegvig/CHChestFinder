package com.CHChestFinder.CHChestFinderMod;

public class Structure {
    private String name;
    private Chest[] chests;
    private String ID;
    private BlockPattern pattern;
    public Structure(String name, Chest[] chests, String ID, BlockPattern pattern){
        this.name = name;
        this.chests = chests;
        this.ID = ID;
        this.pattern = pattern;
    }
    public Chest[] getChests(){
        return chests;
    }
    public BlockPattern getBlockPattern() {
        return pattern;
    }
}

