package com.Softwareprojekt.Utilities;

import com.Softwareprojekt.interfaces.Mesh;
import com.Softwareprojekt.interfaces.Polygon;
import com.Softwareprojekt.interfaces.Vector3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ImmutableMesh implements Mesh {

	private final List<Vector3D> _vertices;
	private final List<Polygon> _polygons;

	/**
	 * Constructor of a mesh.
	 * @param vertices
	 * @param polys
	 */
	public ImmutableMesh(Vector3D[] vertices, Polygon[] polys) {
		this(new ArrayList<Vector3D>(Arrays.asList(vertices)), new ArrayList<Polygon>(Arrays.asList(polys)));
	}
	
	/**
	 * Constructor of a mesh.
	 * @param vertices
	 * @param polys
	 */
	public ImmutableMesh(Collection<Vector3D> vertices, Collection<Polygon> polys) {
		this(new ArrayList<Vector3D>(vertices), new ArrayList<Polygon>(polys));
	}

    /**
     * Constructor f a mesh.
     * @param vertices
     */
    public ImmutableMesh(List<Vector3D> vertices) {
        this._vertices = Collections.unmodifiableList(vertices);
        this._polygons = Collections.unmodifiableList(new ArrayList<Polygon>(0));
    }

	/**
	 * Constructor f a mesh.
	 * @param vertices
	 * @param polys
	 */
	public ImmutableMesh(List<Vector3D> vertices, List<Polygon> polys) {
		this._vertices = Collections.unmodifiableList(vertices);
		this._polygons = Collections.unmodifiableList(polys);
	}
	
	@Override
	public List<Vector3D> getVertices() {
		return this._vertices;
	}

	@Override
	public List<Polygon> getFaces() {
		return this._polygons;
	}

}