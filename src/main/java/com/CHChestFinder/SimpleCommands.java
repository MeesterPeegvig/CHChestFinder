package com.CHChestFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class SimpleCommands extends CommandBase {

    @Override
    public String getCommandName() {
        return "chscf";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "chscf <on/off/>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if(args[0].equals("help")) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("This is the Crystal Hollows Structure Chest Finder (chscf). Commands: /chscf aw on - enables the mod. /chscf off - disables the mod.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));
        }
        else if(args[0].equals("auto_waypoint") || args[0].equals("aw")){
            if(args[1].equals("on")){
            	CHChestFinder.seconds = 2;
//            	 if (args[2].matches("[0-9]")) {
//                 	CHChestFinder.seconds=Integer.parseInt(args[2]);
//                 }
//                 else {
//                 	CHChestFinder.seconds = 10;
//                 }
                CHChestFinder.autoWaypoint=true;
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Auto Waypoint enabled!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));
            }
            if(args[1].equals("off")){
                CHChestFinder.autoWaypoint=false;
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Auto Waypoint disabled!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));
            }
        }
        else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Try /chscf help").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));

        }
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }

}