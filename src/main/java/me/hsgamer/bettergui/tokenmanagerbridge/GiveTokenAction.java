package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class GiveTokenAction extends BaseAction {

    protected GiveTokenAction(ActionBuilder.Input input) {
        super(input);
    }

    @Override
    public void accept(UUID uuid, TaskProcess process) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            process.next();
            return;
        }

        String parsed = getReplacedString(uuid);
        Optional<Long> optionalTokens = Validate.getNumber(parsed).map(BigDecimal::longValue);
        if (!optionalTokens.isPresent()) {
            player.sendMessage(ChatColor.RED + "Invalid token amount: " + parsed);
            process.next();
            return;
        }
        long tokensToGive = optionalTokens.get();

        if (tokensToGive > 0) {
            Scheduler.current().sync().runTask(() -> {
                if (!TokenManagerHook.giveTokens(player, tokensToGive)) {
                    player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
                }
                process.next();
            });
        } else {
            process.next();
        }
    }
}
