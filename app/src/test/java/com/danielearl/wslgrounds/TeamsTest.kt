package com.danielearl.wslgrounds

import com.danielearl.wslgrounds.data.Teams
import org.junit.Assert.assertEquals
import org.junit.Test

class TeamsTest {
    @Test
    fun `teams list should contain exactly 14 teams`() {
        assertEquals(14, Teams.all.size)
    }
}
