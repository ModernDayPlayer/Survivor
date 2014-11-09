package io.anw.Survivor.Utils.Math;

import java.util.ArrayList;
import java.util.List;

import io.anw.Survivor.Utils.Math.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Sphere extends Structure {

    public int posX;
    public int posY;
    public int posZ;
    public int radius;

    public World world;

    public Sphere(Location loc, int r) {
        posX = loc.getBlockX();
        posY = loc.getBlockY();
        posZ = loc.getBlockZ();

        world = loc.getWorld();

        radius = r;
    }

    public boolean isInside(Location loc) {
        if (!world.equals(loc.getWorld())) {
            return false;
        }

        double distance = (loc.getX() - posX) * (loc.getX() - posX) + (loc.getY() - posY) * (loc.getY() - posY) + (loc.getZ() - posZ) * (loc.getZ() - posZ);

        if (distance > radius * radius) {
            return false;
        }

        return true;
    }

    public boolean intersects(Structure s) {
        List<Block> blocks = s.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            if (isInside((blocks.get(i)).getLocation())) {
                return true;
            }
        }
        return false;
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (int x = posX - radius; x <= posX + radius; x++) {
            for (int y = posY - radius; y <= posY + radius; y++) {
                for (int z = posZ - radius; z <= posZ + radius; z++) {
                    double distance = (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z);
                    if (distance < radius * radius) {
                        blocks.add(world.getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }

    public void fill(Material fill, byte data, List<Material> replace, List<Material> keep) {
        for (int x = posX - radius; x <= posX + radius; x++) {
            for (int y = posY - radius; y <= posY + radius; y++) {
                for (int z = posZ - radius; z <= posZ + radius; z++) {
                    double distance = (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z);
                    if (distance < radius * radius) {
                        Block block = world.getBlockAt(x, y, z);
                        if ((keep == null) || (!keep.contains(block.getType()))) {
                            if ((replace == null) || (replace.contains(block.getType()))) {
                                block.setType(fill);
                                block.setData(data);
                            }
                        }
                    }
                }
            }
        }
    }

    public void breakBlocks(List<Material> replace, List<Material> keep) {
        for (int x = posX - radius; x <= posX + radius; x++) {
            for (int y = posY - radius; y <= posY + radius; y++) {
                for (int z = posZ - radius; z <= posZ + radius; z++) {
                    double distance = (posX - x) * (posX - x) + (posY - y) * (posY - y) + (posZ - z) * (posZ - z);
                    if (distance < radius * radius) {
                        Block block = world.getBlockAt(x, y, z);
                        if ((keep == null) || (!keep.contains(block.getType()))) {
                            if ((replace == null) || (replace.contains(block.getType()))) {
                                block.breakNaturally();
                            }
                        }
                    }
                }
            }
        }
    }

    public Location getCenterBlock() {
        return new Location(world, posX, posY, posZ);
    }

    public void setCenterBlock(Location loc) {
        posX = loc.getBlockX();
        posY = loc.getBlockY();
        posZ = loc.getBlockZ();

        world = loc.getWorld();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int r) {
        radius = r;
    }
}
