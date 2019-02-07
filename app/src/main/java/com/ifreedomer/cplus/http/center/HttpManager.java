package com.ifreedomer.cplus.http.center;

import android.util.Log;

import com.google.gson.Gson;
import com.ifreedomer.cplus.entity.BlogContentInfo;
import com.ifreedomer.cplus.entity.DeployBlogContentInfo;
import com.ifreedomer.cplus.http.intercepter.AddCookiesInterceptor;
import com.ifreedomer.cplus.http.intercepter.CookieInterceptor;
import com.ifreedomer.cplus.http.intercepter.HeaderInterceptor;
import com.ifreedomer.cplus.http.intercepter.ReceivedCookiesInterceptor;
import com.ifreedomer.cplus.http.protocol.ArticleApi;
import com.ifreedomer.cplus.http.protocol.BlogApi;
import com.ifreedomer.cplus.http.protocol.CollectApi;
import com.ifreedomer.cplus.http.protocol.CommentApi;
import com.ifreedomer.cplus.http.protocol.FollowApi;
import com.ifreedomer.cplus.http.protocol.ForgetPwdApi;
import com.ifreedomer.cplus.http.protocol.ForumApi;
import com.ifreedomer.cplus.http.protocol.H5ArticleApi;
import com.ifreedomer.cplus.http.protocol.LoginAppV1Api;
import com.ifreedomer.cplus.http.protocol.LoginV3Api;
import com.ifreedomer.cplus.http.protocol.PayLoad;
import com.ifreedomer.cplus.http.protocol.body.FormBodyCustom;
import com.ifreedomer.cplus.http.protocol.req.FollowReq;
import com.ifreedomer.cplus.http.protocol.req.GetVerifyCodeReq;
import com.ifreedomer.cplus.http.protocol.req.LoginReq;
import com.ifreedomer.cplus.http.protocol.resp.AddCollectResp;
import com.ifreedomer.cplus.http.protocol.resp.AddCommentResp;
import com.ifreedomer.cplus.http.protocol.resp.ApproveResp;
import com.ifreedomer.cplus.http.protocol.resp.ArticleDetailInfoResp;
import com.ifreedomer.cplus.http.protocol.resp.ArticleListResp;
import com.ifreedomer.cplus.http.protocol.resp.ArticleResp;
import com.ifreedomer.cplus.http.protocol.resp.BlogCategoryResp;
import com.ifreedomer.cplus.http.protocol.resp.BlogResp;
import com.ifreedomer.cplus.http.protocol.resp.BlogUserProfileResp;
import com.ifreedomer.cplus.http.protocol.resp.CheckCollectResp;
import com.ifreedomer.cplus.http.protocol.resp.CollectListResp;
import com.ifreedomer.cplus.http.protocol.resp.CollectNumResp;
import com.ifreedomer.cplus.http.protocol.resp.CommentListResp;
import com.ifreedomer.cplus.http.protocol.resp.CountResp;
import com.ifreedomer.cplus.http.protocol.resp.DeleteCollectResp;
import com.ifreedomer.cplus.http.protocol.resp.DeployBlogResp;
import com.ifreedomer.cplus.http.protocol.resp.DiggResp;
import com.ifreedomer.cplus.http.protocol.resp.FollowOperationResp;
import com.ifreedomer.cplus.http.protocol.resp.FollowResp;
import com.ifreedomer.cplus.http.protocol.resp.ForgetPwdUserNameResp;
import com.ifreedomer.cplus.http.protocol.resp.ForumDetailResp;
import com.ifreedomer.cplus.http.protocol.resp.ForumHotResp;
import com.ifreedomer.cplus.http.protocol.resp.ForumPostResp;
import com.ifreedomer.cplus.http.protocol.resp.GetRelationResp;
import com.ifreedomer.cplus.http.protocol.resp.GetUserTokenResp;
import com.ifreedomer.cplus.http.protocol.resp.ResetPwdResp;
import com.ifreedomer.cplus.http.protocol.resp.HistoryResp;
import com.ifreedomer.cplus.http.protocol.resp.LoginAppV1TokenResp;
import com.ifreedomer.cplus.http.protocol.resp.MyBlogItemResp;
import com.ifreedomer.cplus.http.protocol.resp.SearchDetailResp;
import com.ifreedomer.cplus.http.protocol.resp.SearchResp;
import com.ifreedomer.cplus.http.protocol.resp.UserBlogInfoResp;
import com.ifreedomer.cplus.http.protocol.resp.UserInfoResp;
import com.ifreedomer.cplus.http.protocol.resp.V2ProfileResp;
import com.ifreedomer.cplus.manager.GlobalDataManager;
import com.ifreedomer.cplus.util.MD5Util;
import com.ifreedomer.cplus.util.SecurityUtil;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HttpManager {
    private Gson mGson = new Gson();

    public Gson getGson() {
        return mGson;
    }

    public void setGson(Gson gson) {
        mGson = gson;
    }

    public static final String TAG = HttpManager.class.getSimpleName();
    private static final String BASE_URL = "https://gw.csdn.net";

    private static final String LOGIN_URL = "https://passport.csdn.net";


    private HttpLoggingInterceptor mLogging = new HttpLoggingInterceptor();
    private Retrofit retrofit;


    private Retrofit mStringRetrofit;


    private Retrofit mCookieRetrofit;

    private Retrofit mLoginRetrofit;


    private OkHttpClient mClient = new OkHttpClient.Builder()
            .addInterceptor(mLogging)
            .addInterceptor(new HeaderInterceptor())
            .build();


    public HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
    private OkHttpClient mCookieClient = new OkHttpClient.Builder()
            .addInterceptor(mLogging)
            .addInterceptor(new ReceivedCookiesInterceptor())
            .addInterceptor(new AddCookiesInterceptor())
//            .addInterceptor(new CookieInterceptor())
            .build();


    private HttpManager() {
        mLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

        mStringRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(mClient)

                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .client(mClient)
                .build();


        mCookieRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .client(mCookieClient)
                .build();


        mLoginRetrofit = new Retrofit.Builder()
                .baseUrl(LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .client(mClient)
                .build();

    }

    ;
    private static HttpManager sInstance = new HttpManager();

    public static HttpManager getInstance() {
        return sInstance;
    }




    //老的V3接口
    public Observable<PayLoad<UserInfoResp>> loginV3(String account, String password) {
        String decryptPwd = SecurityUtil.DESEncrypt(password);
        LoginV3Api loginApi = retrofit.create(LoginV3Api.class);
        Log.d(TAG, "account = " + account + "  password = " + password + "   decryptPwd =" + decryptPwd);
        Observable<PayLoad<UserInfoResp>> loginObservable = loginApi.loginV3(account, decryptPwd, "LH5Dv4pUXww%3D").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return loginObservable;
    }

    //专为app设计的V1接口
    public Observable<PayLoad<LoginAppV1TokenResp>> loginAppV1(String account, String password) {
        String decryptPwd = SecurityUtil.DESEncrypt(password);
        LoginAppV1Api loginApi = mLoginRetrofit.create(LoginAppV1Api.class);
        Log.d(TAG, "account = " + account + "  password = " + password + "   decryptPwd =" + decryptPwd);

        LoginReq loginReq = new LoginReq();
        loginReq.setPwdOrVerifyCode(password);
        loginReq.setUserIdentification(account);
        loginReq.setLoginType(1 + "");
        loginReq.setFkid("201802171746501860b4d3cbd22d2c8a65046a2f1b4f8b016536f80cc2f8b4");
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), mGson.toJson(loginReq));
        Observable<PayLoad<LoginAppV1TokenResp>> loginObservable = loginApi.login(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return loginObservable;
    }


    public Observable<PayLoad<V2ProfileResp>> getV2Profile(String account) {
        String sessionId = GlobalDataManager.getInstance().getSessionId();
        LoginV3Api loginApi = retrofit.create(LoginV3Api.class);
        Observable<PayLoad<V2ProfileResp>> userProfileObservable = loginApi.getUserProfile(account);
        return userProfileObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<PayLoad<List<BlogCategoryResp>>> getBlogCatergory(String username) {
        BlogApi blogApi = retrofit.create(BlogApi.class);
        Observable<PayLoad<List<BlogCategoryResp>>> blogCategoryObservable = blogApi.getBlogCategory( 1, username)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return blogCategoryObservable;
    }

    public Observable<PayLoad<List<BlogResp>>> getBlogListByCategory(int categoryId, String username, int page, int size) {
        Observable<PayLoad<List<BlogResp>>> blogListByCategory = retrofit.create(BlogApi.class).getBlogListByCategory(username, categoryId, page, size);
        return blogListByCategory.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<ArticleListResp<ArticleResp>>> getArticleListByCategory(String category, String type, long offset, int size) {
        Observable<PayLoad<ArticleListResp<ArticleResp>>> articleListByCategoryObservable = retrofit.create(ArticleApi.class).getNews(
                category, "867686022248916", type, offset, size);
        articleListByCategoryObservable = articleListByCategoryObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return articleListByCategoryObservable;
    }


    public Observable<PayLoad<List<HistoryResp>>> getHistory() {
        Observable<PayLoad<List<HistoryResp>>> historyListByCategoryObservable = retrofit.create(ArticleApi.class).getHistory();
        historyListByCategoryObservable = historyListByCategoryObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return historyListByCategoryObservable;
    }

    public Observable<PayLoad<List<FollowResp>>> getIdol(String userName, int page, int size) {
        Observable<PayLoad<List<FollowResp>>> idolObservable = retrofit.create(FollowApi.class).getIdol(userName, page, size);
        idolObservable = idolObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return idolObservable;
    }

    public Observable<PayLoad<List<FollowResp>>> getFans(String userName, int page, int size) {
        Observable<PayLoad<List<FollowResp>>> idolObservable = retrofit.create(FollowApi.class).getFans(userName, page
                , size);
        idolObservable = idolObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return idolObservable;
    }

    public Observable<PayLoad<FollowOperationResp>> unfollow(String username) {
        Observable<PayLoad<FollowOperationResp>> unFollowObservable = retrofit.create(FollowApi.class).unFollow( username, GlobalDataManager.getInstance().getUserInfo().getUserName());
        unFollowObservable = unFollowObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return unFollowObservable;
    }


    public Observable<PayLoad<FollowOperationResp>> follow(String username) {
        FollowReq followReq = new FollowReq();
        followReq.setFans(username);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), mGson.toJson(followReq));
        Observable<PayLoad<FollowOperationResp>> unFollowObservable = retrofit.create(FollowApi.class).doFollow(body);
        unFollowObservable = unFollowObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return unFollowObservable;
    }


    public Observable<PayLoad<ApproveResp>> approve(String username, String articleId) {
        Observable<PayLoad<ApproveResp>> approveObserver = retrofit.create(BlogApi.class).approve(username, articleId);
        approveObserver = approveObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return approveObserver;
    }


    public Observable<PayLoad<CountResp>> getCountProfile(String username) {
        Observable<PayLoad<CountResp>> getCountProfile = retrofit.create(LoginV3Api.class).getCountProfile(username);
        getCountProfile = getCountProfile.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return getCountProfile;
    }

    public Observable<PayLoad<UserBlogInfoResp>> getUserBlogInfo(String userName) {
        Observable<PayLoad<UserBlogInfoResp>> userInfoObserver = retrofit.create(LoginV3Api.class).getBlogUserinfo(userName);
        return userInfoObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PayLoad<GetUserTokenResp>> getUserToken() {
        Observable<PayLoad<GetUserTokenResp>> userInfoObserver = retrofit.create(LoginAppV1Api.class).getUserToken();
        Observable<PayLoad<GetUserTokenResp>> userBlogInfoRespObservable = userInfoObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return userBlogInfoRespObservable;
    }


    public Observable<PayLoad<CollectNumResp>> getCollectNum() {
        Observable<PayLoad<CollectNumResp>> collectNumObserver = retrofit.create(CollectApi.class).getFavoriteNum().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return collectNumObserver;
    }


    public Observable<PayLoad<List<CollectListResp>>> getCollectList(int pageNum, int pageSize) {
        Observable<PayLoad<List<CollectListResp>>> collectListObserver = retrofit.create(CollectApi.class).getCollectList("blog", pageNum, pageSize).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return collectListObserver;
    }

    public Observable<PayLoad<List<MyBlogItemResp>>> getMyBlogList(String userName, int curPage, int pageSize) {
        Observable<PayLoad<List<MyBlogItemResp>>> myBlogListObserver = retrofit.create(BlogApi.class).getMyBlogList(userName, curPage, pageSize).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return myBlogListObserver;
    }

    public Observable<PayLoad<SearchResp>> search(String tag) {
        Observable<PayLoad<SearchResp>> payLoadObservable = retrofit.create(BlogApi.class).search(20, 1, tag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return payLoadObservable;
    }

    public Observable<PayLoad<SearchDetailResp>> getSearchDetailListByTag(String tagResult) {
        BlogApi blogApi = retrofit.create(BlogApi.class);
        return blogApi.getDetailListByTag(20, 1, "so_blog", tagResult).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveArticle(DeployBlogContentInfo deployBlogContentInfo) {
        boolean aPrivate = deployBlogContentInfo.isPrivate();
//
//        Request request = new Request.Builder()
//                .url("https://mp.csdn.net/mdeditor/saveArticle")
//                .post(requestBody)
//                .build();
//
//        mCookieClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                Log.d(TAG, "FAILED = " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                String string = response.body().string();
//                Log.d(TAG, string);
//            }
//        });


//        mCookieClient.newCall()
//        Observable<String> saveArticleRespObservable = mCookieRetrofit.create(H5ArticleApi.class).saveArticle(requestBody);
//        return saveArticleRespObservable;


    }


    public Observable<DeployBlogResp> saveArticleNew(DeployBlogContentInfo deployBlogContentInfo) {
        boolean aPrivate = deployBlogContentInfo.isPrivate();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", deployBlogContentInfo.getTitle())
                .addFormDataPart("markdowncontent", deployBlogContentInfo.getMarkdownContent())
                .addFormDataPart("content", deployBlogContentInfo.getContent())
                .addFormDataPart("id", deployBlogContentInfo.getId())
                .addFormDataPart("private", aPrivate ? (aPrivate + "") : "")
                .addFormDataPart("tags", deployBlogContentInfo.getTags())
                .addFormDataPart("status", aPrivate ? "65" : "")
                .addFormDataPart("categories", deployBlogContentInfo.getCategories())
                .addFormDataPart("channel", deployBlogContentInfo.getChannel())
                .addFormDataPart("type", deployBlogContentInfo.getType())
                .addFormDataPart("articleedittype", deployBlogContentInfo.getArticleedittype())
                .addFormDataPart("Description", deployBlogContentInfo.getDescription())
                .build();
        Observable<DeployBlogResp> deployBlogRespObservable = mCookieRetrofit.create(H5ArticleApi.class).saveArticle(requestBody);
        return deployBlogRespObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<ForgetPwdUserNameResp> getUserNameByPhone(String countryCode, String phone) {
        Observable<ForgetPwdUserNameResp> forgetPwdRespObservable = mCookieRetrofit.create(ForgetPwdApi.class).requestUserName(phone, 0, countryCode).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return forgetPwdRespObservable;

    }

    public Observable<ResetPwdResp> getVerifyCode(GetVerifyCodeReq getVerifyCodeReq) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), mGson.toJson(getVerifyCodeReq));
        Observable<ResetPwdResp> forgetPwdRespObservable = mCookieRetrofit.create(ForgetPwdApi.class).getVerifyCode(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return forgetPwdRespObservable;
    }

    public Observable<ResetPwdResp> checkPWDResetTime() {
        Observable<ResetPwdResp> resetPwdRespObservable = mCookieRetrofit.create(ForgetPwdApi.class).checkResetPwdTime();
        return resetPwdRespObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<ArticleDetailInfoResp>> getArticleInfo(String articleId) {
        Observable<PayLoad<ArticleDetailInfoResp>> articleInfoObserver = retrofit.create(ArticleApi.class).getArticleInfo(GlobalDataManager.getInstance().getUserInfo().getUserName(), articleId);
        articleInfoObserver = articleInfoObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return articleInfoObserver;
    }


    public Observable<PayLoad<BlogContentInfo>> getBlogContent(String articleId, String userName) {
        Observable<PayLoad<BlogContentInfo>> articleInfoObserver = retrofit.create(BlogApi.class).getBlogInfo(articleId,userName);
        articleInfoObserver = articleInfoObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return articleInfoObserver;
    }



    public Observable<PayLoad<AddCollectResp>> addCollect(String title, String url, String userName) {
        Log.d(TAG, "title = " + title + "   url = " + url + "   userName" + userName);
        Observable<PayLoad<AddCollectResp>> collectObserver = retrofit.create(CollectApi.class).addCollect(title, url, userName);
        collectObserver = collectObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return collectObserver;
    }


    public Observable<PayLoad<DeleteCollectResp>> deleteCollect(String id) {
        Observable<PayLoad<DeleteCollectResp>> deleteCollectObserver = retrofit.create(CollectApi.class).deleteCollect(id);
        return deleteCollectObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<CheckCollectResp>> checkFavorite(String username, String url) {
        Observable<PayLoad<CheckCollectResp>> deleteCollectObserver = retrofit.create(CollectApi.class).checkCollect(username, url);
        return deleteCollectObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<DiggResp>> digg(String username, String articleId) {
        Observable<PayLoad<DiggResp>> diggObserver = retrofit.create(BlogApi.class).digg(username, articleId);
        return diggObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<CommentListResp>> getCommentList(String articleId, int page, int pageSize) {
        Observable<PayLoad<CommentListResp>> deleteCollectObserver = retrofit.create(CommentApi.class).getCommentList(articleId, page, pageSize);
        return deleteCollectObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<AddCommentResp>> addComment(String articleId, String content) {
        Observable<PayLoad<AddCommentResp>> addCommentObserver = retrofit.create(CommentApi.class).addComment(articleId, content, "", "application/x-www-form-urlencoded", GlobalDataManager.getInstance().getUserInfo().getUserName());
        return addCommentObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<GetRelationResp>> getRelation(String userName) {
        Observable<PayLoad<GetRelationResp>> getRelationObserver = retrofit.create(FollowApi.class).getRelation(userName);
        return getRelationObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<List<ForumHotResp>>> getForum(String type, int page) {
        Observable<PayLoad<List<ForumHotResp>>> hotForumObserver = retrofit.create(ForumApi.class).getHotForum(type, page);
        return hotForumObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<List<ForumDetailResp>>> getForumDetail(String topicId, int page, int pageSize) {
        Observable<PayLoad<List<ForumDetailResp>>> forumDetailObserver = retrofit.create(ForumApi.class).getForumDetail(topicId, page, pageSize, "");
        return forumDetailObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<Boolean>> forumDigg(String userName, String topicId, String postId) {
        Observable<PayLoad<Boolean>> diggObserver = retrofit.create(ForumApi.class).digg(userName, topicId, postId);
        return diggObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<PayLoad<ForumPostResp>> forumPost(String topicId, String body, String userName) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("topic_id", topicId)
                .addFormDataPart("body", body)
                .addFormDataPart("username", userName)
                .build();
        Observable<PayLoad<ForumPostResp>> payLoadObservable = retrofit.create(ForumApi.class).forumPost(requestBody);
        return payLoadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }


    public Observable<PayLoad<Boolean>> forumReport(int reasonType, String topicId, String postId, String userName) {
        Observable<PayLoad<Boolean>> payLoadObservable = retrofit.create(ForumApi.class).forumReport(reasonType, userName, topicId, postId);
        return payLoadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }


    public void forumCreateTopic(String title, String body, String forumId, int point) {
        String sign = MD5Util.md5(new StringBuffer("title=").append(title).append("&forum_id=").append("MSSQL_NewTech").append("&key=").append(SecurityUtil.getSignatureByMD5("csdn-android-" + "3.1.2")).toString()).toUpperCase();

//        FormBody requestBody = null;
        FormBodyCustom formBodyCustom = new FormBodyCustom();
        formBodyCustom.add("sign", sign);
        formBodyCustom.add("title", title);
        formBodyCustom.add("body", body);
        formBodyCustom.add("point", point + "");
        formBodyCustom.add("forum_id", "MSSQL_NewTech", true);
        Request request = new Request.Builder().url("https://ms.csdn.net/api/v3/bbs/create_topic")
                .post(formBodyCustom).build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "RESPONSE = " + response.body().string());
            }
        });

//        Observable<PayLoad<CreateTopicResp>> payLoadObservable = null;
//        Observable<String> payLoadObservable = mStringRetrofit.create(ForumApi.class).forumCreateTopic(formBodyCustom);


//        return payLoadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public void verifyCode(String verifyCode) {

        Observable<PayLoad<AddCollectResp>> collectObserver = mCookieRetrofit.create(CollectApi.class).addCollect("", "", "");
        collectObserver.subscribe(new Consumer<PayLoad<AddCollectResp>>() {
            @Override
            public void accept(PayLoad<AddCollectResp> addCollectRespPayLoad) throws Exception {

            }
        });
//        retrofit.create(ForgetPwdApi.class).verifyCode(verifyCode);
    }
}
