<?php

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

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::get('get-orders', 'App\Http\Controllers\Controller@getOrders');
Route::post('add-orders', 'App\Http\Controllers\Controller@addOrders');
Route::post('add-status-orders', 'App\Http\Controllers\Controller@addStatusOrders');
Route::post('add-users', 'App\Http\Controllers\Controller@addUsers');
Route::post('edit-users', 'App\Http\Controllers\Controller@editUsers');
Route::post('delete-users', 'App\Http\Controllers\Controller@deleteUsers');
Route::post('login-users', 'App\Http\Controllers\Controller@loginUsers');
Route::post('add-latlng-driver-users', 'App\Http\Controllers\Controller@addLatlngUsers');
Route::get('get-driver-users', 'App\Http\Controllers\Controller@getDriverUsers');
Route::post('add-status-users', 'App\Http\Controllers\Controller@addStatusUsers');
Route::post('add-hospitals', 'App\Http\Controllers\Controller@addHospitals');
Route::post('edit-hospitals', 'App\Http\Controllers\Controller@editHospitals');
Route::post('delete-hospitals', 'App\Http\Controllers\Controller@deleteHospitals');
Route::post('get-hospitals', 'App\Http\Controllers\Controller@getHospitalSearchOrId');
Route::post('add-articles', 'App\Http\Controllers\Controller@addArticles');
Route::post('edit-articles', 'App\Http\Controllers\Controller@editArticles');
Route::post('delete-articles', 'App\Http\Controllers\Controller@deleteArticles');
Route::get('get-articles', 'App\Http\Controllers\Controller@getArticles');
