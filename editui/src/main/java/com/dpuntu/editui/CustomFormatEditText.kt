package com.dpuntu.editui

import android.content.Context
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import java.lang.IllegalStateException

/**
 * Created  by fangmingxing on 2018/7/13.
 */
class CustomFormatEditText : android.support.v7.widget.AppCompatEditText, TextWatcher {

    // 以下各个自断的含义请参考 UtilEditTextFilter 类
    private var mCurrentInputType: Int = -1
    private var mCurrentInputFilter: UtilEditTextFilter.AbsInputFilter? = null
    private var mInputLength: Int = 0
    private var mIsOnlyMobilePhone: Boolean = true
    private var mIsSupportDelimit: Boolean = false
    private var mDelimitString: String = UtilEditTextFilter.DELIMIT
    private var mIsSupportFormatter: Boolean = false
    private var mFormatterString: String = UtilEditTextFilter.EMPTY
    private var mIsSupportDecimal: Boolean = true
    private var mIsSupportFirstZero: Boolean = false
    private var mMaxIntegerLength: Int = 7
    private var mMaxDecimalLength: Int = 2
    private var mMoneyFormatterString: String = UtilEditTextFilter.MONEY_PRE
    private var mGroupInputType: Int = -1
    private var mSupportGroupSize: Int = 4
    private var mGroupItemLength: Int = 4
    private var mGroupItemString: String = UtilEditTextFilter.EMPTY
    private var mMaxPointLength: Int = 9
    private var mPointItemLength = 3
    private var mPointFormatterString: String = UtilEditTextFilter.COMMA

    private var mOnEditTextChange: (String) -> Unit = {}

    constructor(context: Context) : super(context) {
        initFormatEditText(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initFormatEditText(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initFormatEditText(context, attrs, defStyleAttr)
    }

    private fun initFormatEditText(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomFormatEditText, defStyleAttr, 0)
        if (a != null) {
            mCurrentInputType = a.getInt(R.styleable.CustomFormatEditText_input_type, mCurrentInputType)
            mInputLength = a.getInt(R.styleable.CustomFormatEditText_input_length, mInputLength)
            mIsOnlyMobilePhone = a.getBoolean(R.styleable.CustomFormatEditText_input_phone_mobile_only, mIsOnlyMobilePhone)
            mIsSupportDelimit = a.getBoolean(R.styleable.CustomFormatEditText_input_phone_delimit_support, mIsSupportDelimit)
            mDelimitString = a.getString(R.styleable.CustomFormatEditText_input_phone_delimit_string) ?: mDelimitString
            mIsSupportFormatter = a.getBoolean(R.styleable.CustomFormatEditText_input_phone_format_support, mIsSupportFormatter)
            mFormatterString = a.getString(R.styleable.CustomFormatEditText_input_phone_format_string) ?: mFormatterString
            mIsSupportDecimal = a.getBoolean(R.styleable.CustomFormatEditText_input_number_support_decimal, mIsSupportDecimal)
            mIsSupportFirstZero = a.getBoolean(R.styleable.CustomFormatEditText_input_number_support_first_zero, mIsSupportFirstZero)
            mMaxIntegerLength = a.getInt(R.styleable.CustomFormatEditText_input_number_integer_length, mMaxIntegerLength)
            mMaxDecimalLength = a.getInt(R.styleable.CustomFormatEditText_input_number_decimal_length, mMaxDecimalLength)
            mGroupInputType = a.getInt(R.styleable.CustomFormatEditText_input_group_type, mGroupInputType)
            mSupportGroupSize = a.getInt(R.styleable.CustomFormatEditText_input_group_item_size, mSupportGroupSize)
            mGroupItemLength = a.getInt(R.styleable.CustomFormatEditText_input_group_item_length, mGroupItemLength)
            mGroupItemString = a.getString(R.styleable.CustomFormatEditText_input_group_format_string) ?: mGroupItemString
            mMaxPointLength = a.getInt(R.styleable.CustomFormatEditText_input_point_length, mMaxPointLength)
            mPointItemLength = a.getInt(R.styleable.CustomFormatEditText_input_point_item_length, mPointItemLength)
            mPointFormatterString = a.getString(R.styleable.CustomFormatEditText_input_point_delimit) ?: mPointFormatterString
            a.recycle()
        }
        setRawInputType(Configuration.KEYBOARD_QWERTY) // 默认键盘为数字类型，但是可以输入其他任意符号或字母
        checkArguments()
        setOnClickListener { setSelection(text.length) }
        addTextChangedListener(this)
        isLongClickable = false
        setTextIsSelectable(false)
        addInputType(mCurrentInputType)
    }

    private fun checkArguments() {
        when (mCurrentInputType) {
            INPUT_PHONE -> {
                if (mDelimitString.length != 1) throw IllegalArgumentException("DelimitString can only one")
                if (mFormatterString.length != 1) throw IllegalArgumentException("FormatterString can only one")
            }
            INPUT_GROUP -> {
                if (mGroupItemString.length != 1) throw IllegalArgumentException("GroupItemString can only one")
            }
            INPUT_MONEY -> {
            }
            INPUT_POINT -> {
                if (mPointFormatterString.length != 1) throw IllegalArgumentException("PointFormatterString can only one")
            }
            INPUT_INTEGER -> {
            }
            INPUT_DECIMAL -> {
            }
            else -> {
            }
        }
    }

    private fun addInputType(inputType: Int) {
        mCurrentInputType = inputType
        filters = arrayOf()
        when (mCurrentInputType) {
            INPUT_PHONE -> addInputFilter(UtilEditTextFilter.PhoneFilter(mIsOnlyMobilePhone, mIsSupportDelimit, mDelimitString, mIsSupportFormatter, mFormatterString), true)
            INPUT_GROUP -> addInputFilter(UtilEditTextFilter.GroupFilter(mGroupInputType, mSupportGroupSize, mGroupItemLength, mGroupItemString), true)
            INPUT_MONEY -> addInputFilter(UtilEditTextFilter.MoneyFilter(mIsSupportDecimal, mMaxIntegerLength, mMaxDecimalLength), true)
            INPUT_POINT -> addInputFilter(UtilEditTextFilter.PointFilter(mMaxPointLength, mPointItemLength, mPointFormatterString), true)
            INPUT_INTEGER -> addInputFilter(UtilEditTextFilter.IntegerFilter(mMaxIntegerLength, mIsSupportFirstZero), true)
            INPUT_DECIMAL -> addInputFilter(UtilEditTextFilter.DecimalFilter(mMaxIntegerLength, mMaxDecimalLength), true)
            else -> {
            }
        }
    }

    private fun addInputFilter(inputFilter: UtilEditTextFilter.AbsInputFilter, isFromInit: Boolean) {
        filters = arrayOf()
        mCurrentInputType = inputFilter.inputType()
        mCurrentInputFilter = inputFilter
        filters += mCurrentInputFilter
        if (isFromInit) return
        findArgumentsFromFilter(inputFilter)
        checkArguments()
    }

    /**
     * 强制外部使用不可选参 isFromInit
     * */
    fun addInputFilter(inputFilter: UtilEditTextFilter.AbsInputFilter) {
        addInputFilter(inputFilter, false)
    }

    fun currentInputType() = mCurrentInputType

    private fun findArgumentsFromFilter(inputFilter: UtilEditTextFilter.AbsInputFilter) {
        when (mCurrentInputType) {
            INPUT_PHONE -> {
                if (inputFilter is UtilEditTextFilter.PhoneFilter) {
                    mIsOnlyMobilePhone = inputFilter.isOnlyMobilePhone()
                    mIsSupportDelimit = inputFilter.isSupportDelimit()
                    mDelimitString = inputFilter.delimitString()
                    mIsSupportFormatter = inputFilter.isSupportFormatter()
                    mFormatterString = inputFilter.formatterString()
                }
            }
            INPUT_GROUP -> {
                if (inputFilter is UtilEditTextFilter.GroupFilter) {
                    mSupportGroupSize = inputFilter.supportGroupSize()
                    mGroupItemLength = inputFilter.groupItemLength()
                    mGroupItemString = inputFilter.groupItemString()
                }
            }
            INPUT_MONEY -> {
                if (inputFilter is UtilEditTextFilter.MoneyFilter) {
                    mIsSupportDecimal = inputFilter.isSupportDecimal()
                    mMaxIntegerLength = inputFilter.maxIntegerLength()
                    mMaxDecimalLength = inputFilter.maxDecimalLength()
                }
            }
            INPUT_POINT -> {
                if (inputFilter is UtilEditTextFilter.PointFilter) {
                    mMaxPointLength = inputFilter.maxLength()
                    mPointFormatterString = inputFilter.formatterString()
                    mPointItemLength = inputFilter.pointItemLength()
                }
            }
            INPUT_INTEGER -> {
                if (inputFilter is UtilEditTextFilter.IntegerFilter) {
                    mMaxIntegerLength = inputFilter.maxIntegerLength()
                    mIsSupportFirstZero = inputFilter.isSupportFirstZero()
                }
            }
            INPUT_DECIMAL -> {
                if (inputFilter is UtilEditTextFilter.DecimalFilter) {
                    mMaxIntegerLength = inputFilter.maxIntegerLength()
                    mMaxDecimalLength = inputFilter.maxDecimalLength()
                }
            }
            else -> {
            }
        }
    }

    fun unFormatterString(): String {
        if (mCurrentInputFilter != null && mCurrentInputType != mCurrentInputFilter?.inputType()) throw IllegalStateException("change InputType in InputFilter")
        val mCurrentInputText = text.toString()
        return when (mCurrentInputType) {
            INPUT_PHONE -> if (mIsOnlyMobilePhone && mIsSupportFormatter) mCurrentInputText.replace(mFormatterString, "") else mCurrentInputText
            INPUT_GROUP -> mCurrentInputText.replace(mGroupItemString, "")
            INPUT_MONEY -> mCurrentInputText.replace(mMoneyFormatterString, "")
            INPUT_POINT -> mCurrentInputText.replace(mPointFormatterString, "")
            INPUT_INTEGER -> mCurrentInputText
            INPUT_DECIMAL -> mCurrentInputText
            else -> mCurrentInputText
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.d(TAG, "TextWatcher-before , CharSequence" + s.toString() + "||start" + start + "||count" + count + "||after" + after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d(TAG, "TextWatcher-on , CharSequence" + s.toString() + "||start" + start + "||before" + before + "||count" + count)
    }

    override fun afterTextChanged(s: Editable) {
        Log.d(TAG, "TextWatcher-after , Editable = $s , mPointFormatterString = $mPointFormatterString")
        if (this.hasFocus()) mOnEditTextChange(unFormatterString())
        if (mCurrentInputType == INPUT_POINT) {
            val pointText = unFormatterString()
            if (pointText.length <= 3) {
                if (s.toString() == pointText) return else setText(pointText)
            }
            val charFormat = mPointFormatterString.toCharArray()[0]
            val reversedPointList = pointText.reversed().toMutableList()
            val pointArraySize = reversedPointList.size / mPointItemLength
            val hasLastPoint = reversedPointList.size % mPointItemLength == 0
            for (index in 0 until pointArraySize) {
                if (hasLastPoint && index == pointArraySize - 1) continue
                reversedPointList.add((index + 1) * mPointItemLength + index, charFormat)
            }
            val targetPointString = String(reversedPointList.toCharArray()).reversed()
            if (s.toString() == targetPointString) return else setText(targetPointString)
            setSelection(text.length)
        }
        if (mCurrentInputType == INPUT_INTEGER) {
            val inputText = text.toString()
            var targetString = inputText
            if (inputText.startsWith(UtilEditTextFilter.ZERO) && inputText.length > 1)
                targetString = inputText.substring(1)
            if (s.toString() == targetString) return else setText(targetString)
            setSelection(text.length)
        }
    }

    // 禁止光标滑动
    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selEnd != text.length) setSelection(text.length)
    }

    // 输入的字符变化回调格式化后的输入
    fun setOnEditTextChange(onEditTextChange: (String) -> Unit) {
        mOnEditTextChange = onEditTextChange
    }

    companion object {

        private val TAG = this.javaClass.simpleName

        const val INPUT_PHONE = 0
        const val INPUT_GROUP = 1
        const val INPUT_MONEY = 2
        const val INPUT_POINT = 3
        const val INPUT_INTEGER = 4
        const val INPUT_DECIMAL = 5

        const val GROUP_NUMBER = 10
        const val GROUP_LETTER = 11
        const val GROUP_ALL = 12
    }
}