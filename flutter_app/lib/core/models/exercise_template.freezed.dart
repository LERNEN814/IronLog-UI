// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint, type=warning, deprecated_member_use, deprecated_member_use_from_same_package
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'exercise_template.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// GENERATED CODE - DO NOT MODIFY BY HAND
// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ExerciseTemplate {

 String get id; String get name; String get category; String? get equipment; List<String> get targetMuscleIds; int get defaultSets; int get defaultReps; DateTime get createdAt; DateTime get updatedAt; int get schemaVersion;
/// Create a copy of ExerciseTemplate
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ExerciseTemplateCopyWith<ExerciseTemplate> get copyWith => _$ExerciseTemplateCopyWithImpl<ExerciseTemplate>(this as ExerciseTemplate, _$identity);

  /// Serializes this ExerciseTemplate to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  final _this = this as ExerciseTemplate;
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ExerciseTemplate&&(identical(other.id, _this.id) || other.id == _this.id)&&(identical(other.name, _this.name) || other.name == _this.name)&&(identical(other.category, _this.category) || other.category == _this.category)&&(identical(other.equipment, _this.equipment) || other.equipment == _this.equipment)&&const DeepCollectionEquality().equals(other.targetMuscleIds, _this.targetMuscleIds)&&(identical(other.defaultSets, _this.defaultSets) || other.defaultSets == _this.defaultSets)&&(identical(other.defaultReps, _this.defaultReps) || other.defaultReps == _this.defaultReps)&&(identical(other.createdAt, _this.createdAt) || other.createdAt == _this.createdAt)&&(identical(other.updatedAt, _this.updatedAt) || other.updatedAt == _this.updatedAt)&&(identical(other.schemaVersion, _this.schemaVersion) || other.schemaVersion == _this.schemaVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
  final _this = this as ExerciseTemplate;
  return Object.hash(runtimeType,_this.id,_this.name,_this.category,_this.equipment,const DeepCollectionEquality().hash(_this.targetMuscleIds),_this.defaultSets,_this.defaultReps,_this.createdAt,_this.updatedAt,_this.schemaVersion);
}

@override
String toString() {
  final _this = this as ExerciseTemplate;
  return 'ExerciseTemplate(id: ${_this.id}, name: ${_this.name}, category: ${_this.category}, equipment: ${_this.equipment}, targetMuscleIds: ${_this.targetMuscleIds}, defaultSets: ${_this.defaultSets}, defaultReps: ${_this.defaultReps}, createdAt: ${_this.createdAt}, updatedAt: ${_this.updatedAt}, schemaVersion: ${_this.schemaVersion})';
}


}

/// @nodoc
abstract mixin class $ExerciseTemplateCopyWith<$Res>  {
  factory $ExerciseTemplateCopyWith(ExerciseTemplate value, $Res Function(ExerciseTemplate) _then) = _$ExerciseTemplateCopyWithImpl;
@useResult
$Res call({
 String id, String name, String category, String? equipment, List<String> targetMuscleIds, int defaultSets, int defaultReps, DateTime createdAt, DateTime updatedAt, int schemaVersion
});




}
/// @nodoc
class _$ExerciseTemplateCopyWithImpl<$Res>
    implements $ExerciseTemplateCopyWith<$Res> {
  _$ExerciseTemplateCopyWithImpl(this._self, this._then);

  final ExerciseTemplate _self;
  final $Res Function(ExerciseTemplate) _then;

/// Create a copy of ExerciseTemplate
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? category = null,Object? equipment = freezed,Object? targetMuscleIds = null,Object? defaultSets = null,Object? defaultReps = null,Object? createdAt = null,Object? updatedAt = null,Object? schemaVersion = null,}) {
  return _then(ExerciseTemplate(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as String,equipment: freezed == equipment ? _self.equipment : equipment // ignore: cast_nullable_to_non_nullable
as String?,targetMuscleIds: null == targetMuscleIds ? _self.targetMuscleIds : targetMuscleIds // ignore: cast_nullable_to_non_nullable
as List<String>,defaultSets: null == defaultSets ? _self.defaultSets : defaultSets // ignore: cast_nullable_to_non_nullable
as int,defaultReps: null == defaultReps ? _self.defaultReps : defaultReps // ignore: cast_nullable_to_non_nullable
as int,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as DateTime,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as DateTime,schemaVersion: null == schemaVersion ? _self.schemaVersion : schemaVersion // ignore: cast_nullable_to_non_nullable
as int,
  ));
}

}


/// Adds pattern-matching-related methods to [ExerciseTemplate].
extension ExerciseTemplatePatterns on ExerciseTemplate {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ExerciseTemplate value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ExerciseTemplate() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ExerciseTemplate value)  $default,){
final _that = this;
switch (_that) {
case _ExerciseTemplate():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ExerciseTemplate value)?  $default,){
final _that = this;
switch (_that) {
case _ExerciseTemplate() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  String category,  String? equipment,  List<String> targetMuscleIds,  int defaultSets,  int defaultReps,  DateTime createdAt,  DateTime updatedAt,  int schemaVersion)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ExerciseTemplate() when $default != null:
return $default(_that.id,_that.name,_that.category,_that.equipment,_that.targetMuscleIds,_that.defaultSets,_that.defaultReps,_that.createdAt,_that.updatedAt,_that.schemaVersion);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  String category,  String? equipment,  List<String> targetMuscleIds,  int defaultSets,  int defaultReps,  DateTime createdAt,  DateTime updatedAt,  int schemaVersion)  $default,) {final _that = this;
switch (_that) {
case _ExerciseTemplate():
return $default(_that.id,_that.name,_that.category,_that.equipment,_that.targetMuscleIds,_that.defaultSets,_that.defaultReps,_that.createdAt,_that.updatedAt,_that.schemaVersion);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  String category,  String? equipment,  List<String> targetMuscleIds,  int defaultSets,  int defaultReps,  DateTime createdAt,  DateTime updatedAt,  int schemaVersion)?  $default,) {final _that = this;
switch (_that) {
case _ExerciseTemplate() when $default != null:
return $default(_that.id,_that.name,_that.category,_that.equipment,_that.targetMuscleIds,_that.defaultSets,_that.defaultReps,_that.createdAt,_that.updatedAt,_that.schemaVersion);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ExerciseTemplate extends ExerciseTemplate {
  const _ExerciseTemplate({required this.id, required this.name, this.category = 'strength', this.equipment,  List<String> targetMuscleIds = const <String>[], this.defaultSets = 3, this.defaultReps = 10, required this.createdAt, required this.updatedAt, this.schemaVersion = 1}): _targetMuscleIds = targetMuscleIds,super._();
  factory _ExerciseTemplate.fromJson(Map<String, dynamic> json) => _$ExerciseTemplateFromJson(json);

@override final  String id;
@override final  String name;
@override@JsonKey() final  String category;
@override final  String? equipment;
 final  List<String> _targetMuscleIds;
@override@JsonKey() List<String> get targetMuscleIds {
  if (_targetMuscleIds is EqualUnmodifiableListView) return _targetMuscleIds;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(_targetMuscleIds);
}

@override@JsonKey() final  int defaultSets;
@override@JsonKey() final  int defaultReps;
@override final  DateTime createdAt;
@override final  DateTime updatedAt;
@override@JsonKey() final  int schemaVersion;

/// Create a copy of ExerciseTemplate
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ExerciseTemplateCopyWith<_ExerciseTemplate> get copyWith => __$ExerciseTemplateCopyWithImpl<_ExerciseTemplate>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ExerciseTemplateToJson(this, );
}

@override
bool operator ==(Object other) {
    return identical(this, other) || (other.runtimeType == runtimeType&&other is _ExerciseTemplate&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.category, category) || other.category == category)&&(identical(other.equipment, equipment) || other.equipment == equipment)&&const DeepCollectionEquality().equals(other.targetMuscleIds, _targetMuscleIds)&&(identical(other.defaultSets, defaultSets) || other.defaultSets == defaultSets)&&(identical(other.defaultReps, defaultReps) || other.defaultReps == defaultReps)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt)&&(identical(other.schemaVersion, schemaVersion) || other.schemaVersion == schemaVersion));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode {
    return Object.hash(runtimeType,id,name,category,equipment,const DeepCollectionEquality().hash(_targetMuscleIds),defaultSets,defaultReps,createdAt,updatedAt,schemaVersion);
}

@override
String toString() {
    return 'ExerciseTemplate(id: $id, name: $name, category: $category, equipment: $equipment, targetMuscleIds: $targetMuscleIds, defaultSets: $defaultSets, defaultReps: $defaultReps, createdAt: $createdAt, updatedAt: $updatedAt, schemaVersion: $schemaVersion)';
}


}

/// @nodoc
abstract mixin class _$ExerciseTemplateCopyWith<$Res> implements $ExerciseTemplateCopyWith<$Res> {
  factory _$ExerciseTemplateCopyWith(_ExerciseTemplate value, $Res Function(_ExerciseTemplate) _then) = __$ExerciseTemplateCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, String category, String? equipment, List<String> targetMuscleIds, int defaultSets, int defaultReps, DateTime createdAt, DateTime updatedAt, int schemaVersion
});




}
/// @nodoc
class __$ExerciseTemplateCopyWithImpl<$Res>
    implements _$ExerciseTemplateCopyWith<$Res> {
  __$ExerciseTemplateCopyWithImpl(this._self, this._then);

  final _ExerciseTemplate _self;
  final $Res Function(_ExerciseTemplate) _then;

/// Create a copy of ExerciseTemplate
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? category = null,Object? equipment = freezed,Object? targetMuscleIds = null,Object? defaultSets = null,Object? defaultReps = null,Object? createdAt = null,Object? updatedAt = null,Object? schemaVersion = null,}) {
  return _then(_ExerciseTemplate(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as String,equipment: freezed == equipment ? _self.equipment : equipment // ignore: cast_nullable_to_non_nullable
as String?,targetMuscleIds: null == targetMuscleIds ? _self._targetMuscleIds : targetMuscleIds // ignore: cast_nullable_to_non_nullable
as List<String>,defaultSets: null == defaultSets ? _self.defaultSets : defaultSets // ignore: cast_nullable_to_non_nullable
as int,defaultReps: null == defaultReps ? _self.defaultReps : defaultReps // ignore: cast_nullable_to_non_nullable
as int,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as DateTime,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as DateTime,schemaVersion: null == schemaVersion ? _self.schemaVersion : schemaVersion // ignore: cast_nullable_to_non_nullable
as int,
  ));
}


}

// dart format on
