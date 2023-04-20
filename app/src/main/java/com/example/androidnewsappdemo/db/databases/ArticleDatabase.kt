/*
 * MIT License
 *
 * Copyright (c) 2023 Runze Wu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.androidnewsappdemo.db.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.androidnewsappdemo.constants.Constants
import com.example.androidnewsappdemo.db.converters.Converters
import com.example.androidnewsappdemo.db.dal.ArticleDao
import com.example.androidnewsappdemo.models.Article


@Database(
  exportSchema = false,
  entities = [Article::class],
  version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

  abstract fun getArticleDao(): ArticleDao

  companion object {

    @Volatile
    private var instance: ArticleDatabase? = null
    private val lock = Any()

    operator fun invoke(context: Context) = instance?: synchronized(lock) {
      instance?: createDatabase(context).also {
        instance = it
      }
    }

    private fun createDatabase(context: Context) =
      Room.databaseBuilder(
        context.applicationContext,
        ArticleDatabase::class.java,
        Constants.ARTICLE_DB_NAME
      ).build()
  }
}