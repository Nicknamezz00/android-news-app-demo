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

@file:Suppress("DEPRECATION")

package com.example.androidnewsappdemo.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidnewsappdemo.NewsApplication
import com.example.androidnewsappdemo.constants.Constants
import com.example.androidnewsappdemo.models.Article
import com.example.androidnewsappdemo.models.NewsResponse
import com.example.androidnewsappdemo.repository.NewsRepo
import com.example.androidnewsappdemo.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
  app: Application,
  private val newsRepo: NewsRepo
) : AndroidViewModel(app) {

  val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
  var breakingNewsPageNumber = 1
  private var breakingNewsResponse: NewsResponse? = null

  val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
  var searchNewsPageNumber = 1
  private var searchNewsResponse: NewsResponse? = null
  private val newSearchQuery : String? = null
  private var oldSearchQuery : String? = null

  init {
    getBreakingNews(Constants.DEFAULT_COUNTRY_CODE)
  }

  fun getBreakingNews(countryCode: String) = viewModelScope.launch {
    safeBreakingNewsAPICall(countryCode)
  }

  fun searchNews(q: String) = viewModelScope.launch {
    safeSearchNewsAPICall(q)
  }

  private fun searchNewsResponseHandler(response: Response<NewsResponse>): Resource<NewsResponse> {
    if (response.isSuccessful) {
      response.body()?.let { result ->
        if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
          searchNewsPageNumber = 1
          oldSearchQuery = newSearchQuery
          searchNewsResponse = result
        } else {
          searchNewsPageNumber += 1
          val old = searchNewsResponse?.articles
          val new = result.articles
          old?.addAll(new)
        }
        return Resource.Success(searchNewsResponse?: result)
      }
    }
    return Resource.Error(response.message())
  }

  private fun breakingNewsResponseHandler(response: Response<NewsResponse>) : Resource<NewsResponse> {
    if (response.isSuccessful) {
      response.body()?.let { result ->
        breakingNewsPageNumber += 1
        if (breakingNewsResponse == null) {
          breakingNewsResponse = result
        } else {
          val old = breakingNewsResponse?.articles
          val new = result.articles
          old?.addAll(new)
        }
        return Resource.Success(breakingNewsResponse?: result)
      }
    }
    return Resource.Error(response.message())
  }

  fun saveArticle(article: Article) = viewModelScope.launch {
    newsRepo.upsert(article)
  }

  fun getSavedNews() = newsRepo.getSavedNews()

  fun deleteArticle(article: Article) = viewModelScope.launch {
    newsRepo.deleteArticle(article)
  }

  private suspend fun safeBreakingNewsAPICall(countryCode: String) {
    breakingNews.postValue(Resource.Loading())
    try {
      if (hasInternetConnection()) {
        val response = newsRepo.getBreakingNews(countryCode, breakingNewsPageNumber)
        breakingNews.postValue(breakingNewsResponseHandler(response))
      } else {
        breakingNews.postValue(Resource.Error("No internet connection"))
      }
    } catch (t: Throwable) {
      when (t) {
        is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
        // this case shouldn't be reached
        else -> breakingNews.postValue(Resource.Error("Internal Error"))
      }
    }
  }

  private suspend fun safeSearchNewsAPICall(q: String) {
    searchNews.postValue(Resource.Loading())
    try {
      if (hasInternetConnection()) {
        val response = newsRepo.searchNews(q, breakingNewsPageNumber)
        searchNews.postValue(searchNewsResponseHandler(response))
      } else {
        searchNews.postValue(Resource.Error("No internet connection"))
      }
    } catch (t: Throwable) {
      when (t) {
        is IOException -> searchNews.postValue(Resource.Error("Network failure"))
        // this case shouldn't be reached
        else -> searchNews.postValue(Resource.Error("Internal Error"))
      }
    }
  }

  private fun hasInternetConnection() : Boolean {
    val connManager = getApplication<NewsApplication>()
      .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // `connManager.activeNetworkInfo` deprecated with SDK API >= 23
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val activeNetwork = connManager.activeNetwork ?: return false
      val cap = connManager.getNetworkCapabilities(activeNetwork) ?: return false
      return when {
        cap.hasTransport(TRANSPORT_WIFI) -> true
        cap.hasTransport(TRANSPORT_CELLULAR) -> true
        cap.hasTransport(TRANSPORT_ETHERNET) -> true
        else -> false
      }
    } else {
      connManager.activeNetworkInfo?.run {
        return when(type) {
          TYPE_WIFI -> true
          TYPE_MOBILE -> true
          TYPE_ETHERNET -> true
          else -> false
        }
      }
    }
    // never reach
    return false
  }
}