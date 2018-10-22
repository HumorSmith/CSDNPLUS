package com.ifreedomer.cplus.http.protocol;


import com.ifreedomer.cplus.http.protocol.resp.ApproveResp;
import com.ifreedomer.cplus.http.protocol.resp.BlogCategoryResp;
import com.ifreedomer.cplus.http.protocol.resp.BlogResp;
import com.ifreedomer.cplus.http.protocol.resp.MyBlogItemResp;
import com.ifreedomer.cplus.http.protocol.resp.SearchDetailResp;
import com.ifreedomer.cplus.http.protocol.resp.SearchResp;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BlogApi {
    @GET("api/blog/categorylist")
    Observable<PayLoad<List<BlogCategoryResp>>> getBlogCategory(@Query("SessionId") String sessionId, @Query("page") int page, @Query("username") String username);

    @GET("api/blog/articlelist")
    Observable<PayLoad<List<BlogResp>>> getBlogListByCategory(
            @Query("SessionId") String sessionId,
            @Query("username") String username,
            @Query("id") int id,
            @Query("page") int page,
            @Query("size") int size);

    @GET("api/v5/ArticleDiggApp/digg")
    Observable<PayLoad<ApproveResp>> approve(
            @Query("username") String username,
            @Query("article_id") String articleId);


    @GET("api/ask/search_tags")
    Observable<PayLoad<SearchResp>> search(
            @Query("size") int size,
            @Query("page") int page,
            @Query("word") String word);


    @GET("api/v3/search/elastic")
    Observable<PayLoad<SearchDetailResp>> getDetailListByTag(
            @Query("size") int size,
            @Query("page") int page,
            @Query("block") String block,
            @Query("keywords") String words);


    @GET("api/blog/user_blog_list")
    Observable<PayLoad<List<MyBlogItemResp>>> getMyBlogList(@Query("username") String username, @Query("page") int page, @Query("pagesize") int pagesize);





}
