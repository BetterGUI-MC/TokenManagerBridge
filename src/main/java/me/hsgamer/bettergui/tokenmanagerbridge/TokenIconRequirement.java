package me.hsgamer.bettergui.tokenmanagerbridge;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.config.impl.MessageConfig.DefaultMessage;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.LocalVariable;
import me.hsgamer.bettergui.object.Requirement;
import me.hsgamer.bettergui.util.CommonUtils;
import me.hsgamer.bettergui.util.ExpressionUtils;
import me.hsgamer.bettergui.util.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TokenIconRequirement extends Requirement<Object, Long> implements LocalVariable<Icon> {

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
        CommonUtils.sendMessage(player,
            BetterGUI.getInstance().getMessageConfig().get(DefaultMessage.INVALID_NUMBER)
                .replace("{input}", parsed));
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
  public Optional<Icon> getInvolved() {
    return getIcon();
  }

  @Override
  public String getReplacement(Player player, String s) {
    long tokens = getParsedValue(player);
    if (tokens > 0 && !TokenManagerHook.hasTokens(player, tokens)) {
      return String.valueOf(tokens);
    }
    return BetterGUI.getInstance().getMessageConfig()
        .get(DefaultMessage.HAVE_MET_REQUIREMENT_PLACEHOLDER);
  }
}
