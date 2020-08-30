package me.hsgamer.bettergui.tokenmanagerbridge;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.bettergui.object.variable.LocalVariable;
import me.hsgamer.bettergui.object.variable.LocalVariableManager;
import me.hsgamer.bettergui.util.MessageUtils;
import me.hsgamer.bettergui.util.common.Validate;
import me.hsgamer.bettergui.util.expression.ExpressionUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TokenIconRequirement extends Requirement<Object, Long> implements LocalVariable {

  private final Map<UUID, Long> checked = new HashMap<>();

  public TokenIconRequirement() {
    super(true);
  }

  @Override
  public Long getParsedValue(Player player) {
    String parsed = parseFromString(String.valueOf(value).trim(), player);
    if (ExpressionUtils.isValidExpression(parsed)) {
      return ExpressionUtils.getResult(parsed).longValue();
    } else {
      Optional<BigDecimal> number = Validate.getNumber(parsed);
      if (number.isPresent()) {
        return number.get().longValue();
      } else {
        MessageUtils.sendMessage(player,
            MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
        return 0L;
      }
    }
  }

  @Override
  public boolean check(Player player) {
    long tokens = getParsedValue(player);
    if (tokens > 0 && !TokenManagerHook.hasTokens(player, tokens)) {
      return false;
    } else {
      checked.put(player.getUniqueId(), tokens);
      return true;
    }
  }

  @Override
  public void take(Player player) {
    if (!TokenManagerHook.takeTokens(player, checked.remove(player.getUniqueId()))) {
      player.sendMessage(ChatColor.RED
          + "Error: the transaction couldn't be executed. Please inform the staff.");
    }
  }

  @Override
  public String getIdentifier() {
    return "require_tokens";
  }

  @Override
  public LocalVariableManager<?> getInvolved() {
    return getVariableManager();
  }

  @Override
  public String getReplacement(OfflinePlayer player, String s) {
    if (!player.isOnline()) {
      return "";
    }
    long tokens = getParsedValue(player.getPlayer());
    if (tokens > 0 && !TokenManagerHook.hasTokens(player.getPlayer(), tokens)) {
      return String.valueOf(tokens);
    }
    return MessageConfig.HAVE_MET_REQUIREMENT_PLACEHOLDER.getValue();
  }
}
