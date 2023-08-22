<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ParkVehicleReservation extends Model
{
    use HasFactory;

    protected $table = "carpark_vehicle_reservation";
    protected $primaryKey = "carpark_vehicle_reservation_id";
}
