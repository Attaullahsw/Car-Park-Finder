<?php

namespace App\Http\Controllers;

use App\Models\ParkVehicleReservation;
use App\Models\VehicleModel;
use Illuminate\Http\Request;
use DB;

class ParkVehicleReservationController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param \Illuminate\Http\Request $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $fileds = $request->validate([
            "carpark_owner_id" => "required|string",
            "vehicle_id" => "required|string",
            "user_id" => "required|string",
            "user_id" => "required|string",
            "from_time" => "required|string",
            "to_time" => "required|string",
            "amount" => "required|string",
            "payment_status" => "required|string",
        ]);


        $parkReservation = new ParkVehicleReservation();
        $parkReservation->carpark_owner_id = $request->carpark_owner_id;
        $parkReservation->vehicle_id = $request->vehicle_id;
        $parkReservation->user_id = $request->user_id;
        $parkReservation->date = date("Y-m-d");
        $parkReservation->from_time = $request->from_time;
        $parkReservation->to_time = $request->to_time;
        $parkReservation->payment_status = $request->payment_status;
        $parkReservation->reservation_status = 0;
        $save = $parkReservation->save();


        $response = [];
        if ($save) {
            $response['insert'] = true;
            $response['parkReservation'] = $parkReservation;
        } else {
            $response['insert'] = false;
        }
        return response()->json($response, 201);

    }

    /**
     * Display the specified resource.
     *
     * @param \App\Models\ParkVehicleReservationController $parkVehicleReservationController
     * @return \Illuminate\Http\Response
     */
    public function show($park_id)
    {
        $reservation = DB::table('carpark_vehicle_reservation')
            ->join('carpark_owner', 'carpark_owner.carpark_owner_id', '=', 'carpark_vehicle_reservation.carpark_owner_id')
            ->join('vehicle', 'vehicle.vehicle_id', '=', 'carpark_vehicle_reservation.vehicle_id')
            ->where([
                ['carpark_vehicle_reservation.carpark_owner_id', "=", $park_id],
            ])->whereraw(
                'TIMESTAMPDIFF(MINUTE,NOW(),carpark_vehicle_reservation.to_time)>=0'
            )
            ->get();

        $response = [];

        $response['reservation'] = $reservation;

        return response()->json($response, 201);
    }

    public function userShow($user_id)
    {
        $reservation = DB::table('carpark_vehicle_reservation')
            ->join('carpark_owner', 'carpark_owner.carpark_owner_id', '=', 'carpark_vehicle_reservation.carpark_owner_id')
            ->join('vehicle', 'vehicle.vehicle_id', '=', 'carpark_vehicle_reservation.vehicle_id')
            ->where([
                ['carpark_vehicle_reservation.user_id', "=", $user_id],
            ])->whereraw(
                'TIMESTAMPDIFF(MINUTE,NOW(),carpark_vehicle_reservation.to_time)>=0'
            )
            ->get();

        $response = [];

        $response['reservation'] = $reservation;

        return response()->json($response, 201);
    }

    public function userOldShow($user_id)
    {
        $reservation = DB::table('carpark_vehicle_reservation')
            ->join('carpark_owner', 'carpark_owner.carpark_owner_id', '=', 'carpark_vehicle_reservation.carpark_owner_id')
            ->join('vehicle', 'vehicle.vehicle_id', '=', 'carpark_vehicle_reservation.vehicle_id')
            ->where([
                ['carpark_vehicle_reservation.user_id', "=", $user_id],
            ])->whereraw(
                'TIMESTAMPDIFF(MINUTE,NOW(),carpark_vehicle_reservation.to_time)<=0'
            )
            ->get();

        $response = [];

        $response['reservation'] = $reservation;

        return response()->json($response, 201);
    }

    /**
     * Update the specified resource in storage.
     *
     * @param \Illuminate\Http\Request $request
     * @param \App\Models\ParkVehicleReservationController $parkVehicleReservationController
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, ParkVehicleReservationController $parkVehicleReservationController)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param \App\Models\ParkVehicleReservationController $parkVehicleReservationController
     * @return \Illuminate\Http\Response
     */
    public function destroy(ParkVehicleReservationController $parkVehicleReservationController)
    {
        //
    }
}
