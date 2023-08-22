<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\UserModel;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Testing\Fluent\Concerns\Has;

class UserAuthController extends Controller
{
    //

    public function registerUser(Request $request)
    {


        $fileds = $request->validate([
            "full_name" => "required|string",
            "cnic" => "required|string",
            "contact" => "required|string",
            "address" => "required",
            "email" => "required|string|unique:user,email",
            "password" => "required|string",
        ]);


        $user = new User();
        $user->full_name = $request->full_name;
        $user->cnic = $request->cnic;
        $user->email = $request->email;
        $user->contact = $request->contact;
        $user->address = $request->address;
        $user->password = bcrypt($request->password);
        $save = $user->save();


        $token = $user->createToken('myapptoken')->plainTextToken;

        $response = [];
        if ($save) {
            $response['insert'] = true;
            $response['user'] = $user;
            $response['token'] = $token;
        } else {
            $response['insert'] = false;
        }
        return response()->json($response, 201);

    }

    public function updateUser(Request $request)
    {


        $fileds = $request->validate([
            "user_id" => "required|string",
            "cnic" => "required|string",
            "contact" => "required|string",
            "address" => "required",
            "email" => "required|string|unique:user,email," . $request->user_id.",user_id",
            "full_name" => "required|string",
        ]);


        $user = User::find($request->user_id);
        $user->full_name = $request->full_name;
        $user->cnic = $request->cnic;
        $user->email = $request->email;
        $user->contact = $request->contact;
        $user->address = $request->address;
        $save = $user->save();


        $response = [];
        if ($save) {
            $response['insert'] = true;
            $response['user'] = $user;
        } else {
            $response['insert'] = false;
        }
        return response()->json($response, 201);

    }


    public function loginUser(Request $request)
    {


        $fileds = $request->validate([
            "email" => "required|string",
            "password" => "required|string",
        ]);


        $user = User::where('email', $fileds['email'])->first();


        if (!$user || !Hash::check($fileds['password'], $user->password)) {
            $response = [];
            $response['message'] = "Email or password is incorrect";
            $response['login'] = false;
            return response()->json($response, 401);
        }

        $token = $user->createToken('myapptoken')->plainTextToken;
        $response = [];
        $response['login'] = true;
        $response['user'] = $user;
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
