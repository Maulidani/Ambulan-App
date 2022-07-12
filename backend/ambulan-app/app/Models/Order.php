<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Order extends Model
{
    use HasFactory;

    protected $table = 'orders';

    protected $fillable = [
       'id', 'user_customer_id', 'user_driver_id','hospital_id','pick_up_latitude', 'pick_up_longitude','status','updated_at','created_at',
    ];

}
