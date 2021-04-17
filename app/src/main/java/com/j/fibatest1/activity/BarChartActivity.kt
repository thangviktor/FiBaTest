package com.j.fibatest1.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet
import com.j.fibatest1.R
import kotlinx.android.synthetic.main.activity_bar_chart.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class BarChartActivity : AppCompatActivity() {
    private val names = ArrayList<String>()
    private val quantities = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)

        val string = "Tổng công ty THHH 1 thành viên Hùng Huệ Hoàng Mai"
        for (i in 0..48) {
            quantities.add(Random().nextInt(7)*10 + 2)
            names.add(string.substring(0..Random().nextInt(7)*5+5))
        }

        setBarChart()
    }

    private fun setBarChart() {
        barChart.setDrawValueAboveBar(false)
        barChart.description.isEnabled = false
        barChart.minimumHeight = names.size * 200
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.axisMinimum = 0f


        val formatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return names[abs(value.toInt())]
            }
        }
        Log.d("NAMESLOG", "${names.size}, names[1] = ${names[1]}")

        val entries = ArrayList<BarEntry>()
        for (i in quantities.indices) {
            val entry = BarEntry(i.toFloat(), quantities[i].toFloat())
            entries.add(entry)
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.setColors(ContextCompat.getColor(this, android.R.color.holo_blue_light))
        val barData = BarData(barDataSet)
        barData.barWidth = 0.5f
        barData.setDrawValues(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.isGranularityEnabled = true
        xAxis.labelCount = names.size
        xAxis.valueFormatter = formatter
        xAxis.setDrawGridLines(false)
        val metrics = resources.displayMetrics
        xAxis.xOffset = (metrics.widthPixels / metrics.density - 40f) * -1f

        barChart.animateY(700)
        barChart.data = barData
        barChart.invalidate()

    }
}