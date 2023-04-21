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
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidnewsappdemo.R
import com.example.androidnewsappdemo.adapters.NewsAdapter
import com.example.androidnewsappdemo.constants.Constants
import com.example.androidnewsappdemo.ui.NewsActivity
import com.example.androidnewsappdemo.ui.NewsViewModel
import com.example.androidnewsappdemo.utils.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_breaking_news.rvBreakingNews

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

  lateinit var viewModel: NewsViewModel
  lateinit var newsAdapter: NewsAdapter
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel = (activity as NewsActivity).viewModel
    setUpRecyclerView()

    newsAdapter.setOnItemClickListener {
      val bundle = Bundle().apply {
        putSerializable("article", it)
      }
      findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
    }

    viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
      when (response) {
        is Resource.Success -> {
          hideProgress()
          response.data?.let { newsResponse ->
            newsAdapter
              .differ
              .submitList(newsResponse.articles.toList())

            // add 1: For arithmetic ceil
            // add 1: Another page is always empty
            val totalPages = newsResponse.totalResults / Constants.DEFAULT_QUERY_PAGE_SIZE + 2
            isLastPage = viewModel.breakingNewsPageNumber == totalPages
            if (isLastPage) {
              rvBreakingNews.setPadding(0, 0, 0, 0)
            }
          }
        }
        is Resource.Error -> {
          hideProgress()
          response.message?.let { message ->
            Log.e("BreakingNewsFragment", "error: $message")
          }
        }
        is Resource.Loading -> {
          showProgress()
        }
      }
    })
  }

  private fun hideProgress() {
    paginationProgressBar.visibility = View.INVISIBLE
    isLoading = false
  }

  private fun showProgress() {
    paginationProgressBar.visibility = View.VISIBLE
    isLoading = true
  }

  var isLoading = false
  var isLastPage = false
  var isScrolling = false

  private val scrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
      super.onScrollStateChanged(recyclerView, newState)
      if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
        isScrolling = true
      }

    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      super.onScrolled(recyclerView, dx, dy)
      val layoutManager = recyclerView.layoutManager as LinearLayoutManager
      val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
      val childCount = layoutManager.childCount
      val itemCount = layoutManager.itemCount

      val isLastItem = firstVisibleItemPosition + childCount >= itemCount
      val isNotBeginning = firstVisibleItemPosition >= 0
      val isTotalMoreThanVisible = itemCount >= Constants.DEFAULT_QUERY_PAGE_SIZE

      val paginate = !isLoading && !isLastPage
          && isLastItem
          && isNotBeginning
          && isTotalMoreThanVisible

      if (paginate) {
        viewModel.getBreakingNews(Constants.DEFAULT_COUNTRY_CODE)
        isScrolling = false
      }
    }
  }
  private fun setUpRecyclerView() {
    newsAdapter = NewsAdapter()
    rvBreakingNews.apply {
      adapter = newsAdapter
      layoutManager = LinearLayoutManager(activity)
      addOnScrollListener(this@BreakingNewsFragment.scrollListener)
    }
  }
}