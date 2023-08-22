<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\VehicleModel;
use Illuminate\Http\Request;

class VehicleController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $vehicle = VehicleModel::all();
        $response = [];

        $response['vehicle'] = $vehicle;

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
        $fileds = $request->validate([
            "vehicle_name" => "required|string",
            "vehicle_number" => "required|string",
            "user_id" => "required|string",
        ]);


        $vehicle = new VehicleModel();
        $vehicle->vehicle_name = $request->title;
        $vehicle->vehicle_number = $request->vehicle_number;
        $vehicle->user_id = $request->user_id;
        $save = $vehicle->save();


        $response = [];
        if ($save) {
            $response['insert'] = true;
            $response['vehicle'] = $vehicle;
        } else {
            $response['insert'] = false;
        }
        return response()->json($response, 201);

    }

    /**
     * Display the specified resource.
     *
     * @param int $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {

        $vehicle = VehicleModel::where("user_id", $id)->get();
        $response = [];
        $response['vehicle'] = $vehicle;

        return response()->json($response, 201);

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
        $vehicle = VehicleModel::destroy($id);
        $response = [];
        if ($vehicle) {
            $response['delete'] = true;
        } else {
            $response['delete'] = false;
        }


        return response()->json($response, 201);
    }
}
