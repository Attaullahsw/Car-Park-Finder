package com.example.onlinecarparkfinder;

public class ParkReservationModel{


    String id;
    String from_time;
    String to_time;
    String amount;
    String payment_type;
    String payment_status;
    String reservation_status;

    ParkModel parkModel;
    VehicleModel vehicleModel;


    public ParkReservationModel(String id, String from_time, String to_time, String amount,
                                String payment_type, String payment_status, String reservation_status) {
        this.id = id;
        this.from_time = from_time;
        this.to_time = to_time;
        this.amount = amount;
        this.payment_type = payment_type;
        this.payment_status = payment_status;
        this.reservation_status = reservation_status;
    }


    public ParkModel getParkModel() {
        return parkModel;
    }

    public void setParkModel(ParkModel parkModel) {
        this.parkModel = parkModel;
    }

    public VehicleModel getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(VehicleModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getId() {
        return id;
    }

    public String getFrom_time() {
        return from_time;
    }

    public String getTo_time() {
        return to_time;
    }

    public String getAmount() {
        return amount;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getReservation_status() {
        return reservation_status;
    }
}
