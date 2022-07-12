<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class UserCustomer extends Model
{
    use HasFactory;

    protected $table = 'user_customers';

    protected $fillable = [
       'id','name', 'phone','passsword','image','latitude','longitude','updated_at','created_at',
    ];

}
