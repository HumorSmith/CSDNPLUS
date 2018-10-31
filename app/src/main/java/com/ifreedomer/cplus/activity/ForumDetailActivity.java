package com.ifreedomer.cplus.activity;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.ifreedomer.cplus.R;
import com.ifreedomer.cplus.activity.common.PullRefreshActivity;
import com.ifreedomer.cplus.adapter.ForumDetailAdapter;
import com.ifreedomer.cplus.http.center.HttpManager;
import com.ifreedomer.cplus.http.protocol.PayLoad;
import com.ifreedomer.cplus.http.protocol.resp.AddCollectResp;
import com.ifreedomer.cplus.http.protocol.resp.CheckCollectResp;
import com.ifreedomer.cplus.http.protocol.resp.DeleteCollectResp;
import com.ifreedomer.cplus.http.protocol.resp.ForumDetailResp;
import com.ifreedomer.cplus.manager.GlobalDataManager;
import com.ifreedomer.cplus.util.ShareUtil;
import com.ifreedomer.cplus.util.ToolbarUtil;
import com.ifreedomer.cplus.util.WidgetUtil;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ForumDetailActivity extends PullRefreshActivity<ForumDetailResp> {

    public static final String TAG = ForumDetailActivity.class.getSimpleName();
    public static final String TOPIC_ID_KEY = "topic_id";
    public static final String TITLE_KEY = "title";
    @BindView(R.id.sendTv)
    TextView sendTv;
    @BindView(R.id.contentEt)
    EditText contentEt;
    private String mTopicId;
    private String mFavoriteId;
    private String mUrl;
    private String mTitle;


    @Override
    public int getLayoutId() {
        return R.layout.activity_forum_detail;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.blog_content_menu);
        toolbar.getMenu().findItem(R.id.shareItem).setOnMenuItemClickListener(item -> {
            ShareUtil.shareString(ForumDetailActivity.this, mUrl);
            return false;
        });

        toolbar.getMenu().findItem(R.id.collectItem).setOnMenuItemClickListener(item -> {
            if (item.isChecked()) {
                deleteCollect();
            } else {
                addCollect();
            }
            return false;
        });
        checkFavorite();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void fetchData(int page) {
        Observable<PayLoad<List<ForumDetailResp>>> forumDetailObserver = HttpManager.getInstance().getForumDetail(mTopicId, page, 20);
        Disposable subscribe = forumDetailObserver.subscribe(listPayLoad -> refreshList(listPayLoad.getCode(), listPayLoad.getMessage(), listPayLoad.getData()), throwable -> WidgetUtil.showSnackBar(ForumDetailActivity.this, throwable.getMessage()));
    }

    @Override
    public void initTitleAndAdapter() {
        ToolbarUtil.setTitleBarWithBack(this, toolbar, "");
        mTopicId = getIntent().getIntExtra(TOPIC_ID_KEY, -1) + "";
        mTitle = getIntent().getStringExtra(TITLE_KEY);

        mUrl = "http://bbs.csdn.net/topics/" + mTopicId;
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        ForumDetailAdapter forumDetailAdapter = new ForumDetailAdapter(mTopicId, R.layout.item_rv_forumdetail, mDataList);
        forumDetailAdapter.setReplyOnclickListener(forumDetailResp -> contentEt.setHint(getString(R.string.reply) + forumDetailResp.getNickname() + ":"));
        recycleview.setAdapter(forumDetailAdapter);
        fetchData(mFirstPage);
    }


    private void checkFavorite() {
        Observable<PayLoad<CheckCollectResp>> checkFavoriteObserver = HttpManager.getInstance().checkFavorite(GlobalDataManager.getInstance().getUserInfo().getUserName(), mUrl);
        Disposable subscribeCheckFavorite = checkFavoriteObserver.subscribe(checkCollectRespPayLoad -> {
            if (checkCollectRespPayLoad.getCode() == PayLoad.SUCCESS) {
                mFavoriteId = checkCollectRespPayLoad.getData().getFavorite_id();
                setCollectIcon(checkCollectRespPayLoad.getData().getIs_exist() == 1);
            } else {
                WidgetUtil.showSnackBar(ForumDetailActivity.this, checkCollectRespPayLoad.getMessage());
            }
        }, throwable -> WidgetUtil.showSnackBar(ForumDetailActivity.this, throwable.getMessage()));
    }

    private void setCollectIcon(boolean isChecked) {
        MenuItem item = toolbar.getMenu().findItem(R.id.collectItem);
        item.setChecked(isChecked);
        item.setIcon(isChecked ? R.mipmap.ic_collect_press : R.mipmap.ic_collect);
    }


    private void deleteCollect() {
        Observable<PayLoad<DeleteCollectResp>> collectObserver = HttpManager.getInstance().deleteCollect(mFavoriteId + "");
        Disposable subscribe = collectObserver.subscribe(deleteCollectRespPayLoad -> {
            if (deleteCollectRespPayLoad.getCode() == PayLoad.SUCCESS && deleteCollectRespPayLoad.getData().getSuccess() == 1) {
                WidgetUtil.showSnackBar(ForumDetailActivity.this, getString(R.string.cancel_success));
                setCollectIcon(false);
            } else {
                if (!TextUtils.isEmpty(deleteCollectRespPayLoad.getMessage())) {
                    WidgetUtil.showSnackBar(ForumDetailActivity.this, deleteCollectRespPayLoad.getMessage());
                }
                if (!TextUtils.isEmpty(deleteCollectRespPayLoad.getData().getMsg())) {
                    WidgetUtil.showSnackBar(ForumDetailActivity.this, deleteCollectRespPayLoad.getData().getMsg());
                }

            }
        }, throwable -> WidgetUtil.showSnackBar(ForumDetailActivity.this, throwable.getMessage()));
    }

    private void addCollect() {
        Observable<PayLoad<AddCollectResp>> collectObserver = HttpManager.getInstance().addCollect(mTitle, mUrl, GlobalDataManager.getInstance().getUserInfo().getUserName());
        Disposable subscribe = collectObserver.subscribe(addCollectRespPayLoad -> {
            if (addCollectRespPayLoad.getCode() == PayLoad.SUCCESS && addCollectRespPayLoad.getData().getSuccess() == 1) {
                WidgetUtil.showSnackBar(ForumDetailActivity.this, getString(R.string.collect_success));
                mFavoriteId = addCollectRespPayLoad.getData().getData().getId() + "";
                setCollectIcon(true);
            } else {
                if (!TextUtils.isEmpty(addCollectRespPayLoad.getMessage())) {
                    WidgetUtil.showSnackBar(ForumDetailActivity.this, addCollectRespPayLoad.getMessage());
                }
                if (!TextUtils.isEmpty(addCollectRespPayLoad.getData().getMsg())) {
                    WidgetUtil.showSnackBar(ForumDetailActivity.this, addCollectRespPayLoad.getData().getMsg());
                }
            }
        }, throwable -> WidgetUtil.showSnackBar(ForumDetailActivity.this, throwable.getMessage()));
    }


    public interface ReplyClickListener {
        void onClick(ForumDetailResp forumDetailResp);
    }

}
