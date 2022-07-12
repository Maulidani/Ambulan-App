<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Chat extends Model
{
    use HasFactory;

    protected $table = 'chats';

    protected $fillable = [
       'id','from_user_type','to_user_type','from_user_id', 'to_user_id','message','updated_at','created_at',
    ];

}
