package grl.com.subViews.edittext;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import grl.wangu.com.grl.R;

/**
 * Created by macdev001 on 6/27/16.
 */

public class OrderEditText extends EditText implements TextWatcher, View.OnFocusChangeListener{

    private  Context mContext;

    public final String PLACEHOLDER_TYPE = "贵人类型";
    public final String PLACEHOLDER_CONTENT = "需求内容";
    public final String PLUS_STRING = "  +\u2000";
    public final int PLUS_LENGTH = PLUS_STRING.length();

    public String orderType = "";
    public String orderCcontent = "";

    private Boolean isSet = false;
    private int lastDelete = 0;

    private String blockCharacterSet = "+";

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };


    public OrderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        addTextChangedListener(this);
        setOnFocusChangeListener(this);
        setOrderText(PLACEHOLDER_TYPE, PLACEHOLDER_CONTENT);
        setFilters(new InputFilter[]{filter});
        setImeOptions(EditorInfo.IME_ACTION_SEND);

    }

    public String getOrderCcontent() {
        return orderCcontent;
    }

    public void setOrderCcontent(String orderCcontent) {
        this.orderCcontent = orderCcontent;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    // Edit Text Listener
    public void afterTextChanged(Editable s) {
        // you can call or do what you want with your EditText here

    }
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (isSet)
            return;

        int plusPos = getText().toString().indexOf(PLUS_STRING);
        if (plusPos < 0)
            return;
        String type = getText().toString().substring(0, plusPos);
        String content = getText().toString().substring(plusPos + PLUS_LENGTH);

        if (start <= plusPos) {
            if (content.isEmpty())
                content = PLACEHOLDER_CONTENT;
            if (type.compareTo(PLACEHOLDER_TYPE) == 0) {
                setOrderText("", content);
                setSelection(0);
                orderType = "";
                lastDelete = -1;
                return;
            }
        }

        if (start >= plusPos + PLUS_LENGTH) {
            if (content.compareTo(PLACEHOLDER_CONTENT) == 0) {
                setOrderText(type, "");
                setSelection(getText().toString().length());
                orderCcontent = "";
            }
        }

    }
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int plusPos = getText().toString().indexOf(PLUS_STRING);
        if (plusPos < 0)
            return;

        orderType = getText().toString().substring(0, plusPos);
        orderCcontent = getText().toString().substring(plusPos + PLUS_LENGTH);

        if (orderType.compareTo(PLACEHOLDER_TYPE) == 0)
            orderType = "";
        if (orderCcontent.compareTo(PLACEHOLDER_CONTENT) == 0)
            orderCcontent = "";
//        setOrderV(orderType, orderCcontent);
    }



    public void onFocusChange(View v, boolean hasFocus) {
        int startSelection = getSelectionStart();
        int endSelection = getSelectionEnd();

        int plusPos = getText().toString().indexOf(PLUS_STRING);
        if (plusPos < 0)
            return;


        if (!hasFocus) {
            // Begin Editing

            if (startSelection > plusPos && startSelection < plusPos + PLUS_STRING.length())
                setSelection(plusPos);

//            if (startSelection >= plusPos + PLUS_STRING.length()) {
//                String content = getText().toString().substring(plusPos + PLUS_LENGTH);
//                if (content.compareTo(PLACEHOLDER_CONTENT) == 0) {
//                    orderCcontent = content = "";
//                    String type = getText().toString().substring(0, plusPos);
//                    setOrderText(type, PLACEHOLDER_CONTENT);
//                    setSelection(plusPos + PLUS_LENGTH);
//                    isSet = false;
//                    return;
//                }
//            }
//
//            if (startSelection <= plusPos) {
//                String type =  getText().toString().substring(0, plusPos);
//                if (type.compareTo(PLACEHOLDER_TYPE) == 0) {
//                    String content = getText().toString().substring(plusPos + PLUS_LENGTH);
//                    setOrderText(PLACEHOLDER_TYPE, content);
//                    setSelection(0);
//                    isSet = false;
//                    return;
//                }
//            }

        }
        // End Editing
        String type = orderType;
        if (type.isEmpty())
            type = PLACEHOLDER_TYPE;
        String content = orderCcontent;
        if (content.isEmpty())
            content = PLACEHOLDER_CONTENT;
        setOrderText(type, content);
    }

    protected void onSelectionChanged(int selStart, int selEnd){
        Log.e("EditText", "EditText Selection Change");
        if (isSet == null || isSet == true) {
            isSet = false;
            return;
        }

        int startSelection = getSelectionStart();
        int endSelection = getSelectionEnd();
        int selectedLength = endSelection - startSelection;

        if (selectedLength == 0) {
            if (lastDelete != startSelection)
                lastDelete = -1;
        } else {

        }
        int plusPos = getText().toString().indexOf(PLUS_STRING);
        if (plusPos < 0)
            return;

        if (startSelection > plusPos && startSelection < plusPos + PLUS_LENGTH)
            setSelection(plusPos, 0);

        if (startSelection >= plusPos + PLUS_LENGTH) {
            String content = getText().toString().substring(plusPos+PLUS_LENGTH);
            if (plusPos == 0 || content.compareTo(PLACEHOLDER_CONTENT) == 0) {
                if (plusPos == 0) {
                    // Type 문자렬 PlaceHolder
                    if (content.compareTo(PLACEHOLDER_CONTENT) == 0) {
                        // Content == PLACEHOLDER_CONTENT 인 경우 빈 문자렬로
                        setOrderText(PLACEHOLDER_TYPE, "");
                        setSelection(PLACEHOLDER_TYPE.length() + PLUS_LENGTH);
                    } else {
                        setOrderText(PLACEHOLDER_TYPE, PLACEHOLDER_CONTENT);
                        setSelection(startSelection + PLACEHOLDER_TYPE.length());
                    }
                    isSet = false;
                    return;
                }
                if (content.compareTo(PLACEHOLDER_CONTENT) == 0) {
                    content = "";
                    String type = getText().toString().substring(0, plusPos);
                    setOrderText(type, content);
                    setSelection(plusPos + PLUS_LENGTH);
                    isSet = false;
                    return;
                }
            }
        }

        if (startSelection <= plusPos) {
            String type = getText().toString().substring(0, plusPos);
            if (type.compareTo(PLACEHOLDER_TYPE) == 0 || getText().toString().length() == plusPos + PLUS_LENGTH) {
                if (getText().toString().length() == plusPos + PLUS_LENGTH) {
                    if (type.compareTo(PLACEHOLDER_TYPE) == 0) {
                        setOrderText("", PLACEHOLDER_CONTENT);
                        setSelection(0);
                    } else {
                        setOrderText(type, PLACEHOLDER_CONTENT);
                        setSelection(startSelection);
                    }
                    isSet = false;
                    return;
                }
                if (type.compareTo(PLACEHOLDER_TYPE) == 0) {
                    type = "";
                    String content = getText().toString().substring(plusPos + PLUS_LENGTH);
                    setOrderText(type, content);
                    setSelection(0);
                    isSet = false;
                    return;
                }
            }

        }

    }

    public void setOrderValue(String type, String content) {
        if (type == null || type.isEmpty())
            type = PLACEHOLDER_TYPE;
        if (content == null || content.isEmpty())
            content = PLACEHOLDER_CONTENT;
        setOrderText(type, content);
    }

    private void setOrderText(String type, String content) {

        isSet = true;

        String text = String.format("%s%s%s", type, PLUS_STRING, content);

        int typePos = text.indexOf(PLACEHOLDER_TYPE);
        int plusPos = text.indexOf(PLUS_STRING);
        int contentPos = text.indexOf(PLACEHOLDER_CONTENT);

        if (plusPos < 0)
            return;

        final SpannableStringBuilder sb = new SpannableStringBuilder(text);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(mContext.getResources().getColor(R.color.place_holder_color));

        if (typePos >= 0 && contentPos >= 0) {
            sb.setSpan(fcs, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (typePos >= 0 && contentPos < 0) {
            sb.setSpan(fcs, 0, plusPos + PLUS_STRING.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (typePos < 0 && contentPos >= 0) {
            sb.setSpan(fcs, plusPos, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (typePos < 0 && contentPos < 0) {
            sb.setSpan(fcs, plusPos, plusPos + PLUS_STRING.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


//        if (typePos >= 0 ||)

//        if (typePos >= 0) {
//
////            SpannableString typeSpannable= new SpannableString(text);
//            SpannableString typeSpannable= new SpannableString(PLACEHOLDER_TYPE);
//            typeSpannable.setSpan(fcs, 0, PLACEHOLDER_TYPE.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            sb.append(typeSpannable);
//        } else {
//            sb.append(text.substring(0, plusPos));
//        }
//
//        if (plusPos >= 0) {
//
////            SpannableString plusSpannable= new SpannableString(text);
//            SpannableString plusSpannable= new SpannableString(PLUS_STRING);
//            plusSpannable.setSpan(fcs, 0, PLUS_STRING.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            sb.append(plusSpannable);
//        }
//        if (contentPos >= 0) {
////            SpannableString contentSpannable= new SpannableString(text);
//            SpannableString contentSpannable= new SpannableString(PLACEHOLDER_CONTENT);
//            contentSpannable.setSpan(fcs, 0, PLACEHOLDER_CONTENT.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            sb.append(contentSpannable);
//        } else {
//            sb.append(text.substring(plusPos + PLUS_LENGTH));
//        }

        setText(sb);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode==KeyEvent.KEYCODE_ENTER)
        {
            return true;
        }
        if (keyCode==KeyEvent.KEYCODE_DEL) {
            int startSelection = getSelectionStart();
            int endSelection = getSelectionEnd();
            int selectedLength = endSelection - startSelection;
            int plusPos = getText().toString().indexOf(PLUS_STRING);

            String type = getText().toString().substring(0, plusPos);
            String content = getText().toString().substring(plusPos + PLUS_LENGTH);

            if (orderCcontent.length() == 0 && startSelection > plusPos + PLUS_LENGTH) {
                setOrderText(type, "");
                setSelection(getText().toString().length());
                return true;
            }
            if (orderType.length() == 0 && startSelection <= plusPos) {
                setOrderText("", content);
                setSelection(0);
                return true;
            }
            if (startSelection > plusPos && startSelection <= plusPos + PLUS_LENGTH) {
//                setSelection(plusPos);
                return true;
            }
        }

        // Handle all other keys in the default way
        return super.onKeyDown(keyCode, event);
    }
}
