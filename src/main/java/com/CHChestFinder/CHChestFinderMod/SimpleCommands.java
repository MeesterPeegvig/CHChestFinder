package com.CHChestFinder.CHChestFinderMod;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
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
        return "chscf <on/off/help>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args[0].equals("off")){
            CHChestFinder.currentStatus = false;
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Mod disabled!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));
        }
        else if(args[0].equals("on")) {
            CHChestFinder.currentStatus = true;
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Mod enabled!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));
        }
        else if(args[0].equals("help")) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("This is the Crystal Hollows Structure Chest Finder (chscf)\\nCommands:\\n/chscf on - enables the mod\\n/chscf off - disables the mod\\nchscf - shows the command menu").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));
        }
        else if(args[0].equals("auto_waypoint") || args[0].equals("aw")){
            if(args[1].equals("on")){
                CHChestFinder.autoWaypoint=true;
                //CHChestFinder.waypoints.startRendering();
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Auto Waypoint enabled!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE)));
            }
            if(args[1].equals("off")){
                CHChestFinder.autoWaypoint=false;
                //CHChestFinder.waypoints.stopRendering();
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