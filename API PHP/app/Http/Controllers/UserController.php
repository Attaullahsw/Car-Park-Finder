<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\UserModel;
use Illuminate\Http\Request;

class UserController extends Controller
{


    public function index(Request $request)
    {


        $user =  User::all();
        $response = [];

        $response['user'] = $user;

        return response()->json($response, 201);


    }


}
