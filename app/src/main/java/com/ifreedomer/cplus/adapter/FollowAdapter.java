package com.ifreedomer.cplus.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ifreedomer.cplus.R;
import com.ifreedomer.cplus.http.center.HttpManager;
import com.ifreedomer.cplus.http.protocol.PayLoad;
import com.ifreedomer.cplus.http.protocol.resp.FollowOperationResp;
import com.ifreedomer.cplus.http.protocol.resp.FollowResp;
import com.ifreedomer.cplus.manager.GlobalDataManager;
import com.ifreedomer.cplus.util.WidgetUtil;

import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.functions.Consumer;

public class FollowAdapter extends BaseQuickAdapter<FollowResp, BaseViewHolder> {
    public static final int IDOL_TYPE = 1;
    public static final int FANS_TYPE = 2;
    private int mType = IDOL_TYPE;

    public FollowAdapter(int type, int layoutResId, @Nullable List<FollowResp> data) {
        super(layoutResId, data);
        mType = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, FollowResp item) {
        helper.setText(R.id.titleTv, item.getNickname());
        helper.setText(R.id.summaryTv, item.getDescription());
        Glide.with((View) helper.getView(R.id.titleTv)).load(item.getAvatar()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into((ImageView) helper.getView(R.id.avatarIv));
        if (item.getIsFocus() == FollowResp.FOLLOW_EACH_OTHER) { //互相关注
            helper.setText(R.id.operationBtn, mContext.getString(R.string.followed));
        } else if (item.getIsFocus() == FollowResp.BEFOLLOW) { //关注我的
            helper.setText(R.id.operationBtn, mContext.getString(R.string.follow));
        } else if (item.getIsFocus() == FollowResp.FOLLOW) { //我关注的
            helper.setText(R.id.operationBtn, mContext.getString(R.string.followed));
        } else {
            helper.setText(R.id.operationBtn, mContext.getString(R.string.follow));
        }
        helper.setVisible(R.id.operationBtn, item.getUsername().equals(GlobalDataManager.getInstance().getUserInfo().getUserName()));
        helper.setOnClickListener(R.id.operationBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getIsFocus() == FollowResp.FOLLOW || item.getIsFocus() == FollowResp.FOLLOW_EACH_OTHER) {
                    unFollowIdol(item);
                } else {
                    follow(item);
                }
            }
        });


    }

    private void follow(FollowResp item) {
        HttpManager.getInstance().follow(item.getUsername()).subscribe(new Consumer<PayLoad<FollowOperationResp>>() {
            @Override
            public void accept(PayLoad<FollowOperationResp> followOperationRespPayLoad) throws Exception {
                if (followOperationRespPayLoad.getCode() == PayLoad.SUCCESS) {
                    boolean isSuccess = followOperationRespPayLoad.getData().getSucc() == FollowOperationResp.SUCCESS;
                    int focus = item.getIsFocus() == FollowResp.FOLLOW ? FollowResp.NOT_RELATION : FollowResp.FOLLOW;
                    item.setIsFocus(isSuccess ? focus : item.getIsFocus());
                    notifyDataSetChanged();
                    WidgetUtil.showSnackBar((Activity) mContext, followOperationRespPayLoad.getData().getMsg());
                } else {
                    WidgetUtil.showSnackBar((Activity) mContext, followOperationRespPayLoad.getMessage());
                }
            }
        }, throwable -> WidgetUtil.showSnackBar((Activity) mContext, throwable.getMessage()));
    }

    private void unFollowIdol(FollowResp item) {
        HttpManager.getInstance().unfollow(item.getUsername()).subscribe(new Consumer<PayLoad<FollowOperationResp>>() {
            @Override
            public void accept(PayLoad<FollowOperationResp> followOperationRespPayLoad) throws Exception {
                if (followOperationRespPayLoad.getCode() == PayLoad.SUCCESS) {
                    boolean isSuccess = followOperationRespPayLoad.getData().getSucc() == FollowOperationResp.SUCCESS;
                    int focus = item.getIsFocus() == FollowResp.FOLLOW||item.getIsFocus()==FollowResp.FOLLOW_EACH_OTHER ? FollowResp.NOT_RELATION : FollowResp.FOLLOW;
                    item.setIsFocus(isSuccess ? focus : item.getIsFocus());
                    notifyDataSetChanged();
                    WidgetUtil.showSnackBar((Activity) mContext, followOperationRespPayLoad.getData().getMsg());
                } else {
                    WidgetUtil.showSnackBar((Activity) mContext, followOperationRespPayLoad.getMessage());
                }
            }
        }, throwable -> WidgetUtil.showSnackBar((Activity) mContext, throwable.getMessage()));
    }


}
