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

import java.util.ArrayList;

public class Events {
    boolean[][] scannedChunks = new boolean[39][39]; //650 by 160 by 650, 52 by 52 if monkey, 39 by 39 if not
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event){
        if(event.message.getUnformattedText().contains("test")){
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("hello world! " + CHChestFinder.currentStatus));
        }
    }
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event){
        if (CHChestFinder.autoWaypoint){
            CHChestFinder.waypoints.deleteCloseWaypoints(4);
            CHChestFinder.waypoints.renderWaypoints(event);
        }
        long ticks = Minecraft.getMinecraft().thePlayer.getEntityWorld().getWorldTime();
        if (CHChestFinder.autoWaypoint && ticks%200==0) {
            //int radius = 2;
            BlockPos origPos = Minecraft.getMinecraft().thePlayer.getPosition();
            int playerChunkX = (origPos.getX() - 192) / 16;//202-10 to be divisible
            int playerChunkZ = (origPos.getZ() - 192) / 16;
            //System.out.println("cX " + playerChunkX + " cZ " + playerChunkZ );
            BlockPos pos = new BlockPos(0,0,0);
            long timeming = System.currentTimeMillis();
            String[][][] scannedChunk = new String[16][158][16];
            if (scannedChunks[playerChunkX][playerChunkZ] != true) {
                    for (int y = 0; y < 158; y++) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                pos = new BlockPos(playerChunkX * 16, 31, playerChunkZ * 16).east(192 + x).south(192 + z).up(y);//south is +z lmfao//202-10 so its divible by 16
                                Block block = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos).getBlock();
                                IBlockState blockState = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos);
                                String substring = block.toString().substring(16, block.toString().length() - 1);
                                if(substring.equals("web")){
                                System.out.println(pos.getX() + " " + pos.getY() + " " + pos.getZ() + " BLOCK: " + block.toString() + "BLOCKSTATE :: " + blockState.toString());
                                }
                                if ((substring.equals("stone"))) {
                                    String variant = blockState.toString().substring(24, blockState.toString().length() - 1);
                                    scannedChunk[x][y][z] = variant;
                                } else {
                                    scannedChunk[x][y][z] = substring;
                                }
                                //System.out.println(block + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                                if (block.toString().equals("Block{minecraft:diamond_ore}")) {
                                    System.out.println("DIA BLOQ FOUND");
                                    CHChestFinder.waypoints.setWaypoint("diamond", pos, event);
                                }
                            }
                        }
                    }
                scannedChunks[playerChunkX][playerChunkZ] = true;
                System.out.println("SCANNING DONE");
                System.out.println("Timing :: " + (System.currentTimeMillis() - timeming));

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 158; y++) {
                        for (int z = 0; z < 16; z++) {
                            for (Structure s : CHChestFinder.structures) {
                               // System.out.println(scannedChunk[x][y][z] + " " + s.getBlockPattern().getPattern()[0][0]);

                                if (scannedChunk[x][y][z].equals(s.getBlockPattern().getPattern()[0][0])){
                                    System.out.println("AT LEAST ONE MATCHED HAHAHA");
                                    System.out.println(scannedChunk[x+1][y][z] + " " + s.getBlockPattern().getPattern()[1][0]);
                                    System.out.println(scannedChunk[x+2][y][z] + " " + s.getBlockPattern().getPattern()[2][0]);
                                    System.out.println(scannedChunk[x][y][z+1] + " " + s.getBlockPattern().getPattern()[0][1]);
                                    System.out.println(scannedChunk[x+1][y][z+1] + " " + s.getBlockPattern().getPattern()[1][1]);
                                    System.out.println(scannedChunk[x+2][y][z+1] + " " + s.getBlockPattern().getPattern()[2][1]);
                                    System.out.println(scannedChunk[x][y][z+2] + " " + s.getBlockPattern().getPattern()[0][2]);
                                    System.out.println(scannedChunk[x+1][y][z+2] + " " + s.getBlockPattern().getPattern()[1][2]);
                                    System.out.println(scannedChunk[x+2][y][z+2] + " " + s.getBlockPattern().getPattern()[2][2]);
                                }
                                if (scannedChunk[x][y][z].equals(s.getBlockPattern().getPattern()[0][0]) &&
                                        scannedChunk[x + 1][y][z].equals(s.getBlockPattern().getPattern()[1][0]) &&
                                        scannedChunk[x + 2][y][z].equals(s.getBlockPattern().getPattern()[2][0]) &&
                                        scannedChunk[x][y][z + 1].equals(s.getBlockPattern().getPattern()[0][1]) &&
                                        scannedChunk[x + 1][y][z + 1].equals(s.getBlockPattern().getPattern()[1][1]) &&
                                        scannedChunk[x + 2][y][z + 1].equals(s.getBlockPattern().getPattern()[2][1]) &&
                                        scannedChunk[x][y][z + 2].equals(s.getBlockPattern().getPattern()[0][2]) &&
                                        scannedChunk[x + 1][y][z + 2].equals(s.getBlockPattern().getPattern()[1][2]) &&
                                        scannedChunk[x + 2][y][z + 2].equals(s.getBlockPattern().getPattern()[2][2])) {
                                    Chest[] chests = s.getChests();
                                    System.out.println("Structure Found, chest amount " + chests.length);
                                    for (Chest c : chests) {
                                        BlockPos pos2 = new BlockPos(playerChunkX*16 + x + c.getX()+192, c.getY()+31+y, playerChunkZ*16 + z + c.getZ()+192);
                                        System.out.println("WAYPOINT/S MADE " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ());
                                        CHChestFinder.waypoints.setWaypoint("Chest", pos2, event);
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("SCANNING+CHECKING DONE");
                System.out.println("Timing :: " + (System.currentTimeMillis() - timeming));
            }
        }
    }
}