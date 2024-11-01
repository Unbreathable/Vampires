package com.liphium.vampires.game.state;

import com.liphium.core.particle.ParticleBuilder;
import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.game.GameState;
import com.liphium.vampires.game.team.Team;
import com.liphium.vampires.game.team.impl.HumanTeam;
import com.liphium.vampires.game.team.impl.VampireTeam;
import com.liphium.vampires.listener.machines.impl.ItemShop;
import com.liphium.vampires.screens.ItemShopScreen;
import com.liphium.vampires.util.LocationAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IngameState extends GameState {

    private final ParticleBuilder builder, beetroot;

    private final ArrayList<BeetrootData> beetroots = new ArrayList<>();
    private final ArrayList<DroppableTrap> traps = new ArrayList<>();

    private Runnable runnable;

    public IngameState() {
        super("In game", 30);

        builder = new ParticleBuilder().withColor(Color.RED).withSize(4.0f);
        beetroot = new ParticleBuilder().withColor(Color.PURPLE);
    }

    public final ArrayList<Player> infected = new ArrayList<>(), prison = new ArrayList<>();

    private final ArrayList<Block> torches = new ArrayList<>();

    // Config
    private int escapeDistance = 10;

    @Override
    public void start() {

        Objects.requireNonNull(LocationAPI.getLocation("Camp")).getWorld().setDifficulty(Difficulty.HARD);

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
                        player.sendActionBar(Component.text("§7You are §c§linfected§7!"));

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

                    ArrayList<DroppableTrap> trapsToRemove = new ArrayList<>();
                    for (DroppableTrap trap : traps) {
                        if (trap.start + 60000 <= System.currentTimeMillis()) {
                            trap.item.remove();
                            trapsToRemove.add(trap);
                        }
                    }

                    for (DroppableTrap rem : trapsToRemove) {
                        traps.remove(rem);
                    }
                }
            }
        });
    }

    private boolean placeItemShop = false;

    @Override
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ARMOR_STAND && placeItemShop) {
            final var itemShop = new ItemShop((ArmorStand) event.getEntity());
            Vampires.getInstance().getMachineManager().addMachine(itemShop);
            placeItemShop = false;
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Vampires.getInstance().getMachineManager().onInteract(event);

        if (event.getClickedBlock() != null && event.getItem() != null && event.getItem().getType().equals(Material.ARMOR_STAND)) {
            placeItemShop = true;
            return;
        }

        if (event.getPlayer().getCooldown(Material.STICK) > 0
                && event.getItem() != null && event.getItem().getType().equals(Material.STICK)) {
            event.setCancelled(true);
        }

        if (event.getItem() != null) {
            Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(event.getPlayer());
            ItemStack usedItem = event.getItem();
            if (usedItem.getType().equals(Material.BEETROOT) && event.getClickedBlock() != null) {
                beetroots.add(new BeetrootData(event.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5)));
                reduceMainHandItem(event.getPlayer());
            } else if (usedItem.getType().equals(Material.REDSTONE_TORCH) && event.getClickedBlock() != null) {
                traps.add(new SlowTrap(event.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5), team));
                reduceMainHandItem(event.getPlayer());
            } else if (usedItem.getType().equals(Material.GLOWSTONE_DUST) && event.getClickedBlock() != null) {
                traps.add(new GlowTrap(event.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5), team));
                reduceMainHandItem(event.getPlayer());
            } else if (usedItem.getType().equals(Material.FEATHER) && event.getPlayer().getCooldown(Material.FEATHER) <= 0) {
                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().normalize().multiply(event.getPlayer().isOnGround() ? 1.8 : 1.1));
                event.getPlayer().setCooldown(Material.FEATHER, 50);
                ItemShopScreen.removeAmountFromInventory(event.getPlayer(), Material.FEATHER, 1);
            }
        }
    }

    void reduceMainHandItem(Player player) {
        int amount = player.getInventory().getItemInMainHand().getAmount();
        if (amount == 1) {
            player.getInventory().setItemInMainHand(null);
        } else player.getInventory().getItemInMainHand().setAmount(amount - 1);
    }

    @Override
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Vampires.getInstance().getMachineManager().onInteractAtEntity(event);

        if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
            event.setCancelled(true);
        }
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

        } else if (team instanceof HumanTeam) {
            final var cell = Objects.requireNonNull(LocationAPI.getLocation("Cell"));
            if (prison.contains(event.getPlayer()) && event.getPlayer().getLocation().distance(cell) >= escapeDistance) {
                prison.remove(event.getPlayer());
                Bukkit.broadcast(Component.text(" "));
                Bukkit.broadcast(Component.text("    §c" + event.getPlayer().getName() + " §c§lescaped §7the cell!"));
                Bukkit.broadcast(Component.text(" "));
                team.giveKit(event.getPlayer());
            }
        }


        ArrayList<DroppableTrap> toRemove = new ArrayList<>();

        for (DroppableTrap trap : traps) {
            if (trap.location.distance(event.getPlayer().getLocation()) <= 3 && !team.getName().equals(trap.team.getName())) {
                toRemove.add(trap);
                trap.onEnter(event.getPlayer());
                break;
            }
        }

        for (DroppableTrap rem : toRemove) {
            rem.item.remove();
            traps.remove(rem);
        }

    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(player);

            if (team instanceof HumanTeam) {
                event.setDamage(0);
            }
        } else event.setCancelled(true);
    }

    @Override
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {

            Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(damager);
            Team playerTeam = Vampires.getInstance().getGameManager().getTeamManager().getTeam(player);

            final var heldItemMaterial = damager.getInventory().getItemInMainHand().getType();
            if (damager.getCooldown(heldItemMaterial) > 0) {
                event.setCancelled(true);
                return;
            }

            if (team instanceof VampireTeam && damager.getInventory().getItemInMainHand().getType().equals(Material.STICK)
                    && damager.getCooldown(Material.STICK) <= 0
                    && playerTeam instanceof HumanTeam) {

                damager.setCooldown(Material.STICK, 400);

                if (infected.contains(player)) {
                    sendToPrison(player);
                    return;
                }

                infected.add(player);
                Bukkit.broadcast(Vampires.PREFIX.append(Component.text("§c" + player.getName() + " §7was §c§linfected§7!")));
            }
        }
    }

    void sendToPrison(Player player) {
        player.getInventory().clear();
        player.teleport(Objects.requireNonNull(LocationAPI.getLocation("Cell")));

        Bukkit.broadcast(Component.text(" "));
        Bukkit.broadcast(Component.text("    §c" + player.getName() + " §7was §c§lcaught§7!"));
        Bukkit.broadcast(Component.text(" "));

        prison.add(player);
        infected.remove(player);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType().equals(Material.REDSTONE_TORCH)) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlockPlaced().getType().equals(Material.TORCH)) {
            torches.add(event.getBlockPlaced());
        }

        // Place a machine if it is one
        final var machine = Vampires.getInstance().getMachineManager().newMachineByMaterial(event.getBlockPlaced().getType(), event.getBlockPlaced().getLocation());
        if (machine != null) {
            Vampires.getInstance().getMachineManager().addMachine(machine);
        }
    }

    final List<Material> breakableBlocks = List.of(Material.DIRT, Material.GRASS_BLOCK, Material.SHORT_GRASS, Material.TALL_GRASS);

    @Override
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.TORCH)) {
            event.setDropItems(false);
            torches.remove(event.getBlock());
            return;
        }

        if (Vampires.getInstance().getMachineManager().breakLocation(event.getBlock().getLocation())) {
            event.setDropItems(false);
            return;
        }

        event.setCancelled(!breakableBlocks.contains(event.getBlock().getType()));
    }

    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
        event.setDeathMessage(null);
        event.setKeepLevel(true);
        handleDeath(event.getEntity());
    }

    @Override
    public void handleDeath(Player player) {
        player.setGameMode(GameMode.SPECTATOR);

        if (player.getKiller() != null) {
            Bukkit.broadcastMessage(Vampires.PREFIX + "§c" + player.getName() + " §7was killed by §c§l" + player.getKiller().getName() + "§7!");
        } else
            Bukkit.broadcastMessage(Vampires.PREFIX + "§c§l" + player.getKiller().getName() + " §7died!");

        player.getInventory().clear();
        player.setHealth(20);
        player.spigot().respawn();
        player.getInventory().clear();

        Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(player);
        team.getPlayers().remove(player);

        if (team.getPlayers().isEmpty()) {
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
            item.setCanPlayerPickup(false);
            item.setCanMobPickup(false);
            item.setUnlimitedLifetime(true);
            item.setCustomNameVisible(true);
            item.customName(Component.text("Blood Garlic", NamedTextColor.RED, TextDecoration.BOLD));
            start = System.currentTimeMillis();
        }

    }

    public static abstract class DroppableTrap {
        public final Location location;
        public final Team team;
        public final Item item;
        public final long start;

        public DroppableTrap(Location location, Team team, Material material) {
            this.location = location;
            this.team = team;

            item = (Item) location.getWorld().spawnEntity(location.clone().add(0, 1, 0), EntityType.ITEM);
            item.setItemStack(new ItemStackBuilder(material).buildStack());
            item.setVelocity(new Vector(0, 0, 0));
            item.setCanPlayerPickup(false);
            item.setCanMobPickup(false);
            item.setUnlimitedLifetime(true);
            start = System.currentTimeMillis();
        }

        public abstract void onEnter(Player player);
    }

    public static class SlowTrap extends DroppableTrap {

        SlowTrap(Location location, Team team) {
            super(location, team, Material.REDSTONE_TORCH);
        }

        @Override
        public void onEnter(Player player) {

            // Notify all players in the team that planted the trap
            final var x = player.getLocation().getBlockX();
            final var y = player.getLocation().getBlockY();
            final var z = player.getLocation().getBlockZ();
            for (Player human : Vampires.getInstance().getGameManager().getTeamManager().getTeam("Humans").getPlayers()) {
                human.sendMessage(Component.text(" "));
                human.sendMessage(Component.text("     §c§l" + player.getName() + " §7walked into a slow trap."));
                human.sendMessage(Component.text("     §7Location: §c" + x + " " + y + " " + z));
                human.sendMessage(Component.text(" "));
            }

            // Give the player slowness
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
        }
    }

    public static class GlowTrap extends DroppableTrap {

        GlowTrap(Location location, Team team) {
            super(location, team, Material.GLOWSTONE_DUST);
        }

        @Override
        public void onEnter(Player player) {

            // Notify all players in the team that planted the trap
            final var x = player.getLocation().getBlockX();
            final var y = player.getLocation().getBlockY();
            final var z = player.getLocation().getBlockZ();
            for (Player human : Vampires.getInstance().getGameManager().getTeamManager().getTeam("Humans").getPlayers()) {
                human.sendMessage(Component.text(" "));
                human.sendMessage(Component.text("     §c§l" + player.getName() + " §7walked into a glow trap."));
                human.sendMessage(Component.text("     §7Location: §c" + x + " " + y + " " + z));
                human.sendMessage(Component.text(" "));
            }

            // Give the player slowness
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 0));
        }
    }
}
