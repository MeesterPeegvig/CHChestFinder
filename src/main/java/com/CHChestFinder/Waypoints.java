package com.CHChestFinder;


import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.*;

import java.util.ArrayList;

public class Waypoints {
    private boolean isRendering = false;
    private ArrayList<Waypoint> waypoints;
    private ArrayList<Waypoint> visitedWaypoints;
    public Waypoints(){
      waypoints = new ArrayList<Waypoint>();//100
      visitedWaypoints = new ArrayList<Waypoint>();//100
    }
    public Waypoint getWaypoint(int num){
        return waypoints.get(num);
    }
    public void setWaypoint(String str, BlockPos pos, RenderWorldLastEvent event) {
        boolean visited = false;
        for (Waypoint w : visitedWaypoints) {
            if (w.getPos().equals(pos)) {
                visited = true;
            }
        }
        if (!visited){
            waypoints.add(new Waypoint(str, pos, event));
    }
        //renderWaypoints();
    }
    public void clearAllWaypoints(){
        waypoints.clear();
    }
    public void clearAllVisitedWaypoints(){
        visitedWaypoints.clear();
    }

    public void renderWaypoints(RenderWorldLastEvent event){
        for(Waypoint w : waypoints){
            WaypointStuff.renderWaypointText(w.getStr(), w.getPos(), event.partialTicks);
        }
    }
    public void deleteCloseWaypoints(double distance){
        ArrayList<Waypoint> temp = new ArrayList<Waypoint>();
        for (Waypoint w : waypoints){
            BlockPos bPos = w.getPos();
            BlockPos pPos = Minecraft.getMinecraft().thePlayer.getPosition();
            if (Math.sqrt(bPos.distanceSq(pPos))<distance){
                visitedWaypoints.add(w);
            }
            else{
                temp.add(w);
            }
            waypoints=temp;
        }
    }

}
