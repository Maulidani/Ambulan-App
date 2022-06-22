<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\User;
use App\Models\Order;
use App\Models\Hospital;

use function PHPUnit\Framework\isEmpty;
use function PHPUnit\Framework\isNull;

class Controller
{
    public function getOrders()
    {
        // $status = $request->status;
        // $id = $request->id_driver;

        // if($id == "0"){

        //     if($status == "pending"){
        //         $orders = Order::where('orders.status' , '=',$status)
        //             ->get();

        //     } else {
        //         $orders = Order::join('users', 'orders.id_user_driver', '=', 'users.id')
        //         ->where('orders.status' , '=',$status)
        //         ->orderBy('orders.updated_at', 'DESC')
        //             ->get(
        //                 [
        //                     'orders.id as id_orders',
        //                     'orders.*',
        //                     'users.id as id_users',
        //                     'users.status as status_users',
        //                     'orders.status as status_orders',
        //                     'users.*'
        //                 ]
        //             );
        //     }
        // } else {
        //     $orders = Order::join('users', 'orders.id_user_driver', '=', 'users.id')
        //     ->where('users.id' , '=',$id)
        //     ->where('orders.status' , '=',$status)
        //     ->orderBy('orders.updated_at', 'DESC')
        //            ->get(
        //                [
        //                    'orders.id as id_orders',
        //                    'orders.*',
        //                    'users.id as id_users',
        //                    'users.status as status_users',
        //                    'orders.status as status_orders',
        //                    'users.*'
        //                ]
        //            );
        // }
       
        $orders = Order::where(
            'status',
            1
        )->orderBy('updated_at', 'DESC')
               ->get();

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
        $orders = new Order;
        $orders->id_driver = $request->id_user;
        $orders->name = $request->name;
        $orders->phone = $request->phone;
        $orders->pick_up_latitude = $request->pick_up_latitude;
        $orders->pick_up_longitude = $request->pick_up_longitude;
        $orders->id_hospital = $request->id_hospital;
        $orders->save();

        if ($orders) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'order' => $orders
            ]);
        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

    public function addStatusOrders(Request $request)
    {
        $add = Order::find($request->id_order);
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
                    $users->type = $request->type;
                    $users->name = $request->name;
                    $users->phone = $request->phone;
                    $users->username = $request->username;
                    $users->password = $request->password;
                    $users->image = $filename;
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

        $exist = User::where([
            ['id', '=', $request->id_user],
        ])->exists();

        if($exist){

            $files = $request->image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = time() . '.' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {
                
                        $files->move(public_path() . '/image/user/', $filename);

                        $edit = User::find($request->id_user);
                        $edit->name = $request->name;
                        $edit->phone = $request->phone;
                        $edit->username = $request->username;
                        $edit->password = $request->password;
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

    public function addHospitals(Request $request)
    {

            $files = $request->image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = time() . '.' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {

                    $files->move(public_path() . '/image/hospital/', $filename);

                    $hospital = new Hospital;
                    $hospital->name = $request->name;
                    $hospital->address = $request->address;
                    $hospital->latitude = $request->latitude;
                    $hospital->longitude = $request->longitude;
                    $hospital->image = $filename;
                    $hospital->save();

                    if ($hospital) {

                        return response()->json([
                            'message' => 'Success',
                            'errors' => false,
                            'hospital' => $hospital
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

    public function editHospitals(Request $request){
       
        $exist = Hospital::where([
            ['id', '=', $request->id_hospital],
        ])->exists();

        if($exist){

            $files = $request->image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = time() . '.' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {

                    $files->move(public_path() . '/image/hospital/', $filename);

                        $hospital = Hospital::find($request->id_hospital);
                        $hospital->name = $request->name;
                        $hospital->address = $request->address;
                        $hospital->latitude = $request->latitude;
                        $hospital->longitude = $request->longitude;
                        $hospital->image = $filename;
                        $hospital->save();
            
                    if ($hospital) {
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

                $hospital = Hospital::find($request->id_hospital);
                    $hospital->name = $request->name;
                    $hospital->address = $request->address;
                    $hospital->latitude = $request->latitude;
                    $hospital->longitude = $request->longitude;
                    $hospital->save();
                  
                    if ($hospital) {
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

        } else {
            return response()->json([
                    'message' => 'Exist',
                    'errors' => true,
                ]);
        }
    }

    public function deleteHospitals(Request $request)
    {
       $delete =  Hospital::where(
            'id',
            $request->id_hospital
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

    public function getHospitalSearchOrId(Request $request){
        
        $search= $request->search;
        $id_hospital= $request->id_hospital;
        
        if($id_hospital == ""){
         
            $hospital = Hospital::orderBy('updated_at', 'DESC')
            ->where('name', 'like', "%" . $search . "%")
            ->get();

            if ($hospital) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'data' => $hospital,
                ]);
            } else {
    
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }

        } else {
            $hospital = Hospital::where('id',$id_hospital)
            ->first();

            if ($hospital) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'hospital' => $hospital,
                ]);
            } else {
    
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
        }

    }


}


