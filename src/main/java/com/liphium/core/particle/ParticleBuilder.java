package com.liphium.core.particle;

import com.liphium.core.particle.shapes.CircleShape;
import com.liphium.core.particle.shapes.LineShape;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleBuilder {

    private Color particleColor = Color.RED;
    private float particleSize = 1.0f;

    private ParticleRunnable renderer = (player, location) -> {
        player.spawnParticle(Particle.DUST, location, 1, new Particle.DustOptions(particleColor, particleSize));
    };

    public ParticleBuilder() {

    }

    public ParticleBuilder(ParticleRunnable renderer) {
        this.renderer = renderer;
    }

    public ParticleBuilder withColor(Color color) {
        this.particleColor = color;
        return this;
    }

    public ParticleBuilder withSize(float size) {
        this.particleSize = size;
        return this;
    }

    public void renderLine(Player player, Location loc1, Location loc2) {

        LineShape lineShape = new LineShape();
        lineShape.renderShape(player, new Location[]{loc1, loc2}, this);
    }

    public void renderCircle(Player player, Location center, int radius) {

        CircleShape circleShape = new CircleShape(radius);
        circleShape.renderShape(player, new Location[]{center}, this);
    }

    public void renderShape(Player player, ParticleShape shape, Location[] locations) {
        shape.renderShape(player, locations, this);
    }

    public void renderPoint(Player player, Location location) {
        renderer.renderParticle(player, location);
    }

    public ParticleRunnable getRenderer() {
        return renderer;
    }

    public float getParticleSize() {
        return particleSize;
    }

    public Color getParticleColor() {
        return particleColor;
    }

    public interface ParticleRunnable {

        void renderParticle(Player player, Location location);

    }

}
