<?php

namespace App\Http\Controllers;

use App\Models\ParkOwner;
use App\Models\ParkVehicleReservation;
use Illuminate\Http\Request;

class ParkController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $park = ParkOwner::all();
        $response = [];

        $response['park'] = $park;

        return response()->json($response, 201);

    }

    /**
     * Store a newly created resource in storage.
     *
     * @param \Illuminate\Http\Request $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        //
    }

    /**
     * Display the specified resource.
     *
     * @param int $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param \Illuminate\Http\Request $request
     * @param int $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param int $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        //
    }


    public function parkDashboardRecord($id)
    {
        $park = ParkOwner::find($id);

        $park_reservation = ParkVehicleReservation::where('carpark_owner_id', $id)
            ->whereraw("TIMESTAMPDIFF(MINUTE,NOW(),to_time)>=0")->count();

        $response = [];

        $response['park'] = $park;
        $response['park_reservation'] = $park_reservation;

        return response()->json($response, 201);

    }

    public function increaseDecreaseAvailSlot($id, $inStatus)
    {
        $park = ParkOwner::find($id);

        $slot = $park->remain_slot;


        if ($inStatus == 0) {
            $slot++;
            if ($slot <= $park->no_of_slots) {
                $park->remain_slot = $slot;
            }

        } else {
            $slot--;
            if ($slot >= 0) {
                $park->remain_slot = $slot;
            }

        }
        $park->save();
        $response = [];
        $response['park'] = $park;

        return response()->json($response, 201);

    }
}
