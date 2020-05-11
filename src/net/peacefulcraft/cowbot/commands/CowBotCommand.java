package net.peacefulcraft.cowbot.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.peacefulcraft.cowbot.CowBot;

public class CowBotCommand extends Command {

  public CowBotCommand() {
    super("cowbot", "pcn.staff", new String[] { "cb"  });
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (args.length < 1) {
      sender.sendMessage(CowBot.generateTextComponent("No arguments specified"));
    }

    if (args[0].equalsIgnoreCase("reload")) {
      CowBot.getInstance().reload();
    } else {
      sender.sendMessage(CowBot.generateTextComponent("Unknown arguemnt."));
    }
  }
}