<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class User extends Model
{
    use HasFactory;

    protected $table = 'users';

    protected $fillable = [
       'id', 'type','name', 'phone', 'username','passsword','latitude','longitude','image','status','updated_at','created_at',
    ];

}
