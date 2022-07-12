<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class UserDriver extends Model
{
    use HasFactory;

    protected $table = 'user_drivers';

    protected $fillable = [
       'id','name', 'phone','passsword','image','latitude','longitude','status','updated_at','created_at',
    ];

}
