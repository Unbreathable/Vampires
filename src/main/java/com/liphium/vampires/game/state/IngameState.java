package com.liphium.vampires.game.state;

import com.liphium.core.particle.ParticleBuilder;
import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.game.GameState;
import com.liphium.vampires.game.team.Team;
import com.liphium.vampires.game.team.impl.HumanTeam;
import com.liphium.vampires.game.team.impl.VampireTeam;
import com.liphium.vampires.util.LocationAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class IngameState extends GameState {

    private final ParticleBuilder builder, beetroot;

    private final ArrayList<BeetrootData> beetroots = new ArrayList<>();

    private Runnable runnable;

    public IngameState() {
        super("In game", 30);

        builder = new ParticleBuilder().withColor(Color.RED).withSize(4.0f);
        beetroot = new ParticleBuilder().withColor(Color.PURPLE);
    }

    public final ArrayList<Player> infected = new ArrayList<>(), prison = new ArrayList<>();

    private final ArrayList<Block> torches = new ArrayList<>();

    @Override
    public void start() {

        LocationAPI.getLocation("Camp").getWorld().setDifficulty(Difficulty.HARD);

        for (Team team : Vampires.getInstance().getGameManager().getTeamManager().getTeams()) {
            team.sendStartMessage();

            for (Player player : team.getPlayers()) {
                player.getInventory().clear();
                team.giveKit(player);
            }
        }

        Vampires.getInstance().getTaskManager().inject(runnable = new Runnable() {
            int tickCount = 0;

            @Override
            public void run() {
                Vampires.getInstance().getGameManager().getTeamManager().tick();
                Vampires.getInstance().getMachineManager().tick();

                if (tickCount++ >= 20) {
                    tickCount = 0;

                    if (prison.size() >= Vampires.getInstance().getGameManager().getTeamManager().getTeam("Humans").getPlayers().size()) {
                        handleWin(Vampires.getInstance().getGameManager().getTeamManager().getTeam("Vampires"));
                    }

                    for (Player player : infected) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7Du bist §c§linfiziert§7!"));

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            builder.renderPoint(all, player.getLocation().clone().add(0, 2.6, 0));
                        }
                    }

                    ArrayList<BeetrootData> toRemove = new ArrayList<>();

                    for (BeetrootData data : beetroots) {
                        if (data.start + 60000 <= System.currentTimeMillis()) {
                            data.item.remove();
                            toRemove.add(data);
                        }

                        for (Player all : Bukkit.getOnlinePlayers()) {
                            beetroot.renderCircle(all, data.location.clone().add(0, 0.3, 0), 3);
                        }
                    }

                    for (BeetrootData rem : toRemove) {
                        beetroots.remove(rem);
                    }
                }
            }
        });
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Vampires.getInstance().getMachineManager().onInteract(event);

        if (event.getPlayer().getCooldown(Material.BLAZE_ROD) > 0
                && event.getItem() != null && event.getItem().getType().equals(Material.BLAZE_ROD)) {
            event.setCancelled(true);
        }

        if (event.getItem() != null) {

            if (event.getItem().getType().equals(Material.BEETROOT) && event.getClickedBlock() != null) {
                beetroots.add(new BeetrootData(event.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5)));

                int amount = event.getPlayer().getInventory().getItemInMainHand().getAmount();
                if (amount == 1) {
                    event.getPlayer().getInventory().setItemInMainHand(null);
                } else event.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
            } else if (event.getItem().getType().equals(Material.FEATHER) && event.getPlayer().getCooldown(Material.FEATHER) <= 0) {

                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().normalize().multiply(event.getPlayer().isOnGround() ? 1.8 : 1.1));
                event.getPlayer().setCooldown(Material.FEATHER, 200);
            }
        }
    }

    @Override
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Vampires.getInstance().getMachineManager().onInteractAtEntity(event);

        if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType().equals(Material.TORCH)) {
            torches.add(event.getBlockPlaced());
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public void onFirework(FireworkExplodeEvent event) {

        ArrayList<Block> toRemove = new ArrayList<>();
        for (Block torch : torches) {
            if (torch.getLocation().clone().add(0.5, 0.5, 0.5).distance(event.getEntity().getLocation()) <= 3) {
                torch.setType(Material.AIR);
                toRemove.add(torch);
            }
        }

        for (Block block : toRemove) {
            torches.remove(block);
        }

    }

    @Override
    public void onMove(PlayerMoveEvent event) {
        Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(event.getPlayer());

        if (team instanceof VampireTeam) {

            if (event.getPlayer().getLocation().getBlock().getLightFromBlocks() >= 7) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 2));
            }

            ArrayList<BeetrootData> toRemove = new ArrayList<>();

            for (BeetrootData data : beetroots) {
                if (data.location.distance(event.getPlayer().getLocation()) <= 3) {
                    toRemove.add(data);

                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                    break;
                }
            }

            for (BeetrootData rem : toRemove) {
                rem.item.remove();
                beetroots.remove(rem);
            }

        }
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(player);

            if (team.getName().equals("Humans")) {
                event.setDamage(0);
            }
        } else event.setCancelled(true);
    }

    @Override
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {

            Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(damager);
            Team playerTeam = Vampires.getInstance().getGameManager().getTeamManager().getTeam(player);

            if (damager.getCooldown(Material.BLAZE_ROD) > 0) {
                event.setCancelled(true);
            }

            if (team instanceof VampireTeam && damager.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)
                    && damager.getCooldown(Material.BLAZE_ROD) <= 0
                    && playerTeam instanceof HumanTeam) {

                damager.setCooldown(Material.BLAZE_ROD, 400);

                if (infected.contains(player)) {
                    infected.remove(player);
                    player.teleport(LocationAPI.getLocation("Cell"));

                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage("    §c" + player.getName() + " §7wurde §c§lgefangen§7!");
                    Bukkit.broadcastMessage("§7Ihm wird das §cBlut §7abgesaugt und");
                    Bukkit.broadcastMessage("§7die §cVampire §7werden §c§lstärker§7!");
                    Bukkit.broadcastMessage(" ");

                    prison.add(player);

                    return;
                }

                infected.add(player);
                Bukkit.broadcastMessage(Vampires.PREFIX + "§c" + player.getName() + " §7wurde §c§linfiniziert§7!");
            }
        }
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.TORCH)) {
            event.setDropItems(false);
            torches.remove(event.getBlock());
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        handleDeath(event.getEntity());
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        event.setKeepLevel(true);
    }

    @Override
    public void handleDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);

        if (player.getKiller() != null) {
            Bukkit.broadcastMessage(Vampires.PREFIX + "§c" + player.getName() + " §7wurde von §c§l" + player.getKiller().getName() + " §7getötet!");
        } else
            Bukkit.broadcastMessage(Vampires.PREFIX + "§c§l" + player.getKiller().getName() + " §7ist an einem niedrigen §cBlutlevel §7gestorben!");

        player.getInventory().clear();
        player.setHealth(20);
        player.spigot().respawn();
        player.getInventory().clear();

        Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(player);
        team.getPlayers().remove(player);

        if (team.getPlayers().size() == 0) {
            handleWin(Vampires.getInstance().getGameManager().getTeamManager().getTeam("Humans"));
        }
    }

    public void handleWin(Team team) {

        for (Block torch : torches) {
            torch.setType(Material.AIR);
        }

        torches.clear();

        team.handleWin();
        Vampires.getInstance().getTaskManager().uninject(runnable);
        Vampires.getInstance().getGameManager().setCurrentState(new EndState());
    }

    @Override
    public void quit(Player player) {
        Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(player);
        team.getPlayers().remove(player);
    }

    public static class BeetrootData {

        public final Location location;
        public final Item item;
        public final long start;

        public BeetrootData(Location location) {
            this.location = location;

            item = (Item) location.getWorld().spawnEntity(location.clone().add(0, 1, 0), EntityType.ITEM);
            item.setItemStack(new ItemStackBuilder(Material.BEETROOT).buildStack());
            item.setVelocity(new Vector(0, 0, 0));
            item.setPickupDelay(1000000);
            item.setUnlimitedLifetime(true);
            item.setCustomNameVisible(true);
            item.customName(Component.text("Blut Knoblauch", NamedTextColor.RED, TextDecoration.BOLD));
            start = System.currentTimeMillis();
        }

    }
}
