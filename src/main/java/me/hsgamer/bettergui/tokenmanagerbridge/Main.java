package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.builder.CommandBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.manager.VariableManager;
import me.hsgamer.bettergui.object.addon.Addon;

public final class Main extends Addon {

  @Override
  public void onEnable() {
    TokenManagerHook.setupPlugin();
    RequirementBuilder.register("point", TokenIconRequirement.class);
    CommandBuilder.register("give-point:", GiveTokenCommand.class);
    VariableManager.register("tokens", (player, s) -> String
        .valueOf(TokenManagerHook.getTokens(player)));
  }
}
