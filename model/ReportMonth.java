package model;

public class ReportMonth {
    public String appointmentMonth;
    public int appointmentTotal;

    /**
     * @param appointmentMonth
     * @param appointmentTotal
     */
    public ReportMonth(String appointmentMonth, int appointmentTotal) {
        this.appointmentMonth = appointmentMonth;
        this.appointmentTotal = appointmentTotal;
    }

    /**
     * @return appointmentMonth
     */
    public String getAppointmentMonth() {

        return appointmentMonth;
    }

    /**
     * @return appointmentTotal
     */
    public int getAppointmentTotal() {

        return appointmentTotal;
    }
}
