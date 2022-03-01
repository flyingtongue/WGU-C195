package model;

public class Country {
    private int countryID;
    private String countryName;

    /**
     *
     * @param countryID
     * @param countryName
     */
    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     *
     * @return countryID
     */
    public int getCountryID() {

        return countryID;
    }

    /**
     *
     * @return countryName
     */
    public String getCountryName() {

        return countryName;
    }
}
