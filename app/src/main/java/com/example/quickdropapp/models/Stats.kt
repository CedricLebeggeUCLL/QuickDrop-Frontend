package com.example.quickdropapp.models

data class PackageStats(
    val totalSent: Int,
    val statusCounts: List<StatusCount>,
    val shipmentsPerMonth: List<MonthlyCount>
)

data class DeliveryStats(
    val totalDeliveries: Int,
    val statusCounts: List<StatusCount>,
    val deliveriesPerMonth: List<MonthlyCount>
)

data class StatusCount(val status: String, val count: Int)
data class MonthlyCount(val year: Int, val month: Int, val count: Int)