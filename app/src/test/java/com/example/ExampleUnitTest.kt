package com.example

import com.example.data.model.*
import com.example.data.repository.SuperBrainRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testTerminalSelectionAndInitialConfig() {
        val repo = SuperBrainRepository()
        assertNotNull(repo.getSelectedTerminal())
        assertEquals("term-vps-01", repo.getSelectedTerminal()?.id)
        
        val config = repo.getSelectedConfig()
        assertEquals(0.01, config.baseLot, 0.0001)
        assertEquals(20, config.maxPositions)
        assertEquals(10, config.minTPPoints)
    }

    @Test
    fun testSaveConfigIncrementsVersion() {
        val repo = SuperBrainRepository()
        val initialConfig = repo.getSelectedConfig()
        val initialVer = initialConfig.version

        val updated = initialConfig.copy(baseLot = 0.02, maxPositions = 15)
        repo.saveConfig(updated)

        val newConfig = repo.getSelectedConfig()
        assertEquals(initialVer + 1, newConfig.version)
        assertEquals(0.02, newConfig.baseLot, 0.0001)
        assertEquals(15, newConfig.maxPositions)
    }

    @Test
    fun testAuthenticationAndRoleValidation() {
        val repo = SuperBrainRepository()
        assertTrue(repo.login("muhammadarifin31101996@gmail.com", "superbrain2026"))
        assertEquals(UserRole.ADMIN, repo.currentUser.value?.role)

        assertTrue(repo.register("Trader Baru", "trader@gmail.com", "secure123"))
        assertEquals(UserRole.MEMBER, repo.currentUser.value?.role)
    }
}

