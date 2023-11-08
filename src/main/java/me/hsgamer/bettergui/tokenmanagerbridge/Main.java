package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.variable.VariableBundle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Main implements Expansion {
    private final VariableBundle variableBundle = new VariableBundle();

    @Override
    public void onEnable() {
        TokenManagerHook.setupPlugin();
        RequirementBuilder.INSTANCE.register(TokenRequirement::new, "token");
        ActionBuilder.INSTANCE.register(GiveTokenAction::new, "give-token");
        variableBundle.register("tokens", StringReplacer.of((original, uuid) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return "";
            }
            return String.valueOf(TokenManagerHook.getTokens(player));
        }));
    }

    @Override
    public void onDisable() {
        variableBundle.unregisterAll();
    }
}
