// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'backup_bundle.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BackupBundle {

 int get backupVersion; DateTime get exportedAt; Map<String, dynamic> get settings; List<ExerciseTemplate> get exerciseTemplates; List<WorkoutSession> get workoutSessions; List<BodyMetric> get bodyMetrics;
/// Create a copy of BackupBundle
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BackupBundleCopyWith<BackupBundle> get copyWith => _$BackupBundleCopyWithImpl<BackupBundle>(this as BackupBundle, _$identity);

  /// Serializes this BackupBundle to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as BackupBundle;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BackupBundle&&(identical(other.backupVersion, _this.backupVersion) || other.backupVersion == _this.backupVersion)&&(identical(other.exportedAt, _this.exportedAt) || other.exportedAt == _this.exportedAt)&&const DeepCollectionEquality().equals(other.settings, _this.settings)&&const DeepCollectionEquality().equals(other.exerciseTemplates, _this.exerciseTemplates)&&const DeepCollectionEquality().equals(other.workoutSessions, _this.workoutSessions)&&const DeepCollectionEquality().equals(other.bodyMetrics, _this.bodyMetrics));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as BackupBundle;
  return Object.hash(runtimeType,_this.backupVersion,_this.exportedAt,const DeepCollectionEquality().hash(_this.settings),const DeepCollectionEquality().hash(_this.exerciseTemplates),const DeepCollectionEquality().hash(_this.workoutSessions),const DeepCollectionEquality().hash(_this.bodyMetrics));
}

@override
String toString() {
  final _this = this as BackupBundle;
  return 'BackupBundle(backupVersion: ${_this.backupVersion}, exportedAt: ${_this.exportedAt}, settings: ${_this.settings}, exerciseTemplates: ${_this.exerciseTemplates}, workoutSessions: ${_this.workoutSessions}, bodyMetrics: ${_this.bodyMetrics})';
}


}

/// @nodoc
abstract mixin class $BackupBundleCopyWith<$Res>  {
  factory $BackupBundleCopyWith(BackupBundle value, $Res Function(BackupBundle) _then) = _$BackupBundleCopyWithImpl;
@useResult
$Res call({
 int backupVersion, DateTime exportedAt, Map<String, dynamic> settings, List<ExerciseTemplate> exerciseTemplates, List<WorkoutSession> workoutSessions, List<BodyMetric> bodyMetrics
});




}
/// @nodoc
class _$BackupBundleCopyWithImpl<$Res>
    implements $BackupBundleCopyWith<$Res> {
  _$BackupBundleCopyWithImpl(this._self, this._then);

  final BackupBundle _self;
  final $Res Function(BackupBundle) _then;

/// Create a copy of BackupBundle
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? backupVersion = null,Object? exportedAt = null,Object? settings = null,Object? exerciseTemplates = null,Object? workoutSessions = null,Object? bodyMetrics = null,}) {
  return _then(BackupBundle(
backupVersion: null == backupVersion ? _self.backupVersion : backupVersion // ignore: cast_nullable_to_non_nullable
as int,exportedAt: null == exportedAt ? _self.exportedAt : exportedAt // ignore: cast_nullable_to_non_nullable
as DateTime,settings: null == settings ? _self.settings : settings // ignore: cast_nullable_to_non_nullable
as Map<String, dynamic>,exerciseTemplates: null == exerciseTemplates ? _self.exerciseTemplates : exerciseTemplates // ignore: cast_nullable_to_non_nullable
as List<ExerciseTemplate>,workoutSessions: null == workoutSessions ? _self.workoutSessions : workoutSessions // ignore: cast_nullable_to_non_nullable
as List<WorkoutSession>,bodyMetrics: null == bodyMetrics ? _self.bodyMetrics : bodyMetrics // ignore: cast_nullable_to_non_nullable
as List<BodyMetric>,
  ));
}

}


/// Adds pattern-matching-related methods to [BackupBundle].
extension BackupBundlePatterns on BackupBundle {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BackupBundle value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BackupBundle() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BackupBundle value)  $default,){
final _that = this;
switch (_that) {
case _BackupBundle():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BackupBundle value)?  $default,){
final _that = this;
switch (_that) {
case _BackupBundle() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int backupVersion,  DateTime exportedAt,  Map<String, dynamic> settings,  List<ExerciseTemplate> exerciseTemplates,  List<WorkoutSession> workoutSessions,  List<BodyMetric> bodyMetrics)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BackupBundle() when $default != null:
return $default(_that.backupVersion,_that.exportedAt,_that.settings,_that.exerciseTemplates,_that.workoutSessions,_that.bodyMetrics);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int backupVersion,  DateTime exportedAt,  Map<String, dynamic> settings,  List<ExerciseTemplate> exerciseTemplates,  List<WorkoutSession> workoutSessions,  List<BodyMetric> bodyMetrics)  $default,) {final _that = this;
switch (_that) {
case _BackupBundle():
return $default(_that.backupVersion,_that.exportedAt,_that.settings,_that.exerciseTemplates,_that.workoutSessions,_that.bodyMetrics);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int backupVersion,  DateTime exportedAt,  Map<String, dynamic> settings,  List<ExerciseTemplate> exerciseTemplates,  List<WorkoutSession> workoutSessions,  List<BodyMetric> bodyMetrics)?  $default,) {final _that = this;
switch (_that) {
case _BackupBundle() when $default != null:
return $default(_that.backupVersion,_that.exportedAt,_that.settings,_that.exerciseTemplates,_that.workoutSessions,_that.bodyMetrics);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BackupBundle extends BackupBundle {
  const _BackupBundle({this.backupVersion = 1, required this.exportedAt,  Map<String, dynamic> settings = const <String, dynamic>{},  List<ExerciseTemplate> exerciseTemplates = const <ExerciseTemplate>[],  List<WorkoutSession> workoutSessions = const <WorkoutSession>[],  List<BodyMetric> bodyMetrics = const <BodyMetric>[]}): _settings = settings,_exerciseTemplates = exerciseTemplates,_workoutSessions = workoutSessions,_bodyMetrics = bodyMetrics,super._();
  factory _BackupBundle.fromJson(Map<String, dynamic> json) => _$BackupBundleFromJson(json);

@override@JsonKey() final  int backupVersion;
@override final  DateTime exportedAt;
 final  Map<String, dynamic> _settings;
@override@JsonKey() Map<String, dynamic> get settings {
  if (_settings is EqualUnmodifiableMapView) return _settings;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableMapView(_settings);
}

 final  List<ExerciseTemplate> _exerciseTemplates;
@override@JsonKey() List<ExerciseTemplate> get exerciseTemplates {
  if (_exerciseTemplates is EqualUnmodifiableListView) return _exerciseTemplates;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_exerciseTemplates);
}

 final  List<WorkoutSession> _workoutSessions;
@override@JsonKey() List<WorkoutSession> get workoutSessions {
  if (_workoutSessions is EqualUnmodifiableListView) return _workoutSessions;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_workoutSessions);
}

 final  List<BodyMetric> _bodyMetrics;
@override@JsonKey() List<BodyMetric> get bodyMetrics {
  if (_bodyMetrics is EqualUnmodifiableListView) return _bodyMetrics;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_bodyMetrics);
}


/// Create a copy of BackupBundle
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BackupBundleCopyWith<_BackupBundle> get copyWith => __$BackupBundleCopyWithImpl<_BackupBundle>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BackupBundleToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _BackupBundle&&(identical(other.backupVersion, backupVersion) || other.backupVersion == backupVersion)&&(identical(other.exportedAt, exportedAt) || other.exportedAt == exportedAt)&&const DeepCollectionEquality().equals(other.settings, _settings)&&const DeepCollectionEquality().equals(other.exerciseTemplates, _exerciseTemplates)&&const DeepCollectionEquality().equals(other.workoutSessions, _workoutSessions)&&const DeepCollectionEquality().equals(other.bodyMetrics, _bodyMetrics));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,backupVersion,exportedAt,const DeepCollectionEquality().hash(_settings),const DeepCollectionEquality().hash(_exerciseTemplates),const DeepCollectionEquality().hash(_workoutSessions),const DeepCollectionEquality().hash(_bodyMetrics));
}

@override
String toString() {
    return 'BackupBundle(backupVersion: $backupVersion, exportedAt: $exportedAt, settings: $settings, exerciseTemplates: $exerciseTemplates, workoutSessions: $workoutSessions, bodyMetrics: $bodyMetrics)';
}


}

/// @nodoc
abstract mixin class _$BackupBundleCopyWith<$Res> implements $BackupBundleCopyWith<$Res> {
  factory _$BackupBundleCopyWith(_BackupBundle value, $Res Function(_BackupBundle) _then) = __$BackupBundleCopyWithImpl;
@override @useResult
$Res call({
 int backupVersion, DateTime exportedAt, Map<String, dynamic> settings, List<ExerciseTemplate> exerciseTemplates, List<WorkoutSession> workoutSessions, List<BodyMetric> bodyMetrics
});




}
/// @nodoc
class __$BackupBundleCopyWithImpl<$Res>
    implements _$BackupBundleCopyWith<$Res> {
  __$BackupBundleCopyWithImpl(this._self, this._then);

  final _BackupBundle _self;
  final $Res Function(_BackupBundle) _then;

/// Create a copy of BackupBundle
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? backupVersion = null,Object? exportedAt = null,Object? settings = null,Object? exerciseTemplates = null,Object? workoutSessions = null,Object? bodyMetrics = null,}) {
  return _then(_BackupBundle(
backupVersion: null == backupVersion ? _self.backupVersion : backupVersion // ignore: cast_nullable_to_non_nullable
as int,exportedAt: null == exportedAt ? _self.exportedAt : exportedAt // ignore: cast_nullable_to_non_nullable
as DateTime,settings: null == settings ? _self._settings : settings // ignore: cast_nullable_to_non_nullable
as Map<String, dynamic>,exerciseTemplates: null == exerciseTemplates ? _self._exerciseTemplates : exerciseTemplates // ignore: cast_nullable_to_non_nullable
as List<ExerciseTemplate>,workoutSessions: null == workoutSessions ? _self._workoutSessions : workoutSessions // ignore: cast_nullable_to_non_nullable
as List<WorkoutSession>,bodyMetrics: null == bodyMetrics ? _self._bodyMetrics : bodyMetrics // ignore: cast_nullable_to_non_nullable
as List<BodyMetric>,
  ));
}


}

// dart format on
