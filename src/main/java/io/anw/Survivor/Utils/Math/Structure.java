package io.anw.Survivor.Utils.Math;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public abstract class Structure {

    public abstract boolean intersects(Structure structure);

    public abstract boolean isInside(Location location);

    public abstract List<Block> getBlocks();

    public abstract void fill(Material material, byte data, List<Material> materialsToReplace, List<Material> materialsToStore);

}
