package me.hsgamer.bettergui.tokenmanagerbridge;

import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TokenManagerHook {
  private static TokenManager tokenManager;

  public static void setupPlugin() {
    tokenManager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
  }

  public static long getTokens(Player player) {
    return tokenManager.getTokens(player).orElse(0);
  }

  public static boolean hasTokens(Player player, long minimum) {
    return tokenManager.getTokens(player).orElse(0) >= minimum;
  }

  public static boolean takeTokens(Player player, long tokens) {
    return tokenManager.removeTokens(player, tokens);
  }


  public static boolean giveTokens(Player player, long tokens) {
    return tokenManager.addTokens(player, tokens);
  }
}
