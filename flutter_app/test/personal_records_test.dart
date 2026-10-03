import 'package:flutter_test/flutter_test.dart';
import 'package:ironlog/core/models/exercise_set.dart';
import 'package:ironlog/core/models/workout_session.dart';
import 'package:ironlog/features/workout/application/personal_records.dart';

void main() {
  test('personal records use the heaviest completed set per exercise', () {
    final records = personalRecordsByExercise([
      WorkoutSession(
        id: 's1',
        startedAt: DateTime(2026, 1, 1),
        sets: [
          const ExerciseSet(
            id: 'a',
            exerciseId: 'bench',
            setIndex: 0,
            reps: 8,
            weight: 80,
            completed: true,
          ),
          const ExerciseSet(
            id: 'b',
            exerciseId: 'bench',
            setIndex: 1,
            reps: 5,
            weight: 90,
            completed: true,
          ),
          const ExerciseSet(
            id: 'c',
            exerciseId: 'squat',
            setIndex: 0,
            reps: 3,
            weight: 120,
            completed: false,
          ),
        ],
      ),
    ]);
    expect(records, {'bench': 90});
  });

  test('personal records have an empty state with no completed sets', () {
    expect(personalRecordsByExercise(const <WorkoutSession>[]), isEmpty);
  });
}
