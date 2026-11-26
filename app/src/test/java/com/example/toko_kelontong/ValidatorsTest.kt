package com.example.toko_kelontong

import com.example.toko_kelontong.ui.common.Validators
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun required_blankIsError() {
        assertNotNull(Validators.required("  ", "Nama"))
        assertNull(Validators.required("Budi", "Nama"))
    }

    @Test
    fun password_minimumLength() {
        assertNotNull(Validators.password("12345"))
        assertNull(Validators.password("123456"))
    }
}
