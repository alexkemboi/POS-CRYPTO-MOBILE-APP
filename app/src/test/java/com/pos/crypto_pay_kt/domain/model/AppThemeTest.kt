package com.pos.crypto_pay_kt.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppThemeTest {
    @Test
    fun `missing stored theme defaults to light`() {
        assertEquals(AppTheme.LIGHT, AppTheme.fromStoredValue(null))
    }

    @Test
    fun `stored theme values are restored`() {
        AppTheme.entries.forEach { theme ->
            assertEquals(theme, AppTheme.fromStoredValue(theme.storedValue))
        }
    }
}
