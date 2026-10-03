import 'dart:typed_data';
import 'dart:ui';

enum MuscleView { front, back }

enum MuscleSide { left, right, bilateral }

typedef DesignPathBuilder = Path Function();

/// Independently drawable and hittable muscle surface in the fixed design space.
class MuscleRegion {
  const MuscleRegion({
    required this.id,
    required this.regionId,
    required this.label,
    required this.view,
    required this.side,
    required this.builder,
    required this.focus,
    required this.anchor,
    required this.elbow,
    required this.textOrigin,
    this.muscleId,
  });

  final String id;
  final String regionId;
  final String label;
  final MuscleView view;
  final MuscleSide side;
  final DesignPathBuilder builder;
  final Offset focus;
  final Offset anchor;
  final Offset elbow;
  final Offset textOrigin;
  final String? muscleId;

  String get groupId => muscleId ?? id;

  Path path(Size size) => builder().transform(_designMatrix(size));

  Offset designToCanvas(Offset point, Size size) => Offset(
    point.dx * size.width / designSize.width,
    point.dy * size.height / designSize.height,
  );

  static const designSize = Size(1000, 1080);

  static Float64List _designMatrix(Size size) {
    final sx = size.width / designSize.width;
    final sy = size.height / designSize.height;
    return Float64List.fromList(<double>[
      sx,
      0,
      0,
      0,
      0,
      sy,
      0,
      0,
      0,
      0,
      1,
      0,
      0,
      0,
      0,
      1,
    ]);
  }
}

Path _frontLeftDeltoid() => Path()
  ..moveTo(112, 212)
  ..cubicTo(80, 218, 66, 250, 70, 294)
  ..cubicTo(73, 326, 92, 345, 111, 356)
  ..cubicTo(123, 329, 141, 300, 166, 278)
  ..cubicTo(170, 252, 158, 224, 141, 215)
  ..cubicTo(129, 209, 119, 209, 112, 212)
  ..close();

Path _frontRightDeltoid() => Path()
  ..moveTo(359, 212)
  ..cubicTo(391, 218, 406, 250, 402, 294)
  ..cubicTo(399, 326, 380, 345, 361, 356)
  ..cubicTo(349, 329, 331, 300, 306, 278)
  ..cubicTo(302, 252, 314, 224, 331, 215)
  ..cubicTo(343, 209, 353, 209, 359, 212)
  ..close();

Path _frontLeftPectoral() => Path()
  ..moveTo(159, 239)
  ..cubicTo(183, 213, 215, 202, 248, 218)
  ..cubicTo(259, 224, 263, 246, 259, 270)
  ..cubicTo(253, 298, 231, 315, 198, 309)
  ..cubicTo(176, 305, 158, 291, 151, 271)
  ..cubicTo(149, 260, 151, 248, 159, 239)
  ..close();

Path _frontRightPectoral() => Path()
  ..moveTo(341, 239)
  ..cubicTo(317, 213, 285, 202, 252, 218)
  ..cubicTo(241, 224, 237, 246, 241, 270)
  ..cubicTo(247, 298, 269, 315, 302, 309)
  ..cubicTo(324, 305, 342, 291, 349, 271)
  ..cubicTo(351, 260, 349, 248, 341, 239)
  ..close();

Path _frontLeftBiceps() => Path()
  ..moveTo(105, 315)
  ..cubicTo(89, 330, 83, 363, 87, 394)
  ..cubicTo(91, 424, 104, 446, 120, 449)
  ..cubicTo(137, 443, 149, 419, 151, 389)
  ..cubicTo(154, 361, 142, 331, 125, 317)
  ..cubicTo(117, 312, 110, 312, 105, 315)
  ..close();

Path _frontRightBiceps() => Path()
  ..moveTo(367, 315)
  ..cubicTo(383, 330, 389, 363, 385, 394)
  ..cubicTo(381, 424, 368, 446, 352, 449)
  ..cubicTo(335, 443, 323, 419, 321, 389)
  ..cubicTo(318, 361, 330, 331, 347, 317)
  ..cubicTo(355, 312, 362, 312, 367, 315)
  ..close();

Path _frontLeftOblique() => Path()
  ..moveTo(163, 309)
  ..cubicTo(148, 318, 145, 346, 151, 377)
  ..cubicTo(157, 403, 170, 421, 190, 431)
  ..lineTo(203, 376)
  ..cubicTo(192, 350, 180, 327, 163, 309)
  ..close();

Path _frontRightOblique() => Path()
  ..moveTo(337, 309)
  ..cubicTo(352, 318, 355, 346, 349, 377)
  ..cubicTo(343, 403, 330, 421, 310, 431)
  ..lineTo(297, 376)
  ..cubicTo(308, 350, 320, 327, 337, 309)
  ..close();

Path _frontAbs() => Path()
  ..moveTo(204, 304)
  ..cubicTo(220, 298, 238, 298, 250, 304)
  ..lineTo(250, 474)
  ..cubicTo(241, 505, 229, 526, 220, 539)
  ..cubicTo(211, 526, 199, 505, 190, 474)
  ..lineTo(190, 304)
  ..cubicTo(194, 302, 199, 302, 204, 304)
  ..close();

Path _frontLeftQuadriceps() => Path()
  ..moveTo(151, 481)
  ..cubicTo(168, 466, 191, 464, 205, 480)
  ..cubicTo(215, 507, 214, 562, 207, 618)
  ..cubicTo(202, 657, 194, 691, 180, 715)
  ..cubicTo(163, 710, 148, 688, 141, 656)
  ..cubicTo(132, 611, 132, 531, 151, 481)
  ..close();

Path _frontRightQuadriceps() => Path()
  ..moveTo(349, 481)
  ..cubicTo(332, 466, 309, 464, 295, 480)
  ..cubicTo(285, 507, 286, 562, 293, 618)
  ..cubicTo(298, 657, 306, 691, 320, 715)
  ..cubicTo(337, 710, 352, 688, 359, 656)
  ..cubicTo(368, 611, 368, 531, 349, 481)
  ..close();

Path _frontLeftCalf() => Path()
  ..moveTo(151, 720)
  ..cubicTo(168, 708, 185, 711, 191, 730)
  ..cubicTo(198, 763, 190, 826, 183, 878)
  ..cubicTo(178, 912, 166, 934, 151, 934)
  ..cubicTo(137, 927, 130, 898, 133, 859)
  ..lineTo(140, 759)
  ..cubicTo(141, 740, 145, 727, 151, 720)
  ..close();

Path _frontRightCalf() => Path()
  ..moveTo(349, 720)
  ..cubicTo(332, 708, 315, 711, 309, 730)
  ..cubicTo(302, 763, 310, 826, 317, 878)
  ..cubicTo(322, 912, 334, 934, 349, 934)
  ..cubicTo(363, 927, 370, 898, 367, 859)
  ..lineTo(360, 759)
  ..cubicTo(359, 740, 355, 727, 349, 720)
  ..close();

Path _backTrapezius() => Path()
  ..moveTo(681, 184)
  ..cubicTo(700, 174, 740, 174, 759, 184)
  ..cubicTo(782, 206, 805, 225, 824, 236)
  ..cubicTo(801, 259, 781, 282, 760, 325)
  ..lineTo(740, 360)
  ..lineTo(720, 325)
  ..cubicTo(699, 282, 679, 259, 656, 236)
  ..cubicTo(675, 225, 698, 206, 681, 184)
  ..close();

Path _backLeftRearDeltoid() => Path()
  ..moveTo(656, 237)
  ..cubicTo(627, 232, 605, 247, 598, 277)
  ..cubicTo(592, 306, 601, 329, 624, 342)
  ..cubicTo(642, 326, 658, 302, 671, 269)
  ..cubicTo(670, 253, 665, 242, 656, 237)
  ..close();

Path _backRightRearDeltoid() => Path()
  ..moveTo(824, 237)
  ..cubicTo(853, 232, 875, 247, 882, 277)
  ..cubicTo(888, 306, 879, 329, 856, 342)
  ..cubicTo(838, 326, 822, 302, 809, 269)
  ..cubicTo(810, 253, 815, 242, 824, 237)
  ..close();

Path _backLeftLat() => Path()
  ..moveTo(675, 268)
  ..cubicTo(696, 276, 714, 300, 720, 333)
  ..cubicTo(716, 377, 707, 426, 695, 472)
  ..cubicTo(674, 466, 649, 444, 634, 411)
  ..cubicTo(626, 377, 633, 314, 675, 268)
  ..close();

Path _backRightLat() => Path()
  ..moveTo(805, 268)
  ..cubicTo(784, 276, 766, 300, 760, 333)
  ..cubicTo(764, 377, 773, 426, 785, 472)
  ..cubicTo(806, 466, 831, 444, 846, 411)
  ..cubicTo(854, 377, 847, 314, 805, 268)
  ..close();

Path _backLeftTriceps() => Path()
  ..moveTo(622, 340)
  ..cubicTo(602, 355, 596, 389, 603, 425)
  ..cubicTo(608, 454, 617, 478, 629, 488)
  ..cubicTo(645, 477, 654, 451, 656, 417)
  ..cubicTo(657, 382, 645, 352, 622, 340)
  ..close();

Path _backRightTriceps() => Path()
  ..moveTo(858, 340)
  ..cubicTo(878, 355, 884, 389, 877, 425)
  ..cubicTo(872, 454, 863, 478, 851, 488)
  ..cubicTo(835, 477, 826, 451, 824, 417)
  ..cubicTo(823, 382, 835, 352, 858, 340)
  ..close();

Path _backLeftGlute() => Path()
  ..moveTo(664, 477)
  ..cubicTo(686, 457, 748, 458, 798, 475)
  ..cubicTo(802, 497, 780, 539, 726, 565)
  ..cubicTo(706, 583, 679, 581, 657, 563)
  ..cubicTo(646, 539, 648, 499, 664, 477)
  ..close();

Path _backRightGlute() => Path()
  ..moveTo(760, 475)
  ..cubicTo(800, 458, 827, 457, 849, 477)
  ..cubicTo(865, 499, 867, 539, 856, 563)
  ..cubicTo(834, 581, 807, 583, 787, 565)
  ..cubicTo(771, 539, 767, 497, 776, 475)
  ..close();

Path _backLeftHamstring() => Path()
  ..moveTo(658, 574)
  ..cubicTo(680, 585, 706, 586, 725, 573)
  ..cubicTo(731, 624, 726, 687, 714, 731)
  ..cubicTo(705, 752, 685, 754, 672, 735)
  ..cubicTo(661, 695, 653, 626, 658, 574)
  ..close();

Path _backRightHamstring() => Path()
  ..moveTo(782, 573)
  ..cubicTo(801, 586, 827, 585, 849, 574)
  ..cubicTo(854, 626, 846, 695, 835, 735)
  ..cubicTo(822, 754, 802, 752, 793, 731)
  ..cubicTo(781, 687, 776, 624, 782, 573)
  ..close();

Path _backLeftCalf() => Path()
  ..moveTo(672, 742)
  ..cubicTo(688, 730, 705, 732, 713, 750)
  ..cubicTo(721, 781, 714, 852, 707, 905)
  ..cubicTo(701, 930, 686, 943, 673, 938)
  ..cubicTo(660, 929, 654, 899, 658, 861)
  ..lineTo(664, 773)
  ..cubicTo(664, 756, 667, 747, 672, 742)
  ..close();

Path _backRightCalf() => Path()
  ..moveTo(828, 742)
  ..cubicTo(812, 730, 795, 732, 787, 750)
  ..cubicTo(779, 781, 786, 852, 793, 905)
  ..cubicTo(799, 930, 814, 943, 827, 938)
  ..cubicTo(840, 929, 846, 899, 842, 861)
  ..lineTo(836, 773)
  ..cubicTo(836, 756, 833, 747, 828, 742)
  ..close();

MuscleRegion _region({
  required String id,
  required String label,
  required MuscleView view,
  required MuscleSide side,
  required DesignPathBuilder builder,
  required Offset focus,
  required Offset anchor,
  required Offset elbow,
  required Offset textOrigin,
  String? muscleId,
}) => MuscleRegion(
  id: muscleId ?? id,
  regionId: id,
  label: label,
  view: view,
  side: side,
  builder: builder,
  focus: focus,
  anchor: anchor,
  elbow: elbow,
  textOrigin: textOrigin,
  muscleId: muscleId,
);

final muscleRegions = <MuscleRegion>[
  _region(
    id: 'front_deltoid_left',
    label: '三角肌',
    view: MuscleView.front,
    side: MuscleSide.left,
    builder: _frontLeftDeltoid,
    focus: const Offset(112, 270),
    anchor: const Offset(112, 270),
    elbow: const Offset(70, 190),
    textOrigin: const Offset(35, 130),
    muscleId: 'deltoids',
  ),
  _region(
    id: 'front_deltoid_right',
    label: '三角肌',
    view: MuscleView.front,
    side: MuscleSide.right,
    builder: _frontRightDeltoid,
    focus: const Offset(359, 270),
    anchor: const Offset(359, 270),
    elbow: const Offset(430, 190),
    textOrigin: const Offset(435, 130),
    muscleId: 'deltoids',
  ),
  _region(
    id: 'front_pectoral_left',
    label: '胸大肌',
    view: MuscleView.front,
    side: MuscleSide.left,
    builder: _frontLeftPectoral,
    focus: const Offset(205, 258),
    anchor: const Offset(205, 258),
    elbow: const Offset(150, 170),
    textOrigin: const Offset(35, 185),
    muscleId: 'pectorals',
  ),
  _region(
    id: 'front_pectoral_right',
    label: '胸大肌',
    view: MuscleView.front,
    side: MuscleSide.right,
    builder: _frontRightPectoral,
    focus: const Offset(295, 258),
    anchor: const Offset(295, 258),
    elbow: const Offset(360, 170),
    textOrigin: const Offset(435, 185),
    muscleId: 'pectorals',
  ),
  _region(
    id: 'front_biceps_left',
    label: '肱二头肌',
    view: MuscleView.front,
    side: MuscleSide.left,
    builder: _frontLeftBiceps,
    focus: const Offset(117, 380),
    anchor: const Offset(117, 380),
    elbow: const Offset(55, 390),
    textOrigin: const Offset(25, 410),
    muscleId: 'biceps',
  ),
  _region(
    id: 'front_biceps_right',
    label: '肱二头肌',
    view: MuscleView.front,
    side: MuscleSide.right,
    builder: _frontRightBiceps,
    focus: const Offset(355, 380),
    anchor: const Offset(355, 380),
    elbow: const Offset(445, 390),
    textOrigin: const Offset(435, 410),
    muscleId: 'biceps',
  ),
  _region(
    id: 'front_oblique_left',
    label: '腹外斜肌',
    view: MuscleView.front,
    side: MuscleSide.left,
    builder: _frontLeftOblique,
    focus: const Offset(174, 365),
    anchor: const Offset(174, 365),
    elbow: const Offset(120, 460),
    textOrigin: const Offset(35, 475),
    muscleId: 'obliques',
  ),
  _region(
    id: 'front_oblique_right',
    label: '腹外斜肌',
    view: MuscleView.front,
    side: MuscleSide.right,
    builder: _frontRightOblique,
    focus: const Offset(326, 365),
    anchor: const Offset(326, 365),
    elbow: const Offset(390, 460),
    textOrigin: const Offset(435, 475),
    muscleId: 'obliques',
  ),
  _region(
    id: 'front_abs',
    label: '腹肌',
    view: MuscleView.front,
    side: MuscleSide.bilateral,
    builder: _frontAbs,
    focus: const Offset(220, 400),
    anchor: const Offset(220, 400),
    elbow: const Offset(300, 470),
    textOrigin: const Offset(330, 485),
    muscleId: 'abs',
  ),
  _region(
    id: 'front_quadriceps_left',
    label: '股四头肌',
    view: MuscleView.front,
    side: MuscleSide.left,
    builder: _frontLeftQuadriceps,
    focus: const Offset(170, 590),
    anchor: const Offset(170, 590),
    elbow: const Offset(95, 700),
    textOrigin: const Offset(25, 725),
    muscleId: 'quadriceps',
  ),
  _region(
    id: 'front_quadriceps_right',
    label: '股四头肌',
    view: MuscleView.front,
    side: MuscleSide.right,
    builder: _frontRightQuadriceps,
    focus: const Offset(330, 590),
    anchor: const Offset(330, 590),
    elbow: const Offset(405, 700),
    textOrigin: const Offset(420, 725),
    muscleId: 'quadriceps',
  ),
  _region(
    id: 'front_calves_left',
    label: '小腿',
    view: MuscleView.front,
    side: MuscleSide.left,
    builder: _frontLeftCalf,
    focus: const Offset(160, 820),
    anchor: const Offset(160, 820),
    elbow: const Offset(90, 875),
    textOrigin: const Offset(25, 900),
    muscleId: 'calves',
  ),
  _region(
    id: 'front_calves_right',
    label: '小腿',
    view: MuscleView.front,
    side: MuscleSide.right,
    builder: _frontRightCalf,
    focus: const Offset(340, 820),
    anchor: const Offset(340, 820),
    elbow: const Offset(410, 875),
    textOrigin: const Offset(420, 900),
    muscleId: 'calves',
  ),
  _region(
    id: 'back_trapezius',
    label: '斜方肌',
    view: MuscleView.back,
    side: MuscleSide.bilateral,
    builder: _backTrapezius,
    focus: const Offset(720, 255),
    anchor: const Offset(720, 255),
    elbow: const Offset(800, 175),
    textOrigin: const Offset(830, 150),
    muscleId: 'trapezius',
  ),
  _region(
    id: 'back_rear_deltoid_left',
    label: '后三角肌',
    view: MuscleView.back,
    side: MuscleSide.left,
    builder: _backLeftRearDeltoid,
    focus: const Offset(625, 285),
    anchor: const Offset(625, 285),
    elbow: const Offset(570, 220),
    textOrigin: const Offset(515, 200),
    muscleId: 'deltoids',
  ),
  _region(
    id: 'back_rear_deltoid_right',
    label: '后三角肌',
    view: MuscleView.back,
    side: MuscleSide.right,
    builder: _backRightRearDeltoid,
    focus: const Offset(875, 285),
    anchor: const Offset(875, 285),
    elbow: const Offset(930, 220),
    textOrigin: const Offset(940, 200),
    muscleId: 'deltoids',
  ),
  _region(
    id: 'back_lat_left',
    label: '背阔肌',
    view: MuscleView.back,
    side: MuscleSide.left,
    builder: _backLeftLat,
    focus: const Offset(665, 360),
    anchor: const Offset(665, 360),
    elbow: const Offset(590, 335),
    textOrigin: const Offset(515, 330),
    muscleId: 'lats',
  ),
  _region(
    id: 'back_lat_right',
    label: '背阔肌',
    view: MuscleView.back,
    side: MuscleSide.right,
    builder: _backRightLat,
    focus: const Offset(775, 360),
    anchor: const Offset(775, 360),
    elbow: const Offset(850, 335),
    textOrigin: const Offset(860, 330),
    muscleId: 'lats',
  ),
  _region(
    id: 'back_triceps_left',
    label: '肱三头肌',
    view: MuscleView.back,
    side: MuscleSide.left,
    builder: _backLeftTriceps,
    focus: const Offset(625, 405),
    anchor: const Offset(625, 405),
    elbow: const Offset(570, 445),
    textOrigin: const Offset(505, 465),
    muscleId: 'triceps',
  ),
  _region(
    id: 'back_triceps_right',
    label: '肱三头肌',
    view: MuscleView.back,
    side: MuscleSide.right,
    builder: _backRightTriceps,
    focus: const Offset(855, 405),
    anchor: const Offset(855, 405),
    elbow: const Offset(910, 445),
    textOrigin: const Offset(920, 465),
    muscleId: 'triceps',
  ),
  _region(
    id: 'back_glute_left',
    label: '臀肌',
    view: MuscleView.back,
    side: MuscleSide.left,
    builder: _backLeftGlute,
    focus: const Offset(690, 520),
    anchor: const Offset(690, 520),
    elbow: const Offset(610, 590),
    textOrigin: const Offset(505, 610),
    muscleId: 'glutes',
  ),
  _region(
    id: 'back_glute_right',
    label: '臀肌',
    view: MuscleView.back,
    side: MuscleSide.right,
    builder: _backRightGlute,
    focus: const Offset(810, 520),
    anchor: const Offset(810, 520),
    elbow: const Offset(870, 590),
    textOrigin: const Offset(880, 610),
    muscleId: 'glutes',
  ),
  _region(
    id: 'back_hamstring_left',
    label: '腘绳肌',
    view: MuscleView.back,
    side: MuscleSide.left,
    builder: _backLeftHamstring,
    focus: const Offset(690, 650),
    anchor: const Offset(690, 650),
    elbow: const Offset(610, 700),
    textOrigin: const Offset(505, 720),
    muscleId: 'hamstrings',
  ),
  _region(
    id: 'back_hamstring_right',
    label: '腘绳肌',
    view: MuscleView.back,
    side: MuscleSide.right,
    builder: _backRightHamstring,
    focus: const Offset(810, 650),
    anchor: const Offset(810, 650),
    elbow: const Offset(870, 700),
    textOrigin: const Offset(880, 720),
    muscleId: 'hamstrings',
  ),
  _region(
    id: 'back_calves_left',
    label: '小腿',
    view: MuscleView.back,
    side: MuscleSide.left,
    builder: _backLeftCalf,
    focus: const Offset(685, 830),
    anchor: const Offset(685, 830),
    elbow: const Offset(610, 890),
    textOrigin: const Offset(505, 915),
    muscleId: 'calves',
  ),
  _region(
    id: 'back_calves_right',
    label: '小腿',
    view: MuscleView.back,
    side: MuscleSide.right,
    builder: _backRightCalf,
    focus: const Offset(815, 830),
    anchor: const Offset(815, 830),
    elbow: const Offset(870, 890),
    textOrigin: const Offset(880, 915),
    muscleId: 'calves',
  ),
];

MuscleRegion? muscleRegionAt(Offset point, Size size, {MuscleView? view}) {
  for (final region in muscleRegions.reversed) {
    if (view != null && region.view != view) continue;
    if (region.path(size).contains(point)) return region;
  }
  return null;
}
