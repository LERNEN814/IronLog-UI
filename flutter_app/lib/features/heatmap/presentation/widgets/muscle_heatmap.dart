import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../../domain/muscle_region.dart';

class MuscleHeatmap extends StatelessWidget {
  const MuscleHeatmap({
    super.key,
    required this.scores,
    this.activeView = MuscleView.front,
    this.selectedRegionId,
    this.onRegionTap,
    this.showReferenceRaster = false,
  });

  /// Kept for API compatibility with the visual baseline. The reference PNG
  /// is a review artifact and is intentionally never painted in production.
  final bool showReferenceRaster;
  final Map<String, double> scores;
  final MuscleView activeView;
  final String? selectedRegionId;
  final ValueChanged<MuscleRegion>? onRegionTap;

  @override
  Widget build(BuildContext context) {
    return Semantics(
      container: true,
      label: '前后视肌肉训练热力图，点击独立肌群查看训练详情',
      child: ClipRRect(
        borderRadius: BorderRadius.circular(8),
        child: ColoredBox(
          color: const Color(0xff171923),
          child: InteractiveViewer(
            minScale: 1,
            maxScale: 2.4,
            boundaryMargin: const EdgeInsets.all(24),
            child: AspectRatio(
              aspectRatio:
                  MuscleRegion.designSize.width /
                  MuscleRegion.designSize.height,
              child: LayoutBuilder(
                builder: (context, constraints) {
                  final size = constraints.biggest;
                  final sx = size.width / MuscleRegion.designSize.width;
                  final sy = size.height / MuscleRegion.designSize.height;
                  final visible = muscleRegions
                      .where((region) => region.view == activeView)
                      .toList(growable: false);
                  return Stack(
                    fit: StackFit.expand,
                    children: [
                      Positioned.fill(
                        child: CustomPaint(
                          painter: MuscleHeatmapPainter(
                            scores: scores,
                            activeView: activeView,
                            selectedRegionId: selectedRegionId,
                            drawPanel: true,
                            drawRegions: false,
                            drawLabels: false,
                          ),
                        ),
                      ),
                      Positioned.fill(
                        child: IgnorePointer(
                          child: Row(
                            children: [
                              Expanded(
                                child: Padding(
                                  padding: const EdgeInsets.fromLTRB(
                                    12,
                                    18,
                                    6,
                                    18,
                                  ),
                                  child: _SvgBodyLayer(
                                    asset: 'assets/heatmap/body_front.svg',
                                    colorMapper: _HeatmapColorMapper(
                                      scores: scores,
                                      activeView: activeView,
                                      view: MuscleView.front,
                                    ),
                                  ),
                                ),
                              ),
                              Expanded(
                                child: Padding(
                                  padding: const EdgeInsets.fromLTRB(
                                    6,
                                    18,
                                    12,
                                    18,
                                  ),
                                  child: _SvgBodyLayer(
                                    asset: 'assets/heatmap/body_back.svg',
                                    colorMapper: _HeatmapColorMapper(
                                      scores: scores,
                                      activeView: activeView,
                                      view: MuscleView.back,
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      GestureDetector(
                        behavior: HitTestBehavior.opaque,
                        onTapUp: (details) {
                          final designPoint = Offset(
                            details.localPosition.dx / sx,
                            details.localPosition.dy / sy,
                          );
                          final region = muscleRegionAt(
                            designPoint,
                            MuscleRegion.designSize,
                            view: activeView,
                          );
                          if (region != null) onRegionTap?.call(region);
                        },
                        child: CustomPaint(
                          painter: MuscleHeatmapPainter(
                            scores: scores,
                            activeView: activeView,
                            selectedRegionId: selectedRegionId,
                            drawPanel: false,
                            // The reviewed SVG atlas is the production anatomy
                            // layer. Keep the legacy cubic paths for hit testing
                            // and selected outlines, but do not paint their
                            // fills, textures, or default outlines over SVG.
                            drawRegions: false,
                            drawSelections: true,
                            drawLabels: true,
                          ),
                        ),
                      ),
                      for (final region in visible)
                        Positioned(
                          left: region.focus.dx * sx - 22,
                          top: region.focus.dy * sy - 22,
                          width: 44,
                          height: 44,
                          child: Semantics(
                            button: true,
                            label:
                                '${region.label}，${region.side == MuscleSide.left
                                    ? '左侧'
                                    : region.side == MuscleSide.right
                                    ? '右侧'
                                    : '中线'}，训练刺激 ${(scores[region.groupId] ?? 0).clamp(0, 1).toStringAsFixed(2)}',
                            onTap: onRegionTap == null
                                ? null
                                : () => onRegionTap!(region),
                            child: Tooltip(
                              message: region.label,
                              child: const SizedBox.expand(),
                            ),
                          ),
                        ),
                    ],
                  );
                },
              ),
            ),
          ),
        ),
      ),
    );
  }
}

/// Adds the reference-style fibre texture without using the legacy interaction
/// paths as a visual mask. `srcIn` uses the SVG alpha channel, so stripe pixels
/// cannot escape the reviewed anatomical asset boundaries.
class _SvgBodyLayer extends StatelessWidget {
  const _SvgBodyLayer({required this.asset, required this.colorMapper});

  final String asset;
  final ColorMapper colorMapper;

  @override
  Widget build(BuildContext context) {
    return Stack(
      fit: StackFit.expand,
      children: [
        SvgPicture.asset(asset, fit: BoxFit.contain, colorMapper: colorMapper),
        IgnorePointer(
          child: ShaderMask(
            blendMode: BlendMode.srcIn,
            shaderCallback: (bounds) => _stripeShader(bounds),
            child: SvgPicture.asset(
              asset,
              fit: BoxFit.contain,
              colorFilter: const ColorFilter.mode(
                Colors.white,
                BlendMode.srcIn,
              ),
            ),
          ),
        ),
      ],
    );
  }

  Shader _stripeShader(Rect bounds) {
    // Alternating stops create many narrow diagonal bands in one normalized
    // gradient pass, keeping the pattern stable as the atlas scales.
    const stripeCount = 240;
    final colors = <Color>[];
    final stops = <double>[];
    for (var index = 0; index <= stripeCount; index++) {
      final visible = index % 4 == 2;
      colors.add(
        visible ? Colors.white.withValues(alpha: .12) : Colors.transparent,
      );
      stops.add(index / stripeCount);
    }
    return LinearGradient(
      begin: Alignment.bottomLeft,
      end: Alignment.topRight,
      colors: colors,
      stops: stops,
    ).createShader(bounds);
  }
}

class MuscleHeatmapPainter extends CustomPainter {
  const MuscleHeatmapPainter({
    required this.scores,
    required this.activeView,
    required this.selectedRegionId,
    this.drawPanel = false,
    this.drawRegions = true,
    this.drawSelections = true,
    this.drawLabels = true,
  });

  final Map<String, double> scores;
  final MuscleView activeView;
  final String? selectedRegionId;
  final bool drawPanel;
  final bool drawRegions;
  final bool drawSelections;
  final bool drawLabels;

  @override
  void paint(Canvas canvas, Size size) {
    final sx = size.width / MuscleRegion.designSize.width;
    final sy = size.height / MuscleRegion.designSize.height;
    canvas.save();
    canvas.scale(sx, sy);
    final design = MuscleRegion.designSize;

    if (drawPanel) _drawPanel(canvas, design);
    if (drawRegions) {
      for (final region in muscleRegions) {
        final score = (scores[region.groupId] ?? 0).clamp(0.0, 1.0).toDouble();
        final path = region.builder();
        final dimmed = region.view != activeView;
        _drawMuscle(canvas, region, path, score, dimmed: dimmed);
      }
    }
    if (drawSelections && selectedRegionId != null) {
      for (final region in muscleRegions) {
        final selected =
            selectedRegionId == region.regionId ||
            selectedRegionId == region.groupId;
        if (!selected) continue;
        final selectedPaint = Paint()
          ..style = PaintingStyle.stroke
          ..strokeWidth = 6
          ..color = const Color(0xfffff4cf);
        canvas.drawPath(region.builder(), selectedPaint);
      }
    }

    canvas.restore();
    // Text is painted in viewport coordinates. Keeping labels outside the
    // design-space scale avoids tiny glyphs becoming solid bars on low-density
    // Android/Windows raster backends while preserving the path geometry.
    if (drawLabels) {
      _drawLabels(canvas, MuscleView.front, size);
      _drawLabels(canvas, MuscleView.back, size);
    }
  }

  void _drawPanel(Canvas canvas, Size size) {
    final panel = Paint()..color = const Color(0xff171923);
    canvas.drawRect(Offset.zero & size, panel);
    final vignette = Paint()
      ..shader = const RadialGradient(
        center: Alignment(0, -.25),
        radius: .8,
        colors: [Color(0xff222837), Color(0xff171923)],
      ).createShader(Offset.zero & size);
    canvas.drawRect(Offset.zero & size, vignette);
    final border = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 3
      ..color = const Color(0xff59647b);
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        const Rect.fromLTWH(5, 5, 990, 1070),
        const Radius.circular(28),
      ),
      border,
    );
  }

  void _drawMuscle(
    Canvas canvas,
    MuscleRegion region,
    Path path,
    double score, {
    required bool dimmed,
  }) {
    final color = _heatColor(score);
    final alpha = dimmed ? .5 : 1.0;
    final fill = Paint()
      ..shader = RadialGradient(
        center: Alignment.center,
        radius: 1.15,
        colors: [
          Color.lerp(color, Colors.white, .12)!.withValues(alpha: .24 * alpha),
          color.withValues(alpha: .18 * alpha),
          Color.lerp(color, Colors.black, .25)!.withValues(alpha: .22 * alpha),
        ],
        stops: const [0, .45, 1],
      ).createShader(path.getBounds());
    canvas.drawPath(path, fill);
    canvas.save();
    canvas.clipPath(path);
    final bounds = path.getBounds();
    final texture = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.6
      ..strokeCap = StrokeCap.round
      ..color = Colors.white.withValues(alpha: (score > .1 ? .2 : .1) * alpha);
    for (var y = bounds.top - 10; y < bounds.bottom + 10; y += 11) {
      canvas.drawLine(
        Offset(bounds.left - 20, y + 16),
        Offset(bounds.right + 20, y - 16),
        texture,
      );
    }
    final shadow = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 2.5
      ..color = const Color(0xff1d2635).withValues(alpha: .38 * alpha);
    canvas.drawPath(path, shadow);
    canvas.restore();
    final outline = Paint()
      ..style = PaintingStyle.stroke
      ..strokeWidth = 1.8
      ..color = const Color(0xff202938).withValues(alpha: .7 * alpha);
    canvas.drawPath(path, outline);
    if (selectedRegionId == null) return;
    final selected =
        selectedRegionId == region.regionId ||
        selectedRegionId == region.groupId;
    if (selected) {
      final selectedPaint = Paint()
        ..style = PaintingStyle.stroke
        ..strokeWidth = 6
        ..color = const Color(0xfffff4cf);
      canvas.drawPath(path, selectedPaint);
    }
  }

  void _drawLabels(Canvas canvas, MuscleView view, Size size) {
    final sx = size.width / MuscleRegion.designSize.width;
    final sy = size.height / MuscleRegion.designSize.height;
    final scale = sx < sy ? sx : sy;
    Offset toViewport(Offset point) => Offset(point.dx * sx, point.dy * sy);

    // Match the reference composition's sparse callouts. Other regions stay
    // independently hittable and accessible without crowding the panel.
    final specs = view == MuscleView.front
        ? const <String, _HeatmapLabelSpec>{
            'deltoids': _HeatmapLabelSpec(
              anchor: Offset(145, 236),
              elbow: Offset(315, 132),
              textOrigin: Offset(330, 108),
            ),
            'abs': _HeatmapLabelSpec(
              anchor: Offset(245, 392),
              elbow: Offset(345, 430),
              textOrigin: Offset(360, 414),
            ),
            'quadriceps': _HeatmapLabelSpec(
              anchor: Offset(200, 620),
              elbow: Offset(330, 635),
              textOrigin: Offset(345, 616),
            ),
          }
        : const <String, _HeatmapLabelSpec>{
            'trapezius': _HeatmapLabelSpec(
              anchor: Offset(742, 235),
              elbow: Offset(565, 132),
              textOrigin: Offset(550, 108),
            ),
            'lats': _HeatmapLabelSpec(
              anchor: Offset(680, 355),
              elbow: Offset(535, 300),
              textOrigin: Offset(520, 278),
            ),
            'glutes': _HeatmapLabelSpec(
              anchor: Offset(740, 520),
              elbow: Offset(565, 520),
              textOrigin: Offset(550, 498),
            ),
            'hamstrings': _HeatmapLabelSpec(
              anchor: Offset(700, 660),
              elbow: Offset(565, 650),
              textOrigin: Offset(550, 628),
            ),
          };
    final labels = <String>{};
    for (final region in muscleRegions.where((r) => r.view == view)) {
      final spec = specs[region.groupId];
      if (spec == null || !labels.add(region.groupId)) continue;
      final anchor = toViewport(spec.anchor);
      final elbow = toViewport(spec.elbow);
      final textOrigin = toViewport(spec.textOrigin);
      final leader = Paint()
        ..style = PaintingStyle.stroke
        ..strokeWidth = (1.2 * scale).clamp(.8, 1.4)
        ..color = const Color(0xffd6dbe8).withValues(alpha: .85);
      canvas.drawLine(anchor, elbow, leader);
      canvas.drawLine(elbow, Offset(textOrigin.dx, elbow.dy), leader);
      canvas.drawCircle(
        anchor,
        (3.5 * scale).clamp(2.0, 3.5),
        Paint()..color = Colors.white,
      );
      final fontSize = (20 * scale).clamp(11.0, 18.0);
      final painter = TextPainter(
        text: TextSpan(
          text: _englishLabel(region.groupId),
          style: TextStyle(
            color: Color(0xffd6dbe8),
            fontSize: fontSize,
            fontWeight: FontWeight.w400,
          ),
        ),
        textDirection: TextDirection.ltr,
      )..layout();
      final x = textOrigin.dx.clamp(0.0, size.width - painter.width);
      final y = textOrigin.dy.clamp(0.0, size.height - painter.height);
      painter.paint(canvas, Offset(x, y));
    }
  }

  String _englishLabel(String groupId) {
    const labels = <String, String>{
      'deltoids': 'Deltoids',
      'pectorals': 'Pectorals',
      'biceps': 'Biceps',
      'triceps': 'Triceps',
      'obliques': 'Obliques',
      'abs': 'Abs',
      'quadriceps': 'Quadriceps',
      'calves': 'Calves',
      'trapezius': 'Trapezius',
      'lats': 'Lats',
      'glutes': 'Glutes',
      'hamstrings': 'Hamstrings',
    };
    return labels[groupId] ?? groupId;
  }

  Color _heatColor(double score) {
    const nodes = <double>[0, .15, .35, .6, .8, 1];
    const colors = <Color>[
      Color(0xff53677f),
      Color(0xff3d8eaa),
      Color(0xfff39a3d),
      Color(0xffffb52e),
      Color(0xffff6a24),
      Color(0xffe92f2f),
    ];
    for (var i = 1; i < nodes.length; i++) {
      if (score <= nodes[i]) {
        final t = (score - nodes[i - 1]) / (nodes[i] - nodes[i - 1]);
        return Color.lerp(colors[i - 1], colors[i], t.clamp(0, 1))!;
      }
    }
    return colors.last;
  }

  @override
  bool shouldRepaint(covariant MuscleHeatmapPainter oldDelegate) =>
      oldDelegate.scores != scores ||
      oldDelegate.activeView != activeView ||
      oldDelegate.selectedRegionId != selectedRegionId ||
      oldDelegate.drawPanel != drawPanel ||
      oldDelegate.drawRegions != drawRegions ||
      oldDelegate.drawSelections != drawSelections ||
      oldDelegate.drawLabels != drawLabels;
}

class _HeatmapLabelSpec {
  const _HeatmapLabelSpec({
    required this.anchor,
    required this.elbow,
    required this.textOrigin,
  });

  final Offset anchor;
  final Offset elbow;
  final Offset textOrigin;
}

class _HeatmapColorMapper extends ColorMapper {
  const _HeatmapColorMapper({
    required this.scores,
    required this.activeView,
    required this.view,
  });

  final Map<String, double> scores;
  final MuscleView activeView;
  final MuscleView view;

  @override
  Color substitute(
    String? id,
    String elementName,
    String attributeName,
    Color color,
  ) {
    final groupId = groupForSvgId(id);
    final opacity = view == activeView ? 1.0 : .58;
    if (groupId == null) {
      return const Color(0xff465773).withValues(alpha: .92 * opacity);
    }
    final score = (scores[groupId] ?? 0).clamp(0.0, 1.0).toDouble();
    return _heatColorForScore(score).withValues(alpha: .96 * opacity);
  }

  @visibleForTesting
  static String? groupForSvgId(String? id) {
    if (id == null) return null;
    if (id.contains('pectoralis') || id == 'chest') return 'pectorals';
    if (id.contains('deltoid') || id == 'shoulders') return 'deltoids';
    if (id.contains('biceps_brachii')) return 'biceps';
    if (id.contains('triceps_brachii')) return 'triceps';
    if (id.contains('external_oblique')) return 'obliques';
    if (id.contains('rectus_abdominis') || id == 'core') return 'abs';
    if (id.contains('rectus_femoris') || id.contains('vastus_')) {
      return 'quadriceps';
    }
    if (id.contains('sartori') ||
        id.contains('adductor') ||
        id.contains('pectineus') ||
        id.contains('gracilis')) {
      return 'quadriceps';
    }
    if (id.contains('trapezius')) return 'trapezius';
    if (id.contains('latissimus')) return 'lats';
    if (id.contains('gluteus') || id == 'glutes') return 'glutes';
    if (id.contains('hamstring') ||
        id.contains('biceps_femoris') ||
        id.contains('semitendinosus') ||
        id.contains('semimembranosus') ||
        id.contains('iliotibial')) {
      return 'hamstrings';
    }
    if (id.contains('gastrocnemius') ||
        id.contains('tibialis') ||
        id.contains('fibularis') ||
        id == 'legs') {
      return 'calves';
    }
    return null;
  }
}

/// Stable SVG ID mapping exposed through a small test-facing facade so asset
/// audits can verify that every lower-body path receives a heat group without
/// coupling tests to the private ColorMapper implementation.
@visibleForTesting
class MuscleHeatmapColorMapping {
  const MuscleHeatmapColorMapping._();

  static String? groupForSvgId(String? id) =>
      _HeatmapColorMapper.groupForSvgId(id);
}

Color _heatColorForScore(double score) {
  const nodes = <double>[0, .15, .35, .6, .8, 1];
  const colors = <Color>[
    Color(0xff53677f),
    Color(0xff3d8eaa),
    Color(0xfff39a3d),
    Color(0xffffb52e),
    Color(0xffff6a24),
    Color(0xffe92f2f),
  ];
  for (var i = 1; i < nodes.length; i++) {
    if (score <= nodes[i]) {
      final t = (score - nodes[i - 1]) / (nodes[i] - nodes[i - 1]);
      return Color.lerp(colors[i - 1], colors[i], t.clamp(0, 1))!;
    }
  }
  return colors.last;
}
