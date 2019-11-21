package com.example.testprojectsb.network.model

import java.math.BigDecimal

/**
 * Created by grati on 11/21/2019.
 */
data class Ticker(val volume: BigDecimal, val lastPrice: Double, val low: Double, val high: Double,
                  val dailyChange: Double, val dailyChangePercentage: Double)