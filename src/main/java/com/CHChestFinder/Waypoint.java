package com.CHChestFinder;

import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.*;

public class Waypoint {
    private String str;
    private BlockPos pos;
    private RenderWorldLastEvent event;
    public Waypoint(String str, BlockPos pos, RenderWorldLastEvent event){
        this.str = str;
        this.pos = pos;
        this.event = event;
    }
    public String getStr(){
        return str;
    }
    public BlockPos getPos(){
        return pos;
    }
    public RenderWorldLastEvent getEvent(){
        return event;
    }
    public boolean equals(Waypoint w){
        if(w.getPos().equals(pos)){
            return true;
        }
        return false;
    }
}
