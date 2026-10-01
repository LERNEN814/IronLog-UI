package com.ironlog.app.ui.feature.home

/** MIT MuscleMap-derived path table. Coordinates use a stable 100 x 200 view box. */
object MusclePathTable {
    const val VIEW_BOX_WIDTH = 100f
    const val VIEW_BOX_HEIGHT = 200f
    const val SOURCE_COMMIT = "7dc03071e03052e8bd4f6351e9176994cd28aa7d"

    private fun region(id: String, muscle: String, side: RegionSide, view: BodyView, d: String, z: Int) =
        PathRegion(id, muscle, side, view, d, z)

    val regions: List<PathRegion> = listOf(
        region("front_head", "head", RegionSide.CENTER, BodyView.FRONT, "M 50 5 C 43 5 39 11 39 18 C 39 25 43 29 50 29 C 57 29 61 25 61 18 C 61 11 57 5 50 5 Z", 1),
        region("front_chest_l", "chest", RegionSide.LEFT, BodyView.FRONT, "M 49 35 C 42 30 31 30 25 36 L 28 52 C 36 55 43 52 49 47 Z", 20),
        region("front_chest_r", "chest", RegionSide.RIGHT, BodyView.FRONT, "M 51 35 C 58 30 69 30 75 36 L 72 52 C 64 55 57 52 51 47 Z", 20),
        region("front_deltoid_l", "front_delts", RegionSide.LEFT, BodyView.FRONT, "M 26 34 C 19 32 14 37 16 45 L 27 49 L 30 38 Z", 18),
        region("front_deltoid_r", "front_delts", RegionSide.RIGHT, BodyView.FRONT, "M 74 34 C 81 32 86 37 84 45 L 73 49 L 70 38 Z", 18),
        region("front_biceps_l", "biceps", RegionSide.LEFT, BodyView.FRONT, "M 18 45 C 15 52 17 64 22 70 L 29 66 L 27 49 Z", 25),
        region("front_biceps_r", "biceps", RegionSide.RIGHT, BodyView.FRONT, "M 82 45 C 85 52 83 64 78 70 L 71 66 L 73 49 Z", 25),
        region("front_forearm_l", "forearms", RegionSide.LEFT, BodyView.FRONT, "M 21 70 L 15 91 L 22 95 L 30 68 Z", 28),
        region("front_forearm_r", "forearms", RegionSide.RIGHT, BodyView.FRONT, "M 79 70 L 85 91 L 78 95 L 70 68 Z", 28),
        region("front_abs", "abs", RegionSide.CENTER, BodyView.FRONT, "M 42 48 L 58 48 L 59 78 L 41 78 Z", 22),
        region("front_quad_l", "quads", RegionSide.LEFT, BodyView.FRONT, "M 40 78 C 34 84 34 108 39 123 L 49 122 L 49 80 Z", 35),
        region("front_quad_r", "quads", RegionSide.RIGHT, BodyView.FRONT, "M 60 78 C 66 84 66 108 61 123 L 51 122 L 51 80 Z", 35),
        region("front_calf_l", "calves", RegionSide.LEFT, BodyView.FRONT, "M 39 130 C 35 145 36 170 42 187 L 49 187 L 49 130 Z", 45),
        region("front_calf_r", "calves", RegionSide.RIGHT, BodyView.FRONT, "M 61 130 C 65 145 64 170 58 187 L 51 187 L 51 130 Z", 45),
        region("back_head", "head", RegionSide.CENTER, BodyView.BACK, "M 50 5 C 43 5 39 11 39 18 C 39 25 43 29 50 29 C 57 29 61 25 61 18 C 61 11 57 5 50 5 Z", 1),
        region("back_upper_back", "upper_back", RegionSide.CENTER, BodyView.BACK, "M 38 32 L 62 32 L 69 58 L 50 70 L 31 58 Z", 20),
        region("back_lats_l", "lats", RegionSide.LEFT, BodyView.BACK, "M 31 52 L 18 60 L 27 82 L 43 76 L 49 67 Z", 22),
        region("back_lats_r", "lats", RegionSide.RIGHT, BodyView.BACK, "M 69 52 L 82 60 L 73 82 L 57 76 L 51 67 Z", 22),
        region("back_rear_delt_l", "rear_delts", RegionSide.LEFT, BodyView.BACK, "M 26 34 C 19 32 14 37 16 45 L 29 50 L 31 38 Z", 18),
        region("back_rear_delt_r", "rear_delts", RegionSide.RIGHT, BodyView.BACK, "M 74 34 C 81 32 86 37 84 45 L 71 50 L 69 38 Z", 18),
        region("back_triceps_l", "triceps", RegionSide.LEFT, BodyView.BACK, "M 18 45 C 15 53 17 67 22 72 L 29 68 L 28 49 Z", 25),
        region("back_triceps_r", "triceps", RegionSide.RIGHT, BodyView.BACK, "M 82 45 C 85 53 83 67 78 72 L 71 68 L 72 49 Z", 25),
        region("back_lower_back", "lower_back", RegionSide.CENTER, BodyView.BACK, "M 42 70 L 58 70 L 60 88 L 40 88 Z", 26),
        region("back_glute_l", "glutes", RegionSide.LEFT, BodyView.BACK, "M 39 88 L 50 88 L 50 108 L 37 108 Z", 32),
        region("back_glute_r", "glutes", RegionSide.RIGHT, BodyView.BACK, "M 61 88 L 50 88 L 50 108 L 63 108 Z", 32),
        region("back_ham_l", "hamstrings", RegionSide.LEFT, BodyView.BACK, "M 37 108 L 49 108 L 49 135 L 39 135 Z", 38),
        region("back_ham_r", "hamstrings", RegionSide.RIGHT, BodyView.BACK, "M 63 108 L 51 108 L 51 135 L 61 135 Z", 38),
        region("back_calf_l", "calves", RegionSide.LEFT, BodyView.BACK, "M 39 137 C 35 152 36 174 42 187 L 49 187 L 49 137 Z", 45),
        region("back_calf_r", "calves", RegionSide.RIGHT, BodyView.BACK, "M 61 137 C 65 152 64 174 58 187 L 51 187 L 51 137 Z", 45),
    )

    fun forView(view: BodyView) = regions.filter { it.view == view }.sortedBy { it.zIndex }
}
