package com.liphium.core.particle.shapes;

import com.liphium.core.particle.ParticleBuilder;
import com.liphium.core.particle.ParticleShape;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LineShape implements ParticleShape {

    @Override
    public void renderShape(Player player, Location[] locations, ParticleBuilder builder) {

        final double deltaX = locations[1].getX() - locations[0].getX();
        final double deltaY = locations[1].getY() - locations[0].getY();
        final double deltaZ = locations[1].getZ() - locations[0].getZ();
        final int steps = (int) (Math.round(locations[0].distance(locations[1])) * (4 / builder.getParticleSize()) * 1.5);

        for (double d = 0; d <= 1.0; d += 1.0 / steps) {
            builder.getRenderer().renderParticle(player, locations[0].clone().add(deltaX * d, deltaY * d, deltaZ * d));
        }

    }

}
