<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class UserAdmin extends Model
{
    use HasFactory;

    protected $table = 'user_admins';

    protected $fillable = [
       'id','name', 'username','password','updated_at','created_at',
    ];

}
