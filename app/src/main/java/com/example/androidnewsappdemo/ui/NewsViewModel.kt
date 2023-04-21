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

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidnewsappdemo.models.NewsResponse
import com.example.androidnewsappdemo.repository.NewsRepo
import com.example.androidnewsappdemo.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepo) : ViewModel() {

  val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
  var breakingNewsPageNumber = 1

  val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
  var searchNewsPageNumber = 1

  init {
    Log.d("NewsViewModel", "Init")
    getBreakingNews("cn")
  }

  fun getBreakingNews(countryCode: String) = viewModelScope.launch {
    Log.d("NewsViewModel", "Post value begin")
    breakingNews.postValue(Resource.Loading())
    Log.d("NewsViewModel", "Post value end")
    val response =
      newsRepository.getBreakingNews(countryCode, breakingNewsPageNumber)

    Log.d("NewsViewModel", "response: $response")
    breakingNews.postValue(breakingNewsResponseHandler(response))
  }


  fun searchNews(searchQuery: String) = viewModelScope.launch {
    searchNews.postValue(Resource.Loading())
    val response = newsRepository.searchNews(searchQuery, searchNewsPageNumber)
    searchNews.postValue(searchNewsResponseHandler(response))
  }

  private fun searchNewsResponseHandler(response: Response<NewsResponse>): Resource<NewsResponse>? {
    if (response.isSuccessful) {
      response.body()?.let { result ->
        return Resource.Success(result)
      }
    }
    return Resource.Error(response.message())
  }

  private fun breakingNewsResponseHandler(response: Response<NewsResponse>) : Resource<NewsResponse> {
    if (response.isSuccessful) {
      response.body()?.let { result ->
        return Resource.Success(result)
      }
    }
    return Resource.Error(response.message())
  }
}