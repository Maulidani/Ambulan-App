<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Chat;
use App\Models\Order;
use App\Models\UserAdmin;
use App\Models\UserCustomer;
use App\Models\UserDriver;
use App\Models\Hospital;

use function PHPUnit\Framework\isEmpty;
use function PHPUnit\Framework\isNull;

class ChatController
{
    public function addChats(Request $request)
    {
        // chat
        // customer <-> driver
        // driver <-> admin

        $from_user_type = $request->from_user_type;
        $to_user_type = $request->to_user_type;
        $from_user_id = $request->from_user_id;
        $to_user_id = $request->to_user_id;
        $message = $request->message;

        $exist1 = false;
        $exist2 = false;
        $exist3 = false;
        if($from_user_type == 'admin'){
            $check1 = UserAdmin::where([
                ['id', '=', $from_user_id],
            ])->exists();
            
            $check2 = false;
            if($to_user_type == 'driver') {
                $check2 = UserDriver::where([
                    ['id', '=', $to_user_id],
                ])->exists();
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }

            if($check1 && $check2){
               $exist1 = true ;     
            } else {
                $exist1 = false;
            }

        } else if($from_user_type == 'customer'){
            $check1 = UserCustomer::where([
                ['id', '=', $from_user_id],
            ])->exists();

            $check2 = false;
            if($to_user_type == 'driver') {
                $check2 = UserDriver::where([
                    ['id', '=', $to_user_id],
                ])->exists();
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
           
            if($check1 && $check2){
               $exist2 = true ;     
            } else {
                $exist2 = false;
            }

        } else if($from_user_type == 'driver'){

            $check1 = UserDriver::where([
                ['id', '=', $from_user_id],
            ])->exists();

            $check2 = false ;
            if($to_user_type == 'customer') {
                $check2 = UserCustomer::where([
                    ['id', '=', $to_user_id],
                ])->exists();

            } else if($to_user_type == 'admin') {
                $check2 = UserAdmin::where([
                    ['id', '=', $to_user_id],
                ])->exists();
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }

            if($check1 && $check2){
               $exist3 = true ;     
            } else {
                $exist3 = false;
            }

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if($exist1 || $exist2 || $exist3){

            $add_chat = new Chat;
            $add_chat->from_user_type = $from_user_type;
            $add_chat->to_user_type = $to_user_type;
            $add_chat->from_user_id = $from_user_id;
            $add_chat->to_user_id = $to_user_id;
            $add_chat->message = $message;
            $add_chat->save();
            
            if ($add_chat) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'chat' => $add_chat
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

    public function getChats(Request $request)
    {
        // chat
        // customer <-> driver
        // driver <-> admin

        $from_user_type = $request->from_user_type;
        $to_user_type = $request->to_user_type;
        $from_user_id = $request->from_user_id;
        $to_user_id = $request->to_user_id;

      
        $exist1 = false;
        $exist2 = false;
        $exist3 = false;
        if($from_user_type == 'admin'){
            $check1 = UserAdmin::where([
                ['id', '=', $from_user_id],
            ])->exists();
            
            $check2 = false;
            if($to_user_type == 'driver') {
                $check2 = UserDriver::where([
                    ['id', '=', $to_user_id],
                ])->exists();
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }

            if($check1 && $check2){
               $exist1 = true ;     
            } else {
                $exist1 = false;
            }

        } else if($from_user_type == 'customer'){
            $check1 = UserCustomer::where([
                ['id', '=', $from_user_id],
            ])->exists();

            $check2 = false;
            if($to_user_type == 'driver') {
                $check2 = UserDriver::where([
                    ['id', '=', $to_user_id],
                ])->exists();
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }
           
            if($check1 && $check2){
               $exist2 = true ;     
            } else {
                $exist2 = false;
            }

        } else if($from_user_type == 'driver'){

            $check1 = UserDriver::where([
                ['id', '=', $from_user_id],
            ])->exists();

            $check2 = false ;
            if($to_user_type == 'customer') {
                $check2 = UserCustomer::where([
                    ['id', '=', $to_user_id],
                ])->exists();

            } else if($to_user_type == 'admin') {
                $check2 = UserAdmin::where([
                    ['id', '=', $to_user_id],
                ])->exists();
            } else {
                return response()->json([
                    'message' => 'Failed',
                    'errors' => true,
                ]);
            }

            if($check1 && $check2){
               $exist3 = true ;     
            } else {
                $exist3 = false;
            }

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

        if($exist1 || $exist2 || $exist3){
        
            $chat = Chat::where([
                ['from_user_type', '=', $from_user_type],
                ['to_user_type', '=', $to_user_type],
                ['from_user_id', '=', $from_user_id],
                ['to_user_id', '=',  $to_user_id],
            ])->orWhere([ // get message to user
                ['from_user_type', '=', $to_user_type],
                ['to_user_type', '=', $from_user_type],
                ['from_user_id', '=', $to_user_id],
                ['to_user_id', '=',  $from_user_id],
            ])->orderBy('updated_at', 'ASC')
                ->get();
    
            if ($chat) {
                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'data' => $chat
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

    public function getUserChats(Request $request)
    {
        // chat
        // customer <-> driver
        // driver <-> admin

        $from_user_type = $request->from_user_type;
        $from_user_id = $request->from_user_id;
        // $to_user_id = $request->to_user_id;

        $chat = Chat::where([
            ['from_user_type' ,'=' , $from_user_type],
            ['from_user_id' ,'=' , $from_user_id],
            ])
        ->orWhere([
            ['to_user_type' ,'=' , $from_user_type],
            ['to_user_id' ,'=' , $from_user_id],
            ])
        ->orderBy('updated_at', 'DESC')
        ->get();

         $unique = $chat->unique('to_user_id')->values();

         if ($chat) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'data' => $unique
            ]);

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

    }

    public function getUserAdminChats(Request $request) // for driver
    {     
        $chat = UserAdmin::orderBy('updated_at', 'DESC')
        ->get(['id','name']);

         if ($chat) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'data' => $chat
            ]);

        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }
       
    }
}
