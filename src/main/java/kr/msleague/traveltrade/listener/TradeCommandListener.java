package kr.msleague.traveltrade.listener;

import kr.msleague.bgmlib.WarningMessage;
import kr.msleague.traveltrade.Constants;
import kr.msleague.traveltrade.trade.Trade;
import kr.msleague.traveltrade.trade.TradeManager;
import kr.msleague.traveltrade.TravelTrade;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TradeCommandListener implements CommandExecutor {
    public boolean isFar(Player player1, Player player2) {
        return player1.getLocation().distance(player2.getLocation()) > 5;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        UUID playerID = player.getUniqueId();
        if(args.length == 0) {
            player.sendMessage("§f");
            player.sendMessage(Constants.MESSAGE_PREFIX +"§f/거래 신청 [닉네임] | 거래를 신청합니다.");
            player.sendMessage(Constants.MESSAGE_PREFIX +"§f/거래 수락 | 거래를 수락합니다.");
            player.sendMessage(Constants.MESSAGE_PREFIX +"§f/거래 거절 | 거래를 거절합니다.");
            player.sendMessage("§f");
            return false;
        }
        if(args[0].equals("신청")) {
            if(args.length!=2) {
                WarningMessage.sendWarningMsg(player, "플레이어의 이름을 입력해주세요.");
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                WarningMessage.sendWarningMsg(player, "해당 플레이어는 온라인이 아닙니다.");
                return false;
            }
            if(target.equals(player)) {
                WarningMessage.sendWarningMsg(player, "자신에게 거래를 신청할 수 없습니다.");
                return false;
            }
            UUID targetID = target.getUniqueId();
            for(Trade trade : TravelTrade.tradeSet) {
                if(trade.getTargetUniqueId().equals(targetID)) {
                    WarningMessage.sendWarningMsg(player, "해당 플레이어는 현재 거래 신청을 받을 수 없습니다.");
                    return false;
                }
                else if(trade.getSenderUniqueId().equals(playerID)) {
                    WarningMessage.sendWarningMsg(player, "이미 다른 플레이어에게 거래 신청을 보냈습니다.");
                    return false;
                }
            }
            if(isFar(player, target)) {
                WarningMessage.sendWarningMsg(player, "해당 플레이어와의 거리가 너무 멉니다! (5칸 이하)");
                return false;
            }
            Trade trade = new Trade(player, target);
            trade.tradeInvite();

            player.sendMessage("거래 신청을 보냈습니다");
            target.sendMessage(player.getName()+"님에게 거래 신청을 받았습니다. [/거래 수락/거절]");
        }
        if(args[0].equals("거절") || args[0].equals("수락")) {
            Trade var = TradeManager.getTradeByTarget(player);
            if(var == null) {
                WarningMessage.sendWarningMsg(player, "당신에게 온 거래 신청이 없습니다.");
                return false;
            }
            if(args[0].equals("거절"))
                var.inviteDeny();
            else if(args[0].equals("수락")) {
                Player sendPlayer = var.getSenderPlayer();
                if(isFar(player, sendPlayer)) {
                    WarningMessage.sendWarningMsg(player,"해당 플레이어와의 거리가 너무 멉니다! (5칸 이하)");
                    return false;
                }
                var.inviteAccept();
            }
        }

        return false;

    }
}
