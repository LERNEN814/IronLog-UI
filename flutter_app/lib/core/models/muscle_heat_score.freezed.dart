// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'muscle_heat_score.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$MuscleHeatScore {

 String get muscleId; HeatmapView get view; HeatmapSide get side; double get score; double get rawLoad; bool get hasData; int get sampleCount; DateTime get rangeStart; DateTime get rangeEnd;
/// Create a copy of MuscleHeatScore
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$MuscleHeatScoreCopyWith<MuscleHeatScore> get copyWith => _$MuscleHeatScoreCopyWithImpl<MuscleHeatScore>(this as MuscleHeatScore, _$identity);

  /// Serializes this MuscleHeatScore to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as MuscleHeatScore;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is MuscleHeatScore&&(identical(other.muscleId, _this.muscleId) || other.muscleId == _this.muscleId)&&(identical(other.view, _this.view) || other.view == _this.view)&&(identical(other.side, _this.side) || other.side == _this.side)&&(identical(other.score, _this.score) || other.score == _this.score)&&(identical(other.rawLoad, _this.rawLoad) || other.rawLoad == _this.rawLoad)&&(identical(other.hasData, _this.hasData) || other.hasData == _this.hasData)&&(identical(other.sampleCount, _this.sampleCount) || other.sampleCount == _this.sampleCount)&&(identical(other.rangeStart, _this.rangeStart) || other.rangeStart == _this.rangeStart)&&(identical(other.rangeEnd, _this.rangeEnd) || other.rangeEnd == _this.rangeEnd));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as MuscleHeatScore;
  return Object.hash(runtimeType,_this.muscleId,_this.view,_this.side,_this.score,_this.rawLoad,_this.hasData,_this.sampleCount,_this.rangeStart,_this.rangeEnd);
}

@override
String toString() {
  final _this = this as MuscleHeatScore;
  return 'MuscleHeatScore(muscleId: ${_this.muscleId}, view: ${_this.view}, side: ${_this.side}, score: ${_this.score}, rawLoad: ${_this.rawLoad}, hasData: ${_this.hasData}, sampleCount: ${_this.sampleCount}, rangeStart: ${_this.rangeStart}, rangeEnd: ${_this.rangeEnd})';
}


}

/// @nodoc
abstract mixin class $MuscleHeatScoreCopyWith<$Res>  {
  factory $MuscleHeatScoreCopyWith(MuscleHeatScore value, $Res Function(MuscleHeatScore) _then) = _$MuscleHeatScoreCopyWithImpl;
@useResult
$Res call({
 String muscleId, HeatmapView view, HeatmapSide side, double score, double rawLoad, bool hasData, int sampleCount, DateTime rangeStart, DateTime rangeEnd
});




}
/// @nodoc
class _$MuscleHeatScoreCopyWithImpl<$Res>
    implements $MuscleHeatScoreCopyWith<$Res> {
  _$MuscleHeatScoreCopyWithImpl(this._self, this._then);

  final MuscleHeatScore _self;
  final $Res Function(MuscleHeatScore) _then;

/// Create a copy of MuscleHeatScore
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? muscleId = null,Object? view = null,Object? side = null,Object? score = null,Object? rawLoad = null,Object? hasData = null,Object? sampleCount = null,Object? rangeStart = null,Object? rangeEnd = null,}) {
  return _then(MuscleHeatScore(
muscleId: null == muscleId ? _self.muscleId : muscleId // ignore: cast_nullable_to_non_nullable
as String,view: null == view ? _self.view : view // ignore: cast_nullable_to_non_nullable
as HeatmapView,side: null == side ? _self.side : side // ignore: cast_nullable_to_non_nullable
as HeatmapSide,score: null == score ? _self.score : score // ignore: cast_nullable_to_non_nullable
as double,rawLoad: null == rawLoad ? _self.rawLoad : rawLoad // ignore: cast_nullable_to_non_nullable
as double,hasData: null == hasData ? _self.hasData : hasData // ignore: cast_nullable_to_non_nullable
as bool,sampleCount: null == sampleCount ? _self.sampleCount : sampleCount // ignore: cast_nullable_to_non_nullable
as int,rangeStart: null == rangeStart ? _self.rangeStart : rangeStart // ignore: cast_nullable_to_non_nullable
as DateTime,rangeEnd: null == rangeEnd ? _self.rangeEnd : rangeEnd // ignore: cast_nullable_to_non_nullable
as DateTime,
  ));
}

}


/// Adds pattern-matching-related methods to [MuscleHeatScore].
extension MuscleHeatScorePatterns on MuscleHeatScore {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _MuscleHeatScore value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _MuscleHeatScore() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _MuscleHeatScore value)  $default,){
final _that = this;
switch (_that) {
case _MuscleHeatScore():
return $default(_that);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _MuscleHeatScore value)?  $default,){
final _that = this;
switch (_that) {
case _MuscleHeatScore() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String muscleId,  HeatmapView view,  HeatmapSide side,  double score,  double rawLoad,  bool hasData,  int sampleCount,  DateTime rangeStart,  DateTime rangeEnd)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _MuscleHeatScore() when $default != null:
return $default(_that.muscleId,_that.view,_that.side,_that.score,_that.rawLoad,_that.hasData,_that.sampleCount,_that.rangeStart,_that.rangeEnd);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String muscleId,  HeatmapView view,  HeatmapSide side,  double score,  double rawLoad,  bool hasData,  int sampleCount,  DateTime rangeStart,  DateTime rangeEnd)  $default,) {final _that = this;
switch (_that) {
case _MuscleHeatScore():
return $default(_that.muscleId,_that.view,_that.side,_that.score,_that.rawLoad,_that.hasData,_that.sampleCount,_that.rangeStart,_that.rangeEnd);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String muscleId,  HeatmapView view,  HeatmapSide side,  double score,  double rawLoad,  bool hasData,  int sampleCount,  DateTime rangeStart,  DateTime rangeEnd)?  $default,) {final _that = this;
switch (_that) {
case _MuscleHeatScore() when $default != null:
return $default(_that.muscleId,_that.view,_that.side,_that.score,_that.rawLoad,_that.hasData,_that.sampleCount,_that.rangeStart,_that.rangeEnd);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _MuscleHeatScore extends MuscleHeatScore {
  const _MuscleHeatScore({required this.muscleId, required this.view, this.side = HeatmapSide.bilateral, this.score = 0, this.rawLoad = 0, this.hasData = false, this.sampleCount = 0, required this.rangeStart, required this.rangeEnd}): super._();
  factory _MuscleHeatScore.fromJson(Map<String, dynamic> json) => _$MuscleHeatScoreFromJson(json);

@override final  String muscleId;
@override final  HeatmapView view;
@override@JsonKey() final  HeatmapSide side;
@override@JsonKey() final  double score;
@override@JsonKey() final  double rawLoad;
@override@JsonKey() final  bool hasData;
@override@JsonKey() final  int sampleCount;
@override final  DateTime rangeStart;
@override final  DateTime rangeEnd;

/// Create a copy of MuscleHeatScore
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$MuscleHeatScoreCopyWith<_MuscleHeatScore> get copyWith => __$MuscleHeatScoreCopyWithImpl<_MuscleHeatScore>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$MuscleHeatScoreToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _MuscleHeatScore&&(identical(other.muscleId, muscleId) || other.muscleId == muscleId)&&(identical(other.view, view) || other.view == view)&&(identical(other.side, side) || other.side == side)&&(identical(other.score, score) || other.score == score)&&(identical(other.rawLoad, rawLoad) || other.rawLoad == rawLoad)&&(identical(other.hasData, hasData) || other.hasData == hasData)&&(identical(other.sampleCount, sampleCount) || other.sampleCount == sampleCount)&&(identical(other.rangeStart, rangeStart) || other.rangeStart == rangeStart)&&(identical(other.rangeEnd, rangeEnd) || other.rangeEnd == rangeEnd));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,muscleId,view,side,score,rawLoad,hasData,sampleCount,rangeStart,rangeEnd);
}

@override
String toString() {
    return 'MuscleHeatScore(muscleId: $muscleId, view: $view, side: $side, score: $score, rawLoad: $rawLoad, hasData: $hasData, sampleCount: $sampleCount, rangeStart: $rangeStart, rangeEnd: $rangeEnd)';
}


}

/// @nodoc
abstract mixin class _$MuscleHeatScoreCopyWith<$Res> implements $MuscleHeatScoreCopyWith<$Res> {
  factory _$MuscleHeatScoreCopyWith(_MuscleHeatScore value, $Res Function(_MuscleHeatScore) _then) = __$MuscleHeatScoreCopyWithImpl;
@override @useResult
$Res call({
 String muscleId, HeatmapView view, HeatmapSide side, double score, double rawLoad, bool hasData, int sampleCount, DateTime rangeStart, DateTime rangeEnd
});




}
/// @nodoc
class __$MuscleHeatScoreCopyWithImpl<$Res>
    implements _$MuscleHeatScoreCopyWith<$Res> {
  __$MuscleHeatScoreCopyWithImpl(this._self, this._then);

  final _MuscleHeatScore _self;
  final $Res Function(_MuscleHeatScore) _then;

/// Create a copy of MuscleHeatScore
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? muscleId = null,Object? view = null,Object? side = null,Object? score = null,Object? rawLoad = null,Object? hasData = null,Object? sampleCount = null,Object? rangeStart = null,Object? rangeEnd = null,}) {
  return _then(_MuscleHeatScore(
muscleId: null == muscleId ? _self.muscleId : muscleId // ignore: cast_nullable_to_non_nullable
as String,view: null == view ? _self.view : view // ignore: cast_nullable_to_non_nullable
as HeatmapView,side: null == side ? _self.side : side // ignore: cast_nullable_to_non_nullable
as HeatmapSide,score: null == score ? _self.score : score // ignore: cast_nullable_to_non_nullable
as double,rawLoad: null == rawLoad ? _self.rawLoad : rawLoad // ignore: cast_nullable_to_non_nullable
as double,hasData: null == hasData ? _self.hasData : hasData // ignore: cast_nullable_to_non_nullable
as bool,sampleCount: null == sampleCount ? _self.sampleCount : sampleCount // ignore: cast_nullable_to_non_nullable
as int,rangeStart: null == rangeStart ? _self.rangeStart : rangeStart // ignore: cast_nullable_to_non_nullable
as DateTime,rangeEnd: null == rangeEnd ? _self.rangeEnd : rangeEnd // ignore: cast_nullable_to_non_nullable
as DateTime,
  ));
}


}

// dart format on
