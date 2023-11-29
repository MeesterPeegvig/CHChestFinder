package com.CHChestFinder;
//new comment for test 9
// make so only works in ch (optional), resets every time u enter ch lobby

// get structure data for at least one quadrant lol
// SURELY IT WORkS ON SERVER LOL

//issues:
//keeps checking chunk borders even between 2 previously scanned chunks
//
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.util.List;

@Mod(modid = CHChestFinder.MODID, version = CHChestFinder.VERSION)
public class CHChestFinder {
    public static Waypoints waypoints = new Waypoints();
    public static final String MODID = "CHChestFinder";
    public static final String VERSION = "1.0";
    public static boolean autoWaypoint = false;
    public static Structure[] structures;
    public static int seconds;
    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        MinecraftForge.EVENT_BUS.register(new Events());
        ClientCommandHandler.instance.registerCommand(new SimpleCommands());

        // Structure Time!!! D:
        int amtStructures = 5; // should be 126
        structures = new Structure[amtStructures]; // should be 126
        List<String> lines = IOUtils.readLines(CHChestFinder.class.getResourceAsStream("/StructureInfo.txt"));
        for (int i = 0; i < amtStructures; i++){ // i should go to 126 but we only have 1 line rn
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
                System.out.println("chest thing: " + x + " " + y + " " + z );
                Chest chest = new Chest(x, y, z);
                allChests[a] = chest;
            }
            String ID = info[2];
            String[] blocks = info[3].split(",");
            BlockPattern blockPattern = new BlockPattern(blocks);
            for (Chest c : allChests){
                System.out.println("chest offsets: " + c.getX() + " " + c.getY() + " " + c.getZ());
            }
            structures[i] = new Structure(name, allChests, ID, blockPattern);
        }
    }
}
