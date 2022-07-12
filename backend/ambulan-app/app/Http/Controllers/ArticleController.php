<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Article;

use function PHPUnit\Framework\isEmpty;
use function PHPUnit\Framework\isNull;

class ArticleController
{
    public function addArticles(Request $request)
    {
        $title = $request->title;
        $description = $request->description;
        $image = $request->image;

        $files = $image;
        $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
        if ($request->hasfile('image')) {

            $filename = '/image/article/'. time() . '_' . $files->getClientOriginalName();
            $extension = $files->getClientOriginalExtension();

            $check = in_array($extension, $allowedfileExtension);

            if ($check) {

                $files->move(public_path() . '/image/article/', $filename);
              
                $add_article = new Article;
                $add_article->title = $title;
                $add_article->description = $description;
                $add_article->image = $filename;
                $add_article->save();
                
                if ($add_article) {
                    return response()->json([
                        'message' => 'Success',
                        'errors' => false,
                        'article' => $add_article
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

    public function editArticles(Request $request)
    {
        $article_id = $request->article_id;
        $title = $request->title;
        $description = $request->description;
        $image = $request->image;

        $exist = Article::where([
            ['id', '=', $article_id],
        ])->exists();

        if($exist){

            $files = $image;
            $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
            if ($request->hasfile('image')) {

                $filename = '/image/article/'.time() . '.' . $files->getClientOriginalName();
                $extension = $files->getClientOriginalExtension();

                $check = in_array($extension, $allowedfileExtension);

                if ($check) {

                    $files->move(public_path() . '/image/article/', $filename);

                    $edit_article = Article::find($article_id);
                    $edit_article->title = $title;
                    $edit_article->description = $description;
                    $edit_article->image = $filename;
                    $edit_article->save();
                    
                    if ($edit_article) {
                        return response()->json([
                            'message' => 'Success',
                            'errors' => false,
                            'article' => $edit_article
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

                $edit_article = Article::find($article_id);
                $edit_article->title = $title;
                $edit_article->description = $description;
                $edit_article->save();
                
                if ($edit_article) {
                    return response()->json([
                        'message' => 'Success',
                        'errors' => false,
                        'article' => $edit_article
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

    public function getArticles(Request $request)
    {
        $search = $request->search;

        $article = Article::orderBy('created_at', 'DESC')
        ->where('title', 'like', "%" . $search . "%")
        ->get();
      
        if ($article) {
            return response()->json([
                'message' => 'Success',
                'errors' => false,
                'data' => $article,
            ]);
        } else {
            return response()->json([
                'message' => 'Failed',
                'errors' => true,
            ]);
        }        
    }

    public function deleteArticles(Request $request)
    {
        $article_id = $request->article_id;

        $exist = Article::where([
            ['id', '=', $article_id],
        ])->exists();

        if ($exist) {

            $delete = Article::where(
                'id',
                $article_id
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


