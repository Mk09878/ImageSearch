package com.example.imagesearch.data

import androidx.paging.PagingSource
import com.example.imagesearch.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1 // Companion object is not used since the value is not related to the class

/**
 * Knows how to load data from api and then turn it into pages
 */
class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        return try {
            val response = unsplashApi.searchPhotos(query, position, params.loadSize)
            val photos = response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException){
            // if no net connection
            LoadResult.Error(exception)

        } catch (exception: HttpException){
            // issue with server
            LoadResult.Error(exception)
        }

    }

}