<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\UserCustomer;
use App\Models\UserDriver;
use App\Models\UserAdmin;
use App\Models\Order;

use function PHPUnit\Framework\isEmpty;
use function PHPUnit\Framework\isNull;

class UserController
{
    public function addUsers(Request $request)
    {
        $user_type = $request->user_type; // ('customer','driver')
        $name = $request->name;
        $phone = $request->phone;
        $password = $request->password;
        $image = $request->image;
        // $latitude = $request->latitude;
        // $longitude = $request->longitude;
        // $status = $request->status; // driver ('0','1')

        $exist;
        if($user_type == 'customer'){
            $exist = UserCustomer::where([
                ['phone', '=', $phone],
            ])->exists();

        } else if ($user_type == 'driver'){
            $exist = UserDriver::where([
                ['phone', '=', $phone],
            ])->exists();

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if(!$exist){

            $files = $image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = '/image/user/'. time() . '_' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {

                    $files->move(public_path() . '/image/user/', $filename);

                    $add_user;
                    if($user_type == 'customer'){
                        $add_user = new UserCustomer;
            
                    } else if ($user_type == 'driver'){
                        $add_user = new UserDriver;

                    } else {
                        return response()->json([
                            'message' => 'Failed',
                            'errors' => true,
                        ]);
                    }

                    $add_user->name = $name;
                    $add_user->phone = $phone;
                    $add_user->password = $password;
                    $add_user->image = $filename;
                    $add_user->save();
                    
                    if ($add_user) {
                        return response()->json([
                            'message' => 'Success',
                            'errors' => false,
                            'user' => $add_user
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
                    'message' => 'Failed',
                    'errors' => true,
                ]);
        }
    }

    public function editUsers(Request $request)
    {
        $user_id = $request->user_id;
        $user_type = $request->user_type; // ('customer','driver')
        $name = $request->name;
        $phone = $request->phone;
        $password = $request->password;
        $image = $request->image;
        // $latitude = $request->latitude;
        // $longitude = $request->longitude;
        // $status = $request->status; // driver ('0','1')

        $exist;
        if($user_type == 'customer'){
            $exist = UserCustomer::where([
                ['id', '=', $user_id],
            ])->exists();

        } else if ($user_type == 'driver'){
            $exist = UserDriver::where([
                ['id', '=', $user_id],
            ])->exists();

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if($exist){

            $files = $image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = '/image/user/'.time() . '.' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {

                    $files->move(public_path() . '/image/user/', $filename);

                    $edit_user;
                    if($user_type == 'customer'){
                        $edit_user = UserCustomer::find($user_id);
            
                    } else if ($user_type == 'driver'){
                        $edit_user = UserDriver::find($user_id);

                    } else {
                        return response()->json([
                            'message' => 'Failed',
                            'errors' => true,
                        ]);
                    }

                    $edit_user->name = $name;
                    $edit_user->phone = $phone;
                    $edit_user->password = $password;
                    $edit_user->image = $filename;
                    $edit_user->save();
                    
                    if ($edit_user) {
                        return response()->json([
                            'message' => 'Success',
                            'errors' => false,
                            'user' => $edit_user
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
                $edit_user;
                if($user_type == 'customer'){
                    $edit_user = UserCustomer::find($user_id);
        
                } else if ($user_type == 'driver'){
                    $edit_user = UserDriver::find($user_id);

                } else {
                    return response()->json([
                        'message' => 'Failed',
                        'errors' => true,
                    ]);
                }

                $edit_user->name = $name;
                $edit_user->phone = $phone;
                $edit_user->password = $password;
                $edit_user->save();
                
                if ($edit_user) {
                    return response()->json([
                        'message' => 'Success',
                        'errors' => false,
                        'user' => $edit_user
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

    public function loginUsers(Request $request)
    {
        $user_type = $request->user_type; // ('customer','driver')
        $phone = $request->phone;
        $username = $request->username; // admin
        $password = $request->password;

        $exist;
        if($user_type == 'customer'){
            $exist = UserCustomer::where([
                ['phone', '=', $phone],
                ['password', '=', $password],
            ])->exists();

        } else if ($user_type == 'driver'){
            $exist = UserDriver::where([
                ['phone', '=', $phone],
                ['password', '=', $password],
            ])->exists();

        } else if ($user_type == 'admin'){
            $exist = UserAdmin::where([
                ['username', '=', $username],
                ['password', '=', $password],
            ])->exists();

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if ($exist) {

            $user;
            if($user_type == 'customer'){
                $user =  UserCustomer::where([
                    ['phone', '=', $phone]
                ])->first();
    
            } else if ($user_type == 'driver'){
                $user =  UserDriver::where([
                    ['phone', '=', $phone]
                ])->first();

            } else if ($user_type == 'admin'){
                $user =  UserAdmin::where([
                    ['username', '=', $username]
                ])->first();

            }

            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'user' => $user
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
        $user_type = $request->user_type; // ('customer','driver')
        $user_id = $request->user_id;
        $latitude = $request->latitude;
        $longitude = $request->longitude;

        $exist;
        if($user_type == 'customer'){
            $exist = UserCustomer::where([
                ['id', '=', $user_id]
            ])->exists();

        } else if ($user_type == 'driver'){
            $exist = UserDriver::where([
                ['id', '=', $user_id]
            ])->exists();

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if($exist){
            $add;
            if($user_type == 'customer'){
                $add = UserCustomer::find($user_id);
                $add->latitude = $latitude;
                $add->longitude = $longitude;
                $add->save();

            } else if($user_type == 'driver'){
                $add = UserDriver::find($request->user_id);
                $add->latitude = $latitude;
                $add->longitude = $longitude;
                $add->save();

            }

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

        } else {
    
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
    }

    public function getusers(Request $request)
    {
        $user_id = $request->user_id; // only for_driver_status , user_detail
        $user_type = $request->user_type; // show user('customer','driver')
        $get_type = $request->get_type; // ('for_order','for_admin','for_driver_status','user_detail')

        $statusDriver = '1'; // is active

        if($get_type == 'for_order'){

            $user = UserDriver::orderBy('updated_at', 'DESC')
            ->where('status', $statusDriver)
            ->get();

            if ($user) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'data' => $user,
                ]);
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }      

        } else if($get_type == 'for_admin'){

            $user;
            if($user_type == 'customer'){
                $user = UserCustomer::orderBy('updated_at', 'DESC')
                       ->get();
    
            } else if ($user_type == 'driver'){
                $user = UserDriver::orderBy('updated_at', 'DESC')
                       ->get();
    
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
          
            if ($user) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'data' => $user,
                ]);
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }      

        } else if($get_type == 'for_driver_status'){

            $user;
            if ($user_type == 'driver'){
                $user = UserDriver::where('id',$user_id)
                       ->first();
    
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
          
            if ($user) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'user' => $user,
                ]);
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }      

        } else if($get_type == 'user_detail'){

            $user;
            if ($user_type == 'driver'){
                $user = UserDriver::where('id',$user_id)
                       ->first();
    
            } else if ($user_type == 'customer'){
                $user = UserCustomer::where('id',$user_id)
                       ->first();
    
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
          
            if ($user) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'user' => $user,
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

         
    }

    public function addStatusUserDrivers(Request $request)
    {
        $user_driver_id = $request->user_driver_id;
        $status = $request->status;

        $exist = UserDriver::where([
            ['id', '=', $user_driver_id]
        ])->exists();

        if($exist){
            $add = UserDriver::find($user_driver_id);
            $add->status = $status;
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

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

    }

    public function deleteUsers(Request $request)
    {
        $user_type = $request->user_type; // ('customer','driver')
        $user_id = $request->user_id; // ('customer','driver')

        $exist;
        if($user_type == 'customer'){
            $exist = UserCustomer::where([
                ['id', '=', $user_id],
            ])->exists();

        } else if ($user_type == 'driver'){
            $exist = UserDriver::where([
                ['id', '=', $user_id],
            ])->exists();

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if ($exist) {

            $delete;
            if($user_type == 'customer'){
                $delete =  UserCustomer::where(
                    'id',
                    $user_id
                )->delete();
    
            } else if ($user_type == 'driver'){
                $delete =  UserDriver::where(
                    'id',
                    $user_id
                )->delete();

            }

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

        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

    }       

}


