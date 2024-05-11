package com.example.util;

/**
 * Класс, представляющий тип неисправности.
 * Используется контроллером StatisticsTabController для хранения информации о типе ремонта и количестве таких ремонтов.
 */
public class FaultType {
    // Название типа ремонта
    private String repairType;

    // Количество случаев такого типа ремонта
    private int count;

    /**
     * Конструктор для создания экземпляра типа неисправности.
     *
     * @param repairType Название типа ремонта.
     * @param count Количество таких ремонтов.
     */
    public FaultType(String repairType, int count) {
        this.repairType = repairType;
        this.count = count;
    }

    /**
     * Возвращает название типа ремонта.
     *
     * @return Название типа ремонта.
     */
    public String getRepairType() {
        return repairType;
    }

    /**
     * Возвращает количество ремонтов данного типа.
     *
     * @return Количество ремонтов данного типа.
     */
    public int getCount() {
        return count;
    }
}
