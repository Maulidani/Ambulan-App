<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\User;
use App\Models\Order;

use function PHPUnit\Framework\isEmpty;
use function PHPUnit\Framework\isNull;

class Controller
{
    public function showOrders(Request $request)
    {
        $status = $request->status;
        $id = $request->id_driver;

        if($id == "0"){
            $orders = Order::join('users', 'orders.id_user_driver', '=', 'users.id')
            ->where('orders.status' , '=',$status)
            ->orderBy('orders.updated_at', 'DESC')
                   ->get(
                       [
                           'orders.id as id_orders',
                           'orders.*',
                           'users.id as id_users',
                           'users.status as status_users',
                           'orders.status as status_orders',
                           'users.*'
                       ]
                   );
        } else {
            $orders = Order::join('users', 'orders.id_user_driver', '=', 'users.id')
            ->where('users.id' , '=',$id)
            ->where('orders.status' , '=',$status)
            ->orderBy('orders.updated_at', 'DESC')
                   ->get(
                       [
                           'orders.id as id_orders',
                           'orders.*',
                           'users.id as id_users',
                           'users.status as status_users',
                           'orders.status as status_orders',
                           'users.*'
                       ]
                   );
        }


        if ($orders->isEmpty()) {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        } else {

            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'data' => $orders,
            ]);
        }
    }

    public function addOrders(Request $request)
    {

        if($request->status === "pending")
        {
            $orders = new Order;
            $orders->order_by = $request->order_by;
            $orders->note = $request->note;
            $orders->pick_up = $request->pick_up;
            $orders->drop_off = $request->drop_off;
            $orders->pick_up_latitude = $request->pick_up_latitude;
            $orders->pick_up_longitude = $request->pick_up_longitude;
            $orders->drop_off_latitude = $request->drop_off_latitude;
            $orders->drop_off_longitude = $request->drop_off_longitude;
            $orders->status = $request->status;
            $orders->save();

        } else if($request->status === "loading"){
            
            $orders = Order::find($request->id_orders);
            $orders->id_user_driver = $request->id_user_driver;
            $orders->status = $request->status;
            $orders->save();
            
        } else if($request->status === "finish"){
          
            $orders = Order::find($request->id_orders);
            $orders->status = $request->status;
            $orders->save();

        } else if($request->status === "cancel"){
          
            $orders = Order::find($request->id_orders);
            $orders->status = $request->status;
            $orders->save();

        } else {
            
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if ($orders) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

    }

    public function addUsers(Request $request)
    {
        $exist = User::where([
            ['username', '=', $request->username],
        ])->exists();

        if(!$exist){

            $files = $request->image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = time() . '.' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {

                    $files->move(public_path() . '/image/user/', $filename);

                    $users = new User;
                    $users->name = $request->name;
                    $users->phone = $request->phone;
                    $users->image = $filename;
                    $users->username = $request->username;
                    $users->password = $request->password;
                    $users->type = $request->type;
                    $users->status = 0;
                    $users->save();

                    if ($users) {

                        return response()->json([
                            'message' => 'Success',
                            'errors' => false,
                            'user' => $users
                        ]);

                    } else {

                        return response()->json([
                            'message' => 'Failed',
                            'errors' => true,
                        ]);
                    }
                } else {
                    return response()->json([
                        'message' => 'Failed',
                        'errors' => true,
                    ]);
                }
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }

        } else {
            return response()->json([
                    'message' => 'Exist',
                    'errors' => true,
                ]);
        }
    }

    public function editUsers(Request $request)
    {
        $edit = User::find($request->id_user);
        $edit->name = $request->name;
        $edit->phone = $request->phone;
        $edit->username = $request->username;
        $edit->password = $request->password;
        $edit->save();

        if ($edit) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

    public function editImageUsers(Request $request)
    {

        $files = $request->image;
        $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
        if ($request->hasfile('image')) {

            $filename = time() . '.' . $files->getClientOriginalName();
            $extension = $files->getClientOriginalExtension();

            $check = in_array($extension, $allowedfileExtension);

            if ($check) {

                $files->move(public_path() . '/image/user/', $filename);

                $edit = User::find($request->id_user);
                $edit->image = $filename;
                $edit->save();

                if ($edit) {

                    return response()->json([
                        'message' => 'Success',
                        'errors' => false,
                    ]);
                } else {

                    return response()->json([
                        'message' => 'Failed',
                        'errors' => true,
                    ]);
                }
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

    public function deleteUsers(Request $request)
    {
       $delete =  User::where(
            'id',
            $request->id_user
        )->delete();

        if($delete) {

            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

    public function loginUsers(Request $request)
    {
        $username = $request->username;
        $password = $request->password;
        $type = $request->type;

        $exist = User::where([
            ['username', '=', $username],
            ['password', '=', $password],
            ['type', '=', $type]
        ])->exists();

        if ($exist) {
            $data =  User::where([
                ['username', '=', $username],
                ['password', '=', $password],
                ['type', '=', $type]
            ])->first();

            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'user' => $data
            ]);

        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }
    
    public function addLatlngUsers(Request $request)
    {
        $add = User::find($request->id_user);
        $add->latitude = $request->latitude;
        $add->longitude = $request->longitude;
        $add->save();

        if ($add) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

    public function getDriverUsers()
    {
        $drivers = User::where(
            'type',
            'driver'
        )->orderBy('updated_at', 'DESC')
               ->get();

        if ($drivers->isEmpty()) {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        } else {

            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'data' => $drivers,
            ]);
        }        
    }

    public function addStatusUsers(Request $request)
    {
        $add = User::find($request->id_user);
        $add->status = $request->status;
        $add->save();

        if ($add) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

    public function addEditCarUsers(Request $request)
    {
        $add = User::find($request->id_user);
        $add->car_type = $request->car_type;
        $add->car_number = $request->car_number;
        $add->save();

        if ($add) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

}


