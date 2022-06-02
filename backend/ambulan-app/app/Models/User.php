<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class User extends Model
{
    use HasFactory;

    protected $table = 'users';

    protected $fillable = [
       'id', 'name', 'phone', 'image', 'username','passsword','latitude','longitude','status','car_type','car_number','type','created_at',
    ];

}
