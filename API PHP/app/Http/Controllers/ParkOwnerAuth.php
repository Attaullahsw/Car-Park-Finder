<?php

namespace App\Http\Controllers;

use App\Models\ParkOwner;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class ParkOwnerAuth extends Controller
{
    public function registerOwner(Request $request)
    {


        $fileds = $request->validate([
            "name" => "required|string",
            "contact" => "required|string",
            "email" => "required|string|unique:carpark_owner,email",
            "password" => "required|string",
        ]);


        $owner = new ParkOwner();
        $owner->name = $request->name;
        $owner->email = $request->email;
        $owner->contact = $request->contact;
        $owner->password = bcrypt($request->password);
        $save = $owner->save();


        $token = $owner->createToken('myapptoken')->plainTextToken;

        $response = [];
        if ($save) {
            $response['insert'] = true;
            $response['ownerpark'] = $owner;
            $response['token'] = $token;
        } else {
            $response['insert'] = false;
        }
        return response()->json($response, 201);

    }


    public function addParkLocation(Request $request)
    {


        $fileds = $request->validate([
            "no_of_slots" => "required|string",
            "price_per_hour" => "required|string",
            "park_lat" => "required|string",
            "park_lon" => "required|string",
            "carpark_owner_id" => "required|string",
        ]);


        $owner = ParkOwner::find($request->carpark_owner_id);
        $owner->no_of_slots = $request->no_of_slots;
        $owner->remain_slot = 0;
        $owner->price_per_hour = $request->price_per_hour;
        $owner->park_lat = $request->park_lat;
        $owner->park_lon = ($request->park_lon);
        $save = $owner->save();


        $response = [];
        if ($save) {
            $response['insert'] = true;
            $response['ownerpark'] = $owner;
        } else {
            $response['insert'] = false;
        }
        return response()->json($response, 201);

    }


    public function loginOwner(Request $request)
    {


        $fileds = $request->validate([
            "email" => "required|string",
            "password" => "required|string",
        ]);


        $user = ParkOwner::where('email', $fileds['email'])->first();


        if (!$user || !Hash::check($fileds['password'], $user->password)) {
            $response = [];
            $response['message'] = "Email or password is incorrect";
            $response['login'] = false;
            return response()->json($response, 401);
        }

        $token = $user->createToken('myapptoken')->plainTextToken;
        $response = [];
        $response['login'] = true;
        $response['ownerpark'] = $user;
        $response['token'] = $token;
        return response()->json($response, 201);


    }


    public function logoutUser(Request $req)
    {
        auth()->user()->tokens()->delete();
        return [
            "message" => "Logged Out",
        ];
    }
}
