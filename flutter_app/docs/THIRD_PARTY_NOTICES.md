# Third-party notices

The production heatmap currently uses project-authored Dart `Path` geometry and does not bundle geometry or artwork from the candidate packages documented in `docs/reuse_evaluation.md`.

The application uses the package dependencies listed in `pubspec.yaml` under their respective package licenses. Generated Freezed/json_serializable code remains derived source and does not add artwork.

The bundled `assets/heatmap/body_front.svg` and `body_back.svg` are adapted from `flutter_body_atlas` 0.2.1 geometry (pub archive SHA `3b8243c7d06c29699c9c3bb9f41deae5325496d4884c00e49ac8187ddaada57e`, Git HEAD `d28a0848218757f1d43025f3a98c28faa9b3fffe`). The package code is BSD-3-Clause (Copyright 2026 Kit G.). The anatomical SVG artwork is by Ryan Graves, sourced from the Figma Community file https://www.figma.com/community/file/1320468164820924031 and licensed CC BY 4.0: https://creativecommons.org/licenses/by/4.0/. The SVGs are used as the base anatomy layer; this app adds its own heat masks, texture, labels, and interaction paths. The attribution and license must remain with any binary redistribution.

Candidate asset notices are recorded in `docs/reuse_evaluation.md`; other candidates were not bundled.
