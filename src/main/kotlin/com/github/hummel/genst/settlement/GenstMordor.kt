package com.github.hummel.genst.settlement

import com.github.hummel.genst.structure.StructureMordorTower
import lotr.common.entity.LOTREntityNPCRespawner
import lotr.common.entity.npc.*
import lotr.common.world.biome.LOTRBiome
import lotr.common.world.map.LOTRRoadType
import lotr.common.world.structure2.*
import lotr.common.world.village.LOTRVillageGen
import lotr.common.world.village.LocationInfo
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class GenstMordor : LOTRVillageGen(LOTRBiome.forodwaith) {
	init {
		gridScale = 12
		gridRandomDisplace = 1
		spawnChance = 0.0f
		villageChunkRadius = 6
		fixedVillageChunkRadius = 4
	}

	override fun createVillageInstance(
		world: World, i: Int, k: Int, random: Random, loc: LocationInfo
	): AbstractInstance<*> = Instance(this, world, i, k, random, loc)

	class Instance(
		village: GenstMordor, world: World, i: Int, k: Int, random: Random, loc: LocationInfo
	) : AbstractInstance<GenstMordor>(village, world, i, k, random, loc) {

		override fun addVillageStructures(random: Random) {
			addStructure(StructureMordorTower(), 0, 6, 2, true)
			addStructure(object : LOTRWorldGenNPCRespawner(false) {
				override fun setupRespawner(spawner: LOTREntityNPCRespawner) {
					spawner.setSpawnClasses(
						LOTREntityMordorOrc::class.java, LOTREntityMordorOrcArcher::class.java
					)
					spawner.setCheckRanges(40, -12, 12, 40)
					spawner.setSpawnRanges(20, -6, 6, 64)
					spawner.setBlockEnemySpawnRange(60)
				}
			}, 0, 0, 0)
			addStructure(object : LOTRWorldGenNPCRespawner(false) {
				override fun setupRespawner(spawner: LOTREntityNPCRespawner) {
					spawner.setSpawnClasses(
						LOTREntityMordorOrcBombardier::class.java, LOTREntityMordorSpider::class.java
					)
					spawner.setCheckRanges(40, -12, 12, 16)
					spawner.setSpawnRanges(20, -6, 6, 64)
					spawner.setBlockEnemySpawnRange(60)
				}
			}, 0, 0, 0)
			addStructure(object : LOTRWorldGenNPCRespawner(false) {
				override fun setupRespawner(spawner: LOTREntityNPCRespawner) {
					spawner.setSpawnClasses(LOTREntityBlackUruk::class.java, LOTREntityBlackUrukArcher::class.java)
					spawner.setCheckRanges(40, -12, 12, 16)
					spawner.setSpawnRanges(20, -6, 6, 64)
					spawner.setBlockEnemySpawnRange(60)
				}
			}, 0, 0, 0)
			addStructure(object : LOTRWorldGenNPCRespawner(false) {
				override fun setupRespawner(spawner: LOTREntityNPCRespawner) {
					spawner.setSpawnClass(LOTREntityOlogHai::class.java)
					spawner.setCheckRanges(40, -12, 12, 16)
					spawner.setSpawnRanges(20, -6, 6, 64)
					spawner.setBlockEnemySpawnRange(60)
				}
			}, 0, 0, 0)
			addStructure(LOTRWorldGenMordorWargPit(false), -21, 0, 1, true)
			addStructure(LOTRWorldGenMordorSpiderPit(false), 0, -21, 2, true)
			addStructure(LOTRWorldGenMordorWargPit(false), 21, 0, 3, true)
			addStructure(LOTRWorldGenMordorSpiderPit(false), 0, 21, 0, true)
			val houses = 20
			val frac = 1.0f / houses
			var turn = 0.0f
			while (turn < 1.0f) {
				var k: Int
				var l: Int
				var i: Int
				val turnR = Math.toRadians((frac.let { turn += it; turn } * 360.0f).toDouble()).toFloat()
				val sin = MathHelper.sin(turnR)
				val cos = MathHelper.cos(turnR)
				val turn8 = turn * 8.0f
				val r = when {
					turn8 >= 1.0f && turn8 < 3.0f -> 0
					turn8 >= 3.0f && turn8 < 5.0f -> 1
					turn8 >= 5.0f && turn8 < 7.0f -> 2
					turn8 >= 7.0f || turn8 < 1.0f -> 3
					else -> 0
				}
				if (random.nextBoolean()) {
					l = 61
					i = (l * cos).roundToInt()
					k = (l * sin).roundToInt()
					addStructure(LOTRWorldGenMordorTent(false), i, k, r, true)
					continue
				}
			}
			val farmX = 38
			val farmZ = 17
			val farmSize = 6
			addStructure(LOTRWorldGenMordorTent(false), -farmX + farmSize, -farmZ, 1, true)
			addStructure(LOTRWorldGenMordorTent(false), -farmZ + farmSize, -farmX, 1, true)
			addStructure(LOTRWorldGenMordorForgeTent(false), farmX - farmSize, -farmZ, 3, true)
			addStructure(LOTRWorldGenMordorTent(false), farmZ - farmSize, -farmX, 3, true)
			addStructure(LOTRWorldGenMordorTent(false), -farmX + farmSize, farmZ, 1, true)
			addStructure(LOTRWorldGenMordorForgeTent(false), farmX - farmSize, farmZ, 3, true)
		}

		override fun getPath(random: Random, i: Int, k: Int): LOTRRoadType? {
			val i1 = abs(i)
			val k1 = abs(k)
			val dSq = i * i + k * k
			val imn = 20 + random.nextInt(4)
			if (dSq < imn * imn) {
				return LOTRRoadType.MORDOR
			}
			val omn = 53 - random.nextInt(4)
			val omx = 60 + random.nextInt(4)
			if (dSq > omn * omn && dSq < omx * omx || dSq < 2809 && abs(i1 - k1) <= 2 + random.nextInt(4)) {
				return LOTRRoadType.MORDOR
			}
			return null
		}

		override fun isFlat(): Boolean = false

		override fun isVillageSpecificSurface(world: World, i: Int, j: Int, k: Int): Boolean = false

		override fun setupVillageProperties(random: Random) {}
	}
}
