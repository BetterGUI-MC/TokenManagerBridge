package me.hsgamer.bettergui.tokenmanagerbridge;

import java.util.Objects;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.lib.taskchain.TaskChain;
import me.hsgamer.bettergui.object.Command;
import me.hsgamer.bettergui.util.MessageUtils;
import me.hsgamer.bettergui.util.common.Validate;
import me.hsgamer.bettergui.util.expression.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GiveTokenCommand extends Command {

  public GiveTokenCommand(String string) {
    super(string);
  }

  @Override
  public void addToTaskChain(Player player, TaskChain<?> taskChain) {
    long tokensToGive = 0;
    String parsed = getParsedCommand(player);
    if (Validate.isValidPositiveNumber(parsed)) {
      tokensToGive = Long.parseLong(parsed);
    } else if (ExpressionUtils.isValidExpression(parsed)) {
      tokensToGive = Objects.requireNonNull(ExpressionUtils.getResult(parsed)).longValue();
    } else {
      MessageUtils
          .sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
    }

    if (tokensToGive > 0) {
      long finalTokensToGive = tokensToGive;
      taskChain.sync(() -> {
        if (!TokenManagerHook.giveTokens(player, finalTokensToGive)) {
          player.sendMessage(ChatColor.RED
              + "Error: the transaction couldn't be executed. Please inform the staff.");
        }
      });
    }

  }
}
