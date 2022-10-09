package sb.lib.googobottomnavigation.lib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.XmlRes
import androidx.core.graphics.toRectF
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import sb.lib.googobottomnavigation.R
import sb.lib.lib.BottomBarItem
import sb.lib.lib.BottomBarParser

class GoogoBottomNavigation @JvmOverloads constructor(context: Context ,
                                                      attr:AttributeSet?=null ,
                                                      defStyle:Int=0)  : View(context,attr,defStyle){


    private var measuredViewHeight: Int=0
    private val bottomHeight : Int  = 45

    private val  paint : Paint = Paint().apply {

        this.color =  Color.parseColor("#BBDEFB")


    }


    private val textPaint = Paint().apply {
        this.color = Color.BLACK
        this.textSize = 27f

    }


    companion object {

        private const val INVALID_RES = -1

    }



    private var items = listOf<BottomBarItem>()
    private lateinit var  rects : Array<Rect>


    @XmlRes
    private var _itemMenuRes: Int =INVALID_RES

    var itemMenuRes: Int
        @XmlRes get() = _itemMenuRes
        set(@XmlRes value) {
            _itemMenuRes = value
            if (value != INVALID_RES) {
                items = BottomBarParser(context, value).parse()
                invalidate()
            }
        }




    private var defaultHeight: Int = 0
    private val heightMargin: Int = 60


    init {

        init(context,attr,defStyle)
    }

    private fun init(context: Context, attr: AttributeSet?, defStyle: Int) {

        val typedArray = context.obtainStyledAttributes(attr,    R.styleable.GoogoBottomNavigation,defStyle,0)

        itemMenuRes =         typedArray.getResourceId(R.styleable.GoogoBottomNavigation_item_menu ,itemMenuRes)

    }





    override fun onMeasure(widthMeasureSpec: Int , heightMeasureSpec: Int) {

        val measureWidthSize = MeasureSpec.getSize(widthMeasureSpec)


        val iconHeight = drawableToBitmap(items[0].icon)!!.height
        val textHeight =  (paint.descent() + paint.ascent()) / 2


         measuredViewHeight = heightMargin + iconHeight + textHeight.toInt() + bottomHeight

        setMeasuredDimension(measureWidthSize, measuredViewHeight)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        rects = Array<Rect>(items.size){ Rect() }


        val widthLength = width/items.size
        val icon = drawableToBitmap(items[0].icon)

        defaultHeight =  icon!!.height + heightMargin
        var defaultLeft = 0

        for(i:Int in items.indices step 1){
            val rect = Rect()

            rect.left = defaultLeft
            rect.right = defaultLeft + widthLength
            rect.top = 0
            rect.bottom = measuredViewHeight
            defaultLeft += widthLength
            rects[i] = rect
        }


        indicatorX = rects[0].centerX()

    }




   private  fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    override fun onDraw(canvas: Canvas?) {

        if(canvas==null )return

        canvas.drawColor(Color.parseColor("#FFAB91"))

        for(i:Int in 0 until items.size step 1){

            val rect=rects[i]

            canvas.drawRect(rect,paint)
        }

        drawIndicator(canvas)
        drawIcon(canvas)
        drawText(canvas)


    }

    private fun drawText(canvas: Canvas) {


        for(i:Int in items.indices step 1){

           val item =  items[i]

            val heightText = (paint.descent() + paint.ascent()) / 2

            val rectY = rects[i].centerY() + bottomHeight - heightText

            val textX = paint.measureText(item.title)

            val rectX = rects[i].centerX() - textX

            canvas.drawText( item.title ,rectX , rectY ,textPaint)


        }




    }


    private val indicatorPaint = Paint().apply {

        this.color = Color.parseColor("#82B1FF")

    }


    private var activeIndex = 0

    private var indicatorX = 0

    private fun drawIndicator(canvas: Canvas) {

       val width = drawableToBitmap(items[0].icon)!!.width

        val topY = heightMargin/2
        for(i:Int in 0 until  items.size step 1){

            if( activeIndex == i ) {
                val widthSize = width

                val rect = Rect()
                rect.left = indicatorX -  widthSize
                rect.right = indicatorX + widthSize
                rect.top = topY
                rect.bottom = topY + widthSize

                canvas.drawRoundRect(rect.toRectF(), 20f, 20f, indicatorPaint)


            }
        } }


    
    private var ids = arrayListOf(
        R.id.homeFragment ,
        R.id.messageFragment ,
        R.id.musicFragment ,
        R.id.settingsFragment)

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event ==null)return false

        when(event.action){

            MotionEvent.ACTION_UP ->{


            }


            MotionEvent.ACTION_DOWN ->{


                for(i:Int in items.indices step 1){

                    val rect= rects[i]

                    if(rect.contains(event.x.toInt(),event.y.toInt()))
                    {
                        if(activeIndex!=i){

                            activeIndex = i







                            /********/

                            val navigate =ids[i]

                            val builder = NavOptions.Builder().setLaunchSingleTop(true)

                            builder.setEnterAnim(R.anim.left_to_right_animation)
                                .setExitAnim(R.anim.right_to_left_animation)
                                .setPopEnterAnim(R.anim.left_to_right_animation)
                                .setPopExitAnim(R.anim.right_to_left_animation)


                            navController?.navigate(navigate,null , builder.build())





                            /*******/

                            ValueAnimator.ofInt( indicatorX,rects[i].centerX()).apply {

                                this.duration = 200
                                this.interpolator = LinearInterpolator()

                                addUpdateListener {


                                    indicatorX = it.animatedValue as Int
                                    invalidate()

                                }
                                start()
                            }


                        }

                    }


                }


            }




        }


        return true
    }


    private fun drawIcon(canvas: Canvas) {


        for(i:Int in 0 until items.size step 1) {
         val icon =   drawableToBitmap(items[i].icon)


            val topY = heightMargin/2
            val topX = rects[i].centerX() - icon!!.width/2

            canvas.drawBitmap(icon ,topX.toFloat() ,topY.toFloat() ,paint)

        }

    }


    private lateinit  var navController : NavController
    fun setNavigation(navController: NavController) {

        this.navController = navController

    }


}