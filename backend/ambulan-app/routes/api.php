<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
// use App\Http\Controllers\UserController;
// use App\Http\Controllers\ChatController;
// use App\Http\Controllers\HospitalController;
// use App\Http\Controllers\OrderController;
// use App\Http\Controllers\UserController;

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

Route::post('add-user', 'App\Http\Controllers\UserController@addUsers');
Route::post('edit-user', 'App\Http\Controllers\UserController@editUsers');
Route::post('login-user', 'App\Http\Controllers\UserController@loginUsers');
Route::post('add-latlng-user', 'App\Http\Controllers\UserController@addLatlngUsers');
Route::post('get-user', 'App\Http\Controllers\UserController@getusers');
Route::post('add-status-user-driver', 'App\Http\Controllers\UserController@addStatusUserDrivers');
Route::post('delete-user', 'App\Http\Controllers\UserController@deleteUsers');

Route::post('add-hospital', 'App\Http\Controllers\HospitalController@addHospitals');
Route::post('edit-hospital', 'App\Http\Controllers\HospitalController@editHospitals');
Route::post('get-hospital', 'App\Http\Controllers\HospitalController@getHospitals');
Route::post('delete-hospital', 'App\Http\Controllers\HospitalController@deleteHospitals');

Route::post('add-article', 'App\Http\Controllers\ArticleController@addArticles');
Route::post('edit-article', 'App\Http\Controllers\ArticleController@editArticles');
Route::post('get-article', 'App\Http\Controllers\ArticleController@getArticles');
Route::post('delete-article', 'App\Http\Controllers\ArticleController@deleteArticles');

Route::post('add-order', 'App\Http\Controllers\OrderController@addOrders');
Route::post('edit-status-order', 'App\Http\Controllers\OrderController@editStatusOrders');
Route::post('get-order', 'App\Http\Controllers\OrderController@getOrders');

Route::post('add-chat', 'App\Http\Controllers\ChatController@addChats');
Route::post('get-chat', 'App\Http\Controllers\ChatController@getChats');
Route::post('get-user-chat', 'App\Http\Controllers\ChatController@getUserChats');
Route::post('get-user-admin-chat', 'App\Http\Controllers\ChatController@getUserAdminChats');

