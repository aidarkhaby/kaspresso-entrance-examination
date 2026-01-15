package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CerealStorageImplTest {

    private lateinit var storage: CerealStorageImpl

    @BeforeEach
    fun setUp() {
        storage = CerealStorageImpl(10f, 20f)
    }

    @Test
    fun `should throw if containerCapacity is negative`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(-4f, 10f)
        }
    }

    @Test
    fun `should throw if storageCapacity is less than containerCapacity`() {
        assertThrows(IllegalArgumentException::class.java) {
            CerealStorageImpl(5f, 1f)
        }
    }

    @Test
    fun `addCereal should add to existing container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        val remainder = storage.addCereal(Cereal.BUCKWHEAT, 3f)

        assertEquals(0f, remainder, 0.01f)
        assertEquals(8f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `addCereal should create new container`() {
        val remainder = storage.addCereal(Cereal.BUCKWHEAT, 5f)

        assertEquals(0f, remainder, 0.01f)
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `addCereal should return remainder when container overflows`() {
        storage.addCereal(Cereal.BUCKWHEAT, 9f)
        val remainder = storage.addCereal(Cereal.BUCKWHEAT, 3f)

        assertEquals(2f, remainder, 0.01f)
        assertEquals(10f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `addCereal should throw for negative amount`() {
        assertThrows<IllegalArgumentException> {
            storage.addCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `addCereal should throw when no space for new container`() {

        storage.addCereal(Cereal.BUCKWHEAT, 10f)
        storage.addCereal(Cereal.RICE, 10f)

        assertThrows<IllegalStateException> {
            storage.addCereal(Cereal.MILLET, 1f)
        }
    }

    @Test
    fun `getCereal should return requested amount`() {
        storage.addCereal(Cereal.BUCKWHEAT, 8f)
        val received = storage.getCereal(Cereal.BUCKWHEAT, 3f)

        assertEquals(3f, received, 0.01f)
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `getCereal should return remaining when not enough`() {
        storage.addCereal(Cereal.BUCKWHEAT, 3f)
        val received = storage.getCereal(Cereal.BUCKWHEAT, 5f)

        assertEquals(3f, received, 0.01f)
        assertEquals(0f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `getCereal should throw for negative amount`() {
        assertThrows<IllegalArgumentException> {
            storage.getCereal(Cereal.BUCKWHEAT, -1f)
        }
    }

    @Test
    fun `getCereal should return 0 for non-existing cereal`() {
        val received = storage.getCereal(Cereal.BUCKWHEAT, 5f)
        assertEquals(0f, received, 0.01f)
    }

    @Test
    fun `removeContainer should return true for empty container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 0f)

        assertTrue(storage.removeContainer(Cereal.BUCKWHEAT))
        assertEquals(0f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `removeContainer should return false for non-empty container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)

        assertFalse(storage.removeContainer(Cereal.BUCKWHEAT))
        assertEquals(5f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `removeContainer should return false for non-existing container`() {
        assertFalse(storage.removeContainer(Cereal.BUCKWHEAT))
    }

    @Test
    fun `getAmount should return correct amount`() {
        storage.addCereal(Cereal.BUCKWHEAT, 7f)
        storage.addCereal(Cereal.BUCKWHEAT, 2f)

        assertEquals(9f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `getAmount should return 0 for non-existing cereal`() {
        assertEquals(0f, storage.getAmount(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `getSpace should return available space`() {
        storage.addCereal(Cereal.BUCKWHEAT, 7f)

        assertEquals(3f, storage.getSpace(Cereal.BUCKWHEAT), 0.01f)
    }

    @Test
    fun `getSpace should throw for non-existing container`() {
        assertThrows<IllegalStateException> {
            storage.getSpace(Cereal.BUCKWHEAT)
        }
    }

    @Test
    fun `toString should return correct representation`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        storage.addCereal(Cereal.RICE, 3f)

        val result = storage.toString()
        assertTrue(result.contains("Гречка"))
        assertTrue(result.contains("Рис"))
        assertTrue(result.contains("5"))
        assertTrue(result.contains("3"))
    }

    @Test
    fun `multiple containers should not exceed storage capacity`() {
        storage.addCereal(Cereal.BUCKWHEAT, 10f)
        storage.addCereal(Cereal.RICE, 10f)

        // Попытка добавить третий контейнер должна выбросить исключение
        assertThrows<IllegalStateException> {
            storage.addCereal(Cereal.MILLET, 1f)
        }
    }

    @Test
    fun `container should be reused after removal`() {
        storage.addCereal(Cereal.BUCKWHEAT, 5f)
        storage.getCereal(Cereal.BUCKWHEAT, 5f)
        storage.removeContainer(Cereal.BUCKWHEAT)

        storage.addCereal(Cereal.RICE, 8f)
        assertEquals(8f, storage.getAmount(Cereal.RICE), 0.01f)
    }

}