package com.CHChestFinder.CHChestFinderMod;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;


public class Events {
    boolean[][] scannedChunks = new boolean[39][39]; //650 by 160 by 650, 52 by 52 if monkey, 39 by 39 if not

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event){
        if (CHChestFinder.autoWaypoint){
            CHChestFinder.waypoints.deleteCloseWaypoints(4);
            CHChestFinder.waypoints.renderWaypoints(event);
        }
        long ticks = Minecraft.getMinecraft().thePlayer.getEntityWorld().getWorldTime();
        if (CHChestFinder.autoWaypoint && ticks%200==0) {
            int radius = 1;
            BlockPos origPos = Minecraft.getMinecraft().thePlayer.getPosition();
            int playerChunkX = (origPos.getX() - 192) / 16;//202-10 to be divisible
            int playerChunkZ = (origPos.getZ() - 192) / 16;
            // border scan
            for (int chunkX = playerChunkX-radius; chunkX<=playerChunkX+radius; chunkX++){
                for (int chunkZ = playerChunkZ-radius; chunkZ<=playerChunkZ+radius; chunkZ++){
                    if (chunkX>=0 && chunkX<=39 && chunkZ>=0 && chunkZ<=39){
                        if (chunkX==playerChunkX+radius && chunkZ==playerChunkZ-radius){
                            //no scan
                        }
                        else if (chunkX==playerChunkX+radius){
                            scanChunkBorder(chunkX, chunkZ, chunkX, chunkZ - 1, event);
                        }
                        else if (chunkZ==playerChunkZ-radius){
                            scanChunkBorder(chunkX, chunkZ, chunkX+1, chunkZ, event);
                        }
                        else{
                            scanChunkBorder(chunkX, chunkZ, chunkX + 1, chunkZ, event);
                            scanChunkBorder(chunkX, chunkZ, chunkX, chunkZ - 1, event);
                        }
                    }
                }
            }
            // in chunk scan
            for (int chunkX = playerChunkX-radius; chunkX<=playerChunkX+radius; chunkX++){
                for (int chunkZ = playerChunkZ-radius; chunkZ<=playerChunkZ+radius; chunkZ++){
                    if (chunkX>=0 && chunkX<=39 && chunkZ>=0 && chunkZ<=39){
                        scanChunk(chunkX,chunkZ,event);
                    }
                }
            }
        }
    }
    public void scanChunk(int playerChunkX, int playerChunkZ, RenderWorldLastEvent event){
        if (scannedChunks[playerChunkX][playerChunkZ] != true) {
            String[][][] scannedChunk = new String[16][158][16];
            long timeming = System.currentTimeMillis();
            for (int y = 0; y < 158; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = new BlockPos(playerChunkX * 16, 31, playerChunkZ * 16).east(192 + x).south(192 + z).up(y);//south is +z lmfao//202-10 so its divible by 16
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
                    }
                }
            }
            scannedChunks[playerChunkX][playerChunkZ] = true;
            //System.out.println("SCANNING DONE");
            //System.out.println("Timing :: " + (System.currentTimeMillis() - timeming));

            for (int x = 0; x < 14; x++) {
                for (int y = 0; y < 158; y++) {
                    for (int z = 0; z < 14; z++) {
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
                                    CHChestFinder.waypoints.setWaypoint("IN CHUNK Chest", pos2, event);
                                }
                            }
                        }
                    }
                }
            }
            //System.out.println("SCANNING+CHECKING DONE");
            //System.out.println("Timing :: " + (System.currentTimeMillis() - timeming));
        }
    }
    public void scanChunkBorder(int chunkOneX, int chunkOneZ, int chunkTwoX, int chunkTwoZ, RenderWorldLastEvent event){
        System.out.println("STARTED SCAN OF CHUNKS :: " + ((chunkOneX*16+192)/16) + " " + ((chunkOneZ*16+192)/16) + " " + ((chunkTwoX*16+192)/16) + " " + ((chunkTwoZ*16+192)/16));
        if (scannedChunks[chunkOneX][chunkOneZ] != true || scannedChunks[chunkTwoX][chunkTwoZ] != true) {
            System.out.println("HASNT BEEN SCANNED B4, PROCEEDING");
            String[][][] scannedChunkBorder;
            String dirFromOne = "";

            int dispX = chunkTwoX-chunkOneX;
            int dispZ = chunkTwoZ-chunkOneZ;

            switch(dispX){ //make neater when done cuz this ugly lol (understandable tho (cope))
                case 1: dirFromOne = "EAST"; break;
                case -1: dirFromOne = "WEST"; break;
            }
            switch(dispZ){
                case 1: dirFromOne = "NORTH"; break;
                case -1: dirFromOne = "SOUTH"; break;
            }
            System.out.println("DIR :: " + dirFromOne);
            BlockPos startPos = new BlockPos(0, 0, 0);
            if(dirFromOne.equals("EAST")){
                startPos = new BlockPos(chunkTwoX * 16 + 192 + 14, 31, chunkTwoZ * 16 + 192 + 0);
            }
            if(dirFromOne.equals("WEST")){
                startPos = new BlockPos(chunkOneX * 16 + 192 - 2, 31, chunkOneZ * 16 + 192 + 0);
            }
            if(dirFromOne.equals("NORTH")){
                startPos = new BlockPos(chunkTwoX * 16 + 192 + 0, 31, chunkTwoZ * 16 + 192 + 14);
            }
            if(dirFromOne.equals("SOUTH")){
                startPos = new BlockPos(chunkOneX * 16 + 192 + 0, 31, chunkOneZ * 16 + 192 - 2);
            }
            // x 16 z 4 when north south
            // x 4 z 16 when east west
            System.out.println("START BLOCK POS " + startPos.getX() + " " + startPos.getY() + " " + startPos.getZ());
            if (dirFromOne.equals("NORTH") || dirFromOne.equals("SOUTH")) {
                scannedChunkBorder = new String[16][158][4];
                for (int y = 0; y < 158; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 4; z++) {
                            BlockPos pos = startPos.up(y).east(x).south(z);
                            Block block = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos).getBlock();
                            IBlockState blockState = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos);
                            String substring = block.toString().substring(16, block.toString().length() - 1);
                            if (substring.equals("web")) {
                                System.out.println("BORDER " + dirFromOne + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " BLOCK: " + block.toString() + "BLOCKSTATE :: " + blockState.toString());
                            }
                            if ((substring.equals("stone"))) {
                                String variant = blockState.toString().substring(24, blockState.toString().length() - 1);
                                scannedChunkBorder[x][y][z] = variant;
                            } else {
                                scannedChunkBorder[x][y][z] = substring;
                            }

                        }
                    }
                }

                for (int x = 0; x < 14; x++) {
                    for (int y = 0; y < 158; y++) {
                        for (int z = 0; z < 2; z++) {
                            for (Structure s : CHChestFinder.structures) {
                                // System.out.println(scannedChunk[x][y][z] + " " + s.getBlockPattern().getPattern()[0][0]);

                                if (scannedChunkBorder[x][y][z].equals(s.getBlockPattern().getPattern()[0][0])) {
                                    System.out.println("BORDER " + dirFromOne + "AT LEAST ONE MATCHED HAHAHA");
                                    System.out.println(scannedChunkBorder[x + 1][y][z] + " " + s.getBlockPattern().getPattern()[1][0]);
                                    System.out.println(scannedChunkBorder[x + 2][y][z] + " " + s.getBlockPattern().getPattern()[2][0]);
                                    System.out.println(scannedChunkBorder[x][y][z + 1] + " " + s.getBlockPattern().getPattern()[0][1]);
                                    System.out.println(scannedChunkBorder[x + 1][y][z + 1] + " " + s.getBlockPattern().getPattern()[1][1]);
                                    System.out.println(scannedChunkBorder[x + 2][y][z + 1] + " " + s.getBlockPattern().getPattern()[2][1]);
                                    System.out.println(scannedChunkBorder[x][y][z + 2] + " " + s.getBlockPattern().getPattern()[0][2]);
                                    System.out.println(scannedChunkBorder[x + 1][y][z + 2] + " " + s.getBlockPattern().getPattern()[1][2]);
                                    System.out.println(scannedChunkBorder[x + 2][y][z + 2] + " " + s.getBlockPattern().getPattern()[2][2]);
                                }
                                if (scannedChunkBorder[x][y][z].equals(s.getBlockPattern().getPattern()[0][0]) &&
                                        scannedChunkBorder[x + 1][y][z].equals(s.getBlockPattern().getPattern()[1][0]) &&
                                        scannedChunkBorder[x + 2][y][z].equals(s.getBlockPattern().getPattern()[2][0]) &&
                                        scannedChunkBorder[x][y][z + 1].equals(s.getBlockPattern().getPattern()[0][1]) &&
                                        scannedChunkBorder[x + 1][y][z + 1].equals(s.getBlockPattern().getPattern()[1][1]) &&
                                        scannedChunkBorder[x + 2][y][z + 1].equals(s.getBlockPattern().getPattern()[2][1]) &&
                                        scannedChunkBorder[x][y][z + 2].equals(s.getBlockPattern().getPattern()[0][2]) &&
                                        scannedChunkBorder[x + 1][y][z + 2].equals(s.getBlockPattern().getPattern()[1][2]) &&
                                        scannedChunkBorder[x + 2][y][z + 2].equals(s.getBlockPattern().getPattern()[2][2])) {
                                    Chest[] chests = s.getChests();
                                    System.out.println("BORDER " + dirFromOne + "Structure Found, chest amount " + chests.length);
                                    for (Chest c : chests) {
                                        BlockPos pos2 = new BlockPos(startPos.getX() + x + c.getX(), y + 31 + c.getY(), startPos.getZ() + z + c.getZ());
                                        System.out.println("BORDER " + dirFromOne + " WAYPOINT/S MADE" + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ());
                                        CHChestFinder.waypoints.setWaypoint("IN BORDER " + dirFromOne + "Chest", pos2, event);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                scannedChunkBorder = new String[4][158][16];
                for (int y = 0; y < 158; y++) {
                    for (int x = 0; x < 4; x++) {
                        for (int z = 0; z < 16; z++) {
                            BlockPos pos = startPos.up(y).east(x).south(z);
                            Block block = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos).getBlock();
                            IBlockState blockState = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos);
                            String substring = block.toString().substring(16, block.toString().length() - 1);
                            if (substring.equals("web")) {
                                System.out.println("BORDER " + dirFromOne + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " BLOCK: " + block.toString() + "BLOCKSTATE :: " + blockState.toString());
                            }
                            if ((substring.equals("stone"))) {
                                String variant = blockState.toString().substring(24, blockState.toString().length() - 1);
                                scannedChunkBorder[x][y][z] = variant;
                            } else {
                                scannedChunkBorder[x][y][z] = substring;
                            }

                        }
                    }
                }

                for (int x = 0; x < 2; x++) {
                    for (int y = 0; y < 158; y++) {
                        for (int z = 0; z < 14; z++) {
                            for (Structure s : CHChestFinder.structures) {
                                // System.out.println(scannedChunk[x][y][z] + " " + s.getBlockPattern().getPattern()[0][0]);

                                if (scannedChunkBorder[x][y][z].equals(s.getBlockPattern().getPattern()[0][0])) {
                                    System.out.println("BORDER " + dirFromOne + "AT LEAST ONE MATCHED HAHAHA");
                                    System.out.println(scannedChunkBorder[x + 1][y][z] + " " + s.getBlockPattern().getPattern()[1][0]);
                                    System.out.println(scannedChunkBorder[x + 2][y][z] + " " + s.getBlockPattern().getPattern()[2][0]);
                                    System.out.println(scannedChunkBorder[x][y][z + 1] + " " + s.getBlockPattern().getPattern()[0][1]);
                                    System.out.println(scannedChunkBorder[x + 1][y][z + 1] + " " + s.getBlockPattern().getPattern()[1][1]);
                                    System.out.println(scannedChunkBorder[x + 2][y][z + 1] + " " + s.getBlockPattern().getPattern()[2][1]);
                                    System.out.println(scannedChunkBorder[x][y][z + 2] + " " + s.getBlockPattern().getPattern()[0][2]);
                                    System.out.println(scannedChunkBorder[x + 1][y][z + 2] + " " + s.getBlockPattern().getPattern()[1][2]);
                                    System.out.println(scannedChunkBorder[x + 2][y][z + 2] + " " + s.getBlockPattern().getPattern()[2][2]);
                                }
                                if (scannedChunkBorder[x][y][z].equals(s.getBlockPattern().getPattern()[0][0]) &&
                                        scannedChunkBorder[x + 1][y][z].equals(s.getBlockPattern().getPattern()[1][0]) &&
                                        scannedChunkBorder[x + 2][y][z].equals(s.getBlockPattern().getPattern()[2][0]) &&
                                        scannedChunkBorder[x][y][z + 1].equals(s.getBlockPattern().getPattern()[0][1]) &&
                                        scannedChunkBorder[x + 1][y][z + 1].equals(s.getBlockPattern().getPattern()[1][1]) &&
                                        scannedChunkBorder[x + 2][y][z + 1].equals(s.getBlockPattern().getPattern()[2][1]) &&
                                        scannedChunkBorder[x][y][z + 2].equals(s.getBlockPattern().getPattern()[0][2]) &&
                                        scannedChunkBorder[x + 1][y][z + 2].equals(s.getBlockPattern().getPattern()[1][2]) &&
                                        scannedChunkBorder[x + 2][y][z + 2].equals(s.getBlockPattern().getPattern()[2][2])) {
                                    Chest[] chests = s.getChests();
                                    System.out.println("BORDER " + dirFromOne + "Structure Found, chest amount " + chests.length);
                                    for (Chest c : chests) {
                                        BlockPos pos2 = new BlockPos(startPos.getX() + x + c.getX(), y + 31 + c.getY(), startPos.getZ() + z + c.getZ());
                                        System.out.println("BORDER " + dirFromOne + " WAYPOINT/S MADE" + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ());
                                        CHChestFinder.waypoints.setWaypoint("IN BORDER " + dirFromOne + "Chest", pos2, event);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}