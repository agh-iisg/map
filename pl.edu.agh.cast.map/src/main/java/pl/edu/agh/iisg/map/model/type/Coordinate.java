package pl.edu.agh.iisg.map.model.type;


/**
 * Represents one of geographical coordinates.
 *
 */
public final class Coordinate implements Comparable<Coordinate> {

    private boolean sign;

    private int degrees;

    private byte minutes;

    private byte seconds;

    private byte centiseconds;

    private double decimalDegrees;

    /**
     * The constructor.
     *
     * @param sign
     *            sign, <code>true</code> for <b>plus</b>, <code>false</code> for <b>minus</b>
     * @param degrees
     *            degrees (0-180)
     * @param minutes
     *            minutes (0-60)
     * @param seconds
     *            seconds (0-60)
     * @param centiseconds
     *            centiseconds (0-100)
     */
    public Coordinate(boolean sign, int degrees, int minutes, int seconds, int centiseconds) {
        if (degrees < 0 || degrees > 180 || minutes < 0 || minutes >= 60 || seconds < 0 || seconds >= 60 || centiseconds < 0
                || centiseconds >= 100) {
            throw new IllegalArgumentException("One of coordinate element has wrong value."); //$NON-NLS-1$
        }
        this.sign = sign;
        this.degrees = degrees;
        this.minutes = (byte)minutes;
        this.seconds = (byte)seconds;
        this.centiseconds = (byte)centiseconds;

        double d = (sign ? 1 : -1) * ((degrees + (minutes * 60.0 + seconds + centiseconds / 100.0) / 3600.0));
        this.decimalDegrees = d;
    }

    /**
     * Constructor.
     *
     * @param decimalDegrees
     *            decimal degrees
     */
    public Coordinate(double decimalDegrees) {
        this.sign = decimalDegrees >= 0;
        this.decimalDegrees = decimalDegrees;

        double absDecimalDegrees = Math.abs(decimalDegrees);

        this.degrees = (int)absDecimalDegrees;
        double rest = Math.abs(absDecimalDegrees - (int)absDecimalDegrees);
        this.minutes = (byte)(rest * 60);
        rest = (rest * 60) - (int)(rest * 60);
        this.seconds = (byte)((int)(rest * 60));
        rest = (rest * 60) - (int)(rest * 60);
        this.centiseconds = (byte)((int)(rest * 100));
    }

    /**
     * Gets the sign.
     *
     * @return the sign
     */
    public boolean getSign() {
        return sign;
    }

    /**
     * Gets the degrees.
     *
     * @return the degrees
     */
    public int getDegrees() {
        return degrees;
    }

    /**
     * Gets the minutes.
     *
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Gets the seconds.
     *
     * @return the seconds
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Gets the centiseconds.
     *
     * @return the centiseconds
     */
    public int getCentiseconds() {
        return centiseconds;
    }

    /**
     * Returns the coordinates in decimal degree format.
     *
     * @return the coordinates in decimal degree format.
     */
    public Double getDecimalDegrees() {
        return decimalDegrees;
    }

    /**
     * Helper method that returns string representing this coordinate with a letter signifying hemisphere (N,E,W or S).
     *
     * @param isLatitude
     *            flag indicating if coordinate should be treated as latitude
     * @return string in proper format (e.g. 48°51′29.00″N)
     */
    public String toStringWithLetter(boolean isLatitude) {

        String letter = null;
        if (getSign()) {
            letter = isLatitude ? " N" : " E"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            letter = isLatitude ? " S" : " W"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        String coordinateString = toString();
        if (!sign) {
            // clip leading minus sign
            coordinateString = coordinateString.substring(1);
        }
        return coordinateString + letter;
    }

    // BEGIN Overridden methods

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Coordinate)) {
            return false;
        }
        final Coordinate that = (Coordinate)obj;
        return sign == that.sign && degrees == that.degrees && minutes == that.minutes && seconds == that.seconds
                && centiseconds == that.centiseconds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime;
        result = prime * result + degrees + minutes + seconds + centiseconds;
        return result;
    }

    

    @Override
    public int compareTo(Coordinate o) {
        return decimalDegrees < o.decimalDegrees ? -1 : (decimalDegrees == o.decimalDegrees ? 0 : 1);
    }

    // END Overridden methods


}
