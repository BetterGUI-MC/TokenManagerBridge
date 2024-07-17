package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.hscore.action.common.Action;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.task.element.TaskProcess;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class GiveTokenAction implements Action {
    private final String value;

    protected GiveTokenAction(ActionBuilder.Input input) {
        this.value = input.getValue();
    }

    @Override
    public void apply(UUID uuid, TaskProcess process, StringReplacer stringReplacer) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            process.next();
            return;
        }

        String parsed = stringReplacer.replaceOrOriginal(value, uuid);
        Optional<Long> optionalTokens = Validate.getNumber(parsed).map(BigDecimal::longValue);
        if (!optionalTokens.isPresent()) {
            player.sendMessage(ChatColor.RED + "Invalid token amount: " + parsed);
            process.next();
            return;
        }
        long tokensToGive = optionalTokens.get();

        if (tokensToGive > 0) {
            SchedulerUtil.global().run(() -> {
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
