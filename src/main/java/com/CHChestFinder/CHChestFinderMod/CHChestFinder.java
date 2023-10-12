package com.CHChestFinder.CHChestFinderMod;

import jdk.nashorn.internal.ir.Block;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@Mod(modid = CHChestFinder.MODID, version = CHChestFinder.VERSION)
public class CHChestFinder {
    public static Waypoints waypoints = new Waypoints();
    public static final String MODID = "CHChestFinder";
    public static final String VERSION = "1.0";
    public static boolean currentStatus = false; // false -> mod is of, true -> mod in on
    public static boolean autoWaypoint = false;
    public static Structure[] structures;
    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        MinecraftForge.EVENT_BUS.register(new Events());
        ClientCommandHandler.instance.registerCommand(new SimpleCommands());

        // Structure Time!!! D:

        structures = new Structure[126];
        List<String> lines = IOUtils.readLines(CHChestFinder.class.getResourceAsStream("/StructureInfo.txt"));
        for (int i = 0; i < 1; i++){ // i should go to 126 but we only have 1 line rn.
            String[] info = lines.get(i).split(" ");
            String name = info[0];
            String chestInfo = info[1];
            String[] chests = chestInfo.split("\\|");
            Chest[] allChests = new Chest[chests.length];
            for (int a = 0; a < chests.length; a++) {
                String[] chestCoords = chests[a].split(",");
                int x = Integer.parseInt(chestCoords[0]);
                int y = Integer.parseInt(chestCoords[1]);
                int z = Integer.parseInt(chestCoords[2]);
                Chest chest = new Chest(x, y, z);
                allChests[a] = chest;
            }
            String ID = info[2];
            String[] blocks = info[3].split(",");
            BlockPattern blockPattern = new BlockPattern(blocks);
            structures[i] = new Structure(name, allChests, ID, blockPattern);
        }
    }
}
