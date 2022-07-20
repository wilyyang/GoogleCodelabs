package com.example.android.advancedcoroutines

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.android.advancedcoroutines.util.CacheOnSuccess
import com.example.android.advancedcoroutines.utils.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class PlantRepository private constructor(
    private val plantDao: PlantDao,
    private val plantService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private var plantListSortOrderCache =
        CacheOnSuccess(onErrorFallback = { listOf<String>() }){
            plantService.customPlantSortOrder()
        }

    private val customSortFlow = plantListSortOrderCache::getOrAwait.asFlow()
//    private val customSortFlow = flow {emit(plantListSortOrderCache.getOrAwait())}
//    private val customSortFlow = plantListSortOrderCache::getOrAwait.asFlow()
//        .onStart {
//            emit(listOf())
//            delay(1500)
//        }

    val plantsFlow: Flow<List<Plant>>
        get() = plantDao.getPlantsFlow()
            .combine(customSortFlow){  plants, sortOrder ->
                plants.applySort(sortOrder)
            }
            .flowOn(defaultDispatcher)
            .conflate()

    val plants : LiveData<List<Plant>> = liveData<List<Plant>> {
        val plantsLiveData = plantDao.getPlants()
        val customSortOrder = plantListSortOrderCache.getOrAwait()
        emitSource(plantsLiveData.map {
            plantList -> plantList.applySort(customSortOrder)
        })
    }

    fun getPlantsWithGrowZoneFlow(growZoneNumber: GrowZone): Flow<List<Plant>>{
        return plantDao.getPlantsWithGrowZoneNumberFlow(growZoneNumber.number)
    }

    fun getPlantsWithGrowZone(growZone: GrowZone) =
        plantDao.getPlantsWithGrowZoneNumber(growZone.number)
            .switchMap { plantList ->
                liveData {
                    val customSortOrder = plantListSortOrderCache.getOrAwait()
                    emit(plantList.applyMainSafeSort(customSortOrder))
                }
            }

    private fun shouldUpdatePlantsCache(): Boolean {
        return true
    }

    suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZoneNumber: GrowZone) {
        if (shouldUpdatePlantsCache()) fetchPlantsForGrowZone(growZoneNumber)
    }

    private suspend fun fetchRecentPlants() {
        val plants = plantService.allPlants()
        plantDao.insertAll(plants)
    }

    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = plantService.plantsByGrowZone(growZone)
        plantDao.insertAll(plants)
    }

    private fun List<Plant>.applySort(customSortOrder: List<String>): List<Plant>{
        return sortedBy { plant ->
            val positionForItem = customSortOrder.indexOf(plant.plantId).let { order ->
                if(order > -1) order else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, plant.name)
        }
    }

    @AnyThread
    suspend fun List<Plant>.applyMainSafeSort(customSortOrder: List<String>) =
        withContext(defaultDispatcher){
            this@applyMainSafeSort.applySort(customSortOrder)
        }


    companion object {
        @Volatile private var instance: PlantRepository? = null

        fun getInstance(plantDao: PlantDao, plantService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlantRepository(plantDao, plantService).also { instance = it }
            }
    }
}
