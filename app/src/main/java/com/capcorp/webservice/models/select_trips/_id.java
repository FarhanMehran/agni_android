package com.capcorp.webservice.models.select_trips;

public class _id {
    private String pickUpCountry;

    private String dropDownCountry;

    public String getPickUpCountry() {
        return pickUpCountry;
    }

    public void setPickUpCountry(String pickUpCountry) {
        this.pickUpCountry = pickUpCountry;
    }

    public String getDropDownCountry() {
        return dropDownCountry;
    }

    public void setDropDownCountry(String dropDownCountry) {
        this.dropDownCountry = dropDownCountry;
    }

    @Override
    public String toString() {
        return "ClassPojo [pickUpCountry = " + pickUpCountry + ", dropDownCountry = " + dropDownCountry + "]";
    }
}
		