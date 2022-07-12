<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Hospital;

use function PHPUnit\Framework\isEmpty;
use function PHPUnit\Framework\isNull;

class HospitalController
{
    public function addHospitals(Request $request)
    {
        $name = $request->name;
        $address = $request->address;
        $image = $request->image;
        $latitude = $request->latitude;
        $longitude = $request->longitude;

        $files = $image;
        $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
        if ($request->hasfile('image')) {

            $filename = '/image/hospital/'. time() . '_' . $files->getClientOriginalName();
            $extension = $files->getClientOriginalExtension();

            $check = in_array($extension, $allowedfileExtension);

            if ($check) {

                $files->move(public_path() . '/image/hospital/', $filename);
              
                $add_hospital = new Hospital;
                $add_hospital->name = $name;
                $add_hospital->address = $address;
                $add_hospital->image = $filename;
                $add_hospital->latitude = $latitude;
                $add_hospital->longitude = $longitude;
                $add_hospital->save();
                
                if ($add_hospital) {
                    return response()->json([
                        'message' => 'Success',
                        'errors' => false,
                        'hospital' => $add_hospital
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

    public function editHospitals(Request $request)
    {
        $hospital_id = $request->hospital_id;
        $name = $request->name;
        $address = $request->address;
        $image = $request->image;
        $latitude = $request->latitude;
        $longitude = $request->longitude;

        $exist = Hospital::where([
            ['id', '=', $hospital_id],
        ])->exists();

        if($exist){

            $files = $image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = '/image/hospital/'.time() . '.' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {

                    $files->move(public_path() . '/image/hospital/', $filename);

                    $edit_hospital = Hospital::find($hospital_id);
                    $edit_hospital->name = $name;
                    $edit_hospital->address = $address;
                    $edit_hospital->image = $filename;
                    $edit_hospital->latitude = $latitude;
                    $edit_hospital->longitude = $longitude;
                    $edit_hospital->save();
                    
                    if ($edit_hospital) {
                        return response()->json([
                            'message' => 'Success',
                            'errors' => false,
                            'hospital' => $edit_hospital
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

                $edit_hospital = Hospital::find($hospital_id);
                $edit_hospital->name = $name;
                $edit_hospital->address = $address;
                $edit_hospital->latitude = $latitude;
                $edit_hospital->longitude = $longitude;
                $edit_hospital->save();
                
                if ($edit_hospital) {
                    return response()->json([
                        'message' => 'Success',
                        'errors' => false,
                        'hospital' => $edit_hospital
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

    public function getHospitals(Request $request)
    {
        $search = $request->search;

        $hospital = Hospital::orderBy('created_at', 'DESC')
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
    }

    public function deleteHospitals(Request $request)
    {
        $hospital_id = $request->hospital_id;

        $exist = Hospital::where([
            ['id', '=', $hospital_id],
        ])->exists();

        if ($exist) {

            $delete = Hospital::where(
                'id',
                $hospital_id
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

        } else {

            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }

    }       

}


