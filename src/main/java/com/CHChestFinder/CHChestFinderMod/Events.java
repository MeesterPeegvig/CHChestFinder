package com.CHChestFinder.CHChestFinderMod;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Events {
    //String[][][] scannedCoords = new String[650][160][650]; TAKES TOO MUCH SPACE NEED TO FIND NEW WAY TO DO IT.
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event){
        if(event.message.getUnformattedText().contains("test")){
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("hello world! " + CHChestFinder.currentStatus));
        }
    }
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event){
        int radius = 8;
        BlockPos origPos = Minecraft.getMinecraft().thePlayer.getPosition();
        if (CHChestFinder.autoWaypoint){
            CHChestFinder.waypoints.deleteCloseWaypoints(2);
            CHChestFinder.waypoints.renderWaypoints(event);
        }
        long ticks = Minecraft.getMinecraft().thePlayer.getEntityWorld().getWorldTime();
        if (CHChestFinder.autoWaypoint && ticks%200==0) {
            BlockPos pos;
            long timeming = System.currentTimeMillis();
            String[][][] scannedBlocks = new String[radius*2][radius*2][radius*2];
            for (int x = -radius; x <= radius-1; x++) {
                for (int z = -radius; z <= radius-1; z++) {
                    for (int y = -radius; y <= radius-1; y++) {
                        pos = origPos.east(x).north(z).up(y);
                        Block block = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos).getBlock();
                        IBlockState blockState = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos);
                        System.out.println("BLOCK: " + block.toString() + "BLOCKSTATE :: " + blockState.toString());
                        String substring = block.toString().substring(16, block.toString().length() - 1);
                        if ((substring.equals("stone"))){
                            String variant = blockState.toString().substring(24, blockState.toString().length() - 1);
                            scannedBlocks[x + radius][y + radius][z + radius] = variant;
                        }
                        else{
                            scannedBlocks[x + radius][y + radius][z + radius] = substring;
                        }
                            //System.out.println(block + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                        if (block.toString().equals("Block{minecraft:diamond_ore}")) {
                            System.out.println("DIA BLOQ FOUND");
                            CHChestFinder.waypoints.setWaypoint("diamond", pos, event);
                        }
                    }
                }
            }
            System.out.println("SCANNING DONE");
            System.out.println("Timing :: " + (System.currentTimeMillis() - timeming));

            for (int x = 0; x<radius*2-2;x++){
                for (int y = 0; y<radius*2-2;y++){
                    for (int z = 0; z<radius*2-2;z++){
                        for (Structure s : CHChestFinder.structures){
                            System.out.println(scannedBlocks[x][y][z] + " " + s.getBlockPattern().getPattern()[0][0]);
                            if (scannedBlocks[x][y][z].equals(s.getBlockPattern().getPattern()[0][0])){
                                System.out.println("AT LEAST ONE MATCHED HAHAHA");
                                System.out.println(scannedBlocks[x+1][y][z] + " " + s.getBlockPattern().getPattern()[1][0]);
                                System.out.println(scannedBlocks[x+2][y][z] + " " + s.getBlockPattern().getPattern()[2][0]);
                                System.out.println(scannedBlocks[x][y][z+1] + " " + s.getBlockPattern().getPattern()[0][1]);
                                System.out.println(scannedBlocks[x+1][y][z+1] + " " + s.getBlockPattern().getPattern()[1][1]);
                                System.out.println(scannedBlocks[x+2][y][z+1] + " " + s.getBlockPattern().getPattern()[2][1]);
                                System.out.println(scannedBlocks[x][y][z+2] + " " + s.getBlockPattern().getPattern()[0][2]);
                                System.out.println(scannedBlocks[x+1][y][z+2] + " " + s.getBlockPattern().getPattern()[1][2]);
                                System.out.println(scannedBlocks[x+2][y][z+2] + " " + s.getBlockPattern().getPattern()[2][2]);
                            }


                            if (    scannedBlocks[x][y][z].equals(s.getBlockPattern().getPattern()[0][0]) &&
                                    scannedBlocks[x+1][y][z].equals(s.getBlockPattern().getPattern()[1][0]) &&
                                    scannedBlocks[x+2][y][z].equals(s.getBlockPattern().getPattern()[2][0]) &&
                                    scannedBlocks[x][y][z+1].equals(s.getBlockPattern().getPattern()[0][1]) &&
                                    scannedBlocks[x+1][y][z+1].equals(s.getBlockPattern().getPattern()[1][1]) &&
                                    scannedBlocks[x+2][y][z+1].equals(s.getBlockPattern().getPattern()[2][1]) &&
                                    scannedBlocks[x][y][z+2].equals(s.getBlockPattern().getPattern()[0][2]) &&
                                    scannedBlocks[x+1][y][z+2].equals(s.getBlockPattern().getPattern()[1][2]) &&
                                    scannedBlocks[x+2][y][z+2].equals(s.getBlockPattern().getPattern()[2][2])){
                                Chest[] chests = s.getChests();
                                for (Chest c : chests) {
                                    CHChestFinder.waypoints.setWaypoint("waypoint", origPos.east(x+c.getX()).up(y+c.getY()).north(z+c.getZ()), event);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}