import 'dart:ui';

import 'package:flutter_test/flutter_test.dart';

import 'package:ironlog/features/heatmap/domain/muscle_region.dart';
import 'package:ironlog/features/heatmap/presentation/widgets/muscle_heatmap.dart';

void main() {
  test('normalized heatmap regions hit front and back muscles', () {
    const designSize = Size(1000, 1080);

    final chest = muscleRegionAt(const Offset(232, 240), designSize);
    expect(chest?.regionId, 'front_pectoral_left');
    expect(chest?.groupId, 'pectorals');
    expect(
      muscleRegionAt(const Offset(755, 520), designSize)?.groupId,
      'glutes',
    );
    expect(muscleRegionAt(const Offset(500, 500), designSize), isNull);
  });

  test('anatomy ids map lower-body SVG paths to stable heat groups', () {
    expect(MuscleHeatmapColorMapping.groupForSvgId('sartoris_l'), 'quadriceps');
    expect(
      MuscleHeatmapColorMapping.groupForSvgId('adductor_magnus_r'),
      'quadriceps',
    );
    expect(
      MuscleHeatmapColorMapping.groupForSvgId('iliotibial_tract_l'),
      'hamstrings',
    );
    expect(
      MuscleHeatmapColorMapping.groupForSvgId('tibialis_anterior_l'),
      'calves',
    );
  });
}
