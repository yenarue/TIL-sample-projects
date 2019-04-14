package taxipark

import kotlin.math.roundToInt

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        this.allDrivers.filter { !this.trips.map { it.driver }.contains(it) }.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        if (minTrips <= 0) this.allPassengers
        else this.trips.flatMap { it.passengers }
                .groupBy { it.name }
                .filter { it.value.size >= minTrips }
                .flatMap { it.value }
                .toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
    this.trips.filter { it.driver.name.equals(driver.name) }
            .flatMap { it.passengers }
            .groupBy { it.name }
            .filter { it.value.size > 1 }
            .flatMap { it.value }
            .toSet()
/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    val totalTripsEachPassenger = this.trips.flatMap { it.passengers }
            .groupBy { it.name }
            .mapValues { it.value.size }

    return this.trips.filter { it.discount != null }
            .flatMap { it.passengers }
            .groupBy { it.name }
            .filter { it.value.size > totalTripsEachPassenger.get(it.key)!! * 0.5 }
            .flatMap { it.value }
            .toSet()
}
/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    val startRange = this.trips.map { it.duration }
            .groupBy { it / 10 }
            .maxBy { it.value.size }
            ?.key

    return startRange?.times(10)?.rangeTo(startRange.plus(1).times(10).minus(1))
}
/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if (this.trips.isNullOrEmpty()) return false

    val totalIncome = this.trips.sumByDouble { it.cost }

    val highIncomeDriver = this.trips.groupBy { it.driver }
            .mapValues { it.value.sumByDouble { it.cost } }
            .map { it.key to it.value }
            .sortedByDescending { it.second }
            .slice(0 .. ((this.allDrivers.size * 0.2).toInt() - 1))

    return highIncomeDriver.sumByDouble { it.second } >= totalIncome.times(0.8)
}