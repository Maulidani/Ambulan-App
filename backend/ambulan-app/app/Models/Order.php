<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Order extends Model
{
    use HasFactory;

    protected $table = 'orders';

    protected $fillable = [
       'id', 'id_driver', 'name','phone','pick_up_latitude', 'pick_up_longitude', 'id_hospital','status','updated_at','created_at',
    ];

}
