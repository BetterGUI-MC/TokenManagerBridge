package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.addon.Addon;

public final class Main extends Addon {

  @Override
  public void onEnable() {
    TokenManagerHook.setupPlugin();
    RequirementBuilder.register("token", TokenIconRequirement.class);
    CommandBuilder.register("give-token:", GiveTokenCommand.class);
    VariableManager.register("tokens", (player, s) -> {
      if (!player.isOnline()) {
        return "";
      }
      return String.valueOf(TokenManagerHook.getTokens(player.getPlayer()));
    });
  }
}
