package com.Softwareprojekt.visualization;

import com.Softwareprojekt.interfaces.ColorProvider;
import com.Softwareprojekt.interfaces.Mesh;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Polygon;

import java.util.HashMap;
import java.util.Map;

public class ChromaticCellsColorProvider implements ColorProvider {

    private final Map<Mesh, Color> _meshToColor = new HashMap<Mesh, Color>();

    public ChromaticCellsColorProvider() {
    }

    @Override
    public Color getColor(Mesh mesh, Polygon face) {
        if (!this._meshToColor.containsKey(mesh)) {
            Color color = Color.random();
            color.a = 0.8f;
            this._meshToColor.put(mesh, color);
        }
        return this._meshToColor.get(mesh);
    }

    @Override
    public void reset() {
        this._meshToColor.clear();
    }
}