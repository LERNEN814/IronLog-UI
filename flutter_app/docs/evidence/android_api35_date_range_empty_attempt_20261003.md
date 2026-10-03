# API 35 date-range empty-state attempt (2026-10-03)

Device: `emulator-5554` (API 35), package `com.example.fitness.fitness_record_app`.

The date-range control was opened from the overview using the recorded semantic bounds. Calendar mode accepted a tap on October 2, 2026 and returned to the overview with the semantic range `2026年10月02日至2026年10月02日`. The input-mode experiment then attempted to replace both fields with September 1-2, 2026, but Android text injection inserted literal escape/key text (`09%2F...`) into the Flutter date fields; the picker accepted the malformed values and returned to the overview without changing the effective range.

Evidence:

- `android_api35_date_range_empty_attempt_20261003.xml` is the resulting UIAutomator dump.
- `android_api35_date_range_attempt_20261003.png` is the resulting screenshot.
- Final overview semantics were `肌肉训练热力图\n热力图日期范围，2026年10月02日至2026年10月02日\n训练刺激\n高`, `胸大肌...训练刺激 1.00`, and `本周训练\n1 次`; no `所选日期范围内没有已完成训练` or equivalent empty-state semantic was present.

Conclusion: this is a failed automation attempt, not empty-state acceptance evidence. Production code was not changed. The device-level empty-range screenshot remains unverified.
