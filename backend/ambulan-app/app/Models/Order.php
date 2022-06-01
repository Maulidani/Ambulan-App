<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Order extends Model
{
    use HasFactory;

    protected $table = 'orders';

    protected $fillable = [
       'id', 'note', 'pick_up_latitude', 'pick_up_longitude', 'drop_off_latitude','drop_off_longitude','id_user_driver','status','created_at',
    ];

}
