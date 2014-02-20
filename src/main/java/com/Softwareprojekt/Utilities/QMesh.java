package com.Softwareprojekt.Utilities;

import com.Softwareprojekt.interfaces.Mesh;
import com.Softwareprojekt.interfaces.Polygon;
import com.Softwareprojekt.interfaces.Vector3D;
import java.util.LinkedList;
import java.util.List;


public class QMesh implements Mesh {

    private PointList pointlist = new PointList();
    private LinkedList<Integer[]> faces_index = new LinkedList<Integer[]>();
    private LinkedList<Polygon> faces = new LinkedList<Polygon>();
    private final double volume;

    public QMesh(PointList plist, LinkedList<Integer[]> indices){
        this.pointlist=plist;
        this.faces_index=indices;
        this.volume = 0d;
        for(Integer[] i:faces_index){
            LinkedList<Vector3D> a=new LinkedList<Vector3D>();
            for(int j=0;j<i.length;j++){
                a.push(pointlist.get(i[j]));
            }
            faces.push(new ImmutablePolygon(a));
        }
    }

    public QMesh(PointList plist, LinkedList<Integer[]> indices, double volume){
        this.pointlist=plist;
        this.faces_index=indices;
        this.volume = volume;

        for(Integer[] i:faces_index){
            LinkedList<Vector3D> a=new LinkedList<Vector3D>();
            for(int j=0;j<i.length;j++){
                a.push(pointlist.get(i[j]));
            }
            faces.push(new ImmutablePolygon(a));
        }
    }

    public LinkedList<Integer[]> face_indexs() {
        return faces_index;
    }

    @Override
    public List<Vector3D> getVertices() {
        return pointlist;
    }

    @Override
    public List<Polygon> getFaces() {
        return faces;
    }
    @Override
    public String toString(){
        String out = "";
        for (Polygon p: this.getFaces()){
            out = out.concat(p.toString());
        }
        return out;
    }

    public Double getVolume(){
        return this.volume;
    }
}


