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

package com.example.androidnewsappdemo.ui

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.androidnewsappdemo.R
import com.example.androidnewsappdemo.db.databases.ArticleDatabase
import com.example.androidnewsappdemo.repository.NewsRepo
import kotlinx.android.synthetic.main.activity_news.bottomNavigationView
import kotlinx.android.synthetic.main.activity_news.newsNavHostFragment

class NewsActivity : AppCompatActivity() {

  lateinit var viewModel: NewsViewModel
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_news)

    val newsRepository = NewsRepo(ArticleDatabase(this))
    val vmProviderFactory = NewsVMProviderFactory(application, newsRepository)
    viewModel = ViewModelProvider(this, vmProviderFactory)[NewsViewModel::class.java]

//    bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

    val navHostFragment =
      supportFragmentManager
        .findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

    val navController = navHostFragment.navController
    bottomNavigationView.setupWithNavController(navController)
  }
}