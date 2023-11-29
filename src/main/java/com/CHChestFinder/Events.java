package com.CHChestFinder;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.*;


public class Events {
    boolean[][] scannedChunks = new boolean[39][39]; //650 by 160 by 650, 52 by 52 if monkey, 39 by 39 if not
    
    public void resetScannedChunks() {
    	scannedChunks = new boolean[39][39];
    }
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event){
    	long ticks = Minecraft.getMinecraft().thePlayer.getEntityWorld().getWorldTime();
        if (CHChestFinder.autoWaypoint){
            CHChestFinder.waypoints.deleteCloseWaypoints(4);
            CHChestFinder.waypoints.renderWaypoints(event);
        }
        else if (ticks%40==0) {
        	resetScannedChunks();
        	CHChestFinder.waypoints.clearAllVisitedWaypoints();
        	CHChestFinder.waypoints.clearAllWaypoints();
        }
        if (CHChestFinder.autoWaypoint && ticks%(CHChestFinder.seconds*20)==0) {
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
                        	if (chunkZ-1>=0) {
                        		scanChunkBorder(chunkX, chunkZ, chunkX, chunkZ - 1, event);
                        	}
                        }
                        else if (chunkZ==playerChunkZ-radius){
                        	if(chunkX+1<=39) {
                        		scanChunkBorder(chunkX, chunkZ, chunkX + 1, chunkZ, event);
                        	}
                        }
                        else{
                        	//edge of 4 scanned chunks. edge case (literallty hehehehehheah)
                        	if(chunkX+1<=39) {
                        		scanChunkBorder(chunkX, chunkZ, chunkX + 1, chunkZ, event);
                        	}
                        	if (chunkZ-1>=0) {
                        		scanChunkBorder(chunkX, chunkZ, chunkX, chunkZ - 1, event);
                        	}
                        	if(chunkX+1<=39 && chunkZ-1>=0) {
                        		scanChunkEdge(chunkX+1, chunkZ-1, event);
                        	}
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
        if (scannedChunks[playerChunkX][playerChunkZ] == false) {
            String[][][] scannedChunk = new String[16][158][16];
            for (int y = 0; y < 158; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = new BlockPos(playerChunkX * 16, 31, playerChunkZ * 16).east(192 + x).south(192 + z).up(y);//south is +z lmfao//202-10 so its divible by 16
                        Block block = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos).getBlock();
                        IBlockState blockState = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos);
                        String substring = block.toString().substring(16, block.toString().length() - 1);
                        if ((substring.equals("stone"))) {
                            String variant = blockState.toString().substring(24, blockState.toString().length() - 1);
                            scannedChunk[x][y][z] = variant;
                        } else {
                            scannedChunk[x][y][z] = substring;
                        }
                    }
                }
            }
            for (int x = 0; x < 14; x++) {
                for (int y = 0; y < 158; y++) {
                    for (int z = 0; z < 14; z++) {
                        for (Structure s : CHChestFinder.structures) {
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
        }
        scannedChunks[playerChunkX][playerChunkZ] = true;
    }
    public void scanChunkBorder(int chunkOneX, int chunkOneZ, int chunkTwoX, int chunkTwoZ, RenderWorldLastEvent event){
        if (scannedChunks[chunkOneX][chunkOneZ] == false || scannedChunks[chunkTwoX][chunkTwoZ] == false) {
            String[][][] scannedChunkBorder;
            String dirFromOne = "";

            int dispX = chunkTwoX-chunkOneX;
            int dispZ = chunkTwoZ-chunkOneZ;

            switch(dispX){
                case 1: dirFromOne = "EAST"; break;
                case -1: dirFromOne = "WEST"; break;
            }
            switch(dispZ){
                case 1: dirFromOne = "NORTH"; break;
                case -1: dirFromOne = "SOUTH"; break;
            }
            BlockPos startPos = new BlockPos(0, 0, 0);
            if(dirFromOne.equals("EAST")){
                startPos = new BlockPos(chunkOneX * 16 + 192 + 14, 31, chunkOneZ * 16 + 192 + 0);
            }
            if(dirFromOne.equals("WEST")){
                startPos = new BlockPos(chunkOneX * 16 + 192 - 2, 31, chunkOneZ * 16 + 192 + 0);
            }
            if(dirFromOne.equals("NORTH")){
                startPos = new BlockPos(chunkOneX * 16 + 192 + 0, 31, chunkOneZ * 16 + 192 - 2);
            }
            if(dirFromOne.equals("SOUTH")){
                startPos = new BlockPos(chunkOneX * 16 + 192 + 0, 31, chunkOneZ * 16 + 192 + 14);
            }
            // x 16 z 4 when north south
            // x 4 z 16 when east west
            if (dirFromOne.equals("NORTH") || dirFromOne.equals("SOUTH")) {
                scannedChunkBorder = new String[16][158][4];
                for (int y = 0; y < 158; y++) {
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 4; z++) {
                            BlockPos pos = startPos.up(y).east(x).south(z);
                            Block block = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos).getBlock();
                            IBlockState blockState = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos);
                            String substring = block.toString().substring(16, block.toString().length() - 1);
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
                                        //System.out.println("BORDER " + dirFromOne + " WAYPOINT/S MADE" + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ());
                                        CHChestFinder.waypoints.setWaypoint("IN BORDER " + dirFromOne + " Chest", pos2, event);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public void scanChunkEdge(int chunkX, int chunkZ, RenderWorldLastEvent event) {
    	if (scannedChunks[chunkX][chunkZ] == false && scannedChunks[chunkX-1][chunkZ] == false && scannedChunks[chunkX-1][chunkZ+1] == false && scannedChunks[chunkX][chunkZ+1] == false) {
    		String[][][] scannedChunkEdge = new String[4][158][4];
        	System.out.println("SCANNING CHUNK! :: " + chunkX + " " + chunkZ );
            long timeming = System.currentTimeMillis();
            for (int y = 0; y < 158; y++) {
                for (int x = 0; x < 4; x++) {
                    for (int z = 0; z < 4; z++) {
                        BlockPos pos = new BlockPos(chunkX * 16 - 2, 31, chunkZ * 16 + 14).east(192 + x).south(192 + z).up(y);//south is +z lmfao//202-10 so its divible by 16
                        System.out.println(pos.getX() + " " + pos.getY() + " " + pos.getZ());
                        Block block = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos).getBlock();
                        IBlockState blockState = Minecraft.getMinecraft().thePlayer.getEntityWorld().getBlockState(pos);
                        String substring = block.toString().substring(16, block.toString().length() - 1);
                        if ((substring.equals("stone"))) {
                            String variant = blockState.toString().substring(24, blockState.toString().length() - 1);
                            scannedChunkEdge[x][y][z] = variant;
                        } else {
                            scannedChunkEdge[x][y][z] = substring;
                        }
                    }
                }
            }
            for (int x = 0; x < 2; x++) {
                for (int y = 0; y < 158; y++) {
                    for (int z = 0; z < 2; z++) {
                        for (Structure s : CHChestFinder.structures) {
                            if (scannedChunkEdge[x][y][z].equals(s.getBlockPattern().getPattern()[0][0]) &&
                                    scannedChunkEdge[x + 1][y][z].equals(s.getBlockPattern().getPattern()[1][0]) &&
                                    scannedChunkEdge[x + 2][y][z].equals(s.getBlockPattern().getPattern()[2][0]) &&
                                    scannedChunkEdge[x][y][z + 1].equals(s.getBlockPattern().getPattern()[0][1]) &&
                                    scannedChunkEdge[x + 1][y][z + 1].equals(s.getBlockPattern().getPattern()[1][1]) &&
                                    scannedChunkEdge[x + 2][y][z + 1].equals(s.getBlockPattern().getPattern()[2][1]) &&
                                    scannedChunkEdge[x][y][z + 2].equals(s.getBlockPattern().getPattern()[0][2]) &&
                                    scannedChunkEdge[x + 1][y][z + 2].equals(s.getBlockPattern().getPattern()[1][2]) &&
                                    scannedChunkEdge[x + 2][y][z + 2].equals(s.getBlockPattern().getPattern()[2][2])) {
                                Chest[] chests = s.getChests();
                                System.out.println("Structure Found, chest amount " + chests.length);
                                for (Chest c : chests) {
                                    BlockPos pos2 = new BlockPos(chunkX * 16 - 2 + x + c.getX()+192, c.getY()+31+y, chunkZ * 16 + 14 + z + c.getZ()+192);
                                    System.out.println("WAYPOINT/S MADE " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ());
                                    CHChestFinder.waypoints.setWaypoint("EDGE Chest", pos2, event);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
