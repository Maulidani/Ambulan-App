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

Route::post('show-orders', 'App\Http\Controllers\Controller@showOrders');
Route::post('add-orders', 'App\Http\Controllers\Controller@addOrders');
Route::post('add-users', 'App\Http\Controllers\Controller@addUsers');
Route::post('edit-users', 'App\Http\Controllers\Controller@editUsers');
Route::post('edit-image-users', 'App\Http\Controllers\Controller@editImageUsers');
Route::post('delete-users', 'App\Http\Controllers\Controller@deleteUsers');
Route::post('login-users', 'App\Http\Controllers\Controller@loginUsers');
Route::post('add-latlng-driver-users', 'App\Http\Controllers\Controller@addLatlngUsers');
Route::get('get-driver-users', 'App\Http\Controllers\Controller@getDriverUsers');
Route::post('add-status-users', 'App\Http\Controllers\Controller@addStatusUsers');
Route::post('add-edit-car-users', 'App\Http\Controllers\Controller@addEditCarUsers');
Route::post('add-artikels', 'App\Http\Controllers\Controller@addArtikels');
Route::post('edit-artikels', 'App\Http\Controllers\Controller@editArtikels');
Route::post('edit-image-artikels', 'App\Http\Controllers\Controller@editImageArtikels');
Route::post('delete-artikels', 'App\Http\Controllers\Controller@deleteArtikels');
Route::get('show-artikels', 'App\Http\Controllers\Controller@showArtikels');

