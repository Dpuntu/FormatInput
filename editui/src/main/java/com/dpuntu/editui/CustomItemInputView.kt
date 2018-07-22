package com.dpuntu.editui


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * Created  by fangmingxing on 2018/7/4.
 */
class CustomItemInputView : android.support.v7.widget.AppCompatEditText {

    private var mNormalBgPaint: Paint? = null
    private var mPressBgPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mRealWidth = 0
    private var mRealHeight = 0
    private var mCurrentInputText = ""

    // editText显示的默认颜色
    private var mNormalBgColor = DEFAULT_NORMAL_COLOR
    // editText有文字时候的颜色
    private var mPressBgColor = DEFAULT_PRESS_COLOR
    // editText显示的文字颜色
    private var mTextColor = DEFAULT_TEXT_COLOR
    // editText显示的方框或下划线的宽度
    private var mBgLineWidth = DEFAULT_LINE_WIDTH
    // editText显示的文字大小
    private var mTextSize = DEFAULT_TEXT_SIZE
    // editText显示的方式为方框时，其圆角大小
    private var mRoundRadius = DEFAULT_ROUND_RADIUS
    // editText各个字符的大小
    private var mItemSize = DEFAULT_SIZE
    // editText各个字符之间的间距
    private var mItemSpaceSize = DEFAULT_SPACE_SIZE
    // 输入类型
    private var mInputType = PIN_KEY
    // 可输入的最大长度，即为显示的可输入框的个数
    private var mMaxLength = MAX_LENGTH
    // 显示的位置
    private var mGravity = GRAVITY_CENTER
    // 输入时，editText显示的方式
    private var mInputStyle = LINE_INPUT
    // 当选择密钥时，显示的字符
    private var mPwdInputText: String? = PWD_INPUT_TEXT

    private var mRectF = RectF()

    // 输入完成后回调接口
    var onInputComplete: (String) -> Unit = { _ -> }

    constructor(context: Context) : super(context) {
        initItemInputView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initItemInputView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initItemInputView(context, attrs, defStyleAttr)
    }

    private fun initItemInputView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomItemInputView, defStyleAttr, 0)
        if (a != null) {
            mInputType = a.getInt(R.styleable.CustomItemInputView_item_input_type, mInputType)
            mGravity = a.getInt(R.styleable.CustomItemInputView_item_gravity, mGravity)
            mInputStyle = a.getInt(R.styleable.CustomItemInputView_item_style, mInputStyle)
            mMaxLength = a.getInt(R.styleable.CustomItemInputView_item_max_length, mMaxLength)
            mItemSpaceSize = a.getDimensionPixelSize(R.styleable.CustomItemInputView_item_space_size, mItemSpaceSize)
            mItemSize = a.getDimensionPixelSize(R.styleable.CustomItemInputView_item_size, mItemSize)
            mBgLineWidth = a.getDimensionPixelSize(R.styleable.CustomItemInputView_item_line_width, mBgLineWidth)
            mRoundRadius = a.getDimensionPixelSize(R.styleable.CustomItemInputView_item_round_radius, mRoundRadius)
            mTextColor = a.getColor(R.styleable.CustomItemInputView_item_text_color, mTextColor)
            mTextSize = a.getDimensionPixelSize(R.styleable.CustomItemInputView_item_text_size, mTextSize)
            mNormalBgColor = a.getColor(R.styleable.CustomItemInputView_item_normal_color, mNormalBgColor)
            mPressBgColor = a.getColor(R.styleable.CustomItemInputView_item_press_color, mPressBgColor)
            mPwdInputText = a.getString(R.styleable.CustomItemInputView_item_pwd_text)
            a.recycle()
        }
        initEditText()
        calculateRealSize()
        initPaints()
    }

    private fun calculateRealSize() {
        mRealWidth = (mItemSize + mItemSpaceSize) * mMaxLength - mItemSpaceSize + mBgLineWidth * mMaxLength * 2
        mRealHeight = mItemSize + mBgLineWidth * 2
    }

    private fun initEditText() {
        setBackgroundColor(UtilResource.getColor(android.R.color.transparent))
        setTextColor(Color.TRANSPARENT)
        inputType = InputType.TYPE_CLASS_NUMBER
        isCursorVisible = false
        filters = arrayOf(UtilEditTextFilter.PinPwdFormatterFilter(mMaxLength))
        setOnClickListener { setSelection(text.length) }//确保点击后，光标能在最后
        isLongClickable = false
        setTextIsSelectable(false)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == mMaxLength) onInputComplete(s.toString())
            }
        })
    }

    private fun initPaints() {
        mNormalBgPaint = with(Paint()) {
            style = Paint.Style.STROKE
            isDither = true
            isAntiAlias = true
            color = mNormalBgColor
            strokeWidth = mBgLineWidth.toFloat()
            this
        }

        mPressBgPaint = with(Paint()) {
            style = Paint.Style.STROKE
            isDither = true
            isAntiAlias = true
            color = mPressBgColor
            strokeWidth = mBgLineWidth.toFloat()
            this
        }

        mTextPaint = with(Paint()) {
            style = Paint.Style.STROKE
            isDither = true
            isAntiAlias = true
            color = mTextColor
            textAlign = Paint.Align.CENTER
            textSize = mTextSize.toFloat()
            this
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        when {
            widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST -> {
                widthSize = mRealWidth + paddingLeft + paddingRight
                heightSize = mRealHeight + paddingTop + paddingBottom
            }
            widthMode == MeasureSpec.AT_MOST -> widthSize = mRealWidth + paddingLeft + paddingRight
            heightMode == MeasureSpec.AT_MOST -> heightSize = mRealHeight + paddingTop + paddingBottom
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onDraw(canvas: Canvas?) {
        // 确定四个边距的位置
        val realLeft = paddingLeft
        val realRight = width - paddingRight
        val realTop = paddingTop
        val realBottom = height - paddingBottom
        // 计算每个item的尺寸以及间隙距，确保正确显示
        val itemSize = min(realBottom - realTop, mRealHeight)
        var drawItemSpaceSize = if (mRealWidth != width)
            if (GRAVITY_CENTER == mGravity) (mRealWidth - itemSize * mMaxLength) / (mMaxLength - 1) else mItemSpaceSize
        else (realRight - realLeft - itemSize * mMaxLength) / (mMaxLength - 1)
        drawItemSpaceSize = min(drawItemSpaceSize, mItemSpaceSize)
        val drawRealWidth = (drawItemSpaceSize + itemSize) * mMaxLength - drawItemSpaceSize
        // 根据显示的位置来确定左边距
        val gapWidth = realRight - realLeft - drawRealWidth
        val leftWidth = when (mGravity) {
            GRAVITY_LEFT -> 0
            GRAVITY_CENTER -> gapWidth / 2
            GRAVITY_RIGHT -> gapWidth
            else -> 0
        }
        // 确定文字的baseline
        val fontBottom = mTextPaint?.fontMetrics?.bottom ?: 0F
        val fontTop = mTextPaint?.fontMetrics?.top ?: 0F
        val fontY = (height - fontBottom + fontTop) / 2 - fontTop
        // 确定各个图的位置、文字位置，并画图
        val currentInputTextLen = mCurrentInputText.length
        for (index in 0 until mMaxLength) {
            val left = (leftWidth + realLeft + (itemSize + drawItemSpaceSize) * index).toFloat() + mMaxLength / 2
            val right = left + itemSize - mMaxLength
            val mPaint = if (index < currentInputTextLen) mPressBgPaint else mNormalBgPaint
            val bottom = (realTop + itemSize).toFloat() - mMaxLength / 2
            when (mInputStyle) {
                LINE_INPUT -> canvas?.drawLine(left, bottom, right, bottom, mPaint)
                SQUARE_INPUT -> {
                    mRectF.left = left
                    mRectF.top = realTop.toFloat() + mMaxLength / 2
                    mRectF.right = right
                    mRectF.bottom = bottom
                    canvas?.drawRoundRect(mRectF, mRoundRadius.toFloat(), mRoundRadius.toFloat(), mPaint)
                }
            }
            canvas?.drawText(if (index < currentInputTextLen) transInputString2Show(mCurrentInputText[index].toString()) else "",
                    left + (right - left) / 2F, fontY, mTextPaint)
        }
    }

    /**
     * 将输入的文字转成需要显示的内容
     * */
    private fun transInputString2Show(text: String): String {
        return when (mInputType) {
            PIN_KEY -> text
            PWD_KEY -> mPwdInputText ?: PWD_INPUT_TEXT
            else -> text
        }
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        val inputText = text.toString()
        if (inputText.length <= mMaxLength) {
            mCurrentInputText = inputText
            invalidate()
        }
    }

    fun setUpInputView(inputType: Int, inputStyle: Int, maxLength: Int) {
        setText("")
        mMaxLength = if (isPossibleLength(maxLength)) maxLength else MAX_LENGTH
        filters = arrayOf(UtilEditTextFilter.PinPwdFormatterFilter(mMaxLength))
        mInputType = inputType
        mInputStyle = inputStyle
        calculateRealSize()
        requestLayout()
        invalidate()
    }

    /**
     * 当前输入类型
     * */
    fun currentInputType() = mInputType

    private fun isPossibleLength(maxLength: Int): Boolean = maxLength <= MAX_LENGTH

    companion object {
        const val PIN_KEY = 0
        const val PWD_KEY = 1

        const val GRAVITY_LEFT = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_RIGHT = 2

        const val LINE_INPUT = 0
        const val SQUARE_INPUT = 1

        const val MAX_LENGTH = 6 // 可输入最大字符为6
        const val PWD_INPUT_TEXT = "●"

        val DEFAULT_SIZE = UtilResource.getDimensionPixelSize(R.dimen.size_44)
        val DEFAULT_SPACE_SIZE = UtilResource.getDimensionPixelSize(R.dimen.size_12)
        val DEFAULT_TEXT_SIZE = UtilResource.getDimensionPixelSize(R.dimen.font_14)
        val DEFAULT_LINE_WIDTH = UtilResource.getDimensionPixelSize(R.dimen.size_2)
        val DEFAULT_ROUND_RADIUS = UtilResource.getDimensionPixelSize(R.dimen.size_2)

        val DEFAULT_NORMAL_COLOR = UtilResource.getColor(R.color.grey_9)
        val DEFAULT_PRESS_COLOR = UtilResource.getColor(R.color.brand_color_blue)
        val DEFAULT_TEXT_COLOR = UtilResource.getColor(R.color.grey_3)
    }
}