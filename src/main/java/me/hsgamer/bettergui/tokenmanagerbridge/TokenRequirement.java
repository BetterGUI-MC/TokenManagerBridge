package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.api.requirement.TakableRequirement;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.lib.core.bukkit.utils.MessageUtils;
import me.hsgamer.bettergui.lib.core.expression.ExpressionUtils;
import me.hsgamer.bettergui.lib.core.variable.VariableManager;
import me.hsgamer.bettergui.manager.PluginVariableManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TokenRequirement extends TakableRequirement<Long> {

    private final Map<UUID, Long> checked = new HashMap<>();

    public TokenRequirement(String name) {
        super(name);
        PluginVariableManager.register(name, (original, uuid) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return "";
            }
            long tokens = getParsedValue(uuid);
            if (tokens > 0 && !TokenManagerHook.hasTokens(player, tokens)) {
                return String.valueOf(tokens);
            }
            return MessageConfig.HAVE_MET_REQUIREMENT_PLACEHOLDER.getValue();
        });
    }

    @Override
    protected boolean getDefaultTake() {
        return true;
    }

    @Override
    protected Object getDefaultValue() {
        return "0";
    }

    @Override
    protected void takeChecked(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        if (!TokenManagerHook.takeTokens(player, checked.remove(uuid))) {
            player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
        }
    }

    @Override
    public Long getParsedValue(UUID uuid) {
        String parsed = VariableManager.setVariables(String.valueOf(value).trim(), uuid);
        return Optional.ofNullable(ExpressionUtils.getResult(parsed)).map(BigDecimal::longValue).orElseGet(() -> {
            Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> MessageUtils.sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed)));
            return 0L;
        });
    }

    @Override
    public boolean check(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return true;
        }
        long tokens = getParsedValue(uuid);
        if (tokens > 0 && !TokenManagerHook.hasTokens(player, tokens)) {
            return false;
        }
        checked.put(uuid, tokens);
        return true;
    }
}
