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

package com.example.androidnewsappdemo.api

import com.example.androidnewsappdemo.constants.Constants
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

  companion object {

    val api by lazy {
      _retrofit.create(NewsAPI::class.java)
    }

    private val _retrofit by lazy {
      val log = HttpLoggingInterceptor()
      log.setLevel(HttpLoggingInterceptor.Level.BODY)

//      val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//        .tlsVersions(TlsVersion.TLS_1_2)
//        .cipherSuites(
//          CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//          CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//          CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
//          CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
//          CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
//          CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA)
//        .build()

      val client = OkHttpClient.Builder()
        .addInterceptor(log)
//        .connectionSpecs(listOf(spec))
        .build()

      Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    }
  }
}