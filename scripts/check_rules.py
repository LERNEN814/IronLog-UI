"""Static red-line checks for IronLog (see AGENTS.md section 5).

Usage:
  python scripts/check_rules.py                 static checks
  python scripts/check_rules.py --results-only  summarize unit-test XML results
  python scripts/check_rules.py --update-lock   (auditor only) refresh protected-file hashes
"""
import hashlib
import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
MAIN = ROOT / "app/src/main"
MAIN_KT = MAIN / "java"
TEST_KT = ROOT / "app/src/test/java"
LOCK = ROOT / "scripts/protected.lock.json"

# Files the coding agent must never modify. Changes require auditor approval (--update-lock).
PROTECTED = [
    "AGENTS.md",
    "settings.gradle.kts",
    "build.gradle.kts",
    "gradle.properties",
    "gradle/libs.versions.toml",
    "gradle/wrapper/gradle-wrapper.properties",
    "gradle/wrapper/gradle-wrapper.jar",
    "app/build.gradle.kts",
    "scripts/check_rules.py",
    "scripts/verify.sh",
    "scripts/env.sh",
    "docs/PRD.md",
    "docs/ARCHITECTURE.md",
    "docs/DATA_MODEL.md",
    "docs/DOMAIN_RULES.md",
    "docs/REST_TIMER.md",
    "docs/handoff/TEMPLATE.md",
    "docs/AUDIT.md",
    "docs/KICKOFF.md",
] + sorted(
    str(p.relative_to(ROOT)).replace("\\", "/")
    for d in ("docs/tasks", "docs/audit")
    for p in (ROOT / d).glob("*.md")
)

failures = []
warnings = []


def fail(rule, msg):
    failures.append(f"[{rule}] {msg}")


def warn(rule, msg):
    warnings.append(f"[{rule}] {msg}")


def rel(p):
    return str(p.relative_to(ROOT)).replace("\\", "/")


def kt_files(base):
    return sorted(base.rglob("*.kt")) if base.exists() else []


def strip_comments(src):
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.S)
    return re.sub(r"//[^\n]*", "", src)


def sha(path):
    # Normalize line endings so git checkout (autocrlf) does not change the hash.
    data = path.read_bytes()
    if not path.name.endswith(".jar"):
        data = data.replace(b"\r\n", b"\n")
    return hashlib.sha256(data).hexdigest()


def check_protected():
    if not LOCK.exists():
        fail("R1", "scripts/protected.lock.json missing")
        return
    lock = json.loads(LOCK.read_text(encoding="utf-8"))
    for f in PROTECTED:
        p = ROOT / f
        if not p.exists():
            fail("R1", f"protected file missing: {f}")
        elif f not in lock:
            fail("R1", f"protected file not in lock (auditor must approve): {f}")
        elif lock[f] != sha(p):
            fail("R1/R3", f"protected file modified: {f}")


def check_sources():
    time_pat = re.compile(
        r"System\.currentTimeMillis\(|Instant\.now\(|LocalDate\.now\(|LocalDateTime\.now\(|ZonedDateTime\.now\(|OffsetDateTime\.now\("
    )
    for f in kt_files(MAIN_KT):
        r = rel(f)
        raw = f.read_text(encoding="utf-8")
        code = strip_comments(raw)
        in_core_time = "/core/time/" in r
        if not in_core_time and time_pat.search(code):
            fail("R5", f"direct system time call outside core/time: {r}")
        if not in_core_time and "SystemClock.elapsedRealtime" in code:
            fail("R5", f"elapsedRealtime outside core/time: {r}")
        if "fallbackToDestructiveMigration" in code:
            fail("R4", f"fallbackToDestructiveMigration used: {r}")
        if "/domain/" in r and re.search(r"^\s*import\s+androidx?\.", code, flags=re.M):
            fail("R7", f"android/androidx import in domain layer: {r}")
        if "/data/db/entity/" in r and "ExerciseMuscle" not in f.name:
            if re.search(r":\s*(Float|Double)\??\b", code):
                fail("R6", f"Float/Double column in entity: {r}")
        if re.search(r"[一-鿿]", code):
            fail("R8", f"hard-coded CJK text in Kotlin source (use strings.xml): {r}")
        if re.search(r"\bGlobalScope\b", code):
            fail("R9", f"GlobalScope used: {r}")
        if re.search(r"\brunBlocking\b", code):
            fail("R9", f"runBlocking in production code: {r}")
        if "allowMainThreadQueries" in code:
            fail("R9", f"allowMainThreadQueries in production code: {r}")
        if "!!" in code:
            warn("STYLE", f"'!!' used: {r}")

    for f in kt_files(TEST_KT):
        code = strip_comments(f.read_text(encoding="utf-8"))
        if re.search(r"@Ignore\b", code):
            fail("R2", f"@Ignore in tests: {rel(f)}")


def check_manifest():
    m = MAIN / "AndroidManifest.xml"
    if not m.exists():
        fail("BUILD", "AndroidManifest.xml missing")
        return
    text = m.read_text(encoding="utf-8")
    for perm in ["USE_EXACT_ALARM", "REQUEST_IGNORE_BATTERY_OPTIMIZATIONS", "android.permission.INTERNET",
                 "FOREGROUND_SERVICE"]:
        if re.search(r'android:name="android\.permission\.' + perm.split(".")[-1] + r'"', text):
            fail("R10", f"forbidden permission in manifest: {perm}")


def check_schema():
    db = next((f for f in kt_files(MAIN_KT) if f.name == "IronLogDatabase.kt"), None)
    if db is None:
        return
    m = re.search(r"version\s*=\s*(\d+)", db.read_text(encoding="utf-8"))
    if not m:
        fail("R4", "cannot read @Database version")
        return
    version = int(m.group(1))
    schema_dir = ROOT / "app/schemas/com.ironlog.app.data.db.IronLogDatabase"
    for v in range(1, version + 1):
        if not (schema_dir / f"{v}.json").exists():
            fail("R4", f"exported schema missing for version {v}: {rel(schema_dir)}/{v}.json")
    if version > 1:
        tests = "\n".join(f.read_text(encoding="utf-8") for f in kt_files(TEST_KT))
        for v in range(1, version):
            if f"MIGRATION_{v}_{v + 1}" not in tests:
                fail("R4", f"no migration test references MIGRATION_{v}_{v + 1}")


def results():
    d = ROOT / "app/build/test-results/testDebugUnitTest"
    files = sorted(d.glob("TEST-*.xml")) if d.exists() else []
    if not files:
        print("FAIL [TEST] no unit-test results found (did tests run?)")
        return 1
    total = failed = skipped = 0
    bad = []
    for f in files:
        s = ET.parse(f).getroot()
        t, fl, er, sk = (int(s.get(k, 0)) for k in ("tests", "failures", "errors", "skipped"))
        total += t
        failed += fl + er
        skipped += sk
        if fl + er:
            bad.append(s.get("name"))
    print(f"== Unit tests: {total} run, {failed} failed, {skipped} skipped, {len(files)} classes ==")
    for b in bad:
        print(f"FAIL [TEST] {b}")
    if skipped:
        print("FAIL [R2] skipped tests are not allowed")
    return 1 if failed or skipped else 0


def update_lock():
    lock = {f: sha(ROOT / f) for f in PROTECTED if (ROOT / f).exists()}
    LOCK.write_text(json.dumps(lock, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    print(f"lock updated: {len(lock)} files")


def main():
    if "--update-lock" in sys.argv:
        update_lock()
        return 0
    if "--results-only" in sys.argv:
        return results()
    check_protected()
    check_sources()
    check_manifest()
    check_schema()
    print("== Static rule checks ==")
    for w in warnings:
        print(f"WARN {w}")
    for f in failures:
        print(f"FAIL {f}")
    print(f"static: {len(failures)} fail, {len(warnings)} warn")
    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
