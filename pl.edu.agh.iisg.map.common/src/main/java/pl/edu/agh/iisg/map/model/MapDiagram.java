package pl.edu.agh.iisg.map.model;

import java.beans.PropertyChangeListener;

import pl.edu.agh.cast.common.property.IPropertyChangeProvider;
import pl.edu.agh.cast.common.property.PropertyChangeProviderHelper;
import pl.edu.agh.iisg.map.model.type.Coordinate;

public class MapDiagram implements IPropertyChangeProvider {
	
	// BEGIN Map fields
	
	private Coordinate latitude = new Coordinate(50.060);
	
	private Coordinate longitude = new Coordinate(19.959);
	
	private Integer zoomLevel = 10;
	
	// END Map fields
	
	// BEGIN Helper and utility fields
	
	private PropertyChangeProviderHelper pcpHelper = new PropertyChangeProviderHelper(this);
	
	// END Helper and utility fields
	
	public Coordinate getLatitude() {
		return latitude;
	}

	public Coordinate getLongitude() {
		return longitude;
	}

	public void setLatitude(Coordinate latitude) {
		Coordinate oldValue = this.latitude;
		this.latitude = latitude;
		pcpHelper.firePropertyChange(Events.LONGITUDE_EVENT, oldValue, this.latitude);
	}

	public void setLongitude(Coordinate longitude) {
		Coordinate oldValue = this.longitude;
		this.longitude = longitude;
		pcpHelper.firePropertyChange(Events.LATITUDE_EVENT, oldValue, this.longitude);
	}

	public Integer getZoomLevel() {
		return zoomLevel;
	}
	
	public void setZoomLevel(Integer zoomLevel) {
		Integer oldValue = this.zoomLevel;
		this.zoomLevel = zoomLevel;
		pcpHelper.firePropertyChange(Events.ZOOM_EVENT, oldValue, this.zoomLevel);	
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcpHelper.addPropertyChangeListener(l);
		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcpHelper.removePropertyChangeListener(l);
		
	}
	
    public static final class Events {
        /** Map's visible start date change event. */
        public static final String MAP_START_DATE_EVENT = "IMapDataSet.StartDateEvent"; //$NON-NLS-1$

        /** Map's visible start date change event. */
        public static final String MAP_END_DATE_EVENT = "IMapDataSet.EndDateEvent"; //$NON-NLS-1$

        /** Map's visible dates change event. */
        public static final String MAP_DATES_EVENT = "IMapDataSet.DatesEvent"; //$NON-NLS-1$

        /** Map's visible start date change event. */
        public static final String MAP_VISIBLE_START_DATE_EVENT = "IVisualMapDataSet.VisibleStartDateEvent"; //$NON-NLS-1$

        /** Map's visible start date change event. */
        public static final String MAP_VISIBLE_END_DATE_EVENT = "IVisualMapDataSet.VisibleEndDateEvent"; //$NON-NLS-1$

        /** Map's visible dates change event. */
        public static final String MAP_VISIBLE_DATES_EVENT = "IVisualMapDataSet.VisibleDatesEvent"; //$NON-NLS-1$

        /** Map's no dates left event. */
        public static final String MAP_NO_DATES_EVENT = "IVisualMapDataSet.NoDatesEvent"; //$NON-NLS-1$

        /** Name for zoom change event. */
        public static final String ZOOM_EVENT = "IVisualMapDataSet.ZoomEvent"; //$NON-NLS-1$

        /** Name for zoom tool visibility change event. */
        public static final String ZOOM_TOOL_VISIBILITY_EVENT = "IVisualMapDataSet.ZoomToolVisibilityEvent"; //$NON-NLS-1$

        /** Name for time bar tool visibility change event. */
        public static final String TIME_BAR_TOOL_VISIBILITY_EVENT = "IVisualMapDataSet.TimeBarToolVisibilityEvent"; //$NON-NLS-1$

        /** Name for scale tool visibility change event. */
        public static final String SCALE_TOOL_VISIBILITY_EVENT = "IVisualMapDataSet.ScaleToolVisibilityEvent"; //$NON-NLS-1$

        /** Name for refresh event. */
        public static final String REFRESH_MAP_VISUALS = "IVisualMapDataSet.RefreshEvent"; //$NON-NLS-1$

        /** Name for latitude event. */
        public static final String LATITUDE_EVENT = "IVisualMapDataSet.LatitudeEvent"; //$NON-NLS-1$

        /** Name for longitude event. */
        public static final String LONGITUDE_EVENT = "IVisualMapDataSet.LongitudeEvent"; //$NON-NLS-1$

        /** Name for connection router event. */
        public static final String CONNECTION_ROUTER_EVENT = "IVisualMapDataSet.ConnectionRouterEvent"; //$NON-NLS-1$

        /** Event which indicates that editor was blocked/unblocked. */
        public static final String EDITOR_BLOCKED_EVENT = "IVisualMapDataSet.TimeBarBlocked"; //$NON-NLS-1$

        /** Event which indicates that map key was changed. */
        public static final String MAP_KEY_EVENT = "IVisualMapDataSet.MapKey"; //$NON-NLS-1$
    }
}
