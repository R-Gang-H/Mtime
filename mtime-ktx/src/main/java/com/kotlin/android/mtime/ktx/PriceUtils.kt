package com.kotlin.android.mtime.ktx

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * create by lushan on 2020/9/16
 * description: 价格工具类
 */
object PriceUtils {
    /**
     * 如果价格后面有.0或.00则返回整数，否则返回原来字符串
     *
     * @param price
     * @return
     */
    fun formatPrice(price: String): String {
        price.let {
            if (price.endsWith(".00")) {
                return price.substring(0, price.length - 3)
            } else if (price.endsWith(".0")) {
                return price.substring(0, price.length - 2)
            }
        }
        return price
    }

    /**
     * 如果价格后面有.0或.00则返回整数，否则返回原来字符串
     *
     * @param price 价格分
     * @return
     */
    fun formatPrice(price: Double): String = formatPrice((price / 100).toString())

    /**
     * 分转成元
     * @param price 分
     */
    fun formatPrice(price: Long): String = formatPrice(price.toDouble())

    /**
     * 把分转为元
     *
     * @param cent
     * @param needInteger 如果是true 如果最后小数位是0需去掉
     * @return
     */
    fun formatPriceFenToYuan(cent: Long, needInteger: Boolean = false): String {
        var bigDecimal = BigDecimal.valueOf(cent)
        var divide = bigDecimal.divide(BigDecimal.valueOf(100))
        var df = DecimalFormat("#.##")

        val priceFormat = df.format(divide.toDouble())
        return if (needInteger) {
            formatPrice(priceFormat)
        } else {
            priceFormat
        }
    }

    /**
     * 分转成元
     * @param cent
     * @param needInteger 如果是true 如果最后小数位是0需去掉
     * @return
     */
    fun formatPriceFenToYuan(cent: Double, needInteger: Boolean = false): String {
        var bigDecimal = BigDecimal.valueOf(cent)
        var divide = bigDecimal.divide(BigDecimal.valueOf(100))
        var df = DecimalFormat("#.##")
        val priceFormat = df.format(divide.toDouble())
        return if (needInteger){
            formatPrice(priceFormat)
        }else{
            priceFormat
        }
    }

}