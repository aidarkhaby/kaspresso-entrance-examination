package ru.webrelab.kie.cerealstorage

class CerealStorageImpl(
    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {

    /**
     * Блок инициализации класса.
     * Выполняется сразу при создании объекта
     */
    init {
        require(containerCapacity >= 0) {
            "Ёмкость контейнера не может быть отрицательной"
        }
        require(storageCapacity >= containerCapacity) {
            "Ёмкость хранилища не должна быть меньше ёмкости одного контейнера"
        }
    }

    private val storage = mutableMapOf<Cereal, Float>()

    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) { "Количество не может быть отрицательным" }

        val currentAmount = getAmount(cereal)

        if (currentAmount == 0f) {
            val totalContainers = storage.size
            val usedCapacity = totalContainers * containerCapacity
            if (usedCapacity + containerCapacity > storageCapacity) {
                throw IllegalStateException("Хранилище не позволяет разместить ещё один контейнер")
            }
        }

        val newAmount = currentAmount + amount
        val remainder = maxOf(newAmount - containerCapacity, 0f)
        val amountToStore = minOf(newAmount, containerCapacity)

        storage[cereal] = amountToStore

        return remainder
    }

    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) { "Количество не может быть отрицательным" }

        val currentAmount = getAmount(cereal)
        val taken = minOf(currentAmount, amount)
        val newAmount = currentAmount - taken

        if (newAmount > 0) {
            storage[cereal] = newAmount
        } else {
            storage.remove(cereal)
        }

        return taken

    }

    override fun removeContainer(cereal: Cereal): Boolean {
        return if (storage.containsKey(cereal) && storage[cereal] == 0f) {
            storage.remove(cereal)
            true
        } else {
            false
        }
    }

    override fun getAmount(cereal: Cereal): Float {
        return storage[cereal] ?: 0f
    }

    override fun getSpace(cereal: Cereal): Float {
        val currentAmount = storage[cereal]
            ?: throw IllegalStateException("Контейнер для $cereal отсутствует")
        return containerCapacity - currentAmount
    }

    override fun toString(): String {
        if (storage.isEmpty()) return "Хранилище пусто"

        return storage.entries.joinToString("\n") { (cereal, amount) ->
            "${cereal.local}: $amount из $containerCapacity"
        }
    }

}
