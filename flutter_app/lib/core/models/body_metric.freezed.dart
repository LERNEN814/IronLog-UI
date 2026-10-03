// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'body_metric.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BodyMetric {

 String get id; DateTime get measuredAt; BodyMetricType get type; double get value; String get unit; String? get note; int get schemaVersion;
/// Create a copy of BodyMetric
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BodyMetricCopyWith<BodyMetric> get copyWith => _$BodyMetricCopyWithImpl<BodyMetric>(this as BodyMetric, _$identity);

  /// Serializes this BodyMetric to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as BodyMetric;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BodyMetric&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.measuredAt, _this.measuredAt) || other.measuredAt == _this.measuredAt)&&(identical(other.type, _this.type) || other.type == _this.type)&&(identical(other.value, _this.value) || other.value == _this.value)&&(identical(other.unit, _this.unit) || other.unit == _this.unit)&&(identical(other.note, _this.note) || other.note == _this.note)&&(identical(other.schemaVersion, _this.schemaVersion) || other.schemaVersion == _this.schemaVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as BodyMetric;
  return Object.hash(runtimeType,_this.id,_this.measuredAt,_this.type,_this.value,_this.unit,_this.note,_this.schemaVersion);
}

@override
String toString() {
  final _this = this as BodyMetric;
  return 'BodyMetric(id: ${_this.id}, measuredAt: ${_this.measuredAt}, type: ${_this.type}, value: ${_this.value}, unit: ${_this.unit}, note: ${_this.note}, schemaVersion: ${_this.schemaVersion})';
}


}

/// @nodoc
abstract mixin class $BodyMetricCopyWith<$Res>  {
  factory $BodyMetricCopyWith(BodyMetric value, $Res Function(BodyMetric) _then) = _$BodyMetricCopyWithImpl;
@useResult
$Res call({
 String id, DateTime measuredAt, BodyMetricType type, double value, String unit, String? note, int schemaVersion
});




}
/// @nodoc
class _$BodyMetricCopyWithImpl<$Res>
    implements $BodyMetricCopyWith<$Res> {
  _$BodyMetricCopyWithImpl(this._self, this._then);

  final BodyMetric _self;
  final $Res Function(BodyMetric) _then;

/// Create a copy of BodyMetric
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? measuredAt = null,Object? type = null,Object? value = null,Object? unit = null,Object? note = freezed,Object? schemaVersion = null,}) {
  return _then(BodyMetric(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,measuredAt: null == measuredAt ? _self.measuredAt : measuredAt // ignore: cast_nullable_to_non_nullable
as DateTime,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as BodyMetricType,value: null == value ? _self.value : value // ignore: cast_nullable_to_non_nullable
as double,unit: null == unit ? _self.unit : unit // ignore: cast_nullable_to_non_nullable
as String,note: freezed == note ? _self.note : note // ignore: cast_nullable_to_non_nullable
as String?,schemaVersion: null == schemaVersion ? _self.schemaVersion : schemaVersion // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [BodyMetric].
extension BodyMetricPatterns on BodyMetric {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BodyMetric value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BodyMetric() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BodyMetric value)  $default,){
final _that = this;
switch (_that) {
case _BodyMetric():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BodyMetric value)?  $default,){
final _that = this;
switch (_that) {
case _BodyMetric() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  DateTime measuredAt,  BodyMetricType type,  double value,  String unit,  String? note,  int schemaVersion)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BodyMetric() when $default != null:
return $default(_that.id,_that.measuredAt,_that.type,_that.value,_that.unit,_that.note,_that.schemaVersion);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  DateTime measuredAt,  BodyMetricType type,  double value,  String unit,  String? note,  int schemaVersion)  $default,) {final _that = this;
switch (_that) {
case _BodyMetric():
return $default(_that.id,_that.measuredAt,_that.type,_that.value,_that.unit,_that.note,_that.schemaVersion);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  DateTime measuredAt,  BodyMetricType type,  double value,  String unit,  String? note,  int schemaVersion)?  $default,) {final _that = this;
switch (_that) {
case _BodyMetric() when $default != null:
return $default(_that.id,_that.measuredAt,_that.type,_that.value,_that.unit,_that.note,_that.schemaVersion);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BodyMetric extends BodyMetric {
  const _BodyMetric({required this.id, required this.measuredAt, required this.type, required this.value, required this.unit, this.note, this.schemaVersion = 1}): super._();
  factory _BodyMetric.fromJson(Map<String, dynamic> json) => _$BodyMetricFromJson(json);

@override final  String id;
@override final  DateTime measuredAt;
@override final  BodyMetricType type;
@override final  double value;
@override final  String unit;
@override final  String? note;
@override@JsonKey() final  int schemaVersion;

/// Create a copy of BodyMetric
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BodyMetricCopyWith<_BodyMetric> get copyWith => __$BodyMetricCopyWithImpl<_BodyMetric>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BodyMetricToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _BodyMetric&&(identical(other.id, id) || other.id == id)&&(identical(other.measuredAt, measuredAt) || other.measuredAt == measuredAt)&&(identical(other.type, type) || other.type == type)&&(identical(other.value, value) || other.value == value)&&(identical(other.unit, unit) || other.unit == unit)&&(identical(other.note, note) || other.note == note)&&(identical(other.schemaVersion, schemaVersion) || other.schemaVersion == schemaVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,id,measuredAt,type,value,unit,note,schemaVersion);
}

@override
String toString() {
    return 'BodyMetric(id: $id, measuredAt: $measuredAt, type: $type, value: $value, unit: $unit, note: $note, schemaVersion: $schemaVersion)';
}


}

/// @nodoc
abstract mixin class _$BodyMetricCopyWith<$Res> implements $BodyMetricCopyWith<$Res> {
  factory _$BodyMetricCopyWith(_BodyMetric value, $Res Function(_BodyMetric) _then) = __$BodyMetricCopyWithImpl;
@override @useResult
$Res call({
 String id, DateTime measuredAt, BodyMetricType type, double value, String unit, String? note, int schemaVersion
});




}
/// @nodoc
class __$BodyMetricCopyWithImpl<$Res>
    implements _$BodyMetricCopyWith<$Res> {
  __$BodyMetricCopyWithImpl(this._self, this._then);

  final _BodyMetric _self;
  final $Res Function(_BodyMetric) _then;

/// Create a copy of BodyMetric
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? measuredAt = null,Object? type = null,Object? value = null,Object? unit = null,Object? note = freezed,Object? schemaVersion = null,}) {
  return _then(_BodyMetric(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,measuredAt: null == measuredAt ? _self.measuredAt : measuredAt // ignore: cast_nullable_to_non_nullable
as DateTime,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as BodyMetricType,value: null == value ? _self.value : value // ignore: cast_nullable_to_non_nullable
as double,unit: null == unit ? _self.unit : unit // ignore: cast_nullable_to_non_nullable
as String,note: freezed == note ? _self.note : note // ignore: cast_nullable_to_non_nullable
as String?,schemaVersion: null == schemaVersion ? _self.schemaVersion : schemaVersion // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
