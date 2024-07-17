package me.hsgamer.bettergui.tokenmanagerbridge;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.api.requirement.TakableRequirement;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.config.MessageConfig;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class TokenRequirement extends TakableRequirement<Long> {
    protected TokenRequirement(RequirementBuilder.Input input) {
        super(input);
        getMenu().getVariableManager().register(getName(), StringReplacer.of((original, uuid) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return "";
            }
            long tokens = getFinalValue(uuid);
            if (tokens > 0 && !TokenManagerHook.hasTokens(player, tokens)) {
                return String.valueOf(tokens);
            }
            return BetterGUI.getInstance().get(MessageConfig.class).getHaveMetRequirementPlaceholder();
        }));
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
    protected Long convert(Object o, UUID uuid) {
        String parsed = StringReplacerApplier.replace(String.valueOf(o).trim(), uuid, this);
        return Validate.getNumber(parsed).map(BigDecimal::longValue).orElseGet(() -> {
            Optional.ofNullable(Bukkit.getPlayer(uuid)).ifPresent(player -> MessageUtils.sendMessage(player, BetterGUI.getInstance().get(MessageConfig.class).getInvalidNumber(parsed)));
            return 0L;
        });
    }

    @Override
    protected Result checkConverted(UUID uuid, Long value) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return Result.success();
        }
        if (value > 0 && !TokenManagerHook.hasTokens(player, value)) {
            return Result.fail();
        }
        return successConditional((uuid1, process) -> SchedulerUtil.global().run(() -> {
            if (!TokenManagerHook.takeTokens(player, value)) {
                player.sendMessage(ChatColor.RED + "Error: the transaction couldn't be executed. Please inform the staff.");
            }
            process.next();
        }));
    }
}
