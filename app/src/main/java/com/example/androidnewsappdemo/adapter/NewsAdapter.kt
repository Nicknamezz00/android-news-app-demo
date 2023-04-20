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

package com.example.androidnewsappdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidnewsappdemo.R
import com.example.androidnewsappdemo.models.Article
import kotlinx.android.synthetic.main.item_article_preview.view.ivArticleImage
import kotlinx.android.synthetic.main.item_article_preview.view.tvDescription
import kotlinx.android.synthetic.main.item_article_preview.view.tvPublishedAt
import kotlinx.android.synthetic.main.item_article_preview.view.tvSource
import kotlinx.android.synthetic.main.item_article_preview.view.tvTitle

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

  inner class ArticleViewHolder(iv: View) : RecyclerView.ViewHolder(iv)

  private val differCallback = object : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
      return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
      return oldItem == newItem
    }
  }

  val differ = AsyncListDiffer(this, differCallback)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
    return ArticleViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.item_article_preview, parent, false)
    )
  }

  override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
    val article = differ.currentList[position]
    holder.itemView.apply {
      Glide.with(this)
        .load(article.urlToImage)
        .into(ivArticleImage)

      tvSource.text = article.source.name
      tvTitle.text = article.title
      tvDescription.text = article.description
      tvPublishedAt.text = article.publishedAt

      setOnClickListener {
        onItemClickListener?.let {
          it(article)
        }
      }
    }
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  private var onItemClickListener: ((Article) -> Unit)? = null

  fun setOnItemClickListener(listener: (Article) -> Unit) {
    onItemClickListener = listener
  }
}