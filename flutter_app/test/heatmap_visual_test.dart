import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:ironlog/features/heatmap/domain/muscle_region.dart';
import 'package:ironlog/features/heatmap/presentation/widgets/muscle_heatmap.dart';

void main() {
  const scores = <String, double>{
    'deltoids': .65,
    'pectorals': .82,
    'abs': .45,
    'quadriceps': .72,
    'trapezius': .58,
    'lats': .76,
    'glutes': .88,
    'hamstrings': .67,
    'calves': .32,
  };

  testWidgets('records repeatable narrow and wide heatmap layout evidence', (
    tester,
  ) async {
    final evidence = <String, Object?>{
      'designSize': {
        'width': MuscleRegion.designSize.width,
        'height': MuscleRegion.designSize.height,
      },
      'view': 'front',
      'activeViews': MuscleView.values.map((view) => view.name).toList(),
      'regionCount': muscleRegions.length,
      'regionIds': muscleRegions.map((region) => region.regionId).toList(),
      'regions': muscleRegions
          .map(
            (region) => {
              'id': region.regionId,
              'view': region.view.name,
              'groupId': region.groupId,
              'focus': [region.focus.dx, region.focus.dy],
              'anchor': [region.anchor.dx, region.anchor.dy],
              'elbow': [region.elbow.dx, region.elbow.dy],
              'textOrigin': [region.textOrigin.dx, region.textOrigin.dy],
              'label': region.label,
            },
          )
          .toList(growable: false),
      'evidenceType': 'deterministic_layout_and_path_metadata',
      'rasterCapture': 'disabled_on_windows_test_backend',
      'labelLayer': {
        'coordinates': 'viewport',
        'fontSize': 'clamp(20 * min(sx, sy), 11, 18)',
        'leaderAndAnchorCoordinates': 'viewport',
      },
      'sizes': <Map<String, Object?>>[],
    };
    for (final region in muscleRegions) {
      expect(
        region.builder().contains(region.focus),
        isTrue,
        reason: '${region.regionId} focus must remain inside its path',
      );
    }
    for (final size in const [Size(360, 780), Size(768, 1024)]) {
      await tester.binding.setSurfaceSize(size);
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: Center(
              child: SizedBox(
                width: size.width - 24,
                child: RepaintBoundary(
                  key: const Key('heatmap-evidence'),
                  child: MuscleHeatmap(scores: scores),
                ),
              ),
            ),
          ),
        ),
      );
      // Asset loading is intentionally given one frame. `pumpAndSettle()` can
      // wait forever while an SVG image stream keeps a pending frame alive on
      // the Windows test backend.
      await tester.pump(const Duration(milliseconds: 100));
      final rendered = tester.getSize(find.byType(MuscleHeatmap));
      expect(rendered.width, closeTo(size.width - 24, 1));
      expect(rendered.height, greaterThan(0));
      (evidence['sizes']! as List<Map<String, Object?>>).add({
        'viewport': '${size.width.toInt()}x${size.height.toInt()}',
        'renderedWidth': rendered.width,
        'renderedHeight': rendered.height,
        'aspectRatio': rendered.width / rendered.height,
      });
    }
    final file = File('docs/evidence/heatmap_layout_evidence.json')
      ..parent.createSync(recursive: true);
    file.writeAsStringSync(
      const JsonEncoder.withIndent('  ').convert(evidence),
    );
  });

  testWidgets('exposes active front and back region focus semantics', (
    tester,
  ) async {
    final semantics = tester.ensureSemantics();
    try {
      await tester.binding.setSurfaceSize(const Size(360, 780));

      for (final view in MuscleView.values) {
        await tester.pumpWidget(
          MaterialApp(
            home: Scaffold(
              body: SizedBox(
                width: 336,
                child: MuscleHeatmap(scores: scores, activeView: view),
              ),
            ),
          ),
        );
        await tester.pump(const Duration(milliseconds: 100));
        final visible = muscleRegions.where((region) => region.view == view);
        for (final region in visible) {
          final side = switch (region.side) {
            MuscleSide.left => '左侧',
            MuscleSide.right => '右侧',
            MuscleSide.bilateral => '中线',
          };
          expect(
            find.bySemanticsLabel(
              RegExp('^${RegExp.escape(region.label)}，$side，'),
            ),
            findsOneWidget,
            reason: '${view.name} ${region.regionId} semantics focus',
          );
        }
      }
    } finally {
      semantics.dispose();
    }
  });

  test('all label origins remain separated in design space', () {
    final visible = muscleRegions
        .where((region) => region.view == MuscleView.front)
        .toList(growable: false);
    for (var i = 0; i < visible.length; i++) {
      for (var j = i + 1; j < visible.length; j++) {
        final distance =
            (visible[i].textOrigin - visible[j].textOrigin).distance;
        expect(
          distance,
          greaterThanOrEqualTo(24),
          reason:
              '${visible[i].regionId} and ${visible[j].regionId} label origins overlap',
        );
      }
    }
  });

  test('label boxes stay inside their viewport and do not overlap', () {
    const viewport = Size(1000, 1080);
    const textWidths = <String, double>{
      'Deltoids': 94,
      'Pectorals': 104,
      'Biceps': 70,
      'Triceps': 76,
      'Obliques': 94,
      'Abs': 38,
      'Quadriceps': 116,
      'Calves': 66,
      'Trapezius': 106,
      'Lats': 42,
      'Glutes': 66,
      'Hamstrings': 114,
    };
    const textHeight = 22.0;
    final seen = <String, Rect>{};
    for (final region in muscleRegions) {
      if (seen.containsKey(region.groupId)) continue;
      final label = switch (region.groupId) {
        'deltoids' => 'Deltoids',
        'pectorals' => 'Pectorals',
        'biceps' => 'Biceps',
        'triceps' => 'Triceps',
        'obliques' => 'Obliques',
        'abs' => 'Abs',
        'quadriceps' => 'Quadriceps',
        'calves' => 'Calves',
        'trapezius' => 'Trapezius',
        'lats' => 'Lats',
        'glutes' => 'Glutes',
        'hamstrings' => 'Hamstrings',
        _ => region.groupId,
      };
      final origin = region.textOrigin;
      final width = textWidths[label] ?? 120;
      final rect = Rect.fromLTWH(origin.dx, origin.dy, width, textHeight);
      expect(
        rect.left,
        greaterThanOrEqualTo(0),
        reason: '${region.groupId} label starts outside viewport',
      );
      expect(
        rect.right,
        lessThanOrEqualTo(viewport.width),
        reason: '${region.groupId} label ends outside viewport',
      );
      expect(
        rect.top,
        greaterThanOrEqualTo(0),
        reason: '${region.groupId} label starts above viewport',
      );
      expect(
        rect.bottom,
        lessThanOrEqualTo(viewport.height),
        reason: '${region.groupId} label ends below viewport',
      );
      for (final previous in seen.entries) {
        expect(
          rect.intersect(previous.value).isEmpty,
          isTrue,
          reason: '${region.groupId} label overlaps ${previous.key}',
        );
      }
      seen[region.groupId] = rect;
    }
  });

  test(
    'all interactive regions have non-empty paths and in-bounds focus points',
    () {
      const design = MuscleRegion.designSize;
      for (final region in muscleRegions) {
        final path = region.builder();
        expect(path.getBounds().isEmpty, isFalse, reason: region.regionId);
        expect(path.contains(region.focus), isTrue, reason: region.regionId);
        expect(region.focus.dx, inInclusiveRange(0, design.width));
        expect(region.focus.dy, inInclusiveRange(0, design.height));
      }
    },
  );

  testWidgets('production layer keeps legacy paths interaction-only', (
    tester,
  ) async {
    await tester.binding.setSurfaceSize(const Size(360, 780));
    await tester.pumpWidget(
      const MaterialApp(
        home: Scaffold(body: MuscleHeatmap(scores: <String, double>{})),
      ),
    );
    await tester.pump(const Duration(milliseconds: 100));

    final painters = tester
        .widgetList<CustomPaint>(find.byType(CustomPaint))
        .map((paint) => paint.painter)
        .whereType<MuscleHeatmapPainter>()
        .toList(growable: false);
    expect(painters, hasLength(2));
    final interactionPainter = painters.singleWhere(
      (painter) => painter.drawLabels,
    );
    expect(interactionPainter.drawRegions, isFalse);
    expect(interactionPainter.drawSelections, isTrue);
  });
}
