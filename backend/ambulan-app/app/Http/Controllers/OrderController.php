<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Order;
use App\Models\UserCustomer;
use App\Models\UserDriver;
use App\Models\Hospital;

use function PHPUnit\Framework\isEmpty;
use function PHPUnit\Framework\isNull;

class OrderController
{
    public function addOrders(Request $request)
    {
        $user_customer_id = $request->user_customer_id;
        $user_driver_id = $request->user_driver_id;
        $hospital_id = $request->hospital_id;
        $pick_up_latitude = $request->pick_up_latitude;
        $pick_up_longitude = $request->pick_up_longitude;
        $status = $request->status; // ('to_pick_up','to_drop_off','finish','cancel')

        $statusDriver = '1'; // is active

        $exist = Order::where([
            ['user_driver_id', '=', $user_driver_id],
        ])->where('status', 'to_pick_up')
        ->orWhere('status', 'to_drop_off')
        ->first();

        $exist1 = UserCustomer::where([
            ['id', '=', $user_customer_id],
        ])->exists();
      
        $exist2 = UserDriver::where([
            ['id', '=', $user_driver_id],
            ['status', '=', $statusDriver],
        ])->exists();
      
        $exist3 = Hospital::where([
            ['id', '=', $hospital_id],
        ])->exists();

        if( !$exist && $exist1 && $exist2 && $exist3){

            $add_order = new Order;
            $add_order->user_customer_id = $user_customer_id;
            $add_order->user_driver_id = $user_driver_id;
            $add_order->hospital_id = $hospital_id;
            $add_order->pick_up_latitude = $pick_up_latitude;
            $add_order->pick_up_longitude = $pick_up_longitude;
            $add_order->status = $status;
            $add_order->save();
            
            if ($add_order) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'order' => $add_order
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

    public function editStatusOrders(Request $request)
    {
        $order_id = $request->order_id;    
        $status = $request->status; // ('to_pick_up','to_drop_off','finish','cancel')

        $exist = Order::where([
            ['id', '=', $order_id],
        ])->exists();

        if($exist){

            $edit_status_order = Order::find($order_id);
            $edit_status_order->status = $status;
            $edit_status_order->save();
            
            if ($edit_status_order) {
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

    public function getOrders(Request $request)
    {
        $user_type = $request->user_type; // show order by user('customer','driver')
        $get_type = $request->get_type; // ('ordering','show_list')
        // $order_id = $request->order_id;
        $user_customer_id = $request->user_customer_id;
        $user_driver_id = $request->user_driver_id;
        $status = $request->status;

        if($get_type == 'ordering'){

            if($user_type == 'customer'){
            
                $order = Order::join('user_customers', 'user_customers.id', 'orders.user_customer_id')
                ->join('user_drivers', 'user_drivers.id', '=', 'orders.user_driver_id')
                ->join('hospitals', 'hospitals.id', 'orders.hospital_id')
                ->where('orders.user_customer_id', $user_customer_id)
                ->where('orders.status', 'to_pick_up')
                ->orWhere('orders.status', 'to_drop_off')
                ->orderBy('orders.updated_at', 'DESC')
                ->first(['orders.id','orders.pick_up_latitude','orders.pick_up_longitude',
                        'user_customers.id as customer_id','user_customers.name as customer_name','user_customers.phone as customer_phone',
                        'user_customers.image as customer_image',
                        'user_drivers.name as driver_name','user_drivers.id as driver_id','user_drivers.phone as driver_phone',
                        'user_drivers.image as driver_image','user_drivers.latitude as driver_latitude','user_drivers.longitude as driver_longitude',
                        'hospitals.id as hospital_id', 'hospitals.name as hospital_name', 'hospitals.latitude as hospital_latitude',
                        'hospitals.longitude as hospital_longitude',
                        'orders.status','orders.updated_at','orders.created_at'
                        ]
                );

            } else if($user_type == 'driver'){
            
                $order = Order::join('user_customers', 'user_customers.id', 'orders.user_customer_id')
                ->join('user_drivers', 'user_drivers.id', '=', 'orders.user_driver_id')
                ->join('hospitals', 'hospitals.id', 'orders.hospital_id')
                ->where('orders.user_driver_id', $user_driver_id)
                ->where('orders.status', 'to_pick_up')
                ->orWhere('orders.status', 'to_drop_off')
                ->orderBy('orders.updated_at', 'DESC')
                ->first(['orders.id','orders.pick_up_latitude','orders.pick_up_longitude',
                        'user_customers.id as customer_id','user_customers.name as customer_name','user_customers.phone as customer_phone',
                        'user_customers.image as customer_image',
                        'user_drivers.name as driver_name','user_drivers.id as driver_id','user_drivers.phone as driver_phone',
                        'user_drivers.image as driver_image',
                        'hospitals.id as hospital_id', 'hospitals.name as hospital_name', 'hospitals.latitude as hospital_latitude',
                        'hospitals.longitude as hospital_longitude',
                        'orders.status','orders.updated_at','orders.created_at'
                        ]
                );

            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }

            if ($order) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'order' => $order,
                ]);
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }   


        } else if($get_type == 'show_list'){

            $order;
            if($user_type == 'driver'){
                $order = Order::join('user_customers', 'user_customers.id', 'orders.user_customer_id')
                ->join('user_drivers', 'user_drivers.id', '=', 'orders.user_driver_id')
                ->join('hospitals', 'hospitals.id', 'orders.hospital_id')
                ->where('orders.user_driver_id', $user_driver_id)
                ->where('orders.status', $status)
                ->orderBy('orders.updated_at', 'DESC')
                ->get(['orders.id','orders.pick_up_latitude','orders.pick_up_longitude',
                        'user_customers.id as customer_id','user_customers.name as customer_name','user_customers.phone as customer_phone',
                        'user_customers.image as customer_image',
                        'user_drivers.name as driver_name','user_drivers.id as driver_id','user_drivers.phone as driver_phone',
                        'user_drivers.image as driver_image',
                        'hospitals.id as hospital_id', 'hospitals.name as hospital_name', 'hospitals.latitude as hospital_latitude',
                        'hospitals.longitude as hospital_longitude',
                        'orders.status','orders.updated_at','orders.created_at'
                        ]
                );
    
            } else if ($user_type == 'customer'){
                $order = Order::join('user_customers', 'user_customers.id', 'orders.user_customer_id')
                ->join('user_drivers', 'user_drivers.id', '=', 'orders.user_driver_id')
                ->join('hospitals', 'hospitals.id', 'orders.hospital_id')
                ->where('orders.user_customer_id', $user_customer_id)
                ->where('orders.status', $status)
                ->orderBy('orders.updated_at', 'DESC')
                ->get(['orders.id','orders.pick_up_latitude','orders.pick_up_longitude',
                        'user_customers.id as customer_id','user_customers.name as customer_name','user_customers.phone as customer_phone',
                        'user_customers.image as customer_image',
                        'user_drivers.name as driver_name','user_drivers.id as driver_id','user_drivers.phone as driver_phone',
                        'user_drivers.image as driver_image',
                        'hospitals.id as hospital_id', 'hospitals.name as hospital_name', 'hospitals.latitude as hospital_latitude',
                        'hospitals.longitude as hospital_longitude',
                        'orders.status','orders.updated_at','orders.created_at'
                        ]
                );
    
            } else if ($user_type == 'admin'){
                $order = Order::join('user_customers', 'user_customers.id', 'orders.user_customer_id')
                ->join('user_drivers', 'user_drivers.id', '=', 'orders.user_driver_id')
                ->join('hospitals', 'hospitals.id', 'orders.hospital_id')
                ->where('orders.status', $status)
                ->orderBy('orders.updated_at', 'DESC')
                ->get(['orders.id','orders.pick_up_latitude','orders.pick_up_longitude',
                        'user_customers.id as customer_id','user_customers.name as customer_name','user_customers.phone as customer_phone',
                        'user_customers.image as customer_image',
                        'user_drivers.name as driver_name','user_drivers.id as driver_id','user_drivers.phone as driver_phone',
                        'user_drivers.image as driver_image',
                        'hospitals.id as hospital_id', 'hospitals.name as hospital_name', 'hospitals.latitude as hospital_latitude',
                        'hospitals.longitude as hospital_longitude',
                        'orders.status','orders.updated_at','orders.created_at'
                        ]
                );

            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
    
            if ($order) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'data' => $order,
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


