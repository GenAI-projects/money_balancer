package com.freewise

import com.freewise.data.BalanceCalculator
import com.freewise.data.SampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BalanceCalculatorTest {
    @Test
    fun calculateBalances_isNetZero() {
        val group = SampleData.demoGroup()
        val net = BalanceCalculator.calculateBalances(group).values.sum()
        assertTrue(kotlin.math.abs(net) < 0.01)
    }

    @Test
    fun simplifyDebts_keepsSettlementVolumeReasonable() {
        val group = SampleData.demoGroup()
        val settlements = BalanceCalculator.simplifyDebts(group)
        val total = settlements.sumOf { it.amount }

        assertEquals(2, settlements.size)
        assertTrue(total > 500)
    }
}
