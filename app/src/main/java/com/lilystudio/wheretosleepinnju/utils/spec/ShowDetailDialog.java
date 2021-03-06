package com.lilystudio.wheretosleepinnju.utils.spec;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lilystudio.wheretosleepinnju.R;
import com.lilystudio.wheretosleepinnju.add.AddActivity;
import com.lilystudio.wheretosleepinnju.app.Constant;
import com.lilystudio.wheretosleepinnju.data.bean.Course;
import com.lilystudio.wheretosleepinnju.utils.LogUtil;

/**
 * 显示详细信息
 * Created by mnnyang on 17-11-7.
 */

public class ShowDetailDialog {

    private PopupWindow mPopupWindow;

    /**
     * @param activity
     * @param course          时间信息必须完整
     * @param dismissListener
     */
    public void show(final Activity activity, final Course course,
                     final PopupWindow.OnDismissListener dismissListener) {
        if (null == course) {
            LogUtil.e(this, "show()--> course is null");
            return;
        }

        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        activity.getWindow().setAttributes(lp);

        mPopupWindow = new PopupWindow(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final View popupView = LayoutInflater.from(activity).inflate(R.layout.dialog_detail_course,
                null);

        TextView tvTitle = popupView.findViewById(R.id.tv_title);
        TextView tvClassroom = popupView.findViewById(R.id.tv_calssroom);
        TextView tvTeacher = popupView.findViewById(R.id.tv_teacher);
        TextView tvNode = popupView.findViewById(R.id.tv_node);
        TextView tvWeekRange = popupView.findViewById(R.id.tv_week_range);

        StringBuilder nodeInfo = getNodeInfo(course);
        tvNode.setText(nodeInfo);

        tvTitle.setText(course.getName());
        tvClassroom.setText(adaptClassroomStr(course.getClassRoom()));
        tvTeacher.setText(course.getTeacher());

        tvWeekRange.setText(course.getStartWeek() + "-" + course.getEndWeek() + "周");

        View close = popupView.findViewById(R.id.iv_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        View edit = popupView.findViewById(R.id.iv_eidt);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddActivity.class);
                intent.putExtra(Constant.INTENT_COURSE, course);
                activity.startActivity(intent);
                dismiss();
            }
        });

        initWindow(activity, dismissListener, popupView);
    }

    private void initWindow(final Activity activity, final PopupWindow.OnDismissListener dismissListener, View popupView) {
        mPopupWindow.setContentView(popupView);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setClippingEnabled(true);
        mPopupWindow.setAnimationStyle(R.style.animZoomIn);

        mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                activity.getWindow().setAttributes(lp);
                dismissListener.onDismiss();
            }
        });
    }

    @NonNull
    private StringBuilder getNodeInfo(Course course) {
        StringBuilder nodeInfo = new StringBuilder();
        if (course.getNodes().size() != 0) {
            nodeInfo = new StringBuilder(String.valueOf(course.getNodes().get(0)));
        }
        for (int i = 1; i < course.getNodes().size(); i++) {
            nodeInfo.append("-").append(course.getNodes().get(i));
        }
        nodeInfo.append("节");
        return nodeInfo;
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private String adaptClassroomStr(String oriClassroom){
        int len=0;
        String halfWidth="[\u0020-\u007e]";//半角字符为ASCII码32-126的字符
        StringBuffer sb=new StringBuffer(oriClassroom);
        for(int i=0;i<sb.length();i++){
            if(!sb.substring(i,i+1).matches(halfWidth))
                len+=2;
            else
                len++;
            if(len>20)
                return sb.substring(0,i-2)+"...";
        }
        return oriClassroom;
    }
}