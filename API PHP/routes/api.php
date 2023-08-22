<?php

use App\Http\Controllers\ParkController;
use App\Http\Controllers\ParkOwnerAuth;
use App\Http\Controllers\ParkVehicleReservationController;
use App\Http\Controllers\UserAuthController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\VehicleController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

Route::group(["middleware" => ['auth:sanctum']], function () {
    Route::get("user", [UserController::class, "index"]);
    Route::post("userLogout", [UserAuthController::class, "logoutUser"]);
});

Route::post("addVehicle", [VehicleController::class, "store"]);
Route::post("parkDashboard/{id?}", [ParkController::class, "parkDashboardRecord"]);
Route::post("remainSlot/{id?}/{status?}", [ParkController::class, "increaseDecreaseAvailSlot"]);

Route::post("allVehicle", [VehicleController::class, "index"]);
Route::post("parkReservation/{id?}", [ParkVehicleReservationController::class, "show"]);
Route::post("userParkReservation/{id?}", [ParkVehicleReservationController::class, "userShow"]);
Route::post("userOldParkReservation/{id?}", [ParkVehicleReservationController::class, "userOldShow"]);
Route::post("userVehicle/{id?}", [VehicleController::class, "show"]);
Route::post("deleteVehicle/{id?}", [VehicleController::class, "destroy"]);

Route::post("allPark", [ParkController::class, "index"]);

Route::post("addVehicleReservation", [ParkVehicleReservationController::class, "store"]);

Route::post("createUserAccount", [UserAuthController::class, "registerUser"]);
Route::post("updateUserAccount/{id?}", [UserAuthController::class, "updateUser"]);
Route::post("createOwnerAccount", [ParkOwnerAuth::class, "registerOwner"]);
Route::post("addParkLocation", [ParkOwnerAuth::class, "addParkLocation"]);
Route::post("userLogin", [UserAuthController::class, "loginUser"]);
Route::post("parkLogin", [ParkOwnerAuth::class, "loginOwner"]);



Route::post('/checkout', function (Request $request, Response $response) {
    // Use an existing Customer ID if this is a returning customer.
    $customer = \Stripe\Customer::create();
    $ephemeralKey = \Stripe\EphemeralKey::create(
        ['customer' => $customer->id],
        ['stripe_version' => '2020-08-27']
    );
    $paymentIntent = \Stripe\PaymentIntent::create([
        'amount' => 1099,
        'currency' => 'usd',
        'customer' => $customer->id
    ]);

    return $response->withJson([
        'paymentIntent' => $paymentIntent->client_secret,
        'ephemeralKey' => $ephemeralKey->secret,
        'customer' => $customer->id
    ])->withStatus(200);
});


