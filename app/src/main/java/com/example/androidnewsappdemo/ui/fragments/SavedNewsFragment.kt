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

package com.example.androidnewsappdemo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidnewsappdemo.R
import com.example.androidnewsappdemo.adapters.NewsAdapter
import com.example.androidnewsappdemo.ui.NewsActivity
import com.example.androidnewsappdemo.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_saved_news.rvSavedNews

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

  lateinit var viewModel: NewsViewModel
  private lateinit var newsAdapter: NewsAdapter
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel = (activity as NewsActivity).viewModel
    setUpRecyclerView()

    newsAdapter.setOnItemClickListener {
      val bundle = Bundle().apply {
        putSerializable("article", it)
      }
      findNavController().navigate(
        R.id.action_savedNewsFragment_to_articleFragment,
        bundle
      )
    }
  }

  private fun setUpRecyclerView() {
    newsAdapter = NewsAdapter()
    rvSavedNews.apply {
      adapter = newsAdapter
      layoutManager = LinearLayoutManager(activity)
    }
  }
}