package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.lib.core.bukkit.utils.MessageUtils;
import me.hsgamer.bettergui.lib.core.common.Validate;
import me.hsgamer.bettergui.lib.core.expression.ExpressionUtils;
import me.hsgamer.bettergui.lib.taskchain.TaskChain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class GiveTokenAction extends BaseAction {

    public GiveTokenAction(String string) {
        super(string);
    }

    @Override
    public void addToTaskChain(UUID uuid, TaskChain<?> taskChain) {
        long tokensToGive = 0;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        String parsed = getReplacedString(uuid);
        if (Validate.isValidPositiveNumber(parsed)) {
            tokensToGive = Long.parseLong(parsed);
        } else if (ExpressionUtils.isValidExpression(parsed)) {
            tokensToGive = Objects.requireNonNull(ExpressionUtils.getResult(parsed)).longValue();
        } else {
            MessageUtils.sendMessage(player, MessageConfig.INVALID_NUMBER.getValue().replace("{input}", parsed));
        }

        if (tokensToGive > 0) {
            long finalTokensToGive = tokensToGive;
            taskChain.sync(() -> {
                if (!TokenManagerHook.giveTokens(player, finalTokensToGive)) {
                    player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
                }
            });
        }
    }
}
