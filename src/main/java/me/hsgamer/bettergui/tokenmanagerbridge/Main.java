package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.api.addon.BetterGUIAddon;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.lib.core.variable.VariableManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Main extends BetterGUIAddon {

    @Override
    public void onEnable() {
        TokenManagerHook.setupPlugin();
        RequirementBuilder.INSTANCE.register(TokenRequirement::new, "token");
        ActionBuilder.INSTANCE.register(GiveTokenAction::new, "give-token");
        VariableManager.register("tokens", (original, uuid) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return "";
            }
            return String.valueOf(TokenManagerHook.getTokens(player));
        });
    }
}
