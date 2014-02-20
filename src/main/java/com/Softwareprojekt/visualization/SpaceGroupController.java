package com.Softwareprojekt.visualization;

import java.io.FileNotFoundException;
import java.util.*;

import com.Softwareprojekt.InternationalShortSymbol.SpaceGroupFactoryImpl;
import com.Softwareprojekt.InternationalShortSymbol.ID;
import com.Softwareprojekt.Utilities.ImmutableMesh;

import com.Softwareprojekt.Utilities.*;
import com.Softwareprojekt.interfaces.*;

public class SpaceGroupController implements Controller<ID> {

	private final Model _model;
	private final View _view;
	private final EnumSet<ViewOptions> _options = EnumSet.of(ViewOptions.ShowWireframe, ViewOptions.ShowFaces
            , ViewOptions.ShowAxeBox);
	private VisualizationSteps _step = VisualizationSteps.VoronoiTesselation;
    protected  ID _currentSpaceGroupID;

    private static final String Default_Group_ID = "F23";

	/**
	 * Factory method to create controller.
	 * @return
	 */
	public static Controller<ID> createController() {
		final SpaceGroupModel model = new SpaceGroupModel();
     	return new SpaceGroupController(model);
	}
	
	/**
	 * Constructor of space group controller.
	 * @param model
	 */
	private SpaceGroupController(Model model) {
		this._model = model;

        try {
            final SpaceGroupFactory<ID> factory = new SpaceGroupFactoryImpl();
            this._currentSpaceGroupID = new ID(Default_Group_ID);
            this._model.setSpaceGroup(factory.createSpaceGroup(this._currentSpaceGroupID));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		this._view = new SpaceGroupView(this);
		this._view.invalidateView();
	}
	
	@Override
	public void setVisualizationStep(VisualizationSteps step) {
		if (step != this._step) {
			this._step = step;
			this._view.invalidateView();
		}
	}

	@Override
	public VisualizationSteps getVisualizationStep() { return this._step; }
	
	@Override
	public Vector3D getOriginPoint() {
		return this._model.getPoint();
	}

	@Override
	public void setOriginPoint(Vector3D point) {
		this._model.setPoint(point);
		this._view.invalidateView();
	}

    @Override
    public void setSpaceGroup(SpaceGroup spaceGroup) {
        this._model.setSpaceGroup(spaceGroup);
        this._view.invalidateView();
    }

    @Override
    public void setSpaceGroup(ID id) {
        try {
            final SpaceGroupFactory<ID> factory = new SpaceGroupFactoryImpl();
            this._model.setSpaceGroup(factory.createSpaceGroup(id));
            this._currentSpaceGroupID = id;
            this._view.invalidateView();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SpaceGroup getSpaceGroup() {
        return this._model.getSpaceGroup();
    }

    @Override
    public ID getSpaceGroupID() {
        return this._currentSpaceGroupID;
    }

    /**
	 * Returns true if the specified view option is turned on otherwise false..
	 */
	public boolean getViewOption(ViewOptions option) { return this._options.contains(option); }
	
	/**
	 * Enables or disables the specified view option.
	 */
	public void setViewOption(ViewOptions option, boolean value) {
		// update enum set
        if (value)
			this._options.add(option);
		else
			this._options.remove(option);

        final boolean invalidateView;
        switch (option) {
            case ShowUnifiedCells: invalidateView = true; break;
            default: invalidateView = false;
        }

        // raise invalidation
        if (invalidateView) {
            this._view.invalidateView();
        }
        else {
            this._view.invalidateViewOptions();
        }
	}
	
	@Override
	public Model getModel() { return this._model; }

	@Override
	public View getView() { return this._view; }
	
	/**
	 * Calculates the mesh according visualization step.
	 * @param
	 * @return
	 */
	@Override
	public List<Mesh> calculateMesh() {
		List<Mesh> mesh = new LinkedList<Mesh>();
        Mesh qMesh;
		PointList p = this._model.getCalculatedPoints();
		// trigger qhull wrapper according current viz step
        try {
            switch (this.getVisualizationStep()) {
                case ScatterPlot:
                    mesh.add(ConvertHelper.convertPointListToMesh(p));
                    break;
                case ConvexHull:
                    mesh.add(QConvex.call(p));
                    break;
                case DelaunayTriangulation:
                    qMesh = QDelaunay.call(p);
                    for (Polygon poly : qMesh.getFaces()) {
                        PointList cellPoints = new PointList();
                        cellPoints.addAll(poly.getVertices());
                        mesh.add(QConvex.call(cellPoints));
                    }
                    break;
                case VoronoiTesselation:
                    qMesh = QVoronoi.call(p);
                    qMesh = removeVertexFromMesh(qMesh.getVertices().get(0), qMesh);
                    /*if (this.getViewOption(ViewOptions.ShowUnifiedCells)) {
                        qMesh = filterForMajorityCell(qMesh);
                    }*/
                    for (Polygon poly : qMesh.getFaces()) {
                        PointList cellPoints = new PointList();
                        cellPoints.addAll(poly.getVertices());
                            mesh.add(QConvex.call(cellPoints));
                    }
                    // filter for majority mesh
                    if (this.getViewOption(ViewOptions.ShowUnifiedCells)) {
                        mesh = filterByVolume(mesh);
                    }
                    break;
            }
        }
        catch (QHullException e) {
            e.printStackTrace();
        }
		return mesh;
	}
	
	/**
	 * Removes the specified vertex from mesh.
	 * @param point
	 * @param mesh
	 */
	private static Mesh removeVertexFromMesh(Vector3D point, Mesh mesh) {
		List<Vector3D> vertices = new LinkedList<Vector3D>(mesh.getVertices());
		List<Polygon> polys = new LinkedList<Polygon>();
		
		if (vertices.remove(point)) {
			// find polys containing the specified point
			for (Polygon poly : mesh.getFaces()) {
				if (!poly.getVertices().contains(point)) {
					polys.add(poly);
				}
			}		
		}

		return new ImmutableMesh(vertices, polys);
	}

    private static Mesh filterForMajorityCell(Mesh mesh){

        Map< Integer, LinkedList<Polygon>> vertCount = new HashMap<Integer,LinkedList<Polygon>>();
        Integer verts = 0;

        for(Polygon cell : mesh.getFaces()){
            verts = cell.getVertices().size();
            if (vertCount.containsKey(verts)) {
                vertCount.get(verts).add(cell);
            }else {
                LinkedList<Polygon> sameCell= new LinkedList<Polygon>();
                sameCell.add(cell);
                vertCount.put(verts,sameCell);
            }
        }
       // Find most common Cell
        Integer max = 0;
        Integer  cellCount;
        LinkedList<Polygon> mayorityGroup = new LinkedList<Polygon>();

        for (Map.Entry<Integer, LinkedList<Polygon>> entry : vertCount.entrySet()){
            cellCount = entry.getValue().size();
           if (cellCount.compareTo(max) > 0){
               max = cellCount;
               mayorityGroup = entry.getValue();
           }
        }
        return new ImmutableMesh(mesh.getVertices() ,mayorityGroup);
    }

    private static List<Mesh> filterByVolume(List<Mesh> mesh_list){
        Map< Double, LinkedList<Mesh>> volume_sort= new HashMap<Double ,LinkedList<Mesh>>();
        QMesh  current_mesh = null;
        Double volume = null;

        for (Mesh x : mesh_list){
            current_mesh = (QMesh) x;
            volume = current_mesh.getVolume();
            if (volume_sort.containsKey(volume)) {
                volume_sort.get(volume).add(x);
            }else {
                LinkedList<Mesh> sameCell = new LinkedList<Mesh>();
                sameCell.add(x);
                volume_sort.put(volume,sameCell);
            }
        }
        Integer max = 0;
        Integer  cellCount;
        LinkedList<Mesh> mayorityGroup = new LinkedList<Mesh>();

        for (Map.Entry<Double , LinkedList<Mesh>> entry : volume_sort.entrySet()){
            cellCount = entry.getValue().size();
            if (cellCount.compareTo(max) > 0){
                max = cellCount;
                mayorityGroup = entry.getValue();
            }
        }
        return mayorityGroup;
    }
}
