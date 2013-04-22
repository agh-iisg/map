package pl.edu.agh.cast.map.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import pl.edu.agh.cast.map.model.MapDiagram;
import pl.edu.agh.cast.map.model.internal.MapViewport;

/**
 * Edits parts factory which creates and provides edit parts for map editor.
 *
 */
public class MapEditPartsFactory implements EditPartFactory {

    /** Constructor. */
    public MapEditPartsFactory() {
    }

    @Override
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;

        if (model instanceof MapDiagram) {
            return new MapEditPart((MapDiagram)model);
        } else if (model instanceof MapViewport) {
            part = new MapViewportEditPart((MapViewport)model);
//        } else if (model instanceof IMapZoomTool) {
//            part = new MapZoomToolEditPart();
//        } else if (model instanceof IMapScaleTool) {
//            part = new MapScaleToolEditPart();
//        } else if (model instanceof IMapEvent) {
//            part = new MapEventEditPart((IMapPoint)model);
//        } else if (model instanceof IMapNote) {
//            part = new MapNoteEditPart((IMapNote)model);
//        } else if (model instanceof IMapPoint) {
//            part = new MapPointEditPart((IMapPoint)model);
//        } else if (model instanceof IMapMovement) {
//            part = new MapMovementEditPart((IMapMovement)model);
//        } else if (model instanceof IMapConnection) {
//            part = new MapConnectionEditPart((IMapConnection<?>)model);
//        } else if (model instanceof IMapTimeBarTool) {
//            part = new MapTimeBarToolEditPart();
//        } else if (model instanceof IMapTrack) {
//            part = new MapTrackEditPart((IMapTrack)model);
//        } else if (model instanceof IMapDistance) {
//            part = new MapDistanceEditPart((IMapDistance)model);
//        } else if (model instanceof IMapDirectedArc) {
//            part = new MapDirectedArcEditPart((IMapDirectedArc)model);
//        } else if (model instanceof IMapCluster) {
//            part = new MapClusterEditPart((IMapCluster)model);
        }

        part.setModel(model);

        return part;
    }
}
