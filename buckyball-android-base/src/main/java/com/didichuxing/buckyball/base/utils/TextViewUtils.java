package com.didichuxing.buckyball.base.utils;

import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 * @Desc
 * @date 18/1/24 11:28
 * @Email 574613441@qq.com
 */
public class TextViewUtils {
    /**
     * 修改字体颜色
     *
     * @param tv
     * @param color                    如Color.BLUE，不是资源ID
     * @param fromIndex                从0开始
     * @param endIndex                 结束的索引 endIndex所在的位置，颜色不变
     * @param spanExclusiveOrInclusive 如：Spannable.SPAN_EXCLUSIVE_EXCLUSIVE EXCLUSIVE表示新输入的字符不保持这种样式，INCLUSIVE表示新输入的字符保持这种样式
     */
    public static void changeColor(TextView tv, int color, int fromIndex, int endIndex, int spanExclusiveOrInclusive) {
        SpannableString spanText = new SpannableString(tv.getText());
        if (spanText.length() < endIndex)
            return;
        spanText.setSpan(new ForegroundColorSpan(color), fromIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spanText);
    }

    /**
     * @param tv                       要改变字体颜色的 TextView
     * @param color                    如Color.BLUE，不是资源ID
     * @param fromIndex                开始索引的数组
     * @param endIndex                 结束数组的索引 改变颜色时不包含
     * @param spanExclusiveOrInclusive Spannable.SPAN_EXCLUSIVE_EXCLUSIVE 此字段主要EditText 时起作用
     */
    public static void changeColor(TextView tv, int[] color, int[] fromIndex, int[] endIndex, int spanExclusiveOrInclusive) {
        SpannableString spanText = new SpannableString(tv.getText());
        for (int i = 0; i < fromIndex.length; i++) {
            if (spanText.length() < endIndex[i])
                return;
            spanText.setSpan(new ForegroundColorSpan(color[i]), fromIndex[i], endIndex[i], spanExclusiveOrInclusive);
        }
        tv.setText(spanText);
    }

    /**
     * @param tv       要部分改变颜色的TextView
     * @param text     原始文字
     * @param startTag 改变颜色的起始标志,如 {  长度只能为1
     * @param endTag   改变颜色的结束标志,如 } ,与startTag必须成对出现  长度只能为1
     * @param colors   value不是R.color.xxxcolor 这样的资源id,是Color.BLUE这样的值 要变成的颜色
     *                 colorMap.get(0)不能为null,默认最少含有一个key=0的颜色值，否则不起作用
     */
    public static void changeColor(TextView tv, String text, String startTag, String endTag, int... colors) {
        //改变颜色开始的索引
        List<Integer> startIndexList = new ArrayList<>();
        //改变颜色结尾的索引
        List<Integer> endIndexList = new ArrayList<>();
        //开始检索的索引
        int startIndex = 0;

        //真实改变颜色的索引
        int realIndex;
        int num = 0;
        while ((startIndex = text.indexOf(startTag, startIndex)) > -1) {
            realIndex = startIndex - (num * 2);
            startIndexList.add(realIndex);
            startIndex = text.indexOf(endTag, startIndex + 1);
            realIndex = startIndex - (num * 2 + 1);
            endIndexList.add(realIndex);
            num++;
        }
        text = text.replace(startTag, "").replace(endTag, "");
        tv.setText(text);
        if (colors == null || colors.length == 0)
            return;
        if (startIndexList.size() > 0 && startIndexList.size() == endIndexList.size()) {
            int len = startIndexList.size();
            int[] colorArr = new int[len];
            int[] startArr = new int[len];
            int[] endArr = new int[len];
            for (int i = 0; i < len; i++) {
                colorArr[i] = colors.length - 1 < i ? colors[0] : colors[i];
                startArr[i] = startIndexList.get(i);
                endArr[i] = endIndexList.get(i);
            }
            changeColor(tv, colorArr, startArr, endArr, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 自动缩放字体
     * 只支持汉字
     *
     * @param textViewWidth   textView的宽度，必须是固定值
     * @param text            要设置的内容
     * @param maxLine         最大行数
     * @param defaultTextSize 默认字体大小 单位px
     * @return
     */
    public static float caculateTextSize(int textViewWidth, String text, int maxLine, float defaultTextSize) {
        //第一步，计算text在默认字体大小的时候，需要几行

        int chineseCharCount = 0;
        for (int i = 0; i < text.length(); i++) {
            if (isChinesePunctuation(text.charAt(i))) {
                chineseCharCount++;
            }
        }
        int len = text.length() + chineseCharCount;
        //文字需要的最大宽度 px
        float maxWidth = defaultTextSize * len;
        int needLines = (int) Math.ceil(maxWidth / textViewWidth);
        //说明最大行数能满足显示内容，而不需要缩放字
        if (needLines <= maxLine) {
            return defaultTextSize;
        }
        //容器最大宽度
        int containerMaxWidth = textViewWidth * maxLine;
        float size = containerMaxWidth / len;
        return size > defaultTextSize ? defaultTextSize : size;
    }

    // 根据UnicodeBlock方法判断中文标点符号
    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                    || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
                return true;
            } else {
                return false;
            }
        } else {
            if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 根据开始标识和结束标识缩放文字到指定长度
     * 如 我是中<[国人]>民 缩放到
     *
     * @param txt
     * @param startTag
     * @param endTag
     * @param targetLen 目标总字数（...算一个字）
     * @return
     */
    public static String ellipsizeTxt(String txt, String startTag, String endTag, int targetLen) {
        if (txt.length() <= targetLen || targetLen < 0)
            return txt.replace(startTag, "").replace(endTag, "");
        try {
            int startTagLen = startTag.length();
            int endTagLen = endTag.length();
            int startTagIndex = txt.indexOf(startTag);
            int endTagIndex = txt.indexOf(endTag);
            if (startTagIndex < 0 || endTagIndex < 0) {
                return txt.replace(startTag, "").replace(endTag, "");
            }
            int totalLen = txt.length();
            //除了缩放区的内容长度
            int otherLen = totalLen - (endTagIndex + endTagLen - startTagIndex);

            //缩放区的内容可以占用的长度，-1的目的是去掉...所占的1个汉字的长度
            int ellipTxtLen = targetLen - otherLen - 1;
            if (ellipTxtLen <= 0) {
                return txt.substring(0, targetLen - 1) + "...";
            }
            String left = txt.substring(0, startTagIndex);
            String middle = txt.substring(startTagIndex + startTagLen, endTagIndex);
            String right = txt.substring(endTagIndex + endTagLen);

            StringBuilder sb = new StringBuilder(left);
            if (middle.length() > ellipTxtLen) {
                middle = middle.substring(0, ellipTxtLen);
            }
            sb.append(middle);
            sb.append("...");
            sb.append(right);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return txt;
        }
    }
}
